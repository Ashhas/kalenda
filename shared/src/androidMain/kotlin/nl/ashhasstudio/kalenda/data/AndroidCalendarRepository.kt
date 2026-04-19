package nl.ashhasstudio.kalenda.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.GoogleCalendar

class AndroidCalendarRepository(
    context: Context,
    private val client: HttpClient = HttpClientProvider.instance
) : CalendarRepository {

    private val dataStore = DataStoreProvider.get(context)
    private val cacheKey = stringPreferencesKey("event_cache")

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun fetchCalendarList(account: GoogleAccount): List<GoogleCalendar> {
        val response = client.get(
            "https://www.googleapis.com/calendar/v3/users/me/calendarList"
        ) {
            header("Authorization", "Bearer ${account.accessToken}")
        }
        ensureSuccess(response, "calendarList")
        val calendarList: GoogleCalendarListResponse = response.body()

        return calendarList.items.map { cal ->
            val existing = account.calendars.find { it.id == cal.id }
            GoogleCalendar(
                id = cal.id,
                accountId = account.id,
                name = cal.summary ?: cal.id,
                color = parseCalendarColor(cal.backgroundColor, cal.id),
                enabled = existing?.enabled ?: true,
                showAllDay = existing?.showAllDay ?: true,
            )
        }
    }

    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> {
        val now = Clock.System.now()
        val timeMin = now.toString()
        val timeMax = now.plus(fromDays, DateTimeUnit.DAY, TimeZone.UTC).toString()

        // Use the cached calendar list from the account rather than re-fetching from Google.
        // FetchEventsUseCase syncs the list before calling fetchEvents.
        val calendars = account.calendars.filter { it.enabled }
        if (calendars.isEmpty()) return emptyList()

        val allDayHiddenCalendarIds = account.calendars
            .filter { !it.showAllDay }
            .map { it.id }
            .toSet()

        return calendars.flatMap { cal ->
            fetchCalendarEventsIsolated(account, cal, timeMin, timeMax, allDayHiddenCalendarIds)
        }
    }

    private suspend fun fetchCalendarEventsIsolated(
        account: GoogleAccount,
        cal: GoogleCalendar,
        timeMin: String,
        timeMax: String,
        allDayHiddenCalendarIds: Set<String>,
    ): List<CalendarEvent> {
        val response = try {
            client.get(
                "https://www.googleapis.com/calendar/v3/calendars/${cal.id}/events"
            ) {
                header("Authorization", "Bearer ${account.accessToken}")
                parameter("timeMin", timeMin)
                parameter("timeMax", timeMax)
                parameter("singleEvents", "true")
                parameter("orderBy", "startTime")
                parameter("fields", "items(id,summary,start,end,colorId)")
            }
        } catch (e: Exception) {
            // Per-calendar network/IO error: skip this calendar, keep going.
            Log.w("KalendaCalRepo", "Network error fetching ${cal.id}, skipping", e)
            return emptyList()
        }

        // 401/403: let AuthException bubble up so FetchEventsUseCase can refresh the token.
        if (response.status.value == 401 || response.status.value == 403) {
            throw AuthException("Auth failed for calendar/${cal.id} (HTTP ${response.status.value})")
        }
        // Any other non-success (404 deleted calendar, 429 rate-limit, 5xx) — skip this
        // calendar and keep fetching the others.
        if (response.status.value !in 200..299) {
            Log.w("KalendaCalRepo", "Skipping ${cal.id}: HTTP ${response.status.value}")
            return emptyList()
        }

        val eventsResponse: GoogleEventsResponse = try {
            response.body()
        } catch (e: Exception) {
            Log.w("KalendaCalRepo", "Malformed response for ${cal.id}, skipping", e)
            return emptyList()
        }

        val hiddenAllDay = cal.id in allDayHiddenCalendarIds
        val calendarColorString = longToHex(cal.color)
        return eventsResponse.items.mapNotNull { event ->
            val domainEvent = runCatching {
                event.toDomain(account.id, cal.id, calendarColorString)
            }.onFailure {
                Log.w("KalendaCalRepo", "Failed to parse event ${event.id} in ${cal.id}", it)
            }.getOrNull()
            if (domainEvent != null && domainEvent.isAllDay && hiddenAllDay) null
            else domainEvent
        }
    }

    override suspend fun getCachedEvents(): EventCache {
        val prefs = dataStore.data.first()
        return prefs[cacheKey]?.let { decodeCache(it) } ?: EventCache()
    }

    override fun observeCachedEvents(): Flow<EventCache> =
        dataStore.data
            .map { prefs -> prefs[cacheKey]?.let { decodeCache(it) } ?: EventCache() }
            .distinctUntilChanged()
            .catch { e ->
                Log.w("KalendaCalRepo", "observeCachedEvents failed, emitting empty cache", e)
                emit(EventCache())
            }

    private fun decodeCache(raw: String): EventCache =
        runCatching { json.decodeFromString<EventCache>(raw) }
            .onFailure { e -> Log.w("KalendaCalRepo", "Corrupt event cache, resetting", e) }
            .getOrElse { EventCache() }

    override suspend fun updateCache(events: List<CalendarEvent>) {
        dataStore.edit { prefs ->
            prefs[cacheKey] = json.encodeToString(
                EventCache(events = events, lastUpdated = Clock.System.now())
            )
        }
    }

    private fun ensureSuccess(response: HttpResponse, context: String) {
        val status = response.status.value
        if (status == 401 || status == 403) {
            throw AuthException("Auth failed for $context (HTTP $status)")
        }
        if (status !in 200..299) {
            throw CalendarFetchException(context, status)
        }
    }
}

class CalendarFetchException(val context: String, val statusCode: Int) :
    Exception("HTTP $statusCode while fetching $context")

private fun parseCalendarColor(raw: String, calendarId: String): Long {
    val trimmed = raw.trimStart('#').trim()
    val value = trimmed.toLongOrNull(16)
    return when {
        value == null -> {
            Log.w("KalendaCalRepo", "Unparseable calendar color '$raw' for $calendarId")
            0xFF2196F3L
        }
        // 6-char hex like "4FC3F7": prepend 0xFF alpha.
        trimmed.length == 6 -> value or 0xFF000000L
        // 8-char hex: take as-is (treated as ARGB).
        trimmed.length == 8 -> value
        else -> {
            Log.w("KalendaCalRepo", "Unexpected calendar color length '$raw' for $calendarId")
            value or 0xFF000000L
        }
    }
}

private fun longToHex(color: Long): String {
    val rgb = (color and 0xFFFFFFL).toString(16).padStart(6, '0')
    return "#$rgb"
}

@Serializable
private data class GoogleCalendarListResponse(val items: List<GoogleCalendarItem> = emptyList())

@Serializable
private data class GoogleCalendarItem(
    val id: String,
    val summary: String? = null,
    val backgroundColor: String = "#0000FF",
)

@Serializable
private data class GoogleEventsResponse(val items: List<GoogleEventItem> = emptyList())

@Serializable
private data class GoogleEventItem(
    val id: String,
    val summary: String? = null,
    val start: GoogleEventTime? = null,
    val end: GoogleEventTime? = null,
    val colorId: String? = null
) {
    fun toDomain(accountId: String, calendarId: String, calendarColor: String): CalendarEvent? {
        val start = start ?: return null
        val isAllDay = start.date != null
        val startInstant = if (isAllDay) {
            LocalDate.parse(start.date!!).atStartOfDayIn(TimeZone.UTC)
        } else {
            Instant.parse(start.dateTime!!)
        }
        val endInstant = if (isAllDay) {
            end?.date?.let { LocalDate.parse(it).atStartOfDayIn(TimeZone.UTC) } ?: startInstant
        } else {
            end?.dateTime?.let { Instant.parse(it) } ?: startInstant
        }
        val colorLong = parseCalendarColor(calendarColor, calendarId)
        return CalendarEvent(
            id = id,
            title = summary ?: "(No title)",
            calendarColor = colorLong,
            accountId = accountId,
            calendarId = calendarId,
            isAllDay = isAllDay,
            startTime = startInstant,
            endTime = endInstant,
            timezoneId = start.timeZone ?: "UTC"
        )
    }
}

@Serializable
private data class GoogleEventTime(
    val dateTime: String? = null,
    val date: String? = null,
    val timeZone: String? = null
)

package nl.ashhasstudio.kalenda.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.first
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
        if (response.status.value == 401 || response.status.value == 403) {
            throw AuthException("Token expired (${response.status})")
        }
        val calendarList: GoogleCalendarListResponse = response.body()

        return calendarList.items.map { cal ->
            val colorLong = try {
                cal.backgroundColor.trimStart('#').toLong(16) or 0xFF000000L
            } catch (_: NumberFormatException) { 0xFF2196F3L }

            val existing = account.calendars.find { it.id == cal.id }
            GoogleCalendar(
                id = cal.id,
                accountId = account.id,
                name = cal.summary ?: cal.id,
                color = colorLong,
                enabled = existing?.enabled ?: true,
                showAllDay = existing?.showAllDay ?: true,
                primary = cal.primary,
            )
        }
    }

    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> {
        val now = Clock.System.now()
        val timeMin = now.toString()
        val timeMax = now.plus(fromDays, DateTimeUnit.DAY, TimeZone.UTC).toString()

        val calendarListResponse = client.get(
            "https://www.googleapis.com/calendar/v3/users/me/calendarList"
        ) {
            header("Authorization", "Bearer ${account.accessToken}")
        }
        if (calendarListResponse.status.value == 401 || calendarListResponse.status.value == 403) {
            throw AuthException("Token expired (${calendarListResponse.status})")
        }
        val calendarList: GoogleCalendarListResponse = calendarListResponse.body()

        val enabledCalendarIds = if (account.calendars.isEmpty()) {
            calendarList.items.map { it.id }.toSet()
        } else {
            account.calendars.filter { it.enabled }.map { it.id }.toSet()
        }

        val allDayHiddenCalendarIds = account.calendars
            .filter { !it.showAllDay }
            .map { it.id }
            .toSet()

        return calendarList.items
            .filter { it.id in enabledCalendarIds }
            .flatMap { cal ->
                val eventsResponse: GoogleEventsResponse = client.get(
                    "https://www.googleapis.com/calendar/v3/calendars/${cal.id}/events"
                ) {
                    header("Authorization", "Bearer ${account.accessToken}")
                    parameter("timeMin", timeMin)
                    parameter("timeMax", timeMax)
                    parameter("singleEvents", "true")
                    parameter("orderBy", "startTime")
                    parameter("fields", "items(id,summary,start,end,colorId)")
                }.body()

                val hiddenAllDay = cal.id in allDayHiddenCalendarIds
                eventsResponse.items.mapNotNull { event ->
                    val domainEvent = event.toDomain(account.id, cal.id, cal.backgroundColor)
                    if (domainEvent != null && domainEvent.isAllDay && hiddenAllDay) null
                    else domainEvent
                }
            }
    }

    override suspend fun getCachedEvents(): EventCache {
        val prefs = dataStore.data.first()
        return prefs[cacheKey]?.let { json.decodeFromString(it) } ?: EventCache()
    }

    override suspend fun updateCache(events: List<CalendarEvent>) {
        dataStore.edit { prefs ->
            prefs[cacheKey] = json.encodeToString(
                EventCache(events = events, lastUpdated = Clock.System.now())
            )
        }
    }
}

@Serializable
private data class GoogleCalendarListResponse(val items: List<GoogleCalendarItem> = emptyList())

@Serializable
private data class GoogleCalendarItem(
    val id: String,
    val summary: String? = null,
    val backgroundColor: String = "#0000FF",
    val primary: Boolean = false,
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
        val colorLong = try {
            calendarColor.trimStart('#').toLong(16) or 0xFF000000L
        } catch (_: NumberFormatException) { 0xFF2196F3L }
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

package nl.kabisa.kalenda.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
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
import nl.kabisa.kalenda.domain.CalendarEvent
import nl.kabisa.kalenda.domain.GoogleAccount

class AndroidCalendarRepository(context: Context) : CalendarRepository {

    private val dataStore = DataStoreProvider.get(context)
    private val cacheKey = stringPreferencesKey("event_cache")

    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(json) }
    }

    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> {
        val now = Clock.System.now()
        val timeMin = now.toString()
        val timeMax = now.plus(fromDays, DateTimeUnit.DAY, TimeZone.UTC).toString()

        val calendarListResponse: GoogleCalendarListResponse = client.get(
            "https://www.googleapis.com/calendar/v3/users/me/calendarList"
        ) {
            header("Authorization", "Bearer ${account.accessToken}")
        }.body()

        return calendarListResponse.items.flatMap { cal ->
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

            eventsResponse.items.mapNotNull { it.toDomain(account.id, cal.backgroundColor) }
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
private data class GoogleCalendarItem(val id: String, val backgroundColor: String = "#0000FF")

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
    fun toDomain(accountId: String, calendarColor: String): CalendarEvent? {
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

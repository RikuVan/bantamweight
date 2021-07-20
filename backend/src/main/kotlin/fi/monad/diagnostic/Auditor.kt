package fi.monad.diagnostic

import org.http4k.core.Filter
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.Event
import org.http4k.events.EventCategory
import org.http4k.events.Events

data class IncomingEvent(val uri: Uri, val status: Status) : Event {
    val category = EventCategory("incoming")
}

data class OutgoingEvent(val uri: Uri, val status: Status) : Event {
    val category = EventCategory("outgoing")
}


/**
 * This auditor is responsible for logging the performance of inbound calls to the system.
 */
object Auditor {

    /**
     * Audit incoming HTTP interactions
     */
    fun Incoming(events: Events) = Filter { next ->
        {
            next(it).apply { events(IncomingEvent(it.uri, status)) }
        }
    }

    /**
     * Audit outgoing HTTP interactions
     */
    fun Outgoing(events: Events) = Filter { next ->
        {
            next(it).apply { events(OutgoingEvent(it.uri, status)) }
        }
    }
}
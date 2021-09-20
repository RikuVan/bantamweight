package fi.monad

import fi.monad.diagnostic.Auditor
import fi.monad.diagnostic.Diagnostic
import fi.monad.formats.Message
import fi.monad.formats.moshiMessageLens
import fi.monad.persistence.initializeDatabase
import fi.monad.user.UserRoutes
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.events.EventFilters.AddTimestamp
import org.http4k.events.EventFilters.AddZipkinTraces
import org.http4k.events.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Undertow
import org.http4k.server.asServer

val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/formats/json/moshi" bind GET to {
        Response(OK).with(moshiMessageLens of Message("Barry", "Hello there!"))
    },

    "/testing/kotest" bind GET to { request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    }
)

object Application {
    val appDeps = object : AppDependencies() {}

    init {
        initializeDatabase(appDeps)
    }

    val timedEvents = AddZipkinTraces()
        .then(AddTimestamp(appDeps.clock))
        .then(appDeps.events)

    val appRoutes = routes(
        UserRoutes(),
        Diagnostic(),
        "/" bind singlePageApp(ResourceLoader.Classpath("/public"))
    )

    val withFilters: HttpHandler = ServerFilters.CatchAll()
        .then(ServerFilters.RequestTracing())
        .then(Auditor.Incoming(timedEvents))
        .then(appRoutes)
}

fun main() {
    // when deployig to Heroku in a container, the port is set by Heroku
    val herokuPort = System.getenv("PORT")
    val port = if (!herokuPort.isNullOrEmpty()) herokuPort.toInt() else Application.appDeps.config.app.port

    val server = Application.withFilters.asServer(Undertow(port)).start()
    println("Server started on " + server.port())
}

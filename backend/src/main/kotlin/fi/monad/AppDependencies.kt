package fi.monad

import fi.monad.bantamweight.Database
import fi.monad.config.config
import fi.monad.persistence.createSource
import fi.monad.user.PasswordEncryption
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.Events
import org.http4k.format.Moshi
import java.time.Clock

abstract class AppDependencies {
    val config by config()
    val database: Database by lazy { createSource(config.database.filePath)() }
    val events: Events by lazy { AutoMarshallingEvents(Moshi) }
    val clock: Clock by lazy { Clock.systemUTC() }
    val pwdEncryption: PasswordEncryption by lazy { PasswordEncryption }
}
package fi.monad

import fi.monad.bantamweight.Database
import fi.monad.config.config
import fi.monad.persistence.createSource
import fi.monad.user.AuthService
import fi.monad.user.PasswordEncryption
import fi.monad.user.TokenHandler
import fi.monad.user.UserRepository
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.Events
import org.http4k.format.Jackson
import java.time.Clock

abstract class AppDependencies {
    val config by config()
    val database: Database by lazy { createSource(config.database.filePath)() }
    val events: Events by lazy { AutoMarshallingEvents(Jackson) }
    val clock: Clock by lazy { Clock.systemUTC() }
    val pwdEncryption: PasswordEncryption by lazy { PasswordEncryption }
    val userRepository: UserRepository by lazy { UserRepository(database) }
    val passwordEncryption: PasswordEncryption by lazy { pwdEncryption }
    val tokenHandler: TokenHandler by lazy { TokenHandler(config.auth.secret) }
    val authService: AuthService by lazy { AuthService(userRepository, passwordEncryption, tokenHandler) }
}
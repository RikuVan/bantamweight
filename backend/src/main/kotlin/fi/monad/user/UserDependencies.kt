package fi.monad.user

import fi.monad.AppDependencies
import fi.monad.Application

interface UserDependencies {
    private val appDeps: AppDependencies get() = Application.appDeps
    val userRepository: UserRepository get() = UserRepository(appDeps.database)
    val passwordEncryption: PasswordEncryption get() = appDeps.pwdEncryption
    val tokenHandler: TokenHandler get() = TokenHandler(appDeps.config.auth.secret)
    val authService: AuthService get() = AuthService(userRepository, passwordEncryption, tokenHandler)
}
package fi.monad.user

import fi.monad.AppDependencies
import fi.monad.Application

interface UserDependencies {
    private val appDeps: AppDependencies get() = Application.appDeps
    val userRepository: UserRepository get() = appDeps.userRepository
    val passwordEncryption: PasswordEncryption get() = appDeps.passwordEncryption
    val tokenHandler: TokenHandler get() = appDeps.tokenHandler
    val authService: AuthService get() = appDeps.authService
}
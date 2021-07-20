package fi.monad.user

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.invalidate
import java.time.LocalDateTime

/*
  Hierarchical sealed class representing login, refresh and logout results
 */
sealed class AuthResult
sealed class LoginResult : AuthResult()
data class UserOk(val user: UserOut, val refreshCookie: Cookie) : LoginResult()
object InvalidCredentials : LoginResult()
object UserNotFoundForLogin : LoginResult()
data class LogoutResult(val invalidatedCookie: Cookie) : AuthResult()
sealed class RefreshResult : AuthResult()
data class UserRefreshed(val user: UserOut, val refreshCookie: Cookie) : RefreshResult()
object UserNotFoundForRefresh : RefreshResult()

/*
 Service for all user related authentication and production and revocation of tokens.
 User data is provided by the login and fresh endpoints, rather than a separate user resource call

 The auth flow is
    1.  login and add httpOnly cookie with refresh token to the response along with the emphemeral, short-lived accessToken and user data to the body
    2.  the client should store the accessToken in memory or session storage and return and provide it as a header in all api requests
    3.  the client should regulary refresh the accessToken via the refresh endpoint which returns the same response as login
    4.  the server expires the cookie with the refresh cookie when logging out, requiring a new login to replace
 */
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncryption: PasswordEncryption,
    private val tokenHandler: TokenHandler
) {

    fun login(creds: Credentials): Result<LoginResult, Throwable> {
        return userRepository.getUserByEmail(creds.email).map { user ->
            if (user == null) return@map UserNotFoundForLogin
            if (passwordEncryption.isValidPassword(creds.password, user.password)) {
                val roles = user.roles.split(",")
                val tokens = tokenHandler.generateTokens(Payload(creds.email, roles))
                UserOk(
                    UserOut(
                        user.email,
                        user.first_name,
                        user.last_name,
                        user.roles.split(","),
                        tokens.accessToken
                    ),
                    jwtCookie(tokens.refreshToken)
                )
            } else {
                InvalidCredentials
            }
        }
    }

    fun logout(): LogoutResult = LogoutResult(jwtCookie("").invalidate())

    fun refresh(refreshToken: String): Result<RefreshResult, Throwable> {
        return tokenHandler.readRefreshToken(refreshToken).flatMap { token ->
            when (token) {
                is Payload -> userRepository.getUserByEmail(token.email).map { user ->
                    if (user == null) return@map UserNotFoundForRefresh
                    val roles = user.roles.split(",")
                    val tokens = tokenHandler.generateTokens(Payload(token.email, roles))
                    UserRefreshed(
                        UserOut(
                            user.email,
                            user.first_name,
                            user.last_name,
                            user.roles.split(","),
                            tokens.accessToken
                        ),
                        jwtCookie(tokens.refreshToken)
                    )
                }
                else       -> Err(throw Exception(token.toString()))
            }
        }
    }

    private fun jwtCookie(refreshToken: String) = Cookie(REFRESH_TOKEN_NAME, refreshToken)
        .maxAge(REFRESH_EXPIRE_TIME_SECONDS * 1000.toLong())
        .expires(LocalDateTime.now().plusSeconds(REFRESH_EXPIRE_TIME_SECONDS.toLong()))
        .path("/")
        .secure()
        .httpOnly()
        .sameSite(SameSite.Lax)
}
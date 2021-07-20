package fi.monad.user

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import com.github.michaelbull.result.onFailure
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.Date

const val ACCESS_TOKEN_HEADER = "X-BANTAMWEIGHT-ACCESS-TOKEN"
const val REFRESH_TOKEN_NAME = "BANTAMWEIGHT-TOKEN"

const val ACCESS_EXPIRE_TIME_SECONDS = 10 * 60
const val REFRESH_EXPIRE_TIME_SECONDS = 10 * 60 * 60

const val ACCESS_TOKEN_TYPE = "access"
const val REFRESH_TOKEN_TYPE = "refresh"

sealed class Token
data class Payload(val email: String, val roles: List<String> = emptyList()) : Token()
data class WrongType(val message: String) : Token()
data class Expired(val message: String) : Token()
data class Failed(val message: String) : Token()


data class Tokens(val accessToken: String, val refreshToken: String)

class TokenHandler(secret: String) {
    private val secretBytes = secret.toByteArray()
    private val jwtTokens = JwtTokens()

    init {
        if (secret.startsWith("changeme")) {
            println("secret is not configured")
        }
    }

    fun generateTokens(token: Payload): Tokens {
        val accessToken =
            jwtTokens.generate(
                token,
                ACCESS_EXPIRE_TIME_SECONDS,
                mapOf("type" to ACCESS_TOKEN_TYPE, "roles" to token.roles)
            )

        val refreshToken =
            jwtTokens.generate(
                Payload(token.email),
                REFRESH_EXPIRE_TIME_SECONDS,
                mapOf("type" to REFRESH_TOKEN_TYPE)
            )

        return Tokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun readAccessToken(tokenString: String): Result<Token, Throwable> =
        jwtTokens.parse(tokenString, ACCESS_TOKEN_TYPE)

    fun readRefreshToken(tokenString: String): Result<Token, Throwable> =
        jwtTokens.parse(tokenString, REFRESH_TOKEN_TYPE)


    inner class JwtTokens {
        private val signer by lazy { MACSigner(secretBytes) }
        private val verifier by lazy { MACVerifier(secretBytes) }

        fun generate(content: Payload, expireSeconds: Int, claims: Map<String, Any>): String {
            val jwtClaims = jwt {
                subject(content.email)
                expirationTime(expiration(expireSeconds))
                apply {
                    claims.forEach { (k, v) -> claim(k, v) }
                }
            }

            val jwt = SignedJWT(
                JWSHeader(JWSAlgorithm.HS384),
                jwtClaims
            )

            jwt.sign(signer)

            return jwt.serialize()
        }

        fun parse(
            tokenString: String?,
            tokenType: String
        ): Result<Token, Throwable> = runCatching {
            val jwt = SignedJWT.parse(tokenString)

            if (!jwt.verify(verifier)) {
                val errMsg = "Token verification failed: $tokenString"
                return@runCatching Failed(errMsg)
            }

            val type = jwt.jwtClaimsSet.getStringClaim("type")
            if (type != tokenType) {
                val errorMsg = "Token is of wrong type: $tokenString ($type is not $tokenType)"
                return@runCatching WrongType(errorMsg)
            }

            if (Date().after(jwt.jwtClaimsSet.expirationTime)) {
                return@runCatching Expired("Token expired: $jwt.jwtClaimsSet.expirationTime")
            }

            val roles = jwt.jwtClaimsSet?.getStringListClaim("roles") ?: emptyList()

            Payload(jwt.jwtClaimsSet.subject, roles)
        }.onFailure { e ->
            println("Invalid token $tokenString: $e")
        }

        private fun expiration(seconds: Int) = Date(Date().time + 1000 * seconds)

        private fun jwt(init: JWTClaimsSet.Builder.() -> Unit) =
            JWTClaimsSet.Builder().apply(init).build()
    }

}

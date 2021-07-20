package fi.monad.user

data class Credentials(val email: String, val password: String)

data class UserIn(val email: String, val firstName: String, val lastName: String, val password: String)

data class UserOut(
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String> = emptyList(),
    val accessToken: String,
)

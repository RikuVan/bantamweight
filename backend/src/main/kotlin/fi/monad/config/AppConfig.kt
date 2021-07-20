package fi.monad.config

enum class Env { DEV, PROD }

data class App(val env: Env, val port: Int)

data class Database(val filePath: String)

data class Auth(val secret: String, val roles: List<String>) {
    override fun toString(): String {
        return "Auth(secret=****, password=****)"
    }
}

data class Config(val app: App, val database: Database, val auth: Auth)

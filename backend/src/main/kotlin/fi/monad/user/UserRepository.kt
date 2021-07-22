package fi.monad.user

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import fi.monad.bantamweight.Database
import fi.monad.bantamweight.database.FetchAllUsers
import fi.monad.bantamweight.database.FetchUserByEmail

class UserRepository(private val db: Database) {
    fun fetchAll(): Result<List<FetchAllUsers>, Throwable> = runCatching {
        db.userQueries.fetchAllUsers().executeAsList()
    }

    fun getUserByEmail(email: String): Result<FetchUserByEmail?, Throwable> = runCatching {
        db.userQueries.fetchUserByEmail(email).executeAsOneOrNull()
    }

    fun updatePassword(hashedPassword: String, email: String): Result<Boolean, Throwable> = runCatching {
        db.userQueries.updateUserPassword(hashedPassword, email.trim())
        val user = db.userQueries.fetchUserByEmail(email).executeAsOne()
        user.password == hashedPassword
    }
}
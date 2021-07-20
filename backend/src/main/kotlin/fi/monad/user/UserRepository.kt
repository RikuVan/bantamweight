package fi.monad.user

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import fi.monad.bantamweight.Database
import fi.monad.bantamweight.database.FetchUserByEmail

class UserRepository(private val db: Database) {
    fun getUserByEmail(email: String): Result<FetchUserByEmail?, Throwable> = runCatching {
        db.userQueries.fetchUserByEmail(email).executeAsOneOrNull()
    }
}
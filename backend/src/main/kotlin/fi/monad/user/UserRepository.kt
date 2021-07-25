package fi.monad.user

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import fi.monad.bantamweight.Database
import fi.monad.bantamweight.database.FetchAllUsers
import fi.monad.bantamweight.database.FetchUserWithRolesByEmail

class UserRepository(private val db: Database) {
    fun fetchAll(): Result<List<FetchAllUsers>, Throwable> = runCatching {
        db.userQueries.fetchAllUsers().executeAsList()
    }

    fun getUserByEmail(email: String): Result<FetchUserWithRolesByEmail?, Throwable> = runCatching {
        db.userQueries.fetchUserWithRolesByEmail(email).executeAsOneOrNull()
    }

    fun updatePassword(hashedPassword: String, email: String): Result<Boolean, Throwable> = runCatching {
        db.userQueries.updateUserPassword(hashedPassword, email.trim())
        val user = db.userQueries.fetchUserByEmail(email).executeAsOne()
        user.password == hashedPassword
    }

    fun add(user: UserIn): Result<Boolean, Throwable> = runCatching {
        println(user)
        db.userQueries.transactionWithResult {
            db.userQueries.insertUser(user.firstName, user.lastName, user.email, user.password)
            db.userQueries.fetchUserByEmail(user.email).executeAsOneOrNull()?.let {
                println(it)
                user.roles.forEach {role -> db.userQueries.insertUserRole(it.id, role) }
            } ?: rollback(false)
            user != null
        }
    }

    fun update(user: UserDetailsIn): Result<Boolean, Throwable> = runCatching {
        db.userQueries.updateUser(user.firstName, user.lastName, user.email, user.id)
        val user = db.userQueries.fetchUserByEmail(user.email).executeAsOneOrNull()
        user != null
    }

    fun fetchAllRoles(): Result<List<String>, Throwable> = runCatching {
        db.userQueries.fetchAllRoles().executeAsList()
    }
}
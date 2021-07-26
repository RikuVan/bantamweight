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
        db.userQueries.transactionWithResult {
            db.userQueries.insertUser(user.firstName, user.lastName, user.email, user.password)
            db.userQueries.fetchUserByEmail(user.email).executeAsOneOrNull()?.let {
                user.roles.forEach { role -> db.userQueries.insertUserRole(it.id, role) }
            } ?: rollback(false)
            user != null
        }
    }

    fun update(id: Long, user: UserDetailsIn): Result<Boolean, Throwable> = runCatching {
        db.userQueries.transactionWithResult {
            db.userQueries.updateUser(user.firstName, user.lastName, user.email, id)
            val currentRoles = db.userQueries.fetchAllUserRolesForUser(id).executeAsList()
            val rolesToRemove = currentRoles.minus(user.roles)
            val rolesToAdd = user.roles.minus(currentRoles)
            rolesToRemove.forEach {
                db.userQueries.deleteUserRole(id, it)
            }
            rolesToAdd.forEach {
                db.userQueries.insertUserRole(id, it)
            }
            db.userQueries.fetchUserWithRolesByEmail(user.email).executeAsOneOrNull()?.let {
                true
            } ?: rollback(false)
        }
    }

    fun remove(id: Long): Result<Unit, Throwable> = runCatching {
        db.userQueries.transaction {
            db.userQueries.deleteAllUserRoles(id)
            db.userQueries.deleteUser(id)
        }
    }

    fun fetchAllRoles(): Result<List<String>, Throwable> = runCatching {
        db.userQueries.fetchAllRoles().executeAsList()
    }
}
package fi.monad.persistence

import fi.monad.AppDependencies

fun initializeDatabase(deps: AppDependencies) {
    val db = deps.database
    val pwd = deps.pwdEncryption
    val users = db.userQueries.fetchAllUsers().executeAsList()
    val roles = db.userQueries.fetchAllRoles().executeAsList()
    val user = deps.config.database.user

    if (roles.isEmpty()) {
        deps.config.auth.roles.forEach { role ->
            db.userQueries.insertRole(role)
        }

    }
    if (users.isEmpty() && user !== null) {
        db.userQueries.insertUser(user.firstName, user.lastName, user.email, pwd.encryptPassword(user.password))
        val savedUser = db.userQueries.fetchLastUserId().executeAsOneOrNull()
        if (savedUser?.id != null) {
            user.roles.forEach { role ->
                db.userQueries.insertUserRole(savedUser.id, role)
            }
        }
    }
}
package fi.monad.persistence
import fi.monad.AppDependencies

fun initializeDatabase(deps: AppDependencies) {
    val db = deps.database
    val pwd = deps.pwdEncryption
    val users = db.userQueries.fetchAllUsers().executeAsList()
    val roles = db.userQueries.fetchAllRoles().executeAsList()

    if (roles.isEmpty()) {
        db.userQueries.insertRole("admin")
        db.userQueries.insertRole("customer")
    }
    if (users.isEmpty()) {
        db.userQueries.insertUser("Richard", "Van Camp", "richard@gmail.com", pwd.encryptPassword("password"))
        val user = db.userQueries.fetchLastUserId().executeAsOneOrNull()
        if (user?.id != null) {
            db.userQueries.insertUserRole(user.id, "admin")
        }
    }
}
package fi.monad.user

import org.mindrot.jbcrypt.BCrypt

object PasswordEncryption {
    fun encryptPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

    fun isValidPassword(password: String, passwordHash: String): Boolean = BCrypt.checkpw(password, passwordHash)
}

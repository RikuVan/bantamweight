package fi.monad.persistence

import fi.monad.bantamweight.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.util.Properties

fun createSource(filePath: String): () -> Database {
    /* alternatively could use hikari-cp
        val datasourceConfig = HikariConfig().apply {
            jdbcUrl = config.database.url
            ...
        }
        val dataSource = HikariDataSource(datasourceConfig)
        val driver = dataSource.asJdbcDriver()
    **/
    val driver by lazy {
        // pass empty string to use in-memory version
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + filePath, Properties())
    }

    val database by lazy {
        val currentVer = driver.schemaVersion
        if (currentVer == 0) {
            try {
                Database.Schema.create(driver)
            } catch(e: Throwable) {
                println("Creating schema failed: $e")
            }
            driver.setSchemaVersion(1)
            println("init: created tables, setVersion to 1")
        } else {
            val schemaVer: Int = Database.Schema.version
            if (schemaVer > currentVer) {
                // TODO: add migrations
                // Database.Schema.migrate(driver, currentVer, schemaVer)
                // driver.setVersion(schemaVer)
                // println("init: migrated from $currentVer to $schemaVer")
                println("migrations...")
            } else {
                //println("init")
            }
        }
        Database(driver)
    }

    return { database }
}


val JdbcSqliteDriver.schemaVersion: Int
    get() {
        val sqlCursor = executeQuery(null, "PRAGMA user_version;", 0, null)
        return sqlCursor.getLong(0)!!.toInt()
    }

fun JdbcSqliteDriver.setSchemaVersion(version: Int) =
    execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
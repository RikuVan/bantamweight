package fi.monad.config
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.EnvironmentVariablesPropertySource
import java.io.File
import java.nio.file.Path

fun loadConfig(): Config = ConfigLoader.Builder()
    .addPropertySource(
        EnvironmentVariablesPropertySource(
            useUnderscoresAsSeparator = true,
            allowUppercaseNames = true
        )
    ).build()
    .let {
        if (File(configPath).exists()) {
            println("Loading configuration from $configPath")
            it.loadConfigOrThrow(Path.of(configPath))
        } else {
            println("Loading configuration from classpath:/application-dev.yaml")
            it.loadConfigOrThrow("/application-dev.yaml")
        }
    }

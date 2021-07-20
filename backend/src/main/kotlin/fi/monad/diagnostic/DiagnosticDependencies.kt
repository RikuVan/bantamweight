package fi.monad.diagnostic

import fi.monad.AppDependencies
import fi.monad.Application
import java.time.Clock

interface DiagnosticDependencies {
    private val appDeps: AppDependencies get() = Application.appDeps
    val clock: Clock get() = appDeps.clock
}
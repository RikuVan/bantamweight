package fi.monad.config

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val configPath = "/application.yaml"

fun config(): ReadOnlyProperty<Any?, Config> =
    object : ReadOnlyProperty<Any?, Config> {
        val c: Config by lazy { loadConfig() }
        override fun getValue(thisRef: Any?, property: KProperty<*>): Config = c
    }

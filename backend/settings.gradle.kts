import de.fayard.refreshVersions.core.FeatureFlag.LIBS

plugins {
    id("de.fayard.refreshVersions").version("0.20.0")
}

refreshVersions {
    featureFlags {
        enable(LIBS)
    }
    enableBuildSrcLibs()
}

rootProject.name = "Bantamweight"
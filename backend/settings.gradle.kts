import de.fayard.refreshVersions.core.FeatureFlag.LIBS

plugins {
    id("de.fayard.refreshVersions").version("0.10.1")
}

refreshVersions {
    featureFlags {
        enable(LIBS)
    }
    enableBuildSrcLibs()
}

rootProject.name = "Bantamweight"
import de.fayard.refreshVersions.core.FeatureFlag.LIBS

plugins {
    id("de.fayard.refreshVersions").version("0.20.0")
    // workaround https://github.com/jmfayard/refreshVersions/issues/361
    id("com.google.cloud.tools.jib").version("3.1.4").apply(false)
}

refreshVersions {
    featureFlags {
        enable(LIBS)
    }
    enableBuildSrcLibs()
}

rootProject.name = "Bantamweight"
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs ktlint, detekt, Android Lint, and Konsist architecture tests."

    dependsOn(
        ":app:ktlintCheck",
        ":app:detekt",
        ":app:lintDebug",
        ":app:testDebugUnitTest",
    )
}

tasks.register("projectRebuild") {
    group = "rebuild"
    description = "Rebuilds project."

    dependsOn(
        ":app:clean",
    )
}

tasks.register("qualityFormat") {
    group = "formatting"
    description = "Formats Kotlin code with ktlint."

    dependsOn(":app:ktlintFormat")
}

tasks.register("registerTest") {
    group = "verification"
    description = "Runs debug unit tests for registration validation."

    dependsOn(":app:testDebugUnitTest")
}

tasks.register("UnitTests") {
    group = "verification"
    description = "Runs all unit tests."

    dependsOn(":app:test")
}

tasks.register("UiTests") {
    group = "verification"
    description = "Runs Compose UI instrumentation tests on a connected device or emulator."

    dependsOn(":app:connectedDebugAndroidTest")
}

tasks.register("Tests") {
    group = "verification"
    description = "Runs unit tests and Compose UI instrumentation tests."

    dependsOn("UnitTests", "UiTests")
}

package com.pokiepaws.mobile

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureKonsistTest {
    private val mainScope = Konsist.scopeFromProject(moduleName = "app", sourceSetName = "main")

    @Test
    fun `domain layer does not depend on app outer layers`() {
        val violations =
            mainScope.files
                .fromPackage("com.pokiepaws.mobile.domain")
                .importsFrom(
                    "com.pokiepaws.mobile.data",
                    "com.pokiepaws.mobile.ui",
                )

        assertTrue(violations.joinToString(separator = "\n"), violations.isEmpty())
    }

    @Test
    fun `ui layer does not depend on data layer`() {
        val violations =
            mainScope.files
                .fromPackage("com.pokiepaws.mobile.ui")
                .importsFrom("com.pokiepaws.mobile.data")

        assertTrue(violations.joinToString(separator = "\n"), violations.isEmpty())
    }

    @Test
    fun `data layer does not depend on ui layer`() {
        val violations =
            mainScope.files
                .fromPackage("com.pokiepaws.mobile.data")
                .importsFrom("com.pokiepaws.mobile.ui")

        assertTrue(violations.joinToString(separator = "\n"), violations.isEmpty())
    }
}

private fun List<KoFileDeclaration>.fromPackage(packageName: String): List<KoFileDeclaration> =
    filter { file ->
        val filePackage = file.packagee?.name
        filePackage == packageName || filePackage?.startsWith("$packageName.") == true
    }

private fun List<KoFileDeclaration>.importsFrom(vararg packageNames: String): List<String> =
    flatMap { file ->
        file.imports
            .filter { import -> packageNames.any { packageName -> import.name.startsWith(packageName) } }
            .map { import -> "${file.path}: imports ${import.name}" }
    }

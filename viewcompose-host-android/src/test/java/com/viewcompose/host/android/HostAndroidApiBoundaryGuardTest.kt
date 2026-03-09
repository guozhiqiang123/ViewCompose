package com.viewcompose.host.android

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

class HostAndroidApiBoundaryGuardTest {
    @Test
    fun `host-android public api source must not import renderer diagnostics types`() {
        val sourceRoot = resolveMainSourceRoot()
        val diagnosticsImports = setOf(
            "import com.viewcompose.renderer.view.tree.RenderStats",
            "import com.viewcompose.renderer.view.tree.RenderTreeResult",
            "import com.viewcompose.renderer.view.tree.RenderStructureStats",
        )
        val violations = mutableListOf<String>()
        Files.walk(sourceRoot).use { paths ->
            paths
                .filter { it.isRegularFile() && it.extension == "kt" }
                .filter { path -> !path.toString().contains("/runtime/") }
                .forEach { file ->
                    file.readLines()
                        .filter { line -> diagnosticsImports.any(line::startsWith) }
                        .forEach { line ->
                            violations += "${sourceRoot.relativize(file)}: $line"
                        }
                }
        }

        assertTrue(
            "host-android public API should not import renderer diagnostics types.\n${violations.joinToString("\n")}",
            violations.isEmpty(),
        )
    }

    private fun resolveMainSourceRoot(): Path {
        val cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize()
        val moduleRoot = when {
            Files.isDirectory(cwd.resolve("src/main/java")) -> cwd
            Files.isDirectory(cwd.resolve("viewcompose-host-android/src/main/java")) ->
                cwd.resolve("viewcompose-host-android")
            else -> error("Cannot locate viewcompose-host-android module root from $cwd")
        }
        return moduleRoot.resolve("src/main/java/com/viewcompose/host/android")
    }
}


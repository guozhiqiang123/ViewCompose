package com.viewcompose.widget.core.guard

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

class WidgetCoreDependencyGuardTest {
    @Test
    fun `widget-core main source must not import renderer package`() {
        val sourceRoot = resolveMainSourceRoot()
        val violations = mutableListOf<String>()
        Files.walk(sourceRoot).use { paths ->
            paths
                .filter { it.isRegularFile() && it.extension == "kt" }
                .forEach { file ->
                    file.readLines()
                        .filter { line -> line.startsWith("import com.viewcompose.renderer.") }
                        .forEach { line ->
                            violations += "${sourceRoot.relativize(file)}: $line"
                        }
                }
        }

        assertTrue(
            "widget-core should not import renderer package.\n${violations.joinToString("\n")}",
            violations.isEmpty(),
        )
    }

    private fun resolveMainSourceRoot(): Path {
        val cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize()
        val moduleRoot = when {
            Files.isDirectory(cwd.resolve("src/main/java")) -> cwd
            Files.isDirectory(cwd.resolve("viewcompose-widget-core/src/main/java")) ->
                cwd.resolve("viewcompose-widget-core")
            else -> error("Cannot locate viewcompose-widget-core module root from $cwd")
        }
        return moduleRoot.resolve("src/main/java")
    }
}

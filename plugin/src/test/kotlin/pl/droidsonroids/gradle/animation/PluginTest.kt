package pl.droidsonroids.gradle.animation

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class PluginTest : BaseGradleTest() {

    @Test
    internal fun checkConfigurationCache() {
        rootDirectory.resolve("settings.gradle").writeText("")
        rootDirectory.resolve("build.gradle").writeText(
            // language=groovy
            """
                plugins {
                    id "pl.droidsonroids.animation-disabler"
                }
                apply plugin: "com.android.library"
               
                android {
                    namespace "pl.droidsonroids.animation-disabler"
                    compileSdkVersion 33
                    defaultConfig {
                        minSdkVersion 17
                        targetSdkVersion 33
                    }
                }
            """.trimIndent(),
        )


        runTask("connectedDisableAnimations", "--configuration-cache").apply {
            assertThat(task(":connectedDisableAnimations")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(output).contains("Configuration cache entry stored")
        }
        runTask("connectedDisableAnimations", "--configuration-cache").apply {
            assertThat(task(":connectedDisableAnimations")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(output).contains("Configuration cache entry reused.")
        }

        runTask("connectedEnableAnimations", "--configuration-cache").apply {
            assertThat(task(":connectedEnableAnimations")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(output).contains("Configuration cache entry stored")
        }
        runTask("connectedEnableAnimations", "--configuration-cache").apply {
            assertThat(task(":connectedEnableAnimations")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            assertThat(output).contains("Configuration cache entry reused.")
        }
    }
}

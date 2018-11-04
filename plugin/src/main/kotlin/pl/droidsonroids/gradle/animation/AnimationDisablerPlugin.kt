package pl.droidsonroids.gradle.animation

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.DdmPreferences
import com.android.ddmlib.IDevice
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import java.io.IOException
import java.util.concurrent.TimeUnit

class AnimationDisablerPlugin : Plugin<Project> {

    private val androidSerials = System.getenv("ANDROID_SERIAL")?.split(',')

    override fun apply(project: Project) {
        project.pluginManager.apply(BasePlugin::class.java)

        arrayOf("com.android.application", "com.android.library", "com.android.test").forEach {
            project.plugins.withId(it) {
                project.addAnimationTasksWithDependencies()
            }
        }
    }

    private fun Project.addAnimationTasksWithDependencies() = afterEvaluate {
        val disableAnimations = createAnimationScaleTask(false)
        val enableAnimations = createAnimationScaleTask(true)

        tasks.withType(DeviceProviderInstrumentTestTask::class.java).forEach { task ->
            task.dependsOn(disableAnimations)
            task.finalizedBy(enableAnimations)
        }
    }

    private fun Project.createAnimationScaleTask(enableAnimations: Boolean): Task {
        val taskName = "connected${if (enableAnimations) "En" else "Dis"}ableAnimations"

        return tasks.create(taskName) {
            val scale = if (enableAnimations) 1 else 0
            AndroidDebugBridge.initIfNeeded(false)
            val android = project.extensions.getByType(BaseExtension::class.java)
            val bridge = AndroidDebugBridge.createBridge(android.adbExecutable.path, false)
            val shellOutputReceiver = ADBShellOutputReceiver(it.logger)

            it.group = "verification"
            it.description = "Sets animation scale to $scale on all connected Android devices and AVDs"
            it.doLast {
                bridge.setAnimationScale(scale, shellOutputReceiver)
            }
        }
    }

    private fun AndroidDebugBridge.setAnimationScale(value: Int, shellOutputReceiver: ADBShellOutputReceiver) {
        val settingsPrefixes = listOf("window_animation", "transition_animation", "animator_duration")
        val affectedDevices = devices.filter { androidSerials == null || it.serialNumber in androidSerials }

        affectedDevices.forEach { device ->
            settingsPrefixes.forEach { prefix ->
                try {
                    device.setScaleSetting("${prefix}_scale", value, shellOutputReceiver)
                } catch (e: Exception) {
                    throw IOException("Setting ${prefix}_scale to $value on ${device.serialNumber}", e)
                }
            }
        }
    }

    private fun IDevice.setScaleSetting(key: String, value: Int, shellOutputReceiver: ADBShellOutputReceiver) {
        executeShellCommand("settings put global $key $value", shellOutputReceiver, DdmPreferences.getTimeOut().toLong(), TimeUnit.MILLISECONDS)
    }
}
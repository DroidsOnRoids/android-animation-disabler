package pl.droidsonroids.gradle.animation

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.NullOutputReceiver
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import java.util.concurrent.TimeUnit

class AnimationDisablerPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.pluginManager.apply(BasePlugin::class.java)

		val androidPlugins = listOf("com.android.application", "com.android.library", "com.android.test")
		androidPlugins.forEach {
			project.plugins.withId(it) {
				project.addAnimationTasksWithDependencies()
			}
		}
	}

	fun Project.addAnimationTasksWithDependencies() = afterEvaluate {
		val disableAnimations = createAnimationScaleTask(false)
		val enableAnimations = createAnimationScaleTask(true)

		tasks.withType(DeviceProviderInstrumentTestTask::class.java).forEach {
			it.dependsOn(disableAnimations)
			it.finalizedBy(enableAnimations)
		}
	}

	fun Project.createAnimationScaleTask(enableAnimations: Boolean): Task =
			tasks.create("connected${if (enableAnimations) "En" else "Dis"}ableAnimations") {
				val scale = if (enableAnimations) 1 else 0

				AndroidDebugBridge.initIfNeeded(false)
				val android = project.extensions.getByType(BaseExtension::class.java)
				val bridge = AndroidDebugBridge.createBridge(android.adbExecutable.path, false)

				it.group = "verification"
				it.description = "Sets animation scale to $scale on all connected Android devices and AVDs"
				it.doLast {
					bridge.setAnimationScale(scale)
				}
			}

	fun AndroidDebugBridge.setAnimationScale(value: Int) {
		val settingsPrefixes = listOf("window_animation", "transition_animation", "animator_duration")
		devices.forEach { device ->
			settingsPrefixes.forEach { prefix ->
				device.setScaleSetting("${prefix}_scale", value)
			}
		}
	}

	fun IDevice.setScaleSetting(key: String, value: Int) {
		executeShellCommand("settings put global $key $value", NullOutputReceiver.getReceiver(), 1, TimeUnit.SECONDS)
	}
}

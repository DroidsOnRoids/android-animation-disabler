package pl.droidsonroids.gradle.animation

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskProvider

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
        val disableAnimations = registerAnimationScaleTask(false)
        val enableAnimations = registerAnimationScaleTask(true)

        tasks.withType(DeviceProviderInstrumentTestTask::class.java).configureEach { task ->
            task.dependsOn(disableAnimations)
            task.finalizedBy(enableAnimations)
        }
    }

    private fun Project.registerAnimationScaleTask(enableAnimations: Boolean): TaskProvider<out Task> {
        val taskName = "connected${if (enableAnimations) "En" else "Dis"}ableAnimations"
        val scale = if (enableAnimations) 1f else 0f

        return tasks.register(taskName, AnimationScaleSettingTask::class.java) {
            it.group = "verification"
            it.description = "Sets animation scale to $scale on all connected Android devices and AVDs"

            it.adbExecutablePath.set(project.extensions.getByType(BaseExtension::class.java).adbExecutable)
            it.scale.set(scale)
        }
    }
}

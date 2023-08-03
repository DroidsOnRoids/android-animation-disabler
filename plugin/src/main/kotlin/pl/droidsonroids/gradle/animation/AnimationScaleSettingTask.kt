package pl.droidsonroids.gradle.animation

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.DdmPreferences
import com.android.ddmlib.IDevice
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class AnimationScaleSettingTask @Inject constructor(
    objectFactory: ObjectFactory,
    providerFactory: ProviderFactory,
) : DefaultTask() {


    @get:InputFile
    val adbExecutablePath: RegularFileProperty = objectFactory.fileProperty()

    @get:Input
    val scale: Property<Float> = objectFactory.property(Float::class.java)

    @Optional
    @get:Input
    val androidSerials: Provider<String> = providerFactory.environmentVariable("ANDROID_SERIAL")

    init {
        this.outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        @Suppress("DEPRECATION")
        AndroidDebugBridge.initIfNeeded(false)
        val shellOutputReceiver = ADBShellOutputReceiver(logger)
        val bridge = AndroidDebugBridge.createBridge(
            adbExecutablePath.asFile.get().path,
            false,
            30,
            TimeUnit.SECONDS,
        )
        if (!bridge.hasInitialDeviceList()) {
            logger.info("ADB not initialized yet")
            val initialDevices = bridge.rawDeviceList.get(30, TimeUnit.SECONDS).map { it.serial }
            logger.info("Initial devices=$initialDevices")
        }
        bridge.setAnimationScale(scale.get(), shellOutputReceiver)
    }

    private fun AndroidDebugBridge.setAnimationScale(value: Float, shellOutputReceiver: ADBShellOutputReceiver) {
        val settingsPrefixes = listOf("window_animation", "transition_animation", "animator_duration")
        val serials = androidSerials.orNull?.split(',')
        val affectedDevices = if (serials.isNullOrEmpty()) {
            devices.toList()
        } else {
            devices.filter { it.serialNumber in serials }
        }
        logger.info("Affected devices=${affectedDevices.map { it.name }}")

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

    private fun IDevice.setScaleSetting(key: String, value: Float, shellOutputReceiver: ADBShellOutputReceiver) {
        executeShellCommand(
            "settings put global $key $value",
            shellOutputReceiver,
            DdmPreferences.getTimeOut().toLong(),
            TimeUnit.MILLISECONDS,
        )
    }
}

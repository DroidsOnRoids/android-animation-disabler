package pl.droidsonroids.gradle.animation

import com.android.ddmlib.MultiLineReceiver
import org.gradle.api.logging.Logger

class ADBShellOutputReceiver(private val logger: Logger) : MultiLineReceiver() {
	override fun isCancelled() = false

	override fun processNewLines(lines: Array<out String>) {
		lines.forEach {
			logger.debug(it)
		}
	}
}
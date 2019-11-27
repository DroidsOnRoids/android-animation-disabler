package pl.droidsonroids.animationdisabler

import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleTest {

    @Test
    fun animationScalesSetToZeroDuringTest() {
        val contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        arrayOf("window_animation", "transition_animation", "animator_duration").forEach {
            val value = Settings.Global.getFloat(contentResolver, it + "_scale")
            Assert.assertEquals(it, 0f, value)
        }
    }
}

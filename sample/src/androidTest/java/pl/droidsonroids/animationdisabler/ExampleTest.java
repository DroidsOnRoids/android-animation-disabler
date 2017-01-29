package pl.droidsonroids.animationdisabler;

import android.content.ContentResolver;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleTest {
	@Test
	public void animationScalesSetToZeroDuringTest() throws Exception {
		final ContentResolver contentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();
		for (String key : new String[]{"window_animation", "transition_animation", "animator_duration"}) {
			final int value = Settings.Global.getInt(contentResolver, key + "_scale");
			Assert.assertEquals(key, 0, value);
		}
	}
}

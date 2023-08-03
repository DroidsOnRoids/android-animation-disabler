### 1.0.10 - Not released
 - Dependencies versions updated

### 1.0.9 - 26.06.2023
 - Update plugin config. Stop requiring specific dependencies version. Ensure Java8 compatibility.

### 1.0.8
 - Dependencies versions updated
 - Use Gradle task configuration avoidance API -[#9](https://github.com/koral--/android-animation-disabler/pull/9)

### 1.0.7
 - Dependencies versions updated
 - Changed settings value type from Int to Float -[#7](https://github.com/koral--/android-animation-disabler/issues/7)

### 1.0.6
 - Dependencies versions updated
 - Code cleanup and optimization
 
### 1.0.5
 - Dependencies versions updated
 - Increase errors logging verbosity

### 1.0.4
 - Hardcoded 1s ADB shell timeout replaced with value from `DdmPreferences`
 - Debug logging added

### 1.0.3
 - `ANDROID_SERIAL` environment variable support added - [#2](https://github.com/koral--/android-animation-disabler/pull/2)

### 1.0.2
 - Tasks names fixed - [#1](https://github.com/koral--/android-animation-disabler/issues/1)
 - Kotlin version updated to 1.0.6
  
### 1.0.1
 - Plugin apply order fixed, Animation Disabler can be applied before or after Android Gradle plugin

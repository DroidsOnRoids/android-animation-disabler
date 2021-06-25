# android-animation-disabler
Gradle plugin for disabling animations in global settings before UI tests and reenabling them afterwards.

## Usage
Add plugin to Android module `build.gradle` (**not** top-level one):
 ```
 plugins {
   id "pl.droidsonroids.animation-disabler" version "1.0.9"
 }
 ```
See also [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.animation-disabler) and [sample project](sample).

## Troubleshooting
Append `--debug` or `-d` option to Gradle invocation to see ADB command output (if any) in console. Normally if commands succeed there is no output. 

## More info
[Setting animation scale for Android UI tests](http://www.thedroidsonroids.com/blog/setting-animation-scale-for-android-ui-tests/) blog post

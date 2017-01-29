# android-animation-disabler
Gradle plugin for disabling animations in global settings before UI tests and reenabling them afterwards.

##Usage
Just add plugin to Android module `build.gradle` (**not** top-level one):
 ```
 plugins {
   id "pl.droidsonroids.animation-disabler" version "1.0.3"
 }
 ```
See also [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.animation-disabler) and [sample project](sample).

##More info
[Setting animation scale for Android UI tests](http://www.thedroidsonroids.com/blog/setting-animation-scale-for-android-ui-tests/) blog post

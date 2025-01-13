# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# When building a minified app, the Geospatial API requires the GMS location modules to be unminified.
-keep class com.google.android.gms.location.** { *; }
# Keep the GMS authentication libraries as-is when using Keyless Authentication.
-keep class com.google.android.gms.auth.** { *; }

-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

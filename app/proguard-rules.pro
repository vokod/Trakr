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

# About libraries
-keep class .R
-keep class **.R$* {
    <fields>;
}

# Support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Support appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Support cardview
-keep class android.support.v7.widget.RoundRectDrawable { *; }

# Dagger 2
-dontwarn com.google.errorprone.annotations.**

# Chrashlitycs
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# MPAndroid charts
-keep class com.github.mikephil.charting.** { *; }

# GPX parser
-keep class com.codebutchery.androidgpx.** { *; }

# Instabug
-keep class com.instabug.**

# Firesotre
-keepclassmembers class com.awolity.trakr.repository.remote.model.** {*;}
# Needed for DNS resolution.  Present in OpenJDK, but not Android
-dontwarn javax.naming.**
-dontwarn org.checkerframework.**
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.lang.model.element.Modifier

-dontwarn okio.**
-dontwarn com.google.j2objc.annotations.**
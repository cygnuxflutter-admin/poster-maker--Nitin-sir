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


# Keep data binding classes
-keep class androidx.databinding.** { *; }

# Keep the generated BR class, which is used for referencing resources in layouts
-keepclassmembers class **.databinding.** {
    <fields>;
}
# Keep classes required for Navigation component with Data Binding
-keep class * implements androidx.navigation.NavArgs
-keep class * implements androidx.navigation.dynamicfeatures.fragment.ui.AbstractProgressFragment



-keep class androidx.appcompat.widget.** { *; }
-dontwarn com.online.flyer.design.postermaker.Leaflet_activities.**
-dontwarn com.online.flyer.design.postermaker.Leaflet_adapter.**
-dontwarn com.online.flyer.design.postermaker.Leaflet_adManager.**
-dontwarn com.online.flyer.design.postermaker.Leaflet_fragment.**
-dontwarn com.online.flyer.design.postermaker.Leaflet_view.**

# Volley
-keep class com.android.volley.** { *; }
-dontwarn com.android.volley.**

# App Models and Utils for JSON / Serialization / Reflection
-keep class com.online.flyer.design.postermaker.Leaflet_model.** { *; }
-keepclassmembers class com.online.flyer.design.postermaker.Leaflet_model.** { *; }
-keep class com.online.flyer.design.postermaker.Leaflet_utils.** { *; }
-keepclassmembers class com.online.flyer.design.postermaker.Leaflet_utils.** { *; }

# Firebase Database serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <fields>;
    @com.google.firebase.database.PropertyName <methods>;
}


# Keep LifecycleObserver & AppOpenManager for Foreground Ads in release/live builds
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    @androidx.lifecycle.OnLifecycleEvent *;
}
-keep class * implements androidx.lifecycle.DefaultLifecycleObserver { *; }
-keep class com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager { *; }

# ================================================================================================
# Midas - Cryptocurrency Tracking App
# ProGuard Rules
# ================================================================================================

# ================================================================================================
# General Android Rules
# ================================================================================================

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# Keep generic signatures for reflection
-keepattributes Signature

# Keep exceptions for better debugging
-keepattributes Exceptions

# Keep inner classes
-keepattributes InnerClasses

# Keep enum classes
-keepattributes Enum

# ================================================================================================
# Kotlin
# ================================================================================================

# Keep Kotlin metadata
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
-keep class kotlin.Metadata { *; }

# Keep Kotlin intrinsics
-keep class kotlin.jvm.internal.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep coroutines debug
-keep class kotlinx.coroutines.debug.internal.** { *; }

# Kotlin serialization
-keepattributes InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.midas.**$$serializer { *; }
-keepclassmembers class com.midas.** {
    *** Companion;
}
-keepclasseswithmembers class com.midas.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ================================================================================================
# AndroidX
# ================================================================================================

# Keep AndroidX classes
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Lifecycle
-keep class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ================================================================================================
# Jetpack Compose
# ================================================================================================

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep remember functions
-keep class androidx.compose.runtime.RememberKt { *; }

# Keep CompositionLocal
-keep class androidx.compose.runtime.CompositionLocal** { *; }

# ================================================================================================
# Hilt / Dagger
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt modules
-keep @dagger.hilt.InstallIn class *
-keep @dagger.Module class *
-keep @dagger.hilt.components.SingletonComponent class *

# Keep injected fields
-keepclassmembers class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <init>(...);
}

# Keep Hilt entry points
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *

# ================================================================================================
# Ktor
# ================================================================================================

# Keep Ktor client
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep CIO engine
-keep class io.ktor.client.engine.cio.** { *; }

# Keep Ktor serialization
-keep class io.ktor.serialization.** { *; }
-keep class io.ktor.client.plugins.contentnegotiation.** { *; }

# Keep Ktor logging
-keep class io.ktor.client.plugins.logging.** { *; }

# Keep SLF4J (used by Ktor)
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }

# ================================================================================================
# kotlinx.serialization
# ================================================================================================

# Keep serializers
-keepattributes InnerClasses
-keep,includedescriptorclasses class com.midas.**$$serializer { *; }

# Keep @Serializable classes
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serialization annotations
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep JsonNames
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    @kotlinx.serialization.SerialName <fields>;
}

# ================================================================================================
# Room Database
# ================================================================================================

# Keep Room database
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep DAO interfaces
-keep interface * extends androidx.room.Dao { *; }

# Keep Entity classes
-keep @androidx.room.Entity class * { *; }

# Keep Database classes
-keep @androidx.room.Database class * { *; }

# Keep TypeConverters
-keep class * {
    @androidx.room.TypeConverter *;
}

# ================================================================================================
# Coil (Image Loading)
# ================================================================================================

# Keep Coil classes
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# Keep image decoders
-keep class coil.decode.** { *; }

# ================================================================================================
# ViewModels
# ================================================================================================

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep ViewModel factories
-keep class * implements androidx.lifecycle.ViewModelProvider$Factory {
    <init>(...);
}

# ================================================================================================
# Enums
# ================================================================================================

# Keep all enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep enum classes in project
-keep enum com.midas.** { *; }

# ================================================================================================
# Parcelable
# ================================================================================================

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ================================================================================================
# Reflection
# ================================================================================================

# Keep classes accessed via reflection
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ================================================================================================
# Debugging & Crash Reporting
# ================================================================================================

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# Keep stack traces
-keepattributes SourceFile,LineNumberTable

# Rename source file attribute to SourceFile for better stack traces
-renamesourcefileattribute SourceFile

# ================================================================================================
# R8 Full Mode (Aggressive Optimization)
# ================================================================================================

# Keep resources referenced from XML
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Don't warn about missing classes (they might be platform specific)
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ================================================================================================
# Testing (Should be excluded in release)
# ================================================================================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove println in release builds
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static *** println(...);
}

# Remove debug checks
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# ================================================================================================
# Optimization Settings
# ================================================================================================

# Enable optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# ================================================================================================
# Warnings to Suppress
# ================================================================================================

# Suppress warnings for missing classes that are not used
-dontwarn kotlin.reflect.jvm.internal.**
-dontwarn kotlin.reflect.**
-dontwarn java.lang.management.**
-dontwarn javax.naming.**
-dontwarn javax.servlet.**

# ================================================================================================
# End of ProGuard Rules
# ================================================================================================

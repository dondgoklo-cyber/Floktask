# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Kotlin Coroutines
-keepclassmembernames class kotlinx.** { volatile <fields>; }

# Coil
-keep class coil.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Domain models
-keep class com.taskmanager.domain.model.** { *; }

# Data entities
-keep class com.taskmanager.data.local.entity.** { *; }

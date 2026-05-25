-keep class com.fitwell.app.database.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class com.github.mikephil.** { *; }
-dontwarn com.github.mikephil.**

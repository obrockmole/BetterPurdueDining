-dontwarn okhttp3.internal.**

-dontnote META-INF**

-keep class org.sqlite.** { *; }

-keepattributes *Annotation*, InnerClasses, Signature
-keep class kotlinx.serialization.** { *; }
-keepclassmembers enum * { *; }

-keep class *$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class io.ktor.client.plugins.contentnegotiation.** { *; }
-keep class io.ktor.serialization.kotlinx.** { *; }

-keep class androidx.datastore.** { *; }
-dontwarn sun.misc.Unsafe

-keep class com.obrockmole.betterdining.models.** { *; }
-keep class com.obrockmole.betterdining.network.** { *; }
-keep class com.obrockmole.betterdining.repository.** { *; }

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class okio.** { *; }
-dontwarn okio.**
-keep class com.apollographql.apollo.** { *; }
-dontwarn com.apollographql.apollo.**

-keep class io.ktor.client.engine.** { *; }
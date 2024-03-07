-ignorewarnings
-keep class javax.** { *; }
-keep class org.apache.** { *; }
-keep class org.terracotta.offheapstore.** { *; }
-keep class org.to2mbn.jmccc.** { *; }
-keep class org.ehcache.** { *; }
-keep class io.jsonwebtoken.** { *; }

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

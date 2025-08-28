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


# Google API Client, Auth, and Sheets libraries
# 이 규칙들은 API 클라이언트의 핵심 기능 및 인증 관련 클래스들이 제거되지 않도록 합니다.
-keep class com.google.api.client.** { *; }
-keep class com.google.api.services.sheets.** { *; }
-dontwarn com.google.api.client.googleapis.util.Utils
-dontwarn com.google.api.client.util.Data

# Google Sign-In (인증에 사용하는 경우)
# Google 로그인 관련 클래스들을 유지합니다.
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }

# HTTP Client (NetHttpTransport)
# 통신에 사용되는 라이브러리 관련 규칙입니다.
-keepclassmembers class com.google.api.client.http.HttpTransport {
    <fields>;
}
-keep class com.google.api.client.http.apache.v2.ApacheHttpTransport
-keep class com.google.api.client.json.jackson2.** { *; }
-keep class com.google.api.client.json.GenericJson {
    <fields>;
}

# Jackson JSON library (Sheets API가 내부적으로 사용)
# JSON 파싱 라이브러리가 제거되지 않도록 합니다.
-keepnames class com.fasterxml.jackson.core.** { *; }
-dontwarn com.fasterxml.jackson.core.**


# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.naming.InvalidNameException
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.ldap.LdapName
-dontwarn javax.naming.ldap.Rdn
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid
-dontwarn com.sun.net.httpserver.Headers
-dontwarn com.sun.net.httpserver.HttpContext
-dontwarn com.sun.net.httpserver.HttpExchange
-dontwarn com.sun.net.httpserver.HttpHandler
-dontwarn com.sun.net.httpserver.HttpServer
-dontwarn java.awt.Desktop$Action
-dontwarn java.awt.Desktop
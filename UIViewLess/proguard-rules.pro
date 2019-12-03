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

#打印混淆信息
-verbose

#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep class * extends android.support.v4.app.Fragment
-keep class * extends androidx.fragment.app.Fragment
-keep class * extends android.app.Fragment

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

-keep enum **

-keep class * extends FileProvider {*;}

-keep class * extends Activity {*;}
-keep class android.view.View
-keep class * extends View {*;}
-keep class com.luck.picture.lib.rxbus2.**{*;}

-keep class com.bigkoo.pickerview.**{*;}
-keep class com.bigkoo.pickerview.bean.**{*;}
-keep class com.contrarywind.view.**{*;}

# 自绘对象
-keep class com.angcyo.uiview.less.draw.** {*;}
-keep class * extends BaseDraw {*;}

#手动启用support keep注解
#http://tools.android.com/tech-docs/support-annotations
#https://blog.csdn.net/blueangle17/article/details/80521311
-keepattributes *Annotation*
-dontwarn android.support.annotation.Keep
-dontskipnonpubliclibraryclassmembers
-printconfiguration
-keep,allowobfuscation @interface android.support.annotation.Keep

-keep @android.support.annotation.Keep class *
-keep @androidx.annotation.Keep class *

-keepclassmembers class * {
    @android.support.annotation.Keep *;
}

-keep @android.support.annotation.Keep class **{
@android.support.annotation.Keep <fields>;
@android.support.annotation.Keep <methods>;
}

# 保持所有Bean类
-keep class com.angcyo.**.**Bean {*;}
-keep class com.wayto.**.**Bean {*;}

#-keep class android.support.v4.widget.ScrollerCompat
#-keep class android.widget.OverScroller
-keep class android.support.v7.widget.RecyclerView {*;}
-keep class android.support.v4.view.ViewPager {*;}
-keep class android.widget.ArrayAdapter {*;}
-keep class androidx.recyclerview.widget.RecyclerView {*;}
-keep class androidx.recyclerview.widget.RecyclerView$ViewFlinger {*;}
-keep class androidx.viewpager.widget.ViewPager {*;}
-keep class androidx.viewpager2.widget.ViewPager2 {*;}

-keep class com.**.**WeekBar {*;}
-keep class com.**.**MonthView {*;}
-keep class com.**.**WeekView {*;}
-keep class com.**.**YearView {*;}


#androidx
-dontwarn kotlinx.coroutines.flow.**
-dontwarn org.reactivestreams.**
-dontwarn com.angcyo.uiview.less.component.**
-dontwarn com.angcyo.uiview.less.recycler.dslitem.**

-keep public class com.google.vending.licensing.ILicensingService

-keep public class com.android.vending.licensing.ILicensingService

-keep public class com.google.android.vending.licensing.ILicensingService
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class android.support.annotation.Keep

-keep class androidx.annotation.Keep

-keep @android.support.annotation.Keep class * {
    <fields>;
    <methods>;
}

-keep @androidx.annotation.Keep class * {
    <fields>;
    <methods>;
}

-keep class androidx.core.app.CoreComponentFactory {
    <init>();
}
-keep class androidx.core.content.FileProvider {
    <init>();
}
-keep class androidx.appcompat.widget.AppCompatCheckBox {
    <init>(...);
}
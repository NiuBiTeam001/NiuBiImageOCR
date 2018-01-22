

#百度SDK混淆
-keep class com.baidu.ocr.sdk.**{*;}
-dontwarn com.baidu.ocr.**

#友盟代码混淆
-keep class com.umeng.commonsdk.** {*;}
#Bmob代码混淆
-keep class cn.bmob.v3.**

-keep public class com.mi.adtracker.MiAdTracker{ *; }

-keepattributes *Annotation*,InnerClasses

#万普需要添加的混淆
-keep public class cn.waps.** {*;}
-keep public interface cn.waps.** {*;}
-dontwarn cn.waps.**

#基本
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.v4.content.ContextCompat { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * {
  public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}
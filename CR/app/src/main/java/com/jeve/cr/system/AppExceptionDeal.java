package com.jeve.cr.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.jeve.cr.CrApplication;
import com.jeve.cr.activity.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

/**
 * 报错处理(报错后将重启应用)
 * lijiawei
 * 2017-12-21
 */
public class AppExceptionDeal implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    CrApplication crApplication;

    public AppExceptionDeal(CrApplication crApplication) {
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.crApplication = crApplication;
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {

        if (!handleException(ex) && uncaughtExceptionHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            uncaughtExceptionHandler.uncaughtException(t, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(crApplication.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            PendingIntent restartIntent = PendingIntent.getActivity(crApplication.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager) crApplication.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
            crApplication.finishActivity();
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(crApplication.getApplicationContext(), "很抱歉,程序出现异常,即将退出.",
                        Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        return true;
    }
}

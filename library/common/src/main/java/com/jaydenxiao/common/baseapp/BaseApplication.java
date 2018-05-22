package com.jaydenxiao.common.baseapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import android.util.Log;

/**
 * Application和Actovotu,Service一样是android框架的一个系统组件，当android程序启动时系统会创建一个 application对象，
 * 用来存储系统的一些信息。通常我们是不需要指定一个Application的，这时系统会自动帮我们创建，如果需要创建自己 的Application，
 * 也很简单创建一个类继承 Application并在manifest的application标签中进行注册(只需要给Application标签增加个name属性把自己的 Application的名字定入即可)。
 *
 * android系统会为每个程序运行时创建一个Application类的对象且仅创建一个，所以Application可以说是单例 (singleton)模式的一个类.
 * 且application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。因为它是全局 的单例的，
 * 所以在不同的Activity,Service中获得的对象都是同一个对象。所以通过Application来进行一些，数据传递，数据共享 等,数据缓存等操作。
 *
 * 在启动Application时，系统会创建一个PID（进程ID），所属该应用的Activity就会在此进程上运行。如果我们在Application
 * 创建的时候初始化全局变量，这样同一个应用的所有Activity都可以取到这些全局变量的值。
 */
public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        //程序创建的时候执行
        super.onCreate();
        baseApplication = this;
    }

    public static Context getAppContext() {
        return baseApplication;
    }
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行,onLowMemory()低内存的时候执行,onTrimMemory(int level)程序在内存清理的时候执行
        Log.e("onTerminate======", "onTerminate");
        super.onTerminate();
    }

    /**
     * 分包（自动）
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

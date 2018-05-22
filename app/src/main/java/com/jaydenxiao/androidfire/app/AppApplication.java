package com.jaydenxiao.androidfire.app;

import com.jaydenxiao.androidfire.BuildConfig;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.LogUtils;

/**
 * APPLICATION
 */
public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化logger,注意拷贝的话BuildConfig.LOG_DEBUG一定要是在当前module下的包名，配置文件中判断测适和发行版本
        LogUtils.logInit(BuildConfig.LOG_DEBUG);
    }
}

package com.jaydenxiao.common.base;

/**
 * des:baseview
 * Created by xsf
 * on 2016.07.11:53
 */
public interface BaseView {
    /*******内嵌加载*******/
    //数据请求时的
    void showLoading(String title);
    //数据加载完成的隐藏加载框
    void stopLoading();
    //显示错误信息
    void showErrorTip(String msg);
}

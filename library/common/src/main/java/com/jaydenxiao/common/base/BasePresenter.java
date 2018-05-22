package com.jaydenxiao.common.base;

import android.content.Context;

import com.jaydenxiao.common.baserx.RxManager;

/**
 * des:基类presenter
 * Created by xsf
 * on 2016.07.11:55
 */
public abstract class BasePresenter<T,E>{
    public Context mContext;
    public E mModel;
    public T mView;
    public RxManager mRxManage = new RxManager();

    public void setVM(T v, E m) {
        this.mView = v;
        this.mModel = m;
        this.onStart();
    }
    public void onStart(){//事件监听处理 mRxManager.on（MVP模式下）
    }
    //释放 presenter 占用的资源，防止内容泄露
    public void onDestroy() {
        mRxManage.clear();
    }
}

package com.jaydenxiao.androidfire.ui.zone.contract;

import com.jaydenxiao.androidfire.bean.Result;
import com.jaydenxiao.androidfire.ui.zone.bean.CircleItem;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import rx.Observable;

/**
 * Created by asus-pc on 2017/11/1.
 */

public interface PublishContract {
    interface Model extends BaseModel {
        Observable <Result> getPublishAddress();
    }

    interface View extends BaseView {
        void returnPublishAddress(List<CircleItem> circleItem);
    }

    abstract static class Presenter extends BasePresenter<View, Model> {
        //获取发布地址
        public abstract void getPublishAddressRequest();

    }
}

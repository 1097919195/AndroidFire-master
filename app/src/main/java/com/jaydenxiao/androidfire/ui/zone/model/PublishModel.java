package com.jaydenxiao.androidfire.ui.zone.model;

import com.jaydenxiao.androidfire.bean.Result;
import com.jaydenxiao.androidfire.ui.zone.DatasUtil;
import com.jaydenxiao.androidfire.ui.zone.contract.PublishContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by asus-pc on 2017/11/1.
 */

public class PublishModel implements PublishContract.Model {
    @Override
    public Observable<Result> getPublishAddress() {
        //创建一个上游 Observable(发送事件)
        return Observable.create(new Observable.OnSubscribe<Result>() {
            @Override
            public void call(Subscriber<? super Result> subscriber) {
                Result result = DatasUtil.getPublishAddress();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<Result>io_main());
    }
}

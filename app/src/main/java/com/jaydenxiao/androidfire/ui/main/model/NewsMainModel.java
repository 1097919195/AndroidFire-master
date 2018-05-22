package com.jaydenxiao.androidfire.ui.main.model;

import com.jaydenxiao.androidfire.app.AppApplication;
import com.jaydenxiao.androidfire.app.AppConstant;
import com.jaydenxiao.androidfire.bean.NewsChannelTable;
import com.jaydenxiao.androidfire.db.NewsChannelTableManager;
import com.jaydenxiao.androidfire.ui.main.contract.NewsMainContract;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.ACache;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * des:
 * Created by xsf
 * on 2016.09.17:05
 */
public class NewsMainModel implements NewsMainContract.Model {
    @Override
    public Observable<List<NewsChannelTable>> lodeMineNewsChannels() {
        //创建一个上游 Observable(发送事件,当调用了presenter中的subscribe方法,Observable.OnSubscribe的call方法就会被执行，而Observable.OnSubscribe 说白了就是一个继承了Action1接口的接口，Observable.OnSubscribe <- Action1 <- Action <- Function,create()方法也就是个工厂方法)
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                ArrayList<NewsChannelTable> newsChannelTableList = (ArrayList<NewsChannelTable>) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.CHANNEL_MINE);
                if(newsChannelTableList==null){
                   newsChannelTableList= (ArrayList<NewsChannelTable>) NewsChannelTableManager.loadNewsChannelsStatic();
                   ACache.get(AppApplication.getAppContext()).put(AppConstant.CHANNEL_MINE,newsChannelTableList);
                }
                subscriber.onNext(newsChannelTableList);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<NewsChannelTable>>io_main());
    }
}

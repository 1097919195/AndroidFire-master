package com.jaydenxiao.androidfire.ui.news.presenter;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.app.AppConstant;
import com.jaydenxiao.androidfire.bean.NewsSummary;
import com.jaydenxiao.androidfire.ui.news.contract.NewsListContract;
import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.commonutils.LogUtils;

import java.util.List;

import rx.functions.Action1;

/**
 * des:
 * Created by xsf
 * on 2016.09.14:53
 */
public class NewsListPresenter extends NewsListContract.Presenter {

    @Override
    public void onStart() {
        super.onStart();
        //监听返回顶部动作（AppConstant.NEWS_LIST_TO_TOP事件处理，到时只需发起事件请求AppConstant.NEWS_LIST_TO_TOP即可）
        mRxManage.on(AppConstant.NEWS_LIST_TO_TOP, new Action1<Object>() {
           @Override
           public void call(Object o) {
               LogUtils.loge("backTop");
               mView.scrolltoTop();
           }
        });
    }

    /**
     * 请求获取列表数据
     * @param type
     * @param id
     * @param startPage
     */
    @Override
    public void getNewsListDataRequest(String type, String id, int startPage) {
        //调用mRxManger.add()方法 将创建的Observable添加进去,subscribe建立连接,创建一个下游 Observer接收事件(除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。 Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的。)
        //当调用了subscribe方法 XXmodel中Observable.OnSubscribe的call方法就会被执行
         mRxManage.add(mModel.getNewsListData(type,id,startPage).subscribe(new RxSubscriber<List<NewsSummary>>(mContext,false) {
             @Override
             public void onStart() {
                 super.onStart();
                 mView.showLoading(mContext.getString(R.string.loading));
             }

             @Override
             protected void _onNext(List<NewsSummary> newsSummaries) {
                 mView.returnNewsListData(newsSummaries);
                 mView.stopLoading();
             }

             @Override
             protected void _onError(String message) {
                 mView.showErrorTip(message);
             }
         }));
    }
}

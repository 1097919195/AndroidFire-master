package com.jaydenxiao.androidfire.ui.zone.presenter;

import com.jaydenxiao.androidfire.bean.Result;
import com.jaydenxiao.androidfire.ui.zone.bean.CircleItem;
import com.jaydenxiao.androidfire.ui.zone.contract.PublishContract;
import com.jaydenxiao.common.commonutils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by asus-pc on 2017/11/1.
 */

public class PublishPresenter extends PublishContract.Presenter {
    @Override
    public void getPublishAddressRequest() {
        mRxManage.add(mModel.getPublishAddress().subscribe(new Subscriber<Result>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
//                LogUtils.loge("error====="+e);
            }

            @Override
            public void onNext(Result result) {
//                LogUtils.loge("result====="+result);
                if (result != null) {
                    try {
                        List<CircleItem> circleItemList = new ArrayList<CircleItem>();
                        CircleItem circleItems = new CircleItem();
                        circleItems.setAddress(result.getAddress());
                        circleItemList.add(circleItems);
//                        LogUtils.loge("result1====="+circleItemList.get(0).getAddress());

                        mView.returnPublishAddress(circleItemList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }));
    }
}

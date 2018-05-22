package com.jaydenxiao.androidfire.ui.news.contract;

import com.jaydenxiao.androidfire.bean.NewsChannelTable;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * des:
 * Created by xsf
 * on 2016.09.11:53
 */
public interface NewsChannelContract {

    //请求获取的数据
    interface Model extends BaseModel {
        Observable<List<NewsChannelTable>> lodeMineNewsChannels();

        Observable<List<NewsChannelTable>> lodeMoreNewsChannels();

        Observable<String> swapDb(ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, int toPosition);

        Observable<String> updateDb(ArrayList<NewsChannelTable> mineChannelTableList, ArrayList<NewsChannelTable> moreChannelTableList);
    }

    //返回获取的数据
    interface View extends BaseView {
        void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine);

        void returnMoreNewsChannels(List<NewsChannelTable> newsChannelsMore);
    }

    //发起数据请求
    abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void lodeChannelsRequest();

        public abstract void onItemSwap(ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, final int toPosition);

        public abstract void onItemAddOrRemove(ArrayList<NewsChannelTable> mineChannelTableList, ArrayList<NewsChannelTable> moreChannelTableList);
    }
}

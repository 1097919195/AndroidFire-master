package com.jaydenxiao.androidfire.ui.news.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.app.AppConstant;
import com.jaydenxiao.androidfire.bean.NewsChannelTable;
import com.jaydenxiao.androidfire.ui.news.adapter.ChannelAdapter;
import com.jaydenxiao.androidfire.ui.news.contract.NewsChannelContract;
import com.jaydenxiao.androidfire.ui.news.event.ChannelItemMoveEvent;
import com.jaydenxiao.androidfire.ui.news.model.NewsChannelModel;
import com.jaydenxiao.androidfire.ui.news.presenter.NewsChanelPresenter;
import com.jaydenxiao.androidfire.widget.ItemDragHelperCallback;
import com.jaydenxiao.common.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * des:选择要关注的频道
 * Created by xsf
 * on 2016.09.11:51
 */
public class NewsChannelActivity extends BaseActivity<NewsChanelPresenter, NewsChannelModel>implements NewsChannelContract.View{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.news_channel_mine_rv)
    RecyclerView newsChannelMineRv;
    @Bind(R.id.news_channel_more_rv)
    RecyclerView newsChannelMoreRv;

    private ChannelAdapter channelAdapterMine;
    private ChannelAdapter channelAdapterMore;

    @Override
    public int getLayoutId() {
        return R.layout.act_news_channel;
    }

    /**
     * 入口
     * @param context
     */
    public static void startAction(Context context){
        Intent intent = new Intent(context, NewsChannelActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //监听处理新闻频道拖拽事件处理
        mRxManager.on(AppConstant.CHANNEL_SWAP, new Action1<ChannelItemMoveEvent>() {
            @Override
            public void call(ChannelItemMoveEvent channelItemMoveEvent) {
                if (channelItemMoveEvent!=null) {
                    mPresenter.onItemSwap((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(),channelItemMoveEvent.getFromPosition(),channelItemMoveEvent.getToPosition());
                }
            }
        });
    }

    @Override
    public void initPresenter() {
            mPresenter.setVM(this, mModel);
    }

    @Override
    public void initView() {
        //返回
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
        //发起加载频道请求
        mPresenter.lodeChannelsRequest();
    }

    //显示新闻频道
    @Override
    public void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine) {
        channelAdapterMine = new ChannelAdapter(mContext,R.layout.item_news_channel);
        //频道列表显示的方式
        newsChannelMineRv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        //添加动画
        newsChannelMineRv.setItemAnimator(new DefaultItemAnimator());
        //获得数据之前
        newsChannelMineRv.setAdapter(channelAdapterMine);
        //获得数据之后addAll或replaceAll，然后notifyDataSetChanged进行界面刷新
        channelAdapterMine.replaceAll(newsChannelsMine);
        //添加或删除所点击的频道
        channelAdapterMine.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsChannelTable newsChannel = channelAdapterMine.get(position);
                channelAdapterMore.add(newsChannel);
                channelAdapterMine.removeAt(position);
                mPresenter.onItemAddOrRemove((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(), (ArrayList<NewsChannelTable>)channelAdapterMore.getAll());

            }
        });
        //拖拽频道进行交换位置
        ItemDragHelperCallback itemDragHelperCallback = new ItemDragHelperCallback(channelAdapterMine);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragHelperCallback);
        itemTouchHelper.attachToRecyclerView(newsChannelMineRv);
        channelAdapterMine.setItemDragHelperCallback(itemDragHelperCallback);
    }
    @Override
    public void returnMoreNewsChannels(List<NewsChannelTable> newsChannelsMore) {
        channelAdapterMore = new ChannelAdapter(mContext,R.layout.item_news_channel);
        //频道列表显示的方式
        newsChannelMoreRv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannelMoreRv.setItemAnimator(new DefaultItemAnimator());
        newsChannelMoreRv.setAdapter(channelAdapterMore);
        channelAdapterMore.replaceAll(newsChannelsMore);
        channelAdapterMore.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsChannelTable newsChannel = channelAdapterMore.get(position);
                channelAdapterMine.add(newsChannel);
                channelAdapterMore.removeAt(position);
                mPresenter.onItemAddOrRemove((ArrayList<NewsChannelTable>) channelAdapterMine.getAll(), (ArrayList<NewsChannelTable>)channelAdapterMore.getAll());
            }
        });
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }
}

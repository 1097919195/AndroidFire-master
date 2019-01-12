package com.jaydenxiao.androidfire.ui.main.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.app.AppApplication;
import com.jaydenxiao.androidfire.app.AppConstant;
import com.jaydenxiao.androidfire.bean.TabEntity;
import com.jaydenxiao.androidfire.ui.main.fragment.CareMainFragment;
import com.jaydenxiao.androidfire.ui.main.fragment.NewsMainFragment;
import com.jaydenxiao.androidfire.ui.main.fragment.PhotosMainFragment;
import com.jaydenxiao.androidfire.ui.main.fragment.VideoMainFragment;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baseapp.AppConfig;
import com.jaydenxiao.common.baserx.RxBus;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.PermissionUtils;
import com.jaydenxiao.common.daynightmodeutils.ChangeModeController;

import java.util.ArrayList;

import butterknife.Bind;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import rx.functions.Action1;

import static com.jaydenxiao.common.commonutils.LogUtils.loge;

/**
 * des:主界面
 * Created by xsf
 * on 2016.09.15:32
 */
public class MainActivity extends BaseActivity {
    @Bind(R.id.tab_layout)
    CommonTabLayout tabLayout;

    private String[] mTitles = {"首页", "美女","视频","关注"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal,R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected,R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_care_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private NewsMainFragment newsMainFragment;
    private PhotosMainFragment photosMainFragment;
    private VideoMainFragment videoMainFragment;
    private CareMainFragment careMainFragment;
    private static int tabLayoutHeight;

    SharedPreferences sp = AppApplication.getAppContext().getSharedPreferences("share", MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    boolean isFirstRun = sp.getBoolean("isFirstRun", true);

    int count=0;
    boolean change,a;

    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA};
    String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static int permissionCode = 1;


    public static void startAction(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        //淡入淡出动画。overridePendingTransition一个参数是MainActivity进入时的动画，另外一个参数是SplashActivity退出时的动画
        activity.overridePendingTransition(R.anim.fade_in,
                com.jaydenxiao.common.R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initPresenter() {

    }
    @Override
    public void initView() {
        //程序第一次运行判断
        if (isFirstRun){
            //请求多个权限
            PermissionUtils.checkAndRequestMorePermissions(this,permissions,permissionCode);

            Log.d("debug", "第一次运行");
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }

        //此处填上在http://fir.im/注册账号后获得的API_TOKEN以及APP的应用ID
        UpdateKey.API_TOKEN = AppConfig.API_FIRE_TOKEN;
        UpdateKey.APP_ID = AppConfig.APP_FIRE_ID;
        //如果你想通过Dialog来进行下载，可以如下设置
//        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);//Android的自动更新库
        //初始化菜单
        initTab();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //切换daynight模式要立即变色的页面
        ChangeModeController.getInstance().init(this,R.attr.class);
        super.onCreate(savedInstanceState);
        //初始化frament
        initFragment(savedInstanceState);
        tabLayout.measure(0,0);
        tabLayoutHeight=tabLayout.getMeasuredHeight();

        //处理菜单显示或隐藏(需根据FloatingActionButton的显示与隐藏)
        mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });

    }

    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
    }
    /**
     * 初始化碎片
     */
    private void initFragment(Bundle savedInstanceState) {
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            //findFragmentByTag方法 当UI中没有fragment时查找
            newsMainFragment = (NewsMainFragment) getSupportFragmentManager().findFragmentByTag("newsMainFragment");
            photosMainFragment = (PhotosMainFragment) getSupportFragmentManager().findFragmentByTag("photosMainFragment");
            videoMainFragment = (VideoMainFragment) getSupportFragmentManager().findFragmentByTag("videoMainFragment");
            careMainFragment = (CareMainFragment) getSupportFragmentManager().findFragmentByTag("careMainFragment");
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            newsMainFragment = new NewsMainFragment();
            photosMainFragment = new PhotosMainFragment();
            videoMainFragment = new VideoMainFragment();
            careMainFragment = new CareMainFragment();

            transaction.add(R.id.fl_body, newsMainFragment, "newsMainFragment");
            transaction.add(R.id.fl_body, photosMainFragment, "photosMainFragment");
            transaction.add(R.id.fl_body, videoMainFragment, "videoMainFragment");
            transaction.add(R.id.fl_body, careMainFragment, "careMainFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);//设置当前tabLayout指向哪个fragment
    }
    /**
     * 在onSaveInstanceState()方法之后去调用commit()，就会抛出我们遇到的这个异常，这是因为在
     * onSaveInstanceState()之后调用commit()方法，这些变化就不会被activity存储，即这些状态会被丢失,
     * 但我们可以去用commitAllowingStateLoss()这个方法去代替commit()来解决这个为题,但是并不是可以随便哪里都用的，可能会造成数据丢失（参考阿里android开发手册）
     */

    //切换
    private void SwitchTo(int position) {
        RxBus.getInstance().post(AppConstant.MENU_SHOW_HIDE,true);//发送显示菜单栏事件 为了切换页面后显示菜单栏(防止recycleview滑动时立马切换界面导致菜单栏消失)

//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(photosMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(newsMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //美女
            case 1:
                transaction.hide(newsMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(photosMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //视频
            case 2:
                transaction.hide(newsMainFragment);
                transaction.hide(photosMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(videoMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //关注
            case 3:
                transaction.hide(newsMainFragment);
                transaction.hide(photosMainFragment);
                transaction.hide(videoMainFragment);
                transaction.show(careMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();//getLayoutParams()方法 和 setLayoutParams()方法 重新设置控件布局，一般是宽和高
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;

        if(!showOrHide){
            //隐藏
             valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);//从某个值变化到某个值
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
            RxBus.getInstance().post(AppConstant.FAB_SHOW_HIDE,false);
        }else{
            //显示
             valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
            RxBus.getInstance().post(AppConstant.FAB_SHOW_HIDE,true);
        }
        //动画更新的监听
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            // 启动动画之后, 会不断回调此方法来获取最新的值
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();// 获取最新的高度值
                tabLayout.setLayoutParams(layoutParams);// 重新修改布局高度
            }
        });
//        //AnimatorSet混合动画类，可以并联或者串联地播放动画。一般的AnimatorSet会和ObjectAnimator一起使用用于切实的改变视图的属性(普通的Animation不会改变视图的属性，动画播放完毕后视图又恢复原来的属性)
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setDuration(500);
//        animatorSet.playTogether(valueAnimator, alpha);
//        animatorSet.start();

        //判断 菜单栏前后二次是否都是 显示或者隐藏操作，前后相同操作时不进行动画
        count+=1;

        if (count==1) {
            change = showOrHide;
            a=change;
            LogUtils.loge("112233qian========"+count);
        } else if (count==2) {
            change = showOrHide;
            LogUtils.loge("112233hou========"+count);
        }

        if (count == 1 && !a) {//前后不同
            //AnimatorSet混合动画类，可以并联或者串联地播放动画。一般的AnimatorSet会和ObjectAnimator一起使用用于切实的改变视图的属性(普通的Animation不会改变视图的属性，动画播放完毕后视图又恢复原来的属性)
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(500);
            animatorSet.playTogether(valueAnimator, alpha);
            animatorSet.start();
        } else if (count == 1 && a) {//前后相同
            return;
        } else if (count == 2 && change == a) {
            count = 1;
            a = change;
            return;
        } else if (count == 2 && change != a) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(500);
            animatorSet.playTogether(valueAnimator, alpha);
            animatorSet.start();

            count = 1;
            a = change;
        }

    }

    /**
     * 监听全屏视频时返回键（onBackPressed 对于Activity 可以单独获取Back键的按下事件）
     */
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);//可将activity 退到后台，注意不是finish()退出。参数为false——代表只有当前activity是task根，指应用启动的第一个activity时，才有效;参数为true——则忽略这个限制，任何activity都可以有效。
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    //onresume onstop用于检查APP更新
    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChangeModeController.onDestory();
    }
}

package com.jaydenxiao.androidfire.ui.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.app.AppApplication;
import com.jaydenxiao.androidfire.ui.news.activity.AboutActivity;
import com.jaydenxiao.androidfire.ui.zone.activity.CircleZoneActivity;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.commonutils.ACache;
import com.jaydenxiao.common.commonutils.CollectionUtils;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonwidget.WaveView;
import com.jaydenxiao.common.daynightmodeutils.ChangeModeController;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * des:关注主页
 * Created by xsf
 * on 2016.09.17:07
 */
public class CareMainFragment extends BaseFragment {
    @Bind(R.id.ll_friend_zone)
    LinearLayout llFriendZone;
    @Bind(R.id.wave_view)
    WaveView waveView;
    @Bind(R.id.img_logo)
    ImageView imgLogo;

    private int IMAGE_CROP_CODE=120;
    private static final String STORE_PHOTO = "touxiang";

    @Override
    protected int getLayoutResource() {
        return R.layout.fra_care_main;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        //根据有无缓存 加载显示头像(如果是服务器则需要从服务器获取)
        if (ACache.get(AppApplication.getAppContext()).getAsString(STORE_PHOTO) == null) {
            ImageLoaderUtils.displayRound(getContext(), imgLogo, R.drawable.bgkobe);
        } else {
            ImageLoaderUtils.displayRound(getContext(),imgLogo,ACache.get(AppApplication.getAppContext()).getAsString(STORE_PHOTO));
        }

        //设置头像跟着波浪背景浮动
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.CENTER;
        waveView.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0,0,0,(int)y+2);
                imgLogo.setLayoutParams(lp);
                changeLogo();
            }
        });
    }

    //更换头像logo
    private void changeLogo() {
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choosePhoto();
            }
        });
    }

    /**
     * 开启图片选择器
     */
    private void choosePhoto() {
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // 是否裁剪
                .needCrop(true)
                // 裁剪尺寸
                .cropSize(1,1,220,220)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .titleBgColor(ContextCompat.getColor(getContext(),R.color.main_color))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(getContext(),R.color.main_color))
                // 返回图标ResId
                .backResId(R.drawable.ic_arrow_back)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(true)
                .build();
        ImgSelActivity.startActivity(this, config, IMAGE_CROP_CODE);
    }
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context,imageView,path);
        }
    };

    //选择图片裁剪后设置图片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            if (!CollectionUtils.isNullOrEmpty(pathList)) {
                LogUtils.loge("集合数量+++++"+ pathList.size());
                //将截取到图片以圆形的方式显示到imglogo
                ImageLoaderUtils.displayRound(getContext(), imgLogo, pathList.get(pathList.size()-1));
            }
            //将截取的头像logo放到缓存中(如果是服务器则需上传到服务器)
            ACache.get(AppApplication.getAppContext()).put(STORE_PHOTO,pathList.get(pathList.size()-1));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void chooseDialog() {
//        new AlertDialog.Builder(getContext())//
//                .setTitle("选择头像")//
//
//                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//
//                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                    }
//                }).show();
//    }

    @OnClick(R.id.ll_friend_zone)
    public void friendZone(){
        CircleZoneActivity.startAction(getContext());
    }
    @OnClick(R.id.ll_daynight_toggle)
    public void dayNightToggle(){
        ChangeModeController.toggleThemeSetting(getActivity());
    }
    @OnClick(R.id.ll_daynight_about)
    public void about(){
        AboutActivity.startAction(getContext());
    }
}

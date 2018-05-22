package com.jaydenxiao.androidfire.ui.main.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.common.base.BaseActivity;

import butterknife.Bind;

/**
 * des:启动页
 * Created by xsf
 * on 2016.09.15:16
 */
public class SplashActivity extends BaseActivity {
    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.tv_name)
    TextView tvName;

    @Override
    public int getLayoutId() {
        return R.layout.act_splash;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //沉浸状态栏
        SetTranslanteBar();
        //PropertyValuesHolder这个类可以先将动画属性和值暂时的存储起来，后一起执行，在有些时候可以使用替换掉AnimatorSet，减少代码量
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.3f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.3f, 1f);

        //第一个参数为 view对象，后面的参数为 动画改变的类型
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(tvName, alpha, scaleX, scaleY);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(ivLogo, alpha, scaleX, scaleY);

        //AnimatorSet混合动画类，可以并联或者串联地播放动画。一般的AnimatorSet会和ObjectAnimator一起使用用于切实的改变视图的属性(普通的Animation不会改变视图的属性，动画播放完毕后视图又恢复原来的属性)。
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);//二个动画同时执行
        animatorSet.setInterpolator(new AccelerateInterpolator());//AccelerateInterpolator：动画从开始到结束，变化率是一个加速的过程
        animatorSet.setDuration(2000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.startAction(SplashActivity.this);//启动界面结束后界面跳转
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

}

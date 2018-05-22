/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jaydenxiao.androidfire.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * 通过ViewDragHelper自定义ViewGroup
 * 1.创建实例
 * 2.触摸相关的方法的调用
 * 3.ViewDragHelper.Callback实例的编写
 */
public class PullBackLayout extends FrameLayout {

    private final ViewDragHelper dragger; // http://blog.csdn.net/lmj623565791/article/details/46858663

    private final int minimumFlingVelocity;

    @Nullable
    private Callback callback;

    public PullBackLayout(Context context) {
        this(context, null);
    }

    public PullBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragger = ViewDragHelper.create(this, 1f / 8f, new ViewDragCallback()); // 1f / 8f是敏感度参数参数越大越敏感
        minimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            //决定我们是否应该拦截当前的事件(事件拦截成功后，紧接着就会对事件进行处理，)
            return dragger.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        try {
            //处理事件
            dragger.processTouchEvent(event);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (dragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //声明一个回调接口
    public interface Callback {

        //根据不同操作回调对应的方法
        void onPullStart();

        void onPull(float progress);

        void onPullCancel();

        void onPullComplete();

    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.max(0, top);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getHeight();
        }

        //当captureview被捕获时回调
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            if (callback != null) {
                callback.onPullStart();
            }
        }

        //当captureview的位置发生改变时回调
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (callback != null) {
                callback.onPull((float) top / (float) getHeight());
            }
        }

        //手指释放的时候回调
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int slop = yvel > minimumFlingVelocity ? getHeight() / 6 : getHeight() / 3;
            if (releasedChild.getTop() > slop) {
                if (callback != null) {
                    callback.onPullComplete();
                }
            } else {
                if (callback != null) {
                    callback.onPullCancel();
                }

                dragger.settleCapturedViewAt(0, 0);
                invalidate();//刷新View
            }
        }

    }

}

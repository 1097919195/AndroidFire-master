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

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 *拖拽交换位置
 */
public class ItemDragHelperCallback extends ItemTouchHelper.Callback {
    private OnItemMoveListener mOnItemMoveListener;
    private boolean mIsLongPressEnabled;

    public void setLongPressEnabled(boolean longPressEnabled) {
        mIsLongPressEnabled = longPressEnabled;
    }

    public interface OnItemMoveListener {
        //数据交换
        boolean onItemMove(int fromPosition, int toPosition);
    }

    public ItemDragHelperCallback(OnItemMoveListener onItemMoveListener) {
        mOnItemMoveListener = onItemMoveListener;
    }

    //是否长按启用拖拽功能,默认是false
    @Override
    public boolean isLongPressDragEnabled() {
        return mIsLongPressEnabled;
    }

    //决定拖拽/滑动的方向
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = setDragFlags(recyclerView);
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    private int setDragFlags(RecyclerView recyclerView) {
        int dragFlags;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return dragFlags;
    }

    //和位置交换有关,可用于实现drag功能
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return !isDifferentItemViewType(viewHolder, target) &&
                mOnItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

    }

    private boolean isDifferentItemViewType(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return viewHolder.getItemViewType() != target.getItemViewType();
    }

    //和移除View的状态有关,通常用于清除在onSelectedChanged,onChildDraw中对View设置的动画
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

    }

    //和滑动有关,可用于实现swipe功能
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}

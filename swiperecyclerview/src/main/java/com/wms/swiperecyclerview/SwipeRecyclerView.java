package com.wms.swiperecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by 王梦思 on 2017/3/9.
 * 处理一些事件冲突
 */

public class SwipeRecyclerView extends RecyclerView {

    /**
     * 手指按下的x
     */
    private float startX;
    /**
     * 手指按下的y
     */
    private float startY;
    /**
     * 手指触碰的那个view的frame
     */
    private Rect touchFrame;
    /**
     * 事件是否被子view消费
     */
    private boolean isChildHandle;
    private int mTouchSlop;
    /**
     * 手指触碰的那个view
     */
    private View targetView;

    public SwipeRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            startX = ev.getX();
            startY = ev.getY();
            isChildHandle = false;

            //获取手指触碰的那个view
            targetView = getViewByXAndY(startX, startY);

            if (hasChildOpened()) {
                //如果当前手指触碰的view不是打开的话，则关闭之前打开的view
                if (targetView != null && targetView instanceof SwipeMenuLayout && ((SwipeMenuLayout) targetView).isOpen()) {
                    // 将事件交给child！
                    isChildHandle = true;
                } else {
                    closeAllMenu();
                    return false;
                }
            }
        }

        //禁用多点触控
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //如果竖直方向移动拦截，否则不拦截
        int action = event.getActionMasked();
        float dx = 0f;
        float dy = 0f;
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                dx = Math.abs(event.getX() - startX);
                dy = Math.abs(event.getY() - startY);

                if (isChildHandle) {
                    return false;
                }
                // 如果X轴位移大于Y轴位移，那么将事件交给child处理。
                if (dx > mTouchSlop && dx > dy) {
                    isChildHandle = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (targetView != null && targetView instanceof SwipeMenuLayout) {
                    SwipeMenuLayout swipeChild = (SwipeMenuLayout) targetView;
                    if (swipeChild.isOpen()) {
                        if (dx < mTouchSlop && dy < mTouchSlop) {
                            swipeChild.close();
                        }

                        Rect rect = swipeChild.getMenuRect();
                        if (!(startX > rect.left &&
                                startX < rect.right &&
                                startY > targetView.getTop() &&
                                startY < targetView.getBottom())) {
                            // return true，拦截Item点击事件, 但是菜单能接收到。
                            return true;
                        }
                    }
                }
                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    /**
     * 关闭所有打开的菜单
     */
    private void closeAllMenu() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof SwipeMenuLayout) {
                if (((SwipeMenuLayout) child).isOpen()) {
                    ((SwipeMenuLayout) child).close();
                }
            }
        }
    }

    /**
     * 判断一下子view是否打开了
     */
    private boolean hasChildOpened() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof SwipeMenuLayout) {
                if (((SwipeMenuLayout) child).isOpen()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过x，y获取view
     */
    private View getViewByXAndY(float x, float y) {

        Rect frame = touchFrame;
        if (frame == null) {
            touchFrame = new Rect();
            frame = touchFrame;
        }

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains((int) x, (int) y)) {
                    return child;
                }
            }
        }

        return null;
    }
}

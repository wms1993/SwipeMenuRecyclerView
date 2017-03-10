package com.wms.swiperecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by 王梦思 on 2017/3/9.
 * 菜单item
 */

public class SwipeMenuLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    /**
     * 后面的菜单view
     */
    private View mMenuChild;
    /**
     * 内容view
     */
    private View mContentChild;
    /**
     * 菜单是否打开
     */
    private boolean isOpen;
    /**
     * 菜单的区域
     */
    private Rect mMenuRect = new Rect();

    public SwipeMenuLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return mContentChild == child;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (left < 0) {
                    //向左滑动,left是负数
                    if (Math.abs(left) > mMenuChild.getWidth()) {
                        return -mMenuChild.getWidth();
                    } else {
                        return left;
                    }
                } else {
                    //向右滑动禁止
                    return 0;
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (isOpen) {
                    if (Math.abs(xvel) > mMenuChild.getWidth() ||
                            -mContentChild.getLeft() < mMenuChild.getMeasuredWidth() / 2) {
                        close();
                    } else {
                        open();
                    }
                } else {
                    if (Math.abs(xvel) > mMenuChild.getWidth() ||
                            -mContentChild.getLeft() > mMenuChild.getMeasuredWidth() / 2) {
                        open();
                    } else {
                        close();
                    }
                }
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return 1;
            }
        });
    }

    /**
     * 判断是否是打开的状态
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 打开菜单
     */
    public void open() {
        isOpen = true;
        mDragHelper.smoothSlideViewTo(mContentChild, -mMenuChild.getMeasuredWidth(), 0);
        invalidate();
    }

    /**
     * 关闭菜单
     */
    public void close() {
        isOpen = false;
        mDragHelper.smoothSlideViewTo(mContentChild, 0, 0);
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SwipeMenuLayout can only have two child");
        }

        mMenuChild = getChildAt(0);
        mContentChild = getChildAt(1);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 获取菜单的区域
     */
    public Rect getMenuRect() {
        mMenuChild.getHitRect(mMenuRect);
        return mMenuRect;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        //重要
        mContentChild.setOnClickListener(l);
    }
}

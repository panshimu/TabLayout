package com.miaozij.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabLayout extends ViewGroup {

    private List<List<View>> mChildViews = new ArrayList<>();
    //存放每一行 距离左边最大的距离 用于设置 Gravity
    private List<Integer> mMarginLeftList = new ArrayList<>();
    //布局居中还是居左
    private GRAVITY mGravity = GRAVITY.LEFT;
    public enum GRAVITY{
        LEFT,CENTER
    }

    private TabLayoutBaseAdapter mTabLayoutBaseAdapter;

    public TabLayout(Context context) {
        this(context,null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
        boolean isCenter = typedArray.getBoolean(R.styleable.TabLayout_centerItem, false);
        Log.d("TAG","isCenter="+isCenter);
        if(isCenter){
            mGravity = GRAVITY.CENTER;
        }else {
            mGravity = GRAVITY.LEFT;
        }
        typedArray.recycle();
    }

    /**
     * 会执行两次
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //所以要清空
        mChildViews.clear();
        mMarginLeftList.clear();

        //循环获取 子 view
        int childCount = getChildCount();
        //需要宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);

        //需要计算高度
        int height = 0;

        //一行的宽度
        int lineWidth = 0;

        List<View> childViews = new ArrayList<>();

        mChildViews.add(childViews);

        Log.d("TAG","子view数量==" + childCount);

        Log.d("TAG","  paddingLeft==" + getPaddingLeft()+"  paddingRight==" + getPaddingRight()+"  paddingBottom==" + getPaddingBottom()+"   paddingTop==" + getPaddingTop());

        int canUseViewWidth = width - getPaddingLeft() - getPaddingRight();

        //一行中的最大值
        int maxContentChildViewHeight = 0;

        Log.d("TAG","canUseViewWidth="+canUseViewWidth);

        for( int i = 0 ; i < childCount ; i++ ){

            View childView = getChildAt(i);
            // 这段话会去调用 子view的onMeasure()方法 就可以测量宽高了
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);

            Log.d("TAG","childView的宽高 width="+childView.getMeasuredWidth() + " height="+childView.getMeasuredHeight() );

            //如果设置有margin 怎么办呢
            // ViewGroup.LayoutParams 没有 就用系统的 MarginLayoutParams
            //另外 LinearLayout 有自己的LayoutParams  会复写一个重要的方法 generateLayoutParams()
            // 一定要重写这个方法 否则不能使用 MarginLayoutParams
            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();

            //根据子view计算和指定自己的宽高
            // 什么时候换行呢？  //一行不够 需要换行 累加高度 需要考虑 子view的padding
            //contentChildViewWidth 子 view 占用的宽度 自身的宽度加上 margin

            //子view 所占用 宽度
            int contentChildViewWidth = childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;

            //子view 所占用的 高度
            int contentChildViewHeight = childView.getMeasuredHeight() + params.bottomMargin + params.topMargin;

            Log.d("TAG","contentChildViewWidth="+contentChildViewWidth+" contentChildViewHeight="+contentChildViewHeight);

            if( lineWidth + contentChildViewWidth > canUseViewWidth ){

                if(mGravity == GRAVITY.CENTER) {
                    mMarginLeftList.add((canUseViewWidth - lineWidth) / 2);
                }else {
                    mMarginLeftList.clear();
                }

                //把上一行的最大高度 叠加到 控件高度上
                height += maxContentChildViewHeight;

                lineWidth = contentChildViewWidth;

                //判断是否是最后一个 如果是 则把当前view 的高度叠加到 总高度上
                if(i == childCount-1){
                    height += contentChildViewHeight;
                    if(mGravity == GRAVITY.CENTER) {
                        mMarginLeftList.add((canUseViewWidth - contentChildViewWidth)/2);
                    }
                }else {//如果不是最后一个
                    //把当前 子view 的高度赋值给最大高度
                    maxContentChildViewHeight = contentChildViewHeight;
                }

                childViews = new ArrayList<>();

                childViews.add(childView);

                mChildViews.add(childViews);

            }else {

                childViews.add(childView);

                lineWidth += contentChildViewWidth;

                //同一行的时候 取最大的值
                maxContentChildViewHeight = Math.max(maxContentChildViewHeight,contentChildViewHeight);

                //如果是最后一个 把高度叠加到总高度上
                if(i == childCount-1){
                    height += maxContentChildViewHeight;
                    if(mGravity == GRAVITY.CENTER) {
                        mMarginLeftList.add((canUseViewWidth - lineWidth) / 2);
                    }else {
                        mMarginLeftList.clear();
                    }
                }
            }


        }

        height += getPaddingBottom() + getPaddingTop();

        Log.d("TAG","width="+width+" height="+height);

        setMeasuredDimension(width,height);

    }

    /**
     * 设置布局是否居中显示
     * @param gravity
     */
    public void setGravity(GRAVITY gravity){
        this.mGravity = gravity;
        invalidate();
    }
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    /**
     * 摆放子view
     * @param b
     * @param i
     * @param i1
     * @param i2
     * @param i3
     */
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        int left,top = getPaddingTop(),bottom,right;

        Log.d("TAG","mGravity="+mGravity+"  mChildViews"+mChildViews.size()+"  mMarginLeftList"+mMarginLeftList.size());
        for( int j = 0 ; j < mChildViews.size() ; j ++ ){
            
            if(mGravity == GRAVITY.CENTER && mMarginLeftList.size() == mChildViews.size()) {
                left = getPaddingLeft() + mMarginLeftList.get(j);
            }else {
                left = getPaddingLeft();
            }
           
            //自view所占用的高度
            int contentViewHeight;
            int maxHeight = 0;
            for(View childView:mChildViews.get(j)){
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                left += params.leftMargin;
                int childTop = top +  params.topMargin;

                contentViewHeight = params.topMargin + childView.getMeasuredHeight()+ params.bottomMargin;

                maxHeight = Math.max(contentViewHeight,maxHeight);

                right = left  + childView.getMeasuredWidth();
                bottom = childTop + childView.getMeasuredHeight();
                //摆放
                childView.layout(left,childTop,right,bottom);
                //left叠加
                left += childView.getMeasuredWidth() + params.rightMargin;
            }
            top += maxHeight ;
        }
    }
    public void setTabLayoutBaseAdapter(TabLayoutBaseAdapter adapter){
        if(adapter == null){
            throw new NullPointerException("adapter不能为空");
        }
        //清空所有子view
        removeAllViews();

        mTabLayoutBaseAdapter = null;
        mTabLayoutBaseAdapter = adapter;

        int itemCount = mTabLayoutBaseAdapter.getCount();
        for(int i=0; i < itemCount;i++){
            //通过位置获取view
            View childView = mTabLayoutBaseAdapter.getView(i,this);
            addView(childView);
        }
    }

}

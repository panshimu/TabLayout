package com.miaozij.tablayout;

import android.view.View;
import android.view.ViewGroup;

public abstract class TabLayoutBaseAdapter {
    // 有多少条目
    public abstract int getCount();
    //getView 通过position
    public abstract View getView(int position, ViewGroup parent);
    //观察者模式 及时通知更新
//    public abstract
}

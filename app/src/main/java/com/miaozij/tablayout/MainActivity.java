package com.miaozij.tablayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private List<String> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = findViewById(R.id.tabLayout);
        mItems = new ArrayList<>();
        for(int i=0;i<10;i++){
            mItems.add("java == "+ i);
        }

        mTabLayout.setTabLayoutBaseAdapter(new TabLayoutBaseAdapter() {
            @Override
            public int getCount() {
                return mItems.size();
            }

            @Override
            public View getView(int position, ViewGroup parent) {
                //这里的处理就跟正常处理 listView 一样
                TextView textView = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tag, parent,false);
                textView.setText(mItems.get(position));
                return textView;
            }
        });

    }
}

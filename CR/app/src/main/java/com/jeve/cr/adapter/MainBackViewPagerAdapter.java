package com.jeve.cr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeve.cr.R;

/**
 * 主页背景viewpager适配器
 * lijiawei
 * 2017-12-18
 */
public class MainBackViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] title = {"文字识别", "银行卡识别"};

    public MainBackViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_viewpager_layout, container, false);
        TextView tv = view.findViewById(R.id.viewpager_tv);
        tv.setText(title[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

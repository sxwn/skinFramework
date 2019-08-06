package com.xiaowei.skin;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.view.LayoutInflaterCompat;

public class BaseActivity extends Activity {
    private SkinFactory skinFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().setContext(this);
        skinFactory= new SkinFactory();
        //监听xml的生成过程
        LayoutInflaterCompat.setFactory2(getLayoutInflater(),skinFactory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        skinFactory.apply();
    }

    public void apply(){
        skinFactory.apply();
    }
}

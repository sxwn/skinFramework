package com.xiaowei.skin;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class TwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        findViewById(R.id.changeSkin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSkin();
            }
        });
    }

    public void changeSkin(){
        //1、先加载皮肤apk
        SkinManager.getInstance().loadSkinApk(Environment.getExternalStorageDirectory()+"/skin.apk");
        apply();
    }
}

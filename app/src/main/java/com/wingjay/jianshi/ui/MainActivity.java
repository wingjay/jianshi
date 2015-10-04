package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.os.Bundle;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.base.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, DateChooseActivity.class));

    }

}

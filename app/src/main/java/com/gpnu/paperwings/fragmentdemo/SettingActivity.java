package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("失物招领");
    }

    public void zhuxiao(View view){
        //杀死Activity
        finish();
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void logout(View view){
        finish();
    }
}

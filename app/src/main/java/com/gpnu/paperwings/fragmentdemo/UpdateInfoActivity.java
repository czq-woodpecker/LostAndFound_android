package com.gpnu.paperwings.fragmentdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.gpnu.paperwings.fragmentdemo.utils.UploadFileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UpdateInfoActivity extends AppCompatActivity {
    private static  String BASE_URL ="http://10.0.2.2:8080/Demo/user_updateInfo";
    private TextView tv_title;
    private TextView tv_commit;
    private EditText et_content;
    private static String which;
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String title = getIntent().getStringExtra("title");
        username = getIntent().getStringExtra("username");

        tv_title = (TextView) findViewById(R.id.tv_title);
        et_content = (EditText) findViewById(R.id.et_content);

        if("phone".equals(title)){
            tv_title.setText("修改手机号");
            which = "phone";
        }
        else if("email".equals(title)){
            tv_title.setText("修改邮箱");
            which = "email";
        }
        else if("miaoshu".equals(title)){
            tv_title.setText("修改个人简介");
            which = "miaoshu";
        }

    }

    public void cancelUpdate(View view){
        finish();
    }

    public void  commit(View view){
        String content = et_content.getText().toString().trim();
        if ("".equals(content)){
            Toast.makeText(this,"内容不能为空",Toast.LENGTH_LONG).show();
        }
        else {
            final String url = BASE_URL + "?username="+username +"&which=" + which+"&content="+content;

            //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
                    HttpUtil.GetWithHttpClient(url, handler);
                }
            }).start();
        }
    }
    //接收子线程传过来的数据
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            //成功
            if(msg.what == 1){

                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    //解析json数据里的字符串
                    String isSuccess = result.getString("isSuccess");
                    if("true".equals(isSuccess)){
                        Toast.makeText(UpdateInfoActivity.this,"修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(UpdateInfoActivity.this,HomeActivity.class);
//                        intent.putExtra("userJson",result.getString("user"));
                        SharedPreferences sp =getSharedPreferences("userinfo",MODE_WORLD_READABLE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user",result.getString("user"));
                        editor.commit();
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(UpdateInfoActivity.this,"ERROR"+msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

}

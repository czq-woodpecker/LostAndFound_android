package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.gpnu.paperwings.fragmentdemo.utils.UploadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by paperwings on 2017/11/27.
 */

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://10.0.2.2:8080/Demo/user_login";
    private Button loginBtn;
    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //映射控件
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    //注册按钮事件
    public void register(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    //登录按钮事件
    public void login(View view){
        //登录校验
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if("".equals(username)){
            Toast.makeText(this,"用户名不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(password)){
            Toast.makeText(this,"密码不能为空", Toast.LENGTH_SHORT).show();
        }
        else{
            final String url = BASE_URL+"?username="+username+"&password="+password;
            //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
                    HttpUtil.GetWithHttpClient(url,handler);
                }
            }).start();


        }

    }

    //接收子线程传过来的数据
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            //成功
            if(msg.what == 1){
//                Toast.makeText(MainActivity.this,"SUCCESS"+msg.obj, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    //解析json数据里的字符串
                    String isSuccess = result.getString("isSuccess");
                    if("true".equals(isSuccess)){
                        Toast.makeText(MainActivity.this,"登录成功", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(MainActivity.this,HomeActivity.class);
//                       intent.putExtra("userJson",result.getString("user"));
                        SharedPreferences sp =getSharedPreferences("userinfo",MODE_WORLD_READABLE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user",result.getString("user"));
                        editor.commit();

                       startActivity(intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this,"用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(MainActivity.this,"ERROR"+msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };


}

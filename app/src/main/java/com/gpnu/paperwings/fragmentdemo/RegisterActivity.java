package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.gpnu.paperwings.fragmentdemo.utils.JSONUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {
//    private static final String BASE_URL = "http://localhost:8080/Demo/register";
    //模拟器默认把localhost或者127.0.0.1当做本身了,在模拟器上可以用10.0.2.2代替127.0.0.1和localhost
    private static final String BASE_URL = "http://10.0.2.2:8080/Demo/user_register";
    public static final int SHOW_RESPONSE = 0;


    private EditText et_username;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_repassword;


    ////////////////////////////////////
    	/* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    private ImageView iv_usericon = null;
    private ImageView iv_setting;
    ///////////////////////////////////



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //映射控件
        et_username = (EditText) findViewById(R.id.et_username);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_repassword = (EditText) findViewById(R.id.et_repassword);
        iv_usericon = (ImageView) findViewById(R.id.iv_usericon);

    }

    //接收子线程传过来的数据
    private  Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            //成功
            if(msg.what == 1){
//                Toast.makeText(RegisterActivity.this,"SUCCESS"+msg.obj, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    //解析json数据里的字符串
                    String isSuccess = result.getString("isSuccess");
                    if("true".equals(isSuccess)){
                        Toast.makeText(RegisterActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                        intent.putExtra("userJson",result.getString("user"));
                        startActivity(intent);
                    }
                    else {
                        String message = result.getString("message");
                        Toast.makeText(RegisterActivity.this,"注册失败："+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(RegisterActivity.this,"ERROR", Toast.LENGTH_SHORT).show();
            }


        }
    };

    public void  register(View view){
        String username = et_username.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String repassword = et_repassword.getText().toString().trim();

        if("".equals(username)){
            Toast.makeText(this,"用户名不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(phone)){
            Toast.makeText(this,"手机号不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(password)){
            Toast.makeText(this,"密码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(repassword)){
            Toast.makeText(this,"确认密码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if( !password.equals(repassword)){
            Toast.makeText(this,"两次密码不一致", Toast.LENGTH_SHORT).show();
        }
        else {
            final String url = BASE_URL+"?username="+username+"&password="+password+"&phone="+phone;
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

    //点击头像事件
    public void uploadIcon(View view){
        Log.e("RegisterActivity","uploadIcon");
        choseHeadImageFromGallery();

    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(RegisterActivity.this, "取消", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(RegisterActivity.this, "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }

                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Log.e("setImageToHeadView","setImageToHeadView");
            iv_usericon.setImageBitmap(photo);
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

}

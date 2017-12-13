package com.gpnu.paperwings.fragmentdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.DialogUtil;
import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.gpnu.paperwings.fragmentdemo.utils.ImageUtil;
import com.gpnu.paperwings.fragmentdemo.utils.UploadFileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PublishActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://10.0.2.2:8080/Demo/thing_add";
    // 声明PopupWindow
    private PopupWindow popupWindow;
    // 声明PopupWindow对应的视图
    private View popupView;
    // 声明平移动画
    private TranslateAnimation animation;

    private Spinner sn_type;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_miaoshu;
    private EditText et_place;
    private RadioGroup rg_belong;
    private RadioButton rb_xunwu;
    private  RadioButton rb_xunzhu;
    private Integer belong = 0;
    private Integer type = 0;

    private AlertDialog dialog;
    private String mCurrentPhotoPath;//拍摄所得照片路径

    private ImageView iv_uploadThing;


    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
//    private  static  final  String IMAGE_TEST = "test.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    private static File tempfile = null;
    private static String filename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        //映射控件
        sn_type = (Spinner) findViewById(R.id.sn_type);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_miaoshu = (EditText) findViewById(R.id.et_miaoshu);
        et_place = (EditText) findViewById(R.id.et_place);
        rg_belong = (RadioGroup) findViewById(R.id.rg_belong);
        rb_xunwu = (RadioButton) findViewById(R.id.rb_xunwu);
        rb_xunzhu = (RadioButton) findViewById(R.id.rb_xunzhu);
        iv_uploadThing = (ImageView) findViewById(R.id.iv_uploadThing);

        //设置监听
        rg_belong.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if(checkedId == rb_xunzhu.getId()){
                    belong = 1;
                    Log.e("xunzhu","xunzhu");
                }
                else {
                    belong = 1;
                    Log.e("xunwu","xunwu");
                }
            }
        });
        sn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                Log.e("test:",position+"====");
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


    }


    //上传物品图片
    public void uploadPicture(View view) {
        Log.e("changeIcon","上传头像");
        //选择拍摄或者打开图库
        dialog =  DialogUtil.GalleryOrCamera(PublishActivity.this);
        //dialog条目点击事件
        Button btn_openCamera = dialog.getWindow().findViewById(R.id.btn_openCamera);
        Button btn_openGallery = dialog.getWindow().findViewById(R.id.btn_openGallery);
        btn_openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeCamera(1);//打开相机
                dialog.dismiss();//关闭dialog
            }
        });

        btn_openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseHeadImageFromGallery();//打开图库
                dialog.dismiss();//关闭dialog
            }
        });

    }


    //发布按钮点击事件
    public  void publish(View view){
        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String miaoshu = et_miaoshu.getText().toString().trim();
        String place = et_place.getText().toString().trim();

        Log.e("pulish",type+name+phone+miaoshu+place+belong);

        if("".equals(type)){
            Toast.makeText(this,"物品不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(name)){
            Toast.makeText(this,"物品名称不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(phone)){
            Toast.makeText(this,"联系号码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(miaoshu)){
            Toast.makeText(this,"物品描述不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(place)){
            Toast.makeText(this,"丢失或拾到地点不能为空", Toast.LENGTH_SHORT).show();
        }
        else if("".equals(filename)){
            Toast.makeText(this,"请上传物品图片", Toast.LENGTH_SHORT).show();
        }

        else {
            final String url = BASE_URL+"?type="+type+"&name="+name+"&phone="+phone+"&miaoshu="+miaoshu+"&place="+place+"&belong="+belong+"&filename="+filename;
            Log.e("url",url);

            //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
                    HttpUtil.GetWithHttpClient(url,handler);
                }
            }).start();

            //上传图片到服务器
            //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    File file = new File(tempfile);
                    String result = UploadFileUtil.uploadFile(tempfile,"http://10.0.2.2:8080/Demo/fileUpload");
                    Log.e("图片上传结果：",result);
                }
            }).start();

        }
    }

    //接收子线程传过来的数据
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            //成功
            if(msg.what == 1){
//                Toast.makeText(RegisterActivity.this,"SUCCESS"+msg.obj, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject result = new JSONObject(msg.obj.toString());
                    //解析json数据里的字符串
                    String isSuccess = result.getString("isSuccess");
                    if("true".equals(isSuccess)){
                        Toast.makeText(PublishActivity.this,"发布成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PublishActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        String message = result.getString("message");
                        Toast.makeText(PublishActivity.this,"发布失败："+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(PublishActivity.this,"ERROR", Toast.LENGTH_SHORT).show();
            }


        }
    };


    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    //使用相机拍摄作为头像
    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(PublishActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
            }
        }
        startActivityForResult(takePictureIntent, CODE_CAMERA_REQUEST);//跳转界面传回拍照所得数据
    }

    //创建图片文件
    private File createImageFile() {
        //获取相机目录
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
            image = File.createTempFile(generateFileName(), ".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //生成文件
    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }



    //处理返回的数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(PublishActivity.this, "取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            //调用图库方法
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;
            //调用相机方法
            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    //取出拍照得到的照片
                    File tempFile = new File(mCurrentPhotoPath);
                    //裁剪照片
                    cropRawPhoto(Uri.fromFile(tempFile));
                    //删除临时照片
//                    tempFile.delete();
                } else {
                    Toast.makeText(PublishActivity.this, "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    uploadThingImage(intent);
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
     * 提取保存裁剪之后的图片数据，并上传图片
     */
    private void uploadThingImage(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //生成临时文件
            filename = "thing"+ UUID.randomUUID()+".jpg";
            tempfile = new File(Environment.getExternalStorageDirectory(),filename);
            Log.e("tempfile",tempfile.getName());
            //保存图片到手机里
            ImageUtil.saveBitmapFile(photo,tempfile);
            //设置头像
            iv_uploadThing.setImageBitmap(photo);


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

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}

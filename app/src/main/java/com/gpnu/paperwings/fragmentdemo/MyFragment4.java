package com.gpnu.paperwings.fragmentdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.CircleImageView;
import com.gpnu.paperwings.fragmentdemo.utils.DialogUtil;
import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.gpnu.paperwings.fragmentdemo.utils.ImageUtil;
import com.gpnu.paperwings.fragmentdemo.utils.UploadFileUtil;
import com.loopj.android.image.SmartImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_WORLD_READABLE;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment4 extends Fragment {
    private String BASE_URL = "http://10.0.2.2:8080/Demo/fileUpload";
    ////////////////////////////////////
    	/* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
//    private  static  final  String IMAGE_TEST = "test.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CODE_UPDATE = 0xa3;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    private ImageView iv_usericon = null;
    private ImageView iv_setting;
    ///////////////////////////////////


    private TextView tv_username;
    private TextView tv_phone;
    private TextView tv_email;
    private TextView tv_miaoshu;
    private String username = "xxx";

    private Button btn_openCamera;
    private Button btn_openGallery;
    private AlertDialog dialog;

    private String mCurrentPhotoPath;//拍摄所得照片路径

    private View view;

    public MyFragment4() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            return view;
        }
        view = inflater.inflate(R.layout.fg4_content,container,false);
        return view;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置
         iv_setting = (ImageView) getActivity().findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //验证通过跳转到主页
                Intent intent = new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
            }
        });
        //映射控件
        tv_username = (TextView) getActivity().findViewById(R.id.tv_username);
        tv_phone = (TextView) getActivity().findViewById(R.id.tv_phone);
        tv_miaoshu = (TextView) getActivity().findViewById(R.id.tv_miaoshu);
        tv_email = (TextView) getActivity().findViewById(R.id.tv_email);
        //接收用户信息
        final Intent intent = getActivity().getIntent();
//        String userJson = intent.getStringExtra("userJson");
        SharedPreferences sp = getContext().getSharedPreferences("userinfo", Context.MODE_WORLD_READABLE);
        String userJson = sp.getString("user","");
//      Toast.makeText(getActivity(),userJson, Toast.LENGTH_SHORT).show();
        try {
            JSONObject user = new JSONObject(userJson);
            username = user.getString("username");
            String email = user.getString("email");
            String phone = user.getString("phone");
            String miaoshu = user.getString("miaoshu");
            tv_username.setText(username);
            tv_email.setText("邮箱："+email);
            tv_phone.setText("手机："+phone);
            tv_miaoshu.setText("个人简介："+miaoshu);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("HomeActivity","字符串转化为Json失败");
        }
        //头像
        iv_usericon = (ImageView) getActivity().findViewById(R.id.iv_usericon);
        Bitmap bitmap = getLoacalBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+
                "/"+username+".jpg"); //从本地取图片(在cdcard中获取)  //
        if(bitmap != null){
            iv_usericon .setImageBitmap(bitmap); //设置Bitmap
        }

        iv_usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon();
            }
        });

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UpdateInfoActivity.class);
                intent.putExtra("title","phone");
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UpdateInfoActivity.class);
                intent.putExtra("title","email");
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        tv_miaoshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UpdateInfoActivity.class);
                intent.putExtra("title","miaoshu");
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }

    //点击头像事件
    public void changeIcon(){
        Log.e("changeIcon","上传头像");
        //选择拍摄或者打开图库
         dialog =  DialogUtil.GalleryOrCamera(getActivity());
        //dialog条目点击事件
        btn_openCamera = dialog.getWindow().findViewById(R.id.btn_openCamera);
        btn_openGallery = dialog.getWindow().findViewById(R.id.btn_openGallery);
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
            Toast.makeText(getActivity(), "取消", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }
                break;
//            case CODE_UPDATE:
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }



    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
//        Toast.makeText(getActivity(), uri.toString(),Toast.LENGTH_LONG).show();
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
            File file = new File(Environment.getExternalStorageDirectory(),username+".jpg");
            Log.e("qfilepath",Environment.getExternalStorageDirectory().getPath().toString()+username+".jpg");
            //保存图片到手机里
            ImageUtil.saveBitmapFile(photo,file);
            //设置头像
            iv_usericon.setImageBitmap(photo);

            //上传图片到服务器
            //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(Environment.getExternalStorageDirectory(),username+".jpg");
                    String result = UploadFileUtil.uploadFile(file,BASE_URL);
                    Log.e("图片上传结果：",result);
                }
            }).start();
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

    @Override
    public void onResume() {
        super.onResume();

    }
}

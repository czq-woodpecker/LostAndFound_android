package com.gpnu.paperwings.fragmentdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.gpnu.paperwings.fragmentdemo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by paperwings on 2017/12/7.
 */

public class DialogUtil {
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static String mCurrentPhotoPath;//拍摄所得照片路径
    private static Activity tactivity;
    private static AlertDialog dialog;

    public  static  AlertDialog GalleryOrCamera(Activity activity){
        tactivity = activity;
        dialog = new AlertDialog.Builder(activity).create();//创建一个AlertDialog对象
        View view2 = activity.getLayoutInflater().inflate(R.layout.dialog, null);//自定义布局
        dialog.setView(view2, 0, 0, 0, 0);//把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();//一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = activity.getResources().getDisplayMetrics().widthPixels;;//得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();//得到这个dialog界面的参数对象
        params.width = width-(width/6);//设置dialog的界面宽度
        params.height =  ViewGroup.LayoutParams.WRAP_CONTENT;//设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;//设置dialog的重心
        //dialog.getWindow().setLayout(width-(width/6),  LayoutParams.WRAP_CONTENT);//用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置
        dialog.getWindow().setAttributes(params);//最后把这个参数对象设置进去，即与dialog绑定
        return dialog;
    }
//
//    //使用相机拍摄作为头像
//    public static void takeCamera(Activity activity,int num) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            File photoFile = null;
//            photoFile = createImageFile();
//            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//            }
//        }
//        activity.startActivityForResult(takePictureIntent, CODE_CAMERA_REQUEST);//跳转界面传回拍照所得数据
//    }
//
//
//    //创建图片文件
//    public static File createImageFile() {
//        //获取相机目录
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        File image = null;
//        try {
//            image = File.createTempFile(generateFileName(), ".jpg",storageDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//    //生成文件
//    public static String generateFileName() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        return imageFileName;
//    }
//
//    // 从本地相册选取图片作为头像
//    public static void choseHeadImageFromGallery(Activity activity) {
//        Intent intentFromGallery = new Intent();
//        // 设置文件类型
//        intentFromGallery.setType("image/*");
//        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//        activity.startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
//    }


}

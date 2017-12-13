package com.gpnu.paperwings.fragmentdemo.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by paperwings on 2017/12/4.
 */

public class ImageUtil {
   // Bitmap对象保存味图片文件
    public static void saveBitmapFile(Bitmap bitmap,File file) {
//        File file = new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
//      File file=new File(url);//将要保存图片的路径

        BufferedOutputStream bos = null;
        Log.e("saveBitmapFile","saveBitmapFile1");
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();
            Log.e("saveBitmapFile","saveBitmapFile2");


        } catch (IOException e) {
            Log.e("saveBitmapFile","IOException");
            e.printStackTrace();
        }
    }


}

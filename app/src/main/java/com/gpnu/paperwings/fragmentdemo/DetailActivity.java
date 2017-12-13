package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;


public class DetailActivity extends AppCompatActivity {
    private static  final  String IMAGE_BASE_URL = "http://10.0.2.2:8080/Demo/thingimage_";
    private SmartImageView sv_image;
    private TextView tv_sort;
    private TextView tv_name;
    private TextView tv_place;
    private TextView tv_miaoshu;
    private TextView tv_phone;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //获取数据
        String sort = getIntent().getStringExtra("sort");
        String name = getIntent().getStringExtra("name");
        String place = getIntent().getStringExtra("place");
        String miaoshu = getIntent().getStringExtra("miaoshu");
        String phone = getIntent().getStringExtra("phone");
        String photo = getIntent().getStringExtra("photo");
        String buttonText = getIntent().getStringExtra("buttonText");


        sv_image = (SmartImageView) findViewById(R.id.sv_image);
        tv_sort = (TextView) findViewById(R.id.tv_sort);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_miaoshu = (TextView) findViewById(R.id.tv_miaoshu);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);



        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        String photoheadname = photo.substring(0,photo.length()-4);
        sv_image.setImageUrl(IMAGE_BASE_URL+photoheadname);

        tv_sort.setText(sort);
        tv_name.setText(name);
        tv_place.setText(place);
        tv_miaoshu.setText(miaoshu);
        tv_phone.setText(phone);
        btn_confirm.setText(buttonText);

    }

    public void call(View view){
        String phone = tv_phone.getText().toString().trim();
        if(!"".equals(phone)){
            Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }
    }

    public void deal(View view){
        Toast.makeText(DetailActivity.this,"已提交，等待管理员审核",Toast.LENGTH_LONG).show();
    }
}

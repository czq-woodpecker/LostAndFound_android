package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultListActivity extends AppCompatActivity {
    private static  final  String IMAGE_BASE_URL = "http://10.0.2.2:8080/Demo/thingimage_";
    private static final String BASE_URL = "http://10.0.2.2:8080/Demo/thing_findByType";
    private  static  final String TAG = "ResultListActivity";
    private ListView lv_result_list;
    private String[] sorts;
    private String[] names;
    private String[] places;
    private String[] miaoshus;
    private String[] phones;
    private String[] photos;
    private String photo;
    private Integer type;//类别
    private Integer belong;//寻物还是寻主
    //存放物品集合
    private JSONArray thingsJsonArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        //隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        lv_result_list = (ListView) findViewById(R.id.lv_result_list);
        type = getIntent().getIntExtra("type",-1);//默认值-1
        belong = getIntent().getIntExtra("belong",-1);//默认值-1
        final String  url = BASE_URL +"?type="+type+"&belong="+ belong;
        Log.e(TAG,url);

        //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
        new Thread(new Runnable() {
            @Override
            public void run() {
                //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
                HttpUtil.GetWithHttpClient(url,handler);
            }
        }).start();


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
//                        JSONArray array =  result.getJSONArray("list");
                        thingsJsonArr =  result.getJSONArray("list");
                        int i = 0;
                        sorts = new String[thingsJsonArr.length()];
                        names = new String[thingsJsonArr.length()];
                        places = new String[thingsJsonArr.length()];
                        miaoshus = new String[thingsJsonArr.length()];
                        phones = new String[thingsJsonArr.length()];
                        photos = new String[thingsJsonArr.length()];

                        while (i<thingsJsonArr.length()){
                            //获取分类id
//                            int sortId = Integer.parseInt(thingsJsonArr.getJSONObject(i).getString("ttype"));
                            //根据sortId查询分类名并设置进去
//                            sorts[i] = getResources().getStringArray(R.array.sortarr)[sortId];
                            sorts[i] = thingsJsonArr.getJSONObject(i).getString("ttype");
                            names[i] = thingsJsonArr.getJSONObject(i).getString("tname");
                            places[i] = thingsJsonArr.getJSONObject(i).getString("tplace");
                            miaoshus[i] = thingsJsonArr.getJSONObject(i).getString("tfeature");
                            phones[i] = thingsJsonArr.getJSONObject(i).getString("tphone");
                            photos[i] = thingsJsonArr.getJSONObject(i).getString("tphoto");
                            i++;
                        }
                        //将获取到的数据装载到ListView中
                        //创建一个Adapter的实例
                        MyBaseAdapter myBaseAdapter = new MyBaseAdapter();
                        //设置Adapter
                        lv_result_list.setAdapter(myBaseAdapter);
                        lv_result_list.setOnItemClickListener(new MyOnItemClickListener());
                    }
                    else {
                        Toast.makeText(ResultListActivity.this,"数据获取失败，请检查网络是否可用", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(ResultListActivity.this,"服务器没有相应请求", Toast.LENGTH_SHORT).show();
            }
        }
    };


    //创建一个类继承BaseAdapter
    class MyBaseAdapter extends BaseAdapter {
        //得到Item的总数
        @Override
        public int getCount() {
            //返回ListView中item条目的总数
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            //返回条目对象
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //得到Item的View视图
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            //将list_item.xml文件找出来并转化成View对象
            View view = View.inflate(ResultListActivity.this,R.layout.list_item_result_list,null);
            //找到list_item中创建的
            SmartImageView sv_photo =(SmartImageView) view.findViewById(R.id.sv_photo);
            photo = photos[position];
            String photoheadname = photo.substring(0,photo.length()-4);
            sv_photo.setImageUrl(IMAGE_BASE_URL+photoheadname);
            Log.e("url:",IMAGE_BASE_URL+photoheadname);


//            int sortId = Integer.parseInt(thingsJsonArr.getJSONObject(i));
            //根据sortId查询分类名并设置进去
            int sortId = Integer.parseInt(sorts[position])
            ;
            TextView tv_sort = (TextView) view.findViewById(R.id.tv_sort);

//            tv_sort.append(getResources().getStringArray(R.array.sortarr)[]);
//            tv_sort.append(sorts[position]);
            tv_sort.append(getResources().getStringArray(R.array.sortarr)[sortId]);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_name.append(names[position]);
            TextView tv_place = (TextView) view.findViewById(R.id.tv_place);
            tv_place.append(places[position]);

            return view;
        }
    }

    class  MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(ResultListActivity.this,DetailActivity.class);
            //获取分类id
//                            int sortId = Integer.parseInt(thingsJsonArr.getJSONObject(i).getString("ttype"));
            //根据sortId查询分类名并设置进去
//                            sorts[i] = getResources().getStringArray(R.array.sortarr)[sortId];
//            intent.putExtra("sort",sorts[i]);
            int sortId = Integer.parseInt(sorts[i]);

            intent.putExtra("sort", getResources().getStringArray(R.array.sortarr)[sortId]);
            intent.putExtra("name",names[i]);
            intent.putExtra("place",places[i]);
            intent.putExtra("miaoshu",miaoshus[i]);
            intent.putExtra("phone",phones[i]);
            intent.putExtra("photo",photos[i]);
            intent.putExtra("buttonText","我已领回");
            startActivity(intent);
        }
    }
}

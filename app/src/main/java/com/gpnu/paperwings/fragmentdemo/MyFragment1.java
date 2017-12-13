package com.gpnu.paperwings.fragmentdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpnu.paperwings.fragmentdemo.utils.HttpUtil;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyFragment1 extends Fragment {
    private static  final  String IMAGE_BASE_URL = "http://10.0.2.2:8080/Demo/thingimage_";
    private static final String BASE_URL = "http://10.0.2.2:8080/Demo/thing_findAllThingByBelong?belong=0";
    private View fra_view;
    private ListView lv_found;
    //    private SearchView sv_search;
    private ListView lv_result_list;
    private TextView tv_search;

    //sorts
    private ImageView iv_yikatong;
    private ImageView iv_shouji;
    private ImageView iv_shenfenzheng;
    private ImageView iv_shubao;
    private ImageView iv_shu;
    private ImageView iv_shuiping;
    private ImageView iv_erji;
    private ImageView iv_qita;


    private ImageView iv_uploadThing;
    //物品描述的集合
    private String[] sorts;
    private String[] names;
    private String[] places;
    private String[] miaoshus;
    private String[] phones;
    private String[] photos;
    private String photo;
    //存放物品集合
    private JSONArray thingsJsonArr;

    public MyFragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fra_view = inflater.inflate(R.layout.fg1_content, container, false);
        lv_found = (ListView) fra_view.findViewById(R.id.lv_found);


        iv_uploadThing = (ImageView)  fra_view.findViewById(R.id.iv_uploadThing);
        iv_uploadThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),PublishActivity.class);
                startActivity(intent);
            }
        });

        tv_search = (TextView) fra_view.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });




        //
        iv_yikatong = (ImageView) fra_view.findViewById(R.id.iv_yikatong);
        iv_shouji = (ImageView) fra_view.findViewById(R.id.iv_shouji);
        iv_shenfenzheng = (ImageView) fra_view.findViewById(R.id.iv_shenfenzheng);
        iv_shubao = (ImageView) fra_view.findViewById(R.id.iv_shubao);
        iv_shu = (ImageView) fra_view.findViewById(R.id.iv_shu);
        iv_shuiping = (ImageView) fra_view.findViewById(R.id.iv_shuiping);
        iv_erji = (ImageView) fra_view.findViewById(R.id.iv_erji);
        iv_qita = (ImageView) fra_view.findViewById(R.id.iv_qita);
        //
        iv_yikatong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",0);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_shouji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_shenfenzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",2);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_shubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",3);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_shu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",4);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_shuiping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",5);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_erji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",6);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        iv_qita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ResultListActivity.class);
                intent.putExtra("type",7);
                intent.putExtra("belong",0);
                startActivity(intent);
            }
        });
        return fra_view;
    }

    //创建一个类继承BaseAdapter
    class MyBaseAdapter extends BaseAdapter{
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
            View view = View.inflate(getActivity(),R.layout.list_item,null);

            //找到list_item中创建的TextView
            SmartImageView sv_photo =(SmartImageView) view.findViewById(R.id.sv_photo);
//            sv_photo.setBackgroundResource(photos[position]);//////////////////////////\
            photo = photos[position];
            String photoheadname = photo.substring(0,photo.length()-4);
            sv_photo.setImageUrl(IMAGE_BASE_URL+photoheadname);
            Log.e("url:",IMAGE_BASE_URL+photoheadname);
            TextView tv_sort = (TextView) view.findViewById(R.id.tv_sort);
            tv_sort.append(sorts[position]);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_name.append(names[position]);
            TextView tv_place = (TextView) view.findViewById(R.id.tv_place);
            tv_place.append(places[position]);
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //新建子线程进行HTTP请求（Android 4.0 之后不能在主线程中请求HTTP请求，不然可能假死）
        new Thread(new Runnable() {
            @Override
            public void run() {
                //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
                HttpUtil.GetWithHttpClient(BASE_URL,handler);
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
                            int sortId = Integer.parseInt(thingsJsonArr.getJSONObject(i).getString("ttype"));
                            //根据sortId查询分类名并设置进去
                            sorts[i] = getResources().getStringArray(R.array.sortarr)[sortId];
                            names[i] = thingsJsonArr.getJSONObject(i).getString("tname");
                            places[i] = thingsJsonArr.getJSONObject(i).getString("tplace");
                            miaoshus[i] = thingsJsonArr.getJSONObject(i).getString("tfeature");
                            phones[i] = thingsJsonArr.getJSONObject(i).getString("tphone");
                            photos[i] = thingsJsonArr.getJSONObject(i).getString("tphoto");
                            i++;
                        }
                        //创建一个Adapter的实例
                        MyBaseAdapter myBaseAdapter = new MyBaseAdapter();
                        //设置Adapter
                        lv_found.setAdapter(myBaseAdapter);
                        lv_found.setOnItemClickListener(new MyOnItemClickListener());
                    }
                    else {
                        Toast.makeText(getActivity(),"数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONOBject:","字符串转化为json数据失败");
                }
            }
            //失败
            else if(msg.what == 2){
                Toast.makeText(getActivity(),"ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //ListView条目点击事件
    private class  MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(),DetailActivity.class);
            intent.putExtra("sort",sorts[i]);
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


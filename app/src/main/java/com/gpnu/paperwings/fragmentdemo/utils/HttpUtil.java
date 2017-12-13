package com.gpnu.paperwings.fragmentdemo.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by paperwings on 2017/11/30.
 */

public class HttpUtil {
    /////////////////////////////////////////////////////
    private static final String TAG = "uploadFile";
    private static final String CHARSET = "utf-8"; //设置编码
    //////////////////////////////////////////////////////////


    protected  static final int SUCCESS = 1;
    protected  static final int ERROR = 2;
    private static final int TIME_OUT = 10*1000;   //超时时间
    //使用HttpClient进行get请求（并在子线程中将Message对象发出去）
    public static void GetWithHttpClient(String url, Handler handler){
        //用HttpClient发送请求，分为五步
        //第一步：创建HttpClient对象
        HttpClient httpClient = new DefaultHttpClient();
        //第二步：创建代表请求的对象,参数是访问的服务器地址
        HttpGet httpGet = new HttpGet(url);
        try{
            //第三步：执行请求，获取服务器返回的相应对象
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                Log.e("response",response);
                //在子线程中将Message对象发出去
                Message message = new Message();
                message.what = SUCCESS;
                //将服务器返回的数据转化为字符串后放入message中
                message.obj = response.toString();
                handler.sendMessage(message);
            }
            else {
                Message message = new Message();
                message.what = ERROR;
                message.obj = "error";
                handler.sendMessage(message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Post
    public static void PostWithHttpClient(String url,File file,Handler handler){

//        File file = new File(url);\
        HttpPost request = new HttpPost(url);
        HttpClient httpClient = new DefaultHttpClient();
        FileEntity entity = new FileEntity(file,"binary/octet-stream");
        HttpResponse response;
        try {
            request.setEntity(entity);
            entity.setContentEncoding("binary/octet-stream");
            response = httpClient.execute(request);
           //如果返回状态为200，获得返回的结果
            if(response.getStatusLine().getStatusCode()==200){
             //图片上传成功
                //在子线程中将Message对象发出去
                Message message = new Message();
                message.what = 8;
                //将服务器返回的数据转化为字符串后放入message中
                message.obj = response.toString();
                handler.sendMessage(message);
            }
        }
        catch(Exception e){
        }

    }

    public static String postWithHttpURLConnection(String requestURL, File file,Handler handler){
//        try{
//            URL url = new URL(path);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            //设置超时时间
//            connection.setConnectTimeout(5000);
//            //设置请求方式
//            connection.setRequestMethod("POST");
//            //准备数据，将数据编码
//            String data = "user="+ URLEncoder.encode("zhangsan")+ "&password="+URLEncoder.encode("123");
//            //设置请求头--数据提交方式，这里是以form表单的方式提交
//            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            //设置请求头--设置提交数据的长度
//            connection.setRequestProperty("Content-Length",data.length()+"");
//            //post方式，实际上是浏览器把数据写给了服务器
//            connection.setDoOutput(true);//允许向外写数据
//            OutputStream os = connection.getOutputStream();//得到输出流
//            os.write(data.getBytes());  //将数据写给输出流中
//            int code = connection.getResponseCode();
//            String lj = "touxiang.png";
//            if(code == 200) {
//                //得到服务器返回的输入流
//                InputStream is = connection.getInputStream();
//                //将输入流转化为字符串
//                String text = StreamTools.readInputStream(is);
//                return text;
//            }
//            else {
//                return null;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//
//
//
//
        Log.e("uploadFile","start");
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null)
            {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                OutputStream outputSteam=conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
                if(res==200)
                {
//                    return SUCCESS;
                    Message message = new Message();
                    message.what = 8;
                    //将服务器返回的数据转化为字符串后放入message中
//                    message.obj = response.toString();
                    handler.sendMessage(message);

                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}

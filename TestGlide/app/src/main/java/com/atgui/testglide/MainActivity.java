package com.atgui.testglide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private  static final String urlAtGuiguoLogo = "http://www.atguigu.com/images/logo.gif";

    private static final String[] REMOTE_URLS = new String[]{urlAtGuiguoLogo};
    private ImageView ivShowPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        showByGlide(REMOTE_URLS[0]);
        //showByRemoteUrl();
    }

    private void initView(){
          ivShowPicture = (ImageView) findViewById(R.id.iv_test);
    }

    private void showByGlide(String url){
        Glide.with(this).load(url).into(ivShowPicture);
    }

    private void showByLocalUrl(){
        // 本地路径: 获取当前的资源文件名
        String url = Environment.getExternalStorageDirectory().getPath() + "/test.png";

        // 把文件解码，转换成位图进行呈现
        Bitmap bitmap = BitmapFactory.decodeFile(url);

        ImageView ivShowPicture = (ImageView) findViewById(R.id.iv_test);

        ivShowPicture.setImageBitmap(bitmap);

    }

    private void showByRemoteUrl(){
        final String url = "http://www.atguigu.com/images/logo.gif";

        new Thread(new Runnable() {
            @Override
            public void run() {
                downLoadFile(url);
            }
        }).start();
    }

    private void downLoadFile(String url){
        URL httpUrl = null;

        try {
            httpUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if(httpUrl == null){
            return;
        }

        try {
            //只建立一个connection对象，并没有真正的触发连接服务器的操作
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();

            // 设置连接超时时间
            connection.setConnectTimeout(20000);

            // 设置服务器响应时间
            connection.setReadTimeout(10000);

            // 默认就是为true
            connection.setDoInput(true);

            connection.connect();

            //判断返回值是否是ok
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d(TAG,"reponse is : " + connection.getResponseCode());

                return;
            }

            final InputStream  inputStream = connection.getInputStream();

            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView ivShowPicture = (ImageView) findViewById(R.id.iv_test);

                    ivShowPicture.setImageBitmap(bitmap);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 1. 我们需要URI源，也就是我们可以通过来判断是否是本地请求还是远程请求URI，URI 可以是url，也可以是资源文件的ID
 * 2. 有了URI我们需要知道如何通过URI来获取数据
 * 3. 我们需要开启额外的工作线程去下载和读取数据
 * 4. 我们需要知道如何去把相应的数据源头转换成能够显示的数据源例如bitmap 或者 Drawable
 * 5. 我们需要如何去吧资源显示到目标的view上面
 * 6. 对于已经准备好的资源，我们需要做缓存以备后用，这就需要我们有缓存的策略
 */
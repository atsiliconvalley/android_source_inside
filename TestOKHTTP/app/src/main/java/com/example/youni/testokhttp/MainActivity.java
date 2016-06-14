package com.example.youni.testokhttp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Log.d(TAG,"first interceptor...");

                        Request request = chain.request().newBuilder().addHeader("test","test").build();

                        return chain.proceed(request);
                    }
                });

                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Log.d(TAG,"second interceptor...");

                        return chain.proceed(chain.request());
                    }
                });

                OkHttpClient client = builder.build();

                Request request = new Request.Builder().url("https://www.baidu.com").build();

                try {
                    Log.d("OKHTTP TEST",request.toString());
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

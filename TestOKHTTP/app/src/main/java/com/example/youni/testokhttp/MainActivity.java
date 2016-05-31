package com.example.youni.testokhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url("https://www.baidu.com").build();

                try {
                    Log.d("OKHTTP TEST",request.toString());
                    Response response = client.newCall(request).execute();

                    Log.d("OKHTTP TEST","response body1 : " + response.body().string());

                    response = client.newCall(request).execute();
                    Log.d("OKHTTP TEST","response body2 : " + response.body().string());

                    int i =0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

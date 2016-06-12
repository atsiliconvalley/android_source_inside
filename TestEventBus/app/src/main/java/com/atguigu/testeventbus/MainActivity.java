package com.atguigu.testeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        EventBus.getDefault().post("hello event bus");
        EventBus.getDefault().post(3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void testEventBus(String str){
        Log.d(TAG, str);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void testEventBusOnBackground(Integer arg){
        Log.d(TAG,"sss : " + arg);
    }
}

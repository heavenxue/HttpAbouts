package com.admin.lixue.httpabouts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new HttpServer().start();
        startClinet();
    }

    private void startClinet(){
        HttpPost httpPost = new HttpPost("127.0.0.1");
        //设置两个参数
        httpPost.addParam("username","lixue");
        httpPost.addParam("pwd","123456");
        //执行请求
        httpPost.execute();
    }
}

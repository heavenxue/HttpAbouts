package com.admin.lixue.httpabouts;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by lixue on 16/4/23.
 */
public class HttpServer extends Thread{
    public static final int HTTP_PORT = 8000;
    ServerSocket mSocket = null;

    public HttpServer(){
        try {
            mSocket = new ServerSocket(HTTP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mSocket == null){
            throw new RuntimeException("服务器Soket初始化失败");
        }
    }

    @Override
    public void run() {
        try {
            while (true){
                System.out.print("等待链接中");
                //一旦接收到连接请求，构建一个线程来处理
                new DeliverThread(mSocket.accept()).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

package com.admin.lixue.httpabouts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;

/**
 * 接收到链接请求后的处理类
 * Created by lixue on 16/4/23.
 */
public class DeliverThread extends Thread {
    Socket mClientSocket;
    BufferedReader mInputStream;
    PrintStream mOutputStream;
    String httpMethod;
    String subPath;
    String boundary;//分隔符
    Map<String, String> mParams = new HashMap<>();//请求参数
    Map<String,String> mHeaders = new HashMap<>();//头部参数集合
    boolean isParseHeader;//是否已经解析完head

    public DeliverThread(Socket socket) {
        mClientSocket = socket;
    }


    @Override
    public void run() {

        //获取输入流
        try {
            mInputStream = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream(
            )));
            //获取输出流
            mOutputStream = new PrintStream(mClientSocket.getOutputStream());
            //解析请求
            parseRequest();
            //返回response
            handleResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流和socket
            IoUtils.closeQuitly(mInputStream);
            IoUtils.closeQuitly(mOutputStream);
            IoUtils.closeQuitly(mClientSocket);
        }
    }

    private void parseRequest(){
        String line;
        try {
            int lineNum = 0;
            while ((line = mInputStream.readLine()) != null){
                if (lineNum == 0)
                    parseRequestLine(line);
                //判断是否是数据的结束行
                if (isEnd(line)){
                    break;
                }
                //解析header参数
                if (lineNum != 0 && !isParseHeader){
                    parseHeaders(line);
                }
                //解析请求参数
                if (isParseHeader){
                    parseRequestParams(line);
                }
                lineNum ++;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean isEnd(String line) throws IOException {
        if (mInputStream.read() == -1){
            return true;
        }else {
            return false;
        }
    }

    /** 解析第一行
     *
     * POST /api/feed/ HTTP/1.1
     *
     * **/
    private void parseRequestLine(String lineOne){
        String[] tempStrs = lineOne.split(" ");
        httpMethod = tempStrs[0];
        subPath = tempStrs[1];
        System.out.print("请求方式： " + tempStrs[0]);
        System.out.print("子路径：" + tempStrs[1]);
        System.out.print("HTTP版本：" + tempStrs[2]);
    }

    /**
     * 解析头部
     * content－Length: 1234
     * **/
    private void parseHeaders(String headerLine){
        if (headerLine.equals("")){
            isParseHeader = true;
            System.out.print("-------------> header解析完成\n");
            return;
        }else if(headerLine.contains("boundary")){
            boundary = parseSecondField(headerLine);
            System.out.print("分隔符："+boundary);
        }else{
            parseHeaderParam(headerLine);
        }
    }

    /**
     *解析header中的第二个参数
     * content-Type: multipart/form-data;boundary=OCqxMF6-JxtxoMDHmoG5eY9MGRsTBp
     * **/
    private String parseSecondField(String line){
        String[] headerArray = line.split(";");
        parseHeaderParam(headerArray[0]);
        if (headerArray.length > 1){
            return headerArray[1].split("=")[1];
        }
        return "";
    }

    /**
     * 解析单个header
     * Host:www.myhost.com
     * Connection:Keep-Alive
     * **/
    private void parseHeaderParam(String headerline){
        String[] keyValue = headerline.split(":");
        mHeaders.put(keyValue[0].trim(),keyValue[1].trim());
        System.out.print("header参数名：" + keyValue[0].trim() + "，参数值：" + keyValue[1].trim());
    }

    /**
     * 解析请求的参数
     * --OCqxMF6-JxtxoMDHmoG5w5eY9MGRsTBp
     * Content-Disposition:form-data;name="username"
     * Content-Type:text/plain;charset-UTF-8
     * Content-Transfer-Encoding:8bit
     *
     * Mr.simple
     * **/
    private void parseRequestParams(String line) throws IOException {
        if (line.equals("--" + boundary)){
            //读取Content－Disposition行
            String ContentDisposition = mInputStream.readLine();
            //解析参数名
            String paramName = parseSecondField(ContentDisposition);
            //读取参数header与参数值之间的空行
            mInputStream.readLine();
            //读取参数值
            String paramValue = mInputStream.readLine();
            mParams.put(paramName,paramValue);
            System.out.print("参数名：" + paramName +",参数值：" + paramValue);
        }
    }

    /**
     * 处理响应
     * **/
    private void handleResponse(){
        //模拟处理耗时
        sleep();
        //像输出流写数据
        mOutputStream.println("HTTP/1.1 200 OK");
        mOutputStream.println("Content-Type:application/json");
        mOutputStream.println();
        mOutputStream.println("{\"stCode\":\"success\"}");
    }

    private void sleep(){
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}

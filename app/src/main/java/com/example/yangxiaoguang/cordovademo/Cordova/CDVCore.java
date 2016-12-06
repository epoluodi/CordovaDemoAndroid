package com.example.yangxiaoguang.cordovademo.Cordova;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yangxiaoguang.cordovademo.CordovaWebActivity;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONObject;

/**
 * 处理Cordova核心处理控制类
 * Created by yangxiaoguang on 2016/12/4.
 */

public class CDVCore {

    public static final String NEWOPENWINDOWS = "NEWOPENWINDOWS";
    public static final int NEWOPENWINDOWS_ENUM = 0;

    private CordovaWebView cordovaWebView;
    private Activity activity;

    /**
     * 构造函数
     * @param activity 传入当前所在的上下文
     * @param cordovaWebView 当前cordovawebview
     */
    public CDVCore(Activity activity,CordovaWebView cordovaWebView)
    {
        this.cordovaWebView=cordovaWebView;
        this.activity=activity;
    }


    /**
     * 加载url
     * @param url
     */
    public void loadUrl(String url)
    {
        cordovaWebView.loadUrl(url);
    }


    /**
     * 处理插件进行交互,这个交互是在线程中的，如果要对ui 操作，需要handle 到ui线程
     * @param s 插件命令
     * @param o 插件参数
     */
    public void onMessage(String s, Object o)
    {
        Message message;
        if (s.equals(NEWOPENWINDOWS))//打开新窗口
        {
            message =cdvhandler.obtainMessage();
            message.what=NEWOPENWINDOWS_ENUM;
            message.obj = ((Message)o).obj;//将 js 的参数传递下去
            cdvhandler.sendMessage(message);
            return;
        }
    }


    /**
     * 处理 插件指令，在ui线程中进行处理
     */
    Handler cdvhandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case NEWOPENWINDOWS_ENUM://打开新窗口
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        Log.i("新窗口url地址", jsonObject.getString("url"));
                        Intent intent= new Intent(activity,CordovaWebActivity.class);
                        intent.putExtra("url",jsonObject.getString("url"));
                        activity.startActivity(intent);
                    }
                    catch (Exception e)
                    {e.printStackTrace();}
                    break;
            }
        }
    };


}

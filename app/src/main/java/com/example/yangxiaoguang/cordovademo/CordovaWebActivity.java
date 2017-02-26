package com.example.yangxiaoguang.cordovademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.example.yangxiaoguang.cordovademo.Cordova.CDVCore;
import com.example.yangxiaoguang.cordovademo.Sign.SignActivity;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.yangxiaoguang.cordovademo.Cordova.CDVCore.SIGNREQUESTCODE;


/**
 * CordovaWebActivity 现实web 的窗口
 */
public class CordovaWebActivity extends AppCompatActivity implements CordovaInterface {

    private CordovaWebView cordovaWebView;
    private CDVCore cdvCore;
    private String strpointdata;

    //系统线程池，创建后由Cordova调用。TODO：需要移植
    private final ExecutorService threadPool = Executors.newCachedThreadPool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coedova_web);
        cordovaWebView = (CordovaWebView) findViewById(R.id.cordovawebview);

        //初始化 CordovaCore
        cdvCore = new CDVCore(this, cordovaWebView);
        cordovaWebView.clearCache(true);
        Intent intent = getIntent();
        cdvCore.loadUrl(intent.getStringExtra("url"));

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * 处理其它窗口返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNREQUESTCODE) {
            if (resultCode == RESULT_OK) {
                final String uuid = data.getStringExtra("uuid");
                Log.i("签名文件", uuid);
                cdvCore.callbackContext.success(uuid);
                cdvCore.callbackContext = null;

                return;
            }
            if (resultCode == SignActivity.RESULT_PREVIEW) {
                final String json = data.getStringExtra("json");

                String recordid = data.getStringExtra("recordid");
                try {

                    Log.i("签名文件", json);
                    cdvCore.callbackContext.success(json);
                    cdvCore.callbackContext = null;
                    strpointdata = CDVCore.readStringToTxt(CordovaWebActivity.this, recordid);
                    if (strpointdata==null)
                        return;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(50);
                                handler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return;
            }
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {

                cordovaWebView.loadUrl("javascript:setHandData('" + strpointdata + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * TODO 在activity 进行激活状态需要对cordova 进行恢复
     */
    @Override
    protected void onResume() {
        super.onResume();
        cordovaWebView.handleResume(true, true);
    }

    /**
     * TODO 在activity 进行挂起状态需要对cordova 进行挂起
     */
    @Override
    protected void onPause() {
        super.onPause();
        cordovaWebView.handlePause(true);
    }

    /**
     * TODO 在activity 销毁需要对cordova 进行销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cordovaWebView.handleDestroy();
    }

    //需要复制下面代码到对对应activity中
    //TODO 以下代码是Cordova 的接口实现，在移植过程中，需要加入到被调用的Activity中
    @Override
    public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int i) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {

    }


    //Todo 这个返回当前CordovaWebview 所在的Activity
    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onMessage(String s, Object o) {
        cdvCore.onMessage(s, o);//插件交互传入CordovaCore进行控制处理
        return null;
    }

    //Todo 这个必须要由返回
    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    // 结束
}

package com.example.yangxiaoguang.cordovademo.Cordova.Plugin;

import android.os.Environment;
import android.os.Message;

import com.example.yangxiaoguang.cordovademo.Cordova.CDVCore;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * app 系统插件
 *
 * @author yxg
 */
public class iAppRevision extends CordovaPlugin {



    public static float ratioHand = 0.4f;
    public static float ratioWord = 0.4f;
    public static int words = 88;


    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;


        Message message = new Message();

        message.arg1 = this.webView.cordovaWebViewId; //前端webview  id


        if (action.equals("initSign")) {
            String json = args.getString(0);
            JSONObject jsonObject = new JSONObject(json);//json数据

            ratioHand = Float.valueOf(jsonObject.getString("ratioHand"));
            ratioWord = Float.valueOf(jsonObject.getString("ratioWord"));
            words = Integer.valueOf(jsonObject.getString("words"));
            return true;
        }
        //新窗口
        if (action.equals("showSign")) {
            String json = args.getString(0);
            this.jsondata = new JSONObject(json);//json数据
            this.cordova.onMessage(CDVCore.SIGN, this);

            return true;
        }

        if (action.equals("sendSign")) {
            String json = args.getString(0);
            this.jsondata = new JSONObject(json);//json数据
            this.cordova.onMessage(CDVCore.SENDSIGN, this);
            return true;
        }

        if (action.equals("loadPointData")) {

            final String json =args.getString(0);
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    String recroid="0";
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String pointdata = jsonObject.getString("pointData");//json数据
                        recroid = jsonObject.getString("recordID");
                        FileOutputStream outputStream = null;
                        File file=new File(iAppRevision.this.cordova.getActivity().getCacheDir()
                                + "/" + recroid + ".txt");
                        file.delete();
                        outputStream = new FileOutputStream(file);
                        outputStream.write(pointdata.getBytes("UTF-8"));
                        outputStream.flush();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        iAppRevision.this.callbackContext.success("0");
                    }
                    iAppRevision.this.callbackContext.success(recroid);
                }
            });

            return true;
        }
        return true;
    }
}

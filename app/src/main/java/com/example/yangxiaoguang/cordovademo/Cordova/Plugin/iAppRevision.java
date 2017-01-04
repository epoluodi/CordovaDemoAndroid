package com.example.yangxiaoguang.cordovademo.Cordova.Plugin;

import android.os.Message;

import com.example.yangxiaoguang.cordovademo.Cordova.CDVCore;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * app 系统插件
 *
 * @author yxg
 */
public class iAppRevision extends CordovaPlugin {





    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;


        Message message = new Message();

        message.arg1 = this.webView.cordovaWebViewId; //前端webview  id


        //新窗口
        if (action.equals("showSign")) {
            this.jsondata = args.getJSONObject(0);//json数据
            this.cordova.onMessage(CDVCore.SIGN, this);
            return true;
        }

        if (action.equals("loadSign")) {
            this.jsondata = args.getJSONObject(0);//json数据
            this.cordova.onMessage(CDVCore.LOADSIGN, this);
            return true;
        }

        return true;
    }
}

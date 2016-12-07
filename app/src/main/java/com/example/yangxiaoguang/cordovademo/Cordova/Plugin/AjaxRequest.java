package com.example.yangxiaoguang.cordovademo.Cordova.Plugin;

import android.os.Message;
import android.util.Log;

import com.example.yangxiaoguang.cordovademo.Cordova.CDVCore;
import com.example.yangxiaoguang.cordovademo.HttpController;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * app 系统插件
 *
 * @author yxg
 */
public class AjaxRequest extends CordovaPlugin {


    private CallbackContext callbackContext = null;


    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;


        //新窗口
        if (action.equals("POST")) {
            JSONObject jsonObject = args.getJSONObject(0);//json数据

            String url = jsonObject.getString("url");
            String params = jsonObject.getString("params");

            HttpController httpController=new HttpController();
            httpController.openRequest(url,HttpController.REQ_METHOD_POST);
            httpController.setPostValuesForKey("paramss",params);
            httpController.setEntity(httpController.getPostData());
            if (httpController.sendRequest())
            {
                //获得返回数据
                byte[] buffer = httpController.getRespBodyData();
                if (buffer == null) {
                    httpController.closeRequest();
                    callbackContext.error("请求失败");
                    return true;
                }
//
                try {
                    String result = new String(buffer, "utf-8");
                    Log.e("结果", result);
                    httpController.closeRequest();
                    callbackContext.success(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    callbackContext.error("请求失败");
                    return true;
                }
            }
            else
            {
                callbackContext.error("转发失败");
            }




            return true;
        }


        return true;
    }
}

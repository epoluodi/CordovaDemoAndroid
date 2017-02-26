package com.example.yangxiaoguang.cordovademo.Cordova;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.yangxiaoguang.cordovademo.Cordova.Plugin.iAppRevision;
import com.example.yangxiaoguang.cordovademo.CordovaWebActivity;
import com.example.yangxiaoguang.cordovademo.Sign.SignActivity;
import com.kinggrid.iapprevision_iwebrevision.FieldEntity;
import com.kinggrid.iapprevision_iwebrevision.iAppRevision_iWebRevision;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * 处理Cordova核心处理控制类
 * Created by yangxiaoguang on 2016/12/4.
 */

public class CDVCore {


    public static final int SIGNREQUESTCODE = 1;//SIGN窗口返回的请求code


    public static final String NEWOPENWINDOWS = "NEWOPENWINDOWS";// 打开新的web窗口
    public static final String SIGN = "SIGN";//签名
    public static final String SENDSIGN = "SENDSIGN";//保存签批
    public static final String LOADSIGN = "LOADSIGN";//读取签批数据

    public CallbackContext callbackContext = null;//js回调 对象
    public static final int NEWOPENWINDOWS_ENUM = 0;//打开新窗口
    public static final int SING_ENUM = 1;//签名
    public static final int SENDSING_ENUM = 2;//保存签名
    private CordovaWebView cordovaWebView;
    private Activity activity;

    /**
     * 构造函数
     *
     * @param activity       传入当前所在的上下文
     * @param cordovaWebView 当前cordovawebview
     */
    public CDVCore(Activity activity, CordovaWebView cordovaWebView) {
        this.cordovaWebView = cordovaWebView;
        this.activity = activity;
    }


    /**
     * 加载url
     *
     * @param url
     */
    public void loadUrl(String url) {
        cordovaWebView.loadUrl(url);
    }


    /**
     * 处理插件进行交互,这个交互是在线程中的，如果要对ui 操作，需要handle 到ui线程
     *
     * @param s 插件命令
     * @param o 插件参数
     */
    public void onMessage(String s, Object o) {
        Message message;
        message = cdvhandler.obtainMessage();
        if (s.equals(NEWOPENWINDOWS))//打开新窗口
        {
            message.what = NEWOPENWINDOWS_ENUM;
            message.obj = ((Message) o).obj;//将 js 的参数传递下去
            cdvhandler.sendMessage(message);
            return;
        } else if (s.equals(SIGN)) {
            message.what = SING_ENUM;
            callbackContext = ((CordovaPlugin) o).callbackContext;//暂存 回调对象
            message.obj = ((CordovaPlugin) o).jsondata;//将 js 的参数传递下去
            cdvhandler.sendMessage(message);
            return;
        } else if (s.equals(SENDSIGN)) {
            message.what = SENDSING_ENUM;

            callbackContext = ((CordovaPlugin) o).callbackContext;//暂存 回调对象
            final JSONObject jsonObject = (JSONObject) ((CordovaPlugin) o).jsondata;//将 js 的参数传递下去
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, FieldEntity> recordIdMap = null;
                        FieldEntity fieldEntity = null;
                        iAppRevision_iWebRevision iAppRevisioniWebRevision = new iAppRevision_iWebRevision();
                        iAppRevisioniWebRevision.setCopyRight(activity, SignActivity.copyRight, null);
                        try {
                            if (jsonObject.getString("haveFieldValue").equals("1")) {
                                recordIdMap = iAppRevisioniWebRevision.loadRevision(
                                        jsonObject.getString("webService"),
                                        jsonObject.getString("oldRecordID"),
                                        jsonObject.getString("userName"));
                                fieldEntity = recordIdMap.get(jsonObject.getString("oldFieldName"));
                                if (fieldEntity == null)
                                    throw new Exception();
                            }
                            Bitmap bitmap = BitmapFactory.decodeFile(activity.getCacheDir() + "/" +
                                    jsonObject.getString("recordID") + ".jpg");
                            if (bitmap == null) {
                                throw new Exception();
                            }
                            Bitmap newbitmap = null;
                            if (jsonObject.getInt("mode") == 1)
                                newbitmap = scaleBitmap(bitmap, iAppRevision.ratioHand);
                            else if (jsonObject.getInt("mode") == 2)
                                newbitmap = scaleBitmap(bitmap, iAppRevision.ratioWord);

                            boolean r=false;
                            if (jsonObject.getString("haveFieldValue").equals("1"))
                            {
                                r = iAppRevisioniWebRevision.saveRevision(jsonObject.getString("newRecordID"),
                                        jsonObject.getString("webService"), newbitmap,
                                        jsonObject.getString("fieldName"), jsonObject.getString("userName"),
                                        fieldEntity, true);
                            }else if (jsonObject.getString("haveFieldValue").equals("0"))
                            {
                                r = iAppRevisioniWebRevision.saveRevision(jsonObject.getString("recordID"),
                                        jsonObject.getString("webService"), newbitmap,
                                        jsonObject.getString("fieldName"), jsonObject.getString("userName"),
                                        fieldEntity, true);
                            }

                            Log.e("=====>ERROR_CODE", String.valueOf(iAppRevision_iWebRevision.ERROR_CODE));
                            bitmap.recycle();
                            newbitmap.recycle();
                            File file = new File(activity.getCacheDir() + "/" +
                                    jsonObject.getString("recordID") + ".jpg");
                            file.delete();
                            file = new File(activity.getCacheDir() + "/" +
                                    jsonObject.getString("recordID") + ".txt");
                            file.delete();
                            if (r) {
                                cdvhandler.sendEmptyMessage(9);
                            } else
                                cdvhandler.sendEmptyMessage(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            cdvhandler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                callbackContext.success("1");
            }
            return;
        } else if (s.equals(LOADSIGN)) {

            callbackContext = ((CordovaPlugin) o).callbackContext;//暂存 回调对象
            JSONObject jsonObject = (JSONObject) ((CordovaPlugin) o).jsondata;//将 js 的参数传递下去

            try {
                iAppRevision_iWebRevision iAppRevision_iWebRevision = new iAppRevision_iWebRevision();
                iAppRevision_iWebRevision.setCopyRight(activity, SignActivity.copyRight, null);
                Map<String, FieldEntity> recordIdMap = iAppRevision_iWebRevision.loadRevision(
                        jsonObject.getString("webService"), jsonObject.getString("recordID"),
                        jsonObject.getString("userName"));
                for (String strkey : recordIdMap.keySet()) {
                    FieldEntity fieldEntity = recordIdMap.get(strkey);
                    if (fieldEntity.hasFieldBitmap()) {

                        File f = new File(Environment.getExternalStorageDirectory(), strkey + ".png");
                        if (f.exists()) {
                            f.delete();
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(f);
                            fieldEntity.getAllImage().compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                callbackContext.error("读取签批数据失败");
            }
            return;
        }

    }


    /**
     * 处理 插件指令，在ui线程中进行处理
     */
    Handler cdvhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case NEWOPENWINDOWS_ENUM://打开新窗口
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        Log.i("新窗口url地址", jsonObject.getString("url"));
                        Intent intent = new Intent(activity, CordovaWebActivity.class);
                        intent.putExtra("url", jsonObject.getString("url"));
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SING_ENUM:
                    Intent intent = new Intent(activity, SignActivity.class);
                    JSONObject jsonObject1 = (JSONObject) msg.obj;
                    try {
                        intent.putExtra("webService", jsonObject1.getString("webService"));
                        intent.putExtra("recordID", jsonObject1.getString("recordID"));
                        intent.putExtra("fieldName", jsonObject1.getString("fieldName"));
                        intent.putExtra("userName", jsonObject1.getString("userName"));
                        intent.putExtra("haveFieldValue", jsonObject1.getString("haveFieldValue"));
                        intent.putExtra("mode", jsonObject1.getInt("mode"));
                        if (jsonObject1.getInt("mode") == 2 && jsonObject1.getString("haveFieldValue").equals("1")) {
                            intent.putExtra("word", jsonObject1.getString("word"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    activity.startActivityForResult(intent, SIGNREQUESTCODE);
                    break;
                case -1:
                    callbackContext.success("1");
                    break;
                case 9:
                    callbackContext.success("0");
                    break;

            }
        }
    };


    /**
     * 读取point信息
     *
     * @param filename
     */
    public static String readStringToTxt(Context context, String filename) {

        FileInputStream fileInputStream;
        File file = new File(context.getCacheDir()
                + "/" + filename + ".txt");
        try {
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            fileInputStream.close();
            String s = new String(bytes, "UTF-8");
            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
//        origin.recycle();
        return newBM;
    }
}

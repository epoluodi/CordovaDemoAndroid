package com.example.yangxiaoguang.cordovademo.Sign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangxiaoguang.cordovademo.Cordova.CDVCore;
import com.example.yangxiaoguang.cordovademo.Cordova.Plugin.iAppRevision;
import com.example.yangxiaoguang.cordovademo.R;
import com.kinggrid.iapprevision.iAppRevisionUtil;
import com.kinggrid.iapprevision.iAppRevisionView;
import com.kinggrid.iapprevision_iwebrevision.FieldEntity;
import com.kinggrid.iapprevision_iwebrevision.iAppRevision_iWebRevision;
import com.kinggrid.kinggridsign.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

public class SignActivity extends AppCompatActivity {


    public static final int RESULT_PREVIEW = 2;
    private iAppRevisionView _iAppRevisionView;//手写板

    //控制按钮
    private Button btnundo, btnredo, btnclean, btnreturn, btnsave;
    private String webService, recordID, fieldName, userName, haveFieldValue;

    private String word = null;
    private FieldEntity fieldEntity;
    private iAppRevision_iWebRevision iAppRevisionIWebRevision;
    private String strpoint = null;

    private int mode;//1 手写模式 2 文字模式
    //授权信息
    public static String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU3CQHU1ScyOebPLnpsDlQDzCOCvJ0o3Q+3TNNDtQWpjvfx6aD2yiupr6ji7hzsE6/QqGcC+eseQV1yrWJ/1FwxLAZ0WW0ABzY7A5uS1BgyebOVWEbHWAH22+t7LdPt+jENixZ/ZiYbBr3IJV3FwjNdmPaTd5f45yYpg1M98hLfJUqSgCNRP4FpYjl8hG/IVrYX5HLRplzRZbxZglj2FjzakuW8fXpxdRHfEuWC1PB9ruQ=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        iAppRevisionIWebRevision = new iAppRevision_iWebRevision();
        iAppRevisionIWebRevision.setCopyRight(this, copyRight, null);
        iAppRevisionIWebRevision.isDebug = true;

        //初始化view组件
        _iAppRevisionView = (iAppRevisionView) findViewById(R.id.signview);
        btnundo = (Button) findViewById(R.id.undo);
        btnredo = (Button) findViewById(R.id.redo);
        btnclean = (Button) findViewById(R.id.clean);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnsave = (Button) findViewById(R.id.save);
        _iAppRevisionView.setCopyRight(this, copyRight);//设置授权信息

        //设置签名 颜色，线条宽度，笔类型：现在是钢笔
        _iAppRevisionView.configSign(Color.BLACK, 8, iAppRevisionView.TYPE_BALLPEN);

        _iAppRevisionView.configWord(Color.BLACK, 28, Typeface.DEFAULT);
        _iAppRevisionView.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(iAppRevision.words)});

        _iAppRevisionView.setTimeTextInfo(_iAppRevisionView.getTimeTextWidth(),
                _iAppRevisionView.getTimeTextHeight(), 20, 30,
                Color.BLACK, _iAppRevisionView.getTime_textAlign());

        _iAppRevisionView.setBgIsWhite(true);

        //按钮事件
        btnundo.setOnClickListener(onClickListenerSignCotrol);
        btnredo.setOnClickListener(onClickListenerSignCotrol);
        btnclean.setOnClickListener(onClickListenerSignCotrol);
        btnreturn.setOnClickListener(onClickListenerSignCotrol);
        btnsave.setOnClickListener(onClickListenerSignCotrol);

        webService = getIntent().getStringExtra("webService");
        recordID = getIntent().getStringExtra("recordID");
        fieldName = getIntent().getStringExtra("fieldName");
        userName = getIntent().getStringExtra("userName");
        haveFieldValue = getIntent().getStringExtra("haveFieldValue");
        mode = getIntent().getIntExtra("mode", 1);
//        if (mode == 1) {
//
//            strpoint = getIntent().getStringExtra("pointData");
//        }
        if (mode == 2) {
            //文字模式
            _iAppRevisionView.useWordSign();
            word = getIntent().getStringExtra("word");
            btnundo.setVisibility(View.GONE);
            btnredo.setVisibility(View.GONE);
            btnclean.setVisibility(View.GONE);

        }
        Log.i("recordID", recordID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    handler.sendEmptyMessage(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            switch (msg.what) {
                case -1:
                    Toast.makeText(SignActivity.this, "保存读取签批数据失败", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case -2://没有读取到签批数据
                    Toast.makeText(SignActivity.this, "无法读取签批数据", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case 1:

                    intent.putExtra("uuid", "");
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 2:
                    intent.putExtra("json", msg.obj.toString());
                    intent.putExtra("recordid", recordID);
                    setResult(RESULT_PREVIEW, intent);
                    finish();
                    break;
                case 3:
                    if (haveFieldValue.equals("1")) {

                        switch (mode) {
                            case 1:
                                if (strpoint == null || strpoint.equals(""))
                                    strpoint = CDVCore.readStringToTxt(SignActivity.this, recordID);
                                if (strpoint == null)
                                    return;
                                LinkedList<Point> pointLinkedList = getPointInfoFromJson(strpoint);
                                _iAppRevisionView.drawHandwritePoints(pointLinkedList);
                                break;
                            case 2:
                                if (word == null || word.equals("")) {
                                    return;
                                }

                                _iAppRevisionView.importWordSignData(word);
                                break;
                        }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    fieldEntity = loadFieldEntity(webService, recordID, userName);
//                    if (fieldEntity == null) {
//                        handler.sendEmptyMessage(-2);
//                    }
//                }
//            }).start();
                    }
                    break;
            }
        }
    };
    View.OnClickListener onClickListenerSignCotrol = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.undo://撤销
                    _iAppRevisionView.undoSign();
                    break;
                case R.id.redo://恢复
                    _iAppRevisionView.redoSign();
                    break;
                case R.id.clean://清除
                    _iAppRevisionView.clearSign();
                    break;
                case R.id.btnreturn://返回
                    setResult(0);
                    finish();
                    break;
                case R.id.save://保存


                    Bitmap bitmap = null;
                    String exportstr = null;
                    String strword = "";
                    if (_iAppRevisionView.isEmpty()) {
                        Toast.makeText(SignActivity.this, "不能保存空白签名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (mode) {
                        case 1://手写
                            bitmap = _iAppRevisionView.saveValidSign();
                            LinkedList<Point> pointLinkedList = _iAppRevisionView.exportHandwritePoints();
                            exportstr = pointInfoToJson(pointLinkedList);


                            break;
                        case 2://文字
//                            String timestamp = "ljaskdjsjadjlasjdkljadjalkjdlajdjasdklj" + "\r\n" + userName + "  " + new SimpleDateFormat("yyyy-MM-dd") .format(new Date());
//
//                            SpannableString msp = new SpannableString(timestamp);
//                            msp.setSpan(new AbsoluteSizeSpan(24), "ljaskdjsjadjlasjdkljadjalkjdlajdjasdklj".length() + 2, timestamp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//
//                            return;

                            Bitmap tmpbitmap = _iAppRevisionView.saveValidWord();
                            float scale = 0.9f;
                            if (_iAppRevisionView.exportWordSignData().length() < 5)
                                scale=0.95f;
                            else if (_iAppRevisionView.exportWordSignData().length() > 5 &&
                                    _iAppRevisionView.exportWordSignData().length() < 10)
                                scale=0.9f;
                            else if (_iAppRevisionView.exportWordSignData().length() > 9 &&
                                    _iAppRevisionView.exportWordSignData().length() < 15)
                                scale=0.85f;
                            else if (_iAppRevisionView.exportWordSignData().length() > 14)
                                scale=0.8f;

                                int textseize = (int) (tmpbitmap.getWidth() / (userName.length() + 11) * scale);

                            if (tmpbitmap.getWidth() < (int) (15 * 24 * 1.5)) {
                                _iAppRevisionView.setTimeTextInfo((int) (15 * 24 * 1.5),
                                        _iAppRevisionView.getTimeTextHeight(), px2dip(SignActivity.this, 24), 32,
                                        Color.BLACK, _iAppRevisionView.getTime_textAlign());
                            } else
                                _iAppRevisionView.setTimeTextInfo(tmpbitmap.getWidth(),
                                        tmpbitmap.getHeight(), px2dip(SignActivity.this, 24), textseize,
                                        Color.BLACK, _iAppRevisionView.getTime_textAlign());
                            bitmap = _iAppRevisionView.addTimeStampToBitmap(userName, tmpbitmap);
                            strword = _iAppRevisionView.exportWordSignData();

                            break;
                    }

                    File f = new File(getCacheDir(), recordID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    writeStringToTxt(exportstr, recordID);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("recordID", recordID);
                        jsonObject.put("fieldName", fieldName);
                        jsonObject.put("userName", userName);
                        jsonObject.put("mode", mode);
                        jsonObject.put("word", strword);
                        jsonObject.put("base64", iAppRevisionUtil.getBitmapString(bitmap));


                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(-1);
                    }


                    bitmap.recycle();
                    bitmap = null;
                    Message message = handler.obtainMessage();
                    message.what = 2;
                    message.obj = jsonObject.toString();
                    handler.sendMessage(message);
                    //先做预览
//                    Bitmap bitmap = null;
//
//                    switch (mode) {
//                        case 1://手写
//                            bitmap = _iAppRevisionView.saveValidSign();
//                            break;
//                        case 2://文字
//                            Bitmap tmpbitmap = _iAppRevisionView.saveValidWord();
//                            bitmap = _iAppRevisionView.addTimeStampToBitmap(userName, tmpbitmap);
//
//                            break;
//                    }
//
//                    if (bitmap == null || _iAppRevisionView.isEmpty()) {
//                        Toast.makeText(SignActivity.this, "不能保存空白签名", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    UUID uuid = UUID.randomUUID();
//                    struuid = uuid.toString();
//                    saveBitmap(bitmap);


                    break;
            }
        }
    };


    public void saveBitmap(final Bitmap bitmap) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap newbitmap = null;
                switch (mode) {
                    case 1:
                        newbitmap = scaleBitmap(bitmap, 0.2f);
                        break;
                    case 2:
                        newbitmap = scaleBitmap(bitmap, 0.6f);
                        break;
                }

                if (uploadSignData(newbitmap)) {
                    handler.sendEmptyMessage(1);//保存成功
                } else {
                    handler.sendEmptyMessage(-1);//保存失败
                }
                bitmap.recycle();
                newbitmap.recycle();
            }
        }).start();


    }


    /**
     * 上传签批n
     *
     * @param bitmap
     */
    public Boolean uploadSignData(Bitmap bitmap) {

        boolean r = iAppRevisionIWebRevision.saveRevision(recordID, webService, bitmap, fieldName, userName, fieldEntity, true);
        Log.e("=====>ERROR_CODE", String.valueOf(iAppRevision_iWebRevision.ERROR_CODE));
//        if (r)
//        {
//            r = iAppRevision_iWebRevision.updateDoc(webService,userName,recordID);//更新文档
//        }
        bitmap.recycle();
        return r;

    }


    /**
     * 读取签批数据
     *
     * @param webService
     * @param recordID
     * @param userName
     * @return
     */
    public FieldEntity loadFieldEntity(String webService, String recordID, String userName) {
        try {

            Map<String, FieldEntity> recordIdMap = iAppRevisionIWebRevision.loadRevision(
                    webService, recordID, userName);
            FieldEntity fieldEntity = recordIdMap.get(fieldName);
            if (fieldEntity == null)
                return null;
//            if (fieldEntity.hasFieldBitmap()) {
//
//                File f = new File(Environment.getExternalStorageDirectory(), userName + ".png");
//                if (f.exists()) {
//                    f.delete();
//                }
//                try {
//                    FileOutputStream out = new FileOutputStream(f);
//                    fieldEntity.getFieldBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            return fieldEntity;

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


    /**
     * Point信息转为json
     *
     * @param pointList
     * @return
     */
    private String pointInfoToJson(LinkedList<Point> pointList) {
        try {
            if (pointList == null || pointList.size() <= 0) {
                return null;
            }
            JSONArray array = new JSONArray();
            for (int i = pointList.size() - 1; i >= 0; i--) {
                Point p = pointList.get(i);
                JSONObject jsonObject = new JSONObject();
                String X = String.valueOf(p.getX());
                String Y = String.valueOf(p.getY());
                String state = String.valueOf(p.getState());
                String penSize = String.valueOf(p.getRadius());
                String penColor = String.valueOf(p.getColor());
                String penType = String.valueOf(p.getPenType());

                jsonObject.put("X", X);
                jsonObject.put("Y", Y);
                jsonObject.put("state", state);
                jsonObject.put("penSize", penSize);
                jsonObject.put("penColor", penColor);
                jsonObject.put("penType", penType);


                array.put(jsonObject);
            }

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * json转化为point列表
     *
     * @param json
     * @return
     */
    private LinkedList<Point> getPointInfoFromJson(String json) {
        LinkedList<Point> pointList = new LinkedList<Point>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                String X = jsonObject2.getString("X");
                String Y = jsonObject2.getString("Y");
                String state = jsonObject2.getString("state");
                String penSize = jsonObject2.getString("penSize");
                String penColor = jsonObject2.getString("penColor");
                String penType = jsonObject2.getString("penType");

                Point p = new Point();
                p.setX(Float.parseFloat(X));
                p.setY(Float.parseFloat(Y));
                p.setState(Integer.parseInt(state));
                p.setRadius(Float.parseFloat(penSize));
                p.setPenType(Integer.parseInt(penType));
                p.setColor(Integer.parseInt(penColor));

                pointList.add(p);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pointList;
    }


    /**
     * 数据写入文件
     */
    private void writeStringToTxt(String pointinfo, String filename) {
        if (pointinfo == null) {
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(getCacheDir()
                    + "/" + filename + ".txt");
            outputStream.write(pointinfo.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

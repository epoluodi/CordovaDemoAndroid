package com.example.yangxiaoguang.cordovademo.Sign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yangxiaoguang.cordovademo.R;
import com.kinggrid.iapprevision.iAppRevisionUtil;
import com.kinggrid.iapprevision.iAppRevisionView;
import com.kinggrid.iapprevision_iwebrevision.FieldEntity;
import com.kinggrid.iapprevision_iwebrevision.iAppRevision_iWebRevision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SignActivity extends AppCompatActivity {


    private iAppRevisionView iAppRevisionView;//手写板

    //控制按钮
    private Button btnundo, btnredo, btnclean, btnreturn, btnsave;
    private String webService, recordID, fieldName, userName,haveFieldValue;

    private FieldEntity fieldEntity;
    private iAppRevision_iWebRevision iAppRevision_iWebRevision;
    private String struuid;
    //授权信息
    public static String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU3CQHU1ScyOebPLnpsDlQDzCOCvJ0o3Q+3TNNDtQWpjvfx6aD2yiupr6ji7hzsE6/QqGcC+eseQV1yrWJ/1FwxLAZ0WW0ABzY7A5uS1BgyebOVWEbHWAH22+t7LdPt+jENixZ/ZiYbBr3IJV3FwjNdmPaTd5f45yYpg1M98hLfJUqSgCNRP4FpYjl8hG/IVrYX5HLRplzRZbxZglj2FjzakuW8fXpxdRHfEuWC1PB9ruQ=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        iAppRevision_iWebRevision = new iAppRevision_iWebRevision();
        iAppRevision_iWebRevision.setCopyRight(this, copyRight, null);
        iAppRevision_iWebRevision.isDebug=true;

        //初始化view组件
        iAppRevisionView = (iAppRevisionView) findViewById(R.id.signview);
        btnundo = (Button) findViewById(R.id.undo);
        btnredo = (Button) findViewById(R.id.redo);
        btnclean = (Button) findViewById(R.id.clean);
        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnsave = (Button) findViewById(R.id.save);
        iAppRevisionView.setCopyRight(this, copyRight);//设置授权信息

        //设置签名 颜色，线条宽度，笔类型：现在是钢笔
        iAppRevisionView.configSign(Color.BLACK, 16, iAppRevisionView.TYPE_BALLPEN);

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

        Log.i("recordID",recordID);

        if (haveFieldValue.equals("1")) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    fieldEntity = loadFieldEntity(webService, recordID, userName);
                    if (fieldEntity == null)
                    {
                        handler.sendEmptyMessage(-2);
                    }
                }
            }).start();
        }
    }



    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case -1:
                    Toast.makeText(SignActivity.this,"保存读取签批数据失败",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case -2://没有读取到签批数据
                    Toast.makeText(SignActivity.this,"无法读取签批数据",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.putExtra("uuid", struuid);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    };
    View.OnClickListener onClickListenerSignCotrol = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.undo://撤销
                    iAppRevisionView.undoSign();
                    break;
                case R.id.redo://恢复
                    iAppRevisionView.redoSign();
                    break;
                case R.id.clean://清除
                    iAppRevisionView.clearSign();
                    break;
                case R.id.btnreturn://返回
                    setResult(0);
                    finish();
                    break;
                case R.id.save://保存
                    Bitmap bitmap = iAppRevisionUtil.scaleBitmap(iAppRevisionView.saveSign(),3);
                    if (bitmap == null) {
                        Toast.makeText(SignActivity.this, "不能保存空白签名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UUID uuid = UUID.randomUUID();
                    struuid = uuid.toString();
                    saveBitmap(bitmap);


                    break;
            }
        }
    };


    public void saveBitmap(final  Bitmap bitmap) {

        File f = new File(Environment.getExternalStorageDirectory(), struuid + ".png");
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



        new Thread(new Runnable() {
            @Override
            public void run() {
                if (uploadSignData(bitmap))
                {
                    handler.sendEmptyMessage(1);//保存成功
                }else
                {
                    handler.sendEmptyMessage(-1);//保存失败
                }

            }
        }).start();


    }


    /**
     * 上传签批n
     *
     * @param bitmap
     */
    public Boolean uploadSignData(Bitmap bitmap) {

        boolean r = iAppRevision_iWebRevision.saveRevision(recordID, webService, bitmap, fieldName, userName, fieldEntity, true);
        Log.e("=====>ERROR_CODE",String.valueOf(iAppRevision_iWebRevision.ERROR_CODE));
//        if (r)
//        {
//            r = iAppRevision_iWebRevision.updateDoc(webService,userName,recordID);//更新文档
//        }
        bitmap.recycle();
        return r;

    }


    /**
     * 读取签批数据
     * @param webService
     * @param recordID
     * @param userName
     * @return
     */
    public FieldEntity loadFieldEntity(String webService, String recordID, String userName) {
        try
        {

            Map<String, FieldEntity> recordIdMap = iAppRevision_iWebRevision.loadRevision(
                    webService, recordID, userName);
            FieldEntity fieldEntity = recordIdMap.get(fieldName);
            if (fieldEntity==null)
                return null;
            if (fieldEntity.hasFieldBitmap())
            {

                File f = new File(Environment.getExternalStorageDirectory(), userName + ".png");
                if (f.exists()) {
                    f.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    fieldEntity.getFieldBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return fieldEntity;

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return null;
    }


}

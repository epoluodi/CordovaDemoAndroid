package com.example.yangxiaoguang.cordovademo.Sign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yangxiaoguang.cordovademo.R;
import com.kinggrid.iapprevision.iAppRevisionView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SignActivity extends AppCompatActivity {




    private iAppRevisionView iAppRevisionView;//手写板

    //控制按钮
    private Button btnundo,btnredo,btnclean,btnreturn,btnsave;


    //授权信息
    private String copyRight="SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU368sLBQG57FhM8Bkq7aPAVnSivRuSlEWh/3amWqlmf53x6aD2yiupr6ji7hzsE6/Q8+8WcsrGVLl3zOGriO8JAgfD/aknj9SAdcbqTXUmtPiVWEbHWAH22+t7LdPt+jEN7FwOixTutkXFqN7Z6YvH2Kcd2k72+1SjzDw0qWe3PQeSgCNRP4FpYjl8hG/IVrYXAg6SVLcb1PurpgdtpX/jJOW8fXpxdRHfEuWC1PB9ruQ=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

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
    }


    View.OnClickListener onClickListenerSignCotrol =new View.OnClickListener() {
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
                    Bitmap bitmap = iAppRevisionView.saveSign();
                    if (bitmap == null)
                    {
                        Toast.makeText(SignActivity.this,"不能保存空白签名",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UUID uuid=UUID.randomUUID();
                    String struuid= uuid.toString();
                    saveBitmap(struuid,bitmap);
                    bitmap.recycle();
                    finish();
                    break;
            }
        }
    };


    public void saveBitmap(String uuid,Bitmap bitmap) {

        File f = new File(Environment.getExternalStorageDirectory(),uuid+".png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent intent=new Intent();
        intent.putExtra("uuid",uuid);
        setResult(1,intent);

    }


}

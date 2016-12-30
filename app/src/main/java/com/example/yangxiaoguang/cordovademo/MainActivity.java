package com.example.yangxiaoguang.cordovademo;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yangxiaoguang.cordovademo.Sign.SignActivity;

import org.apache.cordova.CordovaWebView;
import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btnweb1, btnweb2, btndownload1, btndownload2;
    private TextView state, state2;

    private FileController fileController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = (TextView) findViewById(R.id.state);
        state2 = (TextView) findViewById(R.id.state2);

        btnweb1 = (Button) findViewById(R.id.btnweb);
        btnweb2 = (Button) findViewById(R.id.btnweb2);
        btndownload1 = (Button) findViewById(R.id.btndownload);
        btndownload2 = (Button) findViewById(R.id.btndownload2);

        btnweb1.setOnClickListener(onClickListenerbtn);
        btnweb2.setOnClickListener(onClickListenerbtn);
        btndownload1.setOnClickListener(onClickListenerbtn);
        btndownload2.setOnClickListener(onClickListenerbtn);
    }

    View.OnClickListener onClickListenerbtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Intent intent;
            switch (view.getId()) {
                case R.id.btnweb:
//                    file:///android_asset/pms/html/index.html
//                    file:///android_asset/test/webapp-infomation-edit.html
                    intent = new Intent(MainActivity.this, CordovaWebActivity.class);
                    intent.putExtra("url", "http://220.194.33.92/defaultroot/clientview/dealfile/indexAndroid.html");
                    startActivity(intent);
                    break;
                case R.id.btnweb2:
                    Intent intent1=new Intent(MainActivity.this, SignActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.btndownload://下载
                    fileController = new FileController("http://www.ishangban.com/pms/IOS_WEB.zip", Environment.getExternalStorageDirectory().getAbsolutePath());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0);
                            if (fileController.streamDownLoadFile("android.zip")) {
                                handler.sendEmptyMessage(1);

                                File file=new File(Environment.getExternalStorageDirectory()+"/android.zip");

                                try {
                                    fileController.upZipFile(file, Environment.getExternalStorageDirectory().getAbsolutePath());
                                    handler.sendEmptyMessage(3);
                                }
                                catch (Exception e)
                                {e.printStackTrace();
                                    handler.sendEmptyMessage(4);}
                            }

                            else
                                handler.sendEmptyMessage(2);
                        }
                    }).start();
                    break;
                case R.id.btndownload2:
                    break;
            }
        }
    };




    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    state.setText("开始下载");
                    break;
                case 1:
                    state.setText("下载完成,开始解压");
                    break;
                case 2:
                    state.setText("下载错误");
                    break;
                case 3:
                    state.setText("解压完成");
                    break;
                case 4:
                    state.setText("解压失败");
                    break;
            }
        }
    };

}

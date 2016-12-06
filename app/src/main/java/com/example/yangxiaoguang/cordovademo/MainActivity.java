package com.example.yangxiaoguang.cordovademo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.cordova.CordovaWebView;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button btnweb1,btnweb2,btndownload1,btndownload2;
    TextView state,state2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state= (TextView)findViewById(R.id.state);
        state2= (TextView)findViewById(R.id.state2);

        btnweb1 = (Button)findViewById(R.id.btnweb);
        btnweb2 = (Button)findViewById(R.id.btnweb2);
        btndownload1 =(Button)findViewById(R.id.btndownload);
        btndownload2 =(Button)findViewById(R.id.btndownload2);

        btnweb1.setOnClickListener(onClickListenerbtn);
        btnweb2.setOnClickListener(onClickListenerbtn);
        btndownload1.setOnClickListener(onClickListenerbtn);
        btndownload2.setOnClickListener(onClickListenerbtn);
    }

    View.OnClickListener onClickListenerbtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent;
            switch (view.getId())
            {
                case R.id.btnweb:
                    intent = new Intent(MainActivity.this,CordovaWebActivity.class);
                    intent.putExtra("url","file:///android_asset/pms/html/index.html");
                    startActivity(intent);
                    break;
                case R.id.btnweb2:
                    break;
                case R.id.btndownload:
                    break;
                case R.id.btndownload2:
                    break;
            }
        }
    };
}

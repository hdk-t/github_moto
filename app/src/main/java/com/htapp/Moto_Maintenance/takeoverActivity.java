package com.htapp.Moto_Maintenance;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class takeoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takeover);

        findViewById(R.id.export).setOnClickListener(expButton);
    }

    //エクスポートボタンを押した時の処理
    View.OnClickListener expButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //エクスプローラを開いて移動先を指定してもらう

        }
    };
}
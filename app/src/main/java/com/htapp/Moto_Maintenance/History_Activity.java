package com.htapp.Moto_Maintenance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class History_Activity extends AppCompatActivity {

    String moto_data;
    public static final String MOTO_DATA2 = "com.example.motomaintenance.DATA";
    public static final String MOTO_DATA3 = "com.example.motomaintenance.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        History();
    }

    //画面が戻ってきた際にレイアウトをリフレッシュ
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.history);

        History();
    }

    //「新しい記録を追加」ボタンを押した際の処理
    View.OnClickListener mente_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //「新しいバイクを追加」画面(Record_Activity)に遷移して、History_ActivityのListViewのポジションを送る。
            Intent intent = getIntent();
            int num = intent.getIntExtra("ODO_NUM",0);

            Intent intent2 = new Intent(getApplication(), Record_Activity.class);
            intent2.putExtra(MOTO_DATA3, moto_data);
            intent2.putExtra("ODO_NUM2", num);
            startActivity(intent2);
        }
    };

    //以下メソッド

    //画面を表示するメソッド
    public void History() {

        ListView listView = findViewById(R.id.ListView1);
        findViewById(R.id.new_maintenanse).setOnClickListener(mente_plus);

        Intent intent = getIntent();
        moto_data = intent.getStringExtra(MainActivity.MOTO_DATA);

        final String[] day = readFile(moto_data + "day.txt");
        final String[] odo = readFile(moto_data + "odo.txt");
        final String[] cont = readFile(moto_data + "cont.txt");

        if(day != null || odo != null || cont != null ) {

            HistoryAdapter adapter = new HistoryAdapter(getApplicationContext(), R.layout.history_list, day, odo, cont);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent2 = getIntent();
                    int num = intent2.getIntExtra("ODO_NUM",0);

                    Intent intent = new Intent(getApplication(), Maintenance_Data_Activity.class);
                    intent.putExtra(MOTO_DATA2,moto_data);
                    intent.putExtra("NUM",position);
                    intent.putExtra("NUM3",num);
                    startActivity(intent);
                }
            });
        }
    }

    //txtファイル内の一行目を読み込み「,」で区切りString配列に格納するメソッド
    public String[] readFile(String file) {

        String str;
        String[] tokens1 = null;

        try (FileInputStream fileInputStream = openFileInput(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {

            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                str = lineBuffer;

                tokens1 = str.split(",");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens1;
    }
}
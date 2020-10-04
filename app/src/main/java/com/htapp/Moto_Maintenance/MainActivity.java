package com.htapp.Moto_Maintenance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    public static final String MOTO_DATA = "com.example.motomaintenance.DATA";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Main();
    }

    //画面が戻ってきた際にレイアウトをリフレッシュ
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);

        Main();
    }

    //「バイクを追加ボタン」を押したときの処理
    View.OnClickListener MotoPlus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //新しいバイクを追加(NewMoto)に画面遷移
            Intent intent3 = new Intent(getApplication(), NewMoto.class);
            startActivity(intent3);
        }
    };

    //メニューバーからデータ引き継ぎを押した時の処理
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.movedata:
                Intent intent = new Intent(getApplication(), takeoverActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    //以下メソッド

    //画面を表示するメソッド
    public void Main() {

        ListView listView = findViewById(R.id.ListView1);
        findViewById(R.id.plus_moto).setOnClickListener(MotoPlus);

        final String[] model = readFile("md.txt");
        String[] manufacturer = readFile("mf.txt");
        String[] now_odo = readFile("no.txt");

        if (model != null || manufacturer != null) {

            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), R.layout.motolist, model, manufacturer, now_odo);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (view.getId()) {

                        //ListViewを押したときの処理
                        case R.id.machine_list:

                            //メンテナンス履歴(History_Activity)に画面遷移し車種名とListViewのポジションを送る
                            Intent intent = new Intent(getApplication(), History_Activity.class);
                            intent.putExtra(MOTO_DATA, model[position]);
                            intent.putExtra("ODO_NUM", position);
                            startActivity(intent);
                            break;

                        //「バイク情報」ボタンを押したときの処理
                        case R.id.info:

                            //バイク情報(Moto_info)に画面遷移しListViewのポジションを送る
                            Intent intent2 = new Intent(getApplication(), Moto_info.class);
                            intent2.putExtra("MOTO_NUM", position);
                            startActivity(intent2);
                            break;

                    }
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



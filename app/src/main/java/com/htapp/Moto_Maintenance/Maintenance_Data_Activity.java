package com.htapp.Moto_Maintenance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Maintenance_Data_Activity extends AppCompatActivity {

    public static final String MOTO_DATA2 = "com.example.motomaintenance.DATA";

    String moto_data;
    int num;

    TextView hizuke;
    TextView odo;
    TextView cont;
    TextView comment;
    Button editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_data);

        editbtn = (Button) findViewById(R.id.button2);
        findViewById(R.id.button2).setOnClickListener(edit_button);
        findViewById(R.id.del).setOnClickListener(button_del);

        hizuke = (TextView) findViewById(R.id.day);
        odo = (TextView) findViewById(R.id.odometer2);
        cont = (TextView) findViewById(R.id.maintenancelist2);
        comment = (TextView) findViewById(R.id.comment);

        Intent intent = getIntent();
        moto_data = intent.getStringExtra(History_Activity.MOTO_DATA2);
        num = intent.getIntExtra("NUM", 0);

        //TextViewに値を表示
        output();

    }

    //編集ボタンを押した時の処理
    View.OnClickListener edit_button = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent2 = getIntent();
            int num2 = intent2.getIntExtra("NUM3", 0);

            //「メンテナンス情報を編集」(Maintenance_Edit)画面に遷移して、History_Activity、MainActivityから受け取ったのListViewのポジションと車種名を送りアクティビティを終了する
            Intent intent = new Intent(getApplication(), Maintenance_Edit.class);
            intent.putExtra("MENTE_NUM", num);
            intent.putExtra("NUM4", num2);
            intent.putExtra(MOTO_DATA2, moto_data);
            startActivity(intent);

            finish();
        }
    };

    //削除ボタンを押した時の処理
    View.OnClickListener button_del = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            alert_del();
        }
    };

    //ＯＫ　or キャンセルのアラートを表示してＯＫであれば消去する。
    public void alert_del() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("本当にこの整備記録を削除しますか？");
        builder.setPositiveButton("削除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = getIntent();
                int num = intent.getIntExtra("NUM", 0);

                String[] str = readFile(moto_data + "day.txt");

                //現在のメンテナンスデータ(日付)の要素数を読み取り、それが１か判定
                if(str.length == 1){

                    //要素数が１だったらファイルごと削除する
                    deleteFile(moto_data + "day.txt");
                    deleteFile(moto_data + "odo.txt");
                    deleteFile(moto_data + "cont.txt");
                    deleteFile(moto_data + "comment.txt");

                } else {

                    //要素数１がでなければ要素のみ削除する
                    del(moto_data + "day.txt", num);
                    del(moto_data + "odo.txt", num);
                    del(moto_data + "cont.txt", num);
                    del(moto_data + "comment.txt", num);

                }
                    finish();

                    Toast.makeText(Maintenance_Data_Activity.this, "整備記録を削除しました", Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    //入力したtxtファイル内の要素を削除するメソッド
    public void del(String txt, int num) {

        String[] data = readFile(txt);

        List<String> list = new ArrayList<String>(Arrays.asList(data));
        list.remove(num);

        String[] data2 = (String[]) list.toArray(new String[list.size()]);

        try (FileOutputStream fo = openFileOutput(txt, Context.MODE_PRIVATE)) {
            OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            for (int count = 0; count < list.size(); count++) {

                pw.append(data2[count]);
                pw.append(",");

            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TextViewに値を表示させるメソッド
    private void output () {

        final String[] day = readFile(moto_data + "day.txt");
        final String[] odo2 = readFile(moto_data + "odo.txt");
        final String[] cont2 = readFile(moto_data + "cont.txt");
        final String[] comment2 = readFile(moto_data + "comment.txt");

        int odoi = Integer.parseInt(odo2[num]);
        String odo3 = String.format("%,d",odoi) + " Km";

        String[] strs = comment2[num].split("~/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strs.length ; i++){
            sb.append(strs[i] + "\n");
        }
        String comment3 = new String(sb);

        hizuke.setText(day[num]);
        odo.setText(odo3);
        cont.setText(cont2[num]);
        comment.setText(comment3);
    }

    //txtファイル内の一行目を読み込み「,」で区切りString配列に格納するメソッド
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

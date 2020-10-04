package com.htapp.Moto_Maintenance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
public class Moto_info extends AppCompatActivity {

    int num;

    TextView mft;
    TextView mdl;
    TextView now_odo;
    TextView get_odo;
    TextView get_day;
    TextView moto_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motoinfo);

        mft = (TextView) findViewById(R.id.manufacturer);
        mdl = (TextView) findViewById(R.id.model);
        now_odo = (TextView) findViewById(R.id.now_odo);
        get_odo = (TextView) findViewById(R.id.get_odo);
        get_day = (TextView) findViewById(R.id.get_day);
        moto_comment = (TextView) findViewById(R.id.moto_comment);
        findViewById(R.id.button).setOnClickListener(edit_view);
        findViewById(R.id.del).setOnClickListener(button_del);

        final String[] mf = readFile("mf.txt");
        final String[] md = readFile("md.txt");
        final String[] no = readFile("no.txt");
        final String[] go = readFile("go.txt");
        final String[] sd = readFile("sd.txt");
        final String[] com = readFile("com.txt");

        Intent intent = getIntent();
        num = intent.getIntExtra("MOTO_NUM", 0);

        //バイク情報をTextViewへ出力
        String strodo = no[num];
        int odoi;
        String no2;

        //走行距離データが入っていない場合int型に変換するとエラーが発生するため、それを判定
        if(strodo.equals(" ")){
            no2 = ("");
        }else{
            //走行距離を千の値で,で区切る処理
            odoi = Integer.parseInt(no[num]);
            no2 = String.format("%,d",odoi) + " Km";
        }

        //走行距離データが入っていない場合int型に変換するとエラーが発生するため、それを判定
        String strodo2 = (go[num]);
        int odoi2;
        String go2;

        if(strodo2.equals(" ")){
            go2 = ("");
        }else{
            //走行距離を千の値で,で区切る処理
            odoi2 = Integer.parseInt(go[num]);
            go2 = String.format("%,d",odoi2) + " Km";
        }

        //コメントデータを読み取り、変換した改行コード(~/)を本来の\nに変換する処理
        String[] strs = com[num].split("~/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strs.length ; i++){
            sb.append(strs[i] + "\n");
        }
        String com2 = new String(sb);

        mft.setText(mf[num]);
        mdl.setText(md[num]);
        now_odo.setText(no2);
        get_odo.setText(go2);
        get_day.setText(sd[num]);
        moto_comment.setText(com2);

    }

    //バイク情報を編集ボタンを押したときの処理
    View.OnClickListener edit_view = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //バイク情報を編集画面に遷移して、MainActivityのListViewのポジションを送りアクティビティを終了する
            Intent intent = new Intent(getApplication(), Edit_Activity.class);
            intent.putExtra("MOTO_NUM", num);
            startActivity(intent);

            finish();
        }
    };

    //「削除」ボタンを押したときの処理
    View.OnClickListener button_del = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = getIntent();
            int num = intent.getIntExtra("MOTO_NUM",0);

            String[] md = readFile("md.txt");

            //メンテナンス記録データを削除
            deleteFile(  md[num] + "day.txt" );
            deleteFile(  md[num] + "odo.txt" );
            deleteFile(  md[num] + "cont.txt" );
            deleteFile(  md[num] + "comment.txt" );

            //バイクデータを削除
            alert_del();

        }
    };

    //ＯＫ　or キャンセルのアラートを表示してＯＫであれば消去する。
    public void alert_del(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("本当にこのバイクを削除しますか？\n(メンテナンス記録も削除されます)");
        builder.setPositiveButton("削除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = getIntent();
                int num = intent.getIntExtra("MOTO_NUM",0);

                del("mf.txt",num);
                del("md.txt",num);
                del("no.txt",num);
                del("go.txt",num);
                del("sd.txt",num);
                del("com.txt",num);

                finish();

                Toast.makeText(Moto_info.this, "バイクを削除しました", Toast.LENGTH_LONG).show();
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

    //以下メソッド

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
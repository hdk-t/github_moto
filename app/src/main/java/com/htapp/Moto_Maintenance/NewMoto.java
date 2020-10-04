package com.htapp.Moto_Maintenance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NewMoto extends AppCompatActivity {

    EditText new_mft;
    EditText new_mdl;
    EditText now_odo;
    EditText get_odo;
    EditText showDate;
    EditText comment;
    String[] mdl_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmoto);

        new_mdl = (EditText) findViewById(R.id.new_model);
        new_mft = (EditText) findViewById(R.id.new_manufacturer);
        findViewById(R.id.bike_plus).setOnClickListener(Button_register);
        findViewById(R.id.showDate).setOnClickListener(Button_calendar);
        now_odo = (EditText) findViewById(R.id.now_odo);
        get_odo = (EditText) findViewById(R.id.get_odo);
        showDate = (EditText) findViewById(R.id.showDate);
        comment = (EditText) findViewById(R.id.comment);
    }

    //取得日のEditTextを押したときの処理
    View.OnClickListener Button_calendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //カレンダーダイアログを発生させて、EditTextに入力する
            final Calendar date = Calendar.getInstance();
            //DatePickerDialogインスタンスを取得
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    NewMoto.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //setした日付を取得して表示
                            showDate.setText(String.format("%d 年 %02d 月 %02d 日", year, month + 1, dayOfMonth));
                        }
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DATE)
            );
            //dialogを表示
            datePickerDialog.show();
        }
    };

    //「バイクを登録」ボタンを押したときの処理
    View.OnClickListener Button_register = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //各EditTextを空欄で登録した際に発生するエラーを防ぐための処理を行い入力データを読み取る↓
            String mft = new_mft.getText().toString();
            if(mft.equals("")){
                mft = (" ");
            }
            String mdl = new_mdl.getText().toString();

            String no = now_odo.getText().toString();
            if(no.equals("")){
                no = (" ");
            }
            String go = get_odo.getText().toString();
            if(go.equals("")){
                go = (" ");
            }
            String sd = showDate.getText().toString();
            if(sd.equals("")){
                sd = (" ");
            }
            //ここまで↑

            //コメント入力に改行を入れた場合に発生するエラーを防ぐための処理
            SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
            String com = sp.toString();

            //改行(\n)を影響のない記号(~/)に変換して読み取る
            String[] strs = com.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < strs.length ; i++){
                sb.append(strs[i] + "~/");
            }
            String com2 = new String(sb);

            //ファイルが存在しなかった場合に発生するエラーを防ぐためファイルが存在するか確認
            File file_check = new File("md.txt");
            if (!file_check.exists()) {
                mdl_check = readFile("md.txt");
            }

            if(mdl_check == null || !Arrays.asList(mdl_check).contains(mdl)){

            //車種名が入力されているか確認
            if (mdl.equals("")){
                //入力されていない場合はアラートを発生させる
                alert("車種名は必ず入力して下さい");

             //メーカー名、車種名、コメントに「,」が含まれていないかを判定（保存データを「,」で区切っており、「,」を入力されてしまうとデータが混在してしまうため）
            }else if(mft.contains(",") || mdl.contains(",") || com2.contains(",")){
                //メーカー名、車種名、コメントに「,」が含まれていた場合のアラートを発生
                alert("入力欄に「 , 」を使用することはできません");

            }else{

                //上記の条件を通過した場合にこれらのデータをtxtファイルに保存する
                export_txt("mf.txt", mft);
                export_txt("md.txt", mdl);
                export_txt("no.txt", no);
                export_txt("go.txt", go);
                export_txt("sd.txt", sd);
                export_txt("com.txt", com2);

                Toast.makeText(NewMoto.this, "新しいバイクが登録されました", Toast.LENGTH_LONG).show();

                finish();
            }

            }else if(Arrays.asList(mdl_check).contains(mdl)) {
                //車種名が重複した場合に発生させるアラート
                alert("同じ車種名を登録することはできません\n(番号等を語尾に付けて下さい)");

            }
        }
    };

    //以下メソッド

    //引数として渡した文字列のアラートメッセージを発生。選択肢はＯＫのみ
    public void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ボタンをクリックしたときの動作
                    }
                });
        builder.show();
    }

    //txtファイルfileのファイル内の左側にstrを[,]で区切り追加するメソッド
    public void export_txt(String file, String str) {

        File file_check = new File(file);
        if (file_check.exists()) {

            try (FileOutputStream fo = openFileOutput(file, Context.MODE_PRIVATE);) {
                OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
                PrintWriter pw = new PrintWriter(osw);

                pw.append(str);
                pw.append(",");

                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            try (FileOutputStream fo = openFileOutput(file, Context.MODE_APPEND);) {
                OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
                PrintWriter pw = new PrintWriter(osw);

                pw.append(str);
                pw.append(",");

                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

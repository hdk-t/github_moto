package com.htapp.Moto_Maintenance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Record_Activity extends AppCompatActivity {

    EditText showDate;
    EditText odometer;
    EditText comment;
    Spinner maintenancelist;
    String moto_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        findViewById(R.id.showDate).setOnClickListener(Button_calendar);
        showDate = (EditText) findViewById(R.id.showDate);
        odometer = (EditText) findViewById(R.id.odometer2);
        comment = (EditText) findViewById(R.id.comment);
        maintenancelist = (Spinner) findViewById(R.id.maintenancelist2);
        findViewById(R.id.save).setOnClickListener(button_save);
    }

        //日付のEditTextを押したときの処理
        View.OnClickListener Button_calendar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar date = Calendar.getInstance();
                //DatePickerDialogインスタンスを取得
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Record_Activity.this,
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

    //「保存」ボタンを押したときの処理
    View.OnClickListener button_save = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {

            String time = showDate.getText().toString();
            String odo = odometer.getText().toString();
            String cont = maintenancelist.getSelectedItem().toString();

            //コメント入力に改行を入れた場合に発生するエラーを防ぐための処理
            SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
            String comment2 = sp.toString();

            //改行(\n)を影響のない記号(~/)に変換して読み取る
            String[] strs = comment2.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < strs.length ; i++){
                sb.append(strs[i] + "~/");
            }
            String comment3 = new String(sb);

            //日付と走行距離が入力されているか判定
            if (time.equals("") || odo.equals("")) {
                //日付と走行距離が入力されていなかったらアラートを発生させる
                alert("日付と走行距離は必ず入力して下さい");

                //コメントに「,」が含まれていないかを判定(保存データを「,」で区切っており、「,」を入力されてしまうとデータがしてしまうため)
            }else if(comment3.contains(",")){
                //コメントに「,」が含まれていた場合のアラートを発生
                alert("入力欄に「 , 」を使用することはできません");

            } else {

                //メイン画面の「現在の走行距離」を更新する処理
                Intent intent = getIntent();
                int num = intent.getIntExtra("ODO_NUM2",0);

                String strnowodo = (readFile("no.txt")[num]);
                if(!strnowodo.equals(" ")) {
                    int odoi = Integer.parseInt(odo);
                    int odon = Integer.parseInt(strnowodo);
                    //保存した走行距離が現在の走行距離より大きいか判定し、大きければ現在の走行距離に上書きする
                    if (odoi > odon) {
                        edit2("no.txt", odo, num);
                    }
                } else {
                    edit2("no.txt", odo, num);
                }

                //メンテナンスデータを保存し終了する
                Intent intent2 = getIntent();
                moto_data = intent2.getStringExtra(History_Activity.MOTO_DATA3);

                export_txt(moto_data + "day.txt", time);
                export_txt(moto_data + "odo.txt", odo);
                export_txt(moto_data + "cont.txt", cont);
                export_txt(moto_data + "comment.txt", comment3);

                Toast.makeText(Record_Activity.this, "整備内容が保存されました", Toast.LENGTH_LONG).show();

                finish();

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

    //txtファイル内の一行目を読み込み「,」で区切りString配列に格納するメソッド
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void export_txt(String file, String str) {

        File file_check = new File(this.getFilesDir().getAbsolutePath() + "/" + file);
        if (! file_check.exists()) {

            try (FileOutputStream fo = openFileOutput(file, Context.MODE_PRIVATE);) {
                OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
                PrintWriter pw = new PrintWriter(osw);

                pw.print(str);
                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            String[] str2 = readFile(file);

            try (FileOutputStream fo = openFileOutput(file, Context.MODE_PRIVATE);) {
                OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
                PrintWriter pw = new PrintWriter(osw);

                StringBuilder sb = new StringBuilder();

                for(int count = str2.length ; count > 0 ; count--) {

                    sb.insert(0,",");
                    sb.insert(0, str2[count - 1]);

                }

                sb.insert(0,",");
                sb.insert(0,str);

                pw.println(sb.toString());

                pw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //ファイルからＳｔｒｉｎｇ配列データを読み取って、要素を置き換えて、右に追記して再度出力する
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void edit2 (String txt, String str, int num) {

        String[] mf = readFile(txt);

        List<String> list = new ArrayList<String>(Arrays.asList(mf));
        list.set(num, str);

        String[] mf2 = (String[]) list.toArray(new String[list.size()]);

        try (FileOutputStream fo = openFileOutput(txt, Context.MODE_PRIVATE)) {
            OutputStreamWriter osw = new OutputStreamWriter(fo, "UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            for (int count = 0; count < list.size(); count++) {

                pw.append(mf2[count]);
                pw.append(",");

            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


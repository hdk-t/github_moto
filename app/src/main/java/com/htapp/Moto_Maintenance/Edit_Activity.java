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

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Edit_Activity extends AppCompatActivity {

    EditText new_mft;
    EditText new_mdl;
    EditText now_odo;
    EditText get_odo;
    EditText showDate;
    EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        new_mdl = (EditText) findViewById(R.id.new_model);
        new_mft = (EditText) findViewById(R.id.new_manufacturer);
        now_odo = (EditText) findViewById(R.id.now_odo);
        get_odo = (EditText) findViewById(R.id.get_odo);
        showDate = (EditText) findViewById(R.id.showDate);
        comment = (EditText) findViewById(R.id.comment);
        findViewById(R.id.bike_plus).setOnClickListener(button_plus);
        findViewById(R.id.showDate).setOnClickListener(button_calendar);

        Intent intent = getIntent();
        int num = intent.getIntExtra("MOTO_NUM",0);

        final String[] mf = readFile("mf.txt");
        final String[] md = readFile("md.txt");
        final String[] no = readFile("no.txt");
        final String[] go = readFile("go.txt");
        final String[] sd = readFile("sd.txt");
        final String[] com = readFile("com.txt");

        //バイク情報をEditTextに出力
        String no2 = (no[num]);
        if(no2.equals(" ")) {
            no2 = ("");
        }

        String go2 = (go[num]);
        if(go2.equals(" ")) {
            go2 = ("");
        }
        //コメントデータを読み取り、変換した改行コード(~/)を本来の\nに変換する処理
        String[] strs = com[num].split("~/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < strs.length ; i++){
            sb.append(strs[i] + "\n");
        }
        String com2 = new String(sb);

        if(!mf[num].equals(" ")) {
            new_mft.setText(mf[num]);
        }
        new_mdl.setText(md[num]);
        now_odo.setText(no2);
        get_odo.setText(go2);
        if(!sd[num].equals(" ")) {
            showDate.setText(sd[num]);
        }
        comment.setText(com2);

    }
    //取得日のEditTextを押したときの処理
    View.OnClickListener button_calendar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar date = Calendar.getInstance();
            //DatePickerDialogインスタンスを取得
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Edit_Activity.this,
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

    View.OnClickListener button_plus = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {

            String mft = new_mft.getText().toString();
            String mdl = new_mdl.getText().toString();

            //コメント入力に改行を入れた場合に発生するエラーを防ぐための処理
            SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
            String com = sp.toString();

            //改行(\n)を影響のない記号(~/)に変換しておき読み取る
            String[] strs = com.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < strs.length ; i++){
                sb.append(strs[i] + "~/");
            }
            String com2 = new String(sb);

            Intent intent = getIntent();
            int num = intent.getIntExtra("MOTO_NUM",0);

            String[] md = readFile("md.txt");
            //車種名を利用しメンテナンスデータを管理しているため、重複していないかをチェック
            if (!Arrays.asList(md).contains(mdl) || md[num].equals(mdl)) {
                //車種名が入力されているか確認
                if (mdl.equals("")) {
                    //入力されていない場合はアラートを発生させる
                    alert("車種名は必ず入力して下さい");

                    //保存データを「,」で区切っており、「,」を入力されてしまうとデータが混在してしまうため、メーカー名、車種名、コメントに「,」が入力されていないかを判定
                }else if(mft.contains(",") || mdl.contains(",") || com2.contains(",")){
                    //メーカー名、車種名、コメントに「,」が入っていた場合のアラートを発生
                    alert("入力欄に「 , 」を使用することはできません");

                } else {

                    try {
                        //各メンテナンスデータのファイル名を変更する
                        edit_name(md[num], mdl);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //上記の条件を通過した場合にこれらのデータを編集する
                    alert_edit();

                }

            } else {
                //車種名が重複した場合に発生させるアラート
                alert("同じ車種名を登録することはできません");

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

    //アラートを発生させ「変更」を選択した場合に変更処理を」行う
    public void alert_edit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("確認");
        builder.setMessage("このバイク情報を変更しますか？");
        builder.setPositiveButton("変更", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = getIntent();
                int num = intent.getIntExtra("MOTO_NUM",0);

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

                SpannableStringBuilder sp = (SpannableStringBuilder)comment.getText();
                String com = sp.toString();

                String[] strs = com.split("\n");
                StringBuilder sb = new StringBuilder();
                for (int i = 0 ; i < strs.length ; i++){
                    sb.append(strs[i] + "~/");
                }
                String com2 = new String(sb);

                edit2("mf.txt", mft, num);
                edit2("md.txt", mdl, num);
                edit2("no.txt", no, num);
                edit2("go.txt", go, num);
                edit2("sd.txt", sd, num);
                edit2("com.txt", com2, num);

                finish();

                Toast.makeText(Edit_Activity.this, "変更が保存されました", Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    //ファイルからＳｔｒｉｎｇ配列データを読み取って、要素を置き換えて、右に追記して再度出力する
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

    //ファイル名変更
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void edit_name(String oldmdl, String newmdl) throws IOException {

        File fOld1 = new File(this.getFilesDir().getAbsolutePath() + "/" + oldmdl + "day.txt");
        File fOld2 = new File(this.getFilesDir().getAbsolutePath() + "/" + oldmdl + "odo.txt");
        File fOld3 = new File(this.getFilesDir().getAbsolutePath() + "/" + oldmdl + "cont.txt");
        File fOld4 = new File(this.getFilesDir().getAbsolutePath() + "/" + oldmdl + "comment.txt");

        File fNew1 = new File(this.getFilesDir().getAbsolutePath() + "/" + newmdl + "day.txt");
        File fNew2 = new File(this.getFilesDir().getAbsolutePath() + "/" + newmdl + "odo.txt");
        File fNew3 = new File(this.getFilesDir().getAbsolutePath() + "/" + newmdl + "cont.txt");
        File fNew4 = new File(this.getFilesDir().getAbsolutePath() + "/" + newmdl + "comment.txt");

        if (fOld1.exists()) {
            fOld1.renameTo(fNew1);
        } else {
        }

        if (fOld2.exists()) {
            fOld2.renameTo(fNew2);
        } else {
        }

        if (fOld3.exists()) {
            fOld3.renameTo(fNew3);
        } else {
        }

        if (fOld4.exists()) {
            fOld4.renameTo(fNew4);
        } else {
        }
    }

    //txtファイルを読み込んでString配列に格納するメソッド
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


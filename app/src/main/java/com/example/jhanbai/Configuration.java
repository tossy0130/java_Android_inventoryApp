package com.example.jhanbai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Configuration extends AppCompatActivity {

    private Button master_download_btn;
    private Button h_master_download_btn;
    private Button db_create_btn;

    private Button delete_btn_02;

    //----------DB 接続用 ----------
    private TestOpenHelper helper;
    private SQLiteDatabase db;

    private WebView webview;


    private ImageButton back_btn_002;

    //---------- 再起動設定
    private Context context;
    private int waitperiod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        //-------- 再起動設定
        context = getApplicationContext();
        waitperiod = 3000;


        // コンポーネント　接続
        finde();

        webview = new WebView(this);

        // Main activity　へ　戻るボタン　ヘッダー
        back_btn_002.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 戻る
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);

                finish();

            }
        });


        // データベース作成
        db_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


        // 最新　マスター　受信　ボタン
        master_download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webview.reload();

                return;
            }
        }); //---------------- マスター　CSV download END

        h_master_download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ************ 商品　マスターダウロード *********



                //--------------- アラートダイヤログ　削除　設定 ---------------//
                // タイトル
                TextView titleView;
                titleView = new TextView(Configuration.this);
                titleView.setText("商品マスターのダウンロード開始します。");
                titleView.setTextSize(20);
                titleView.setTextColor(Color.WHITE);
                titleView.setBackgroundColor(getResources().getColor(R.color.midori_01));
                titleView.setPadding(20, 20, 20, 20);
                titleView.setGravity(Gravity.CENTER);


                //-------------- アラートログの表示 開始 -------------- //
                AlertDialog.Builder bilder = new AlertDialog.Builder(Configuration.this);

                // ダイアログの項目
                bilder.setCustomTitle(titleView);
                //  bilder.setTitle("削除");
                bilder.setMessage("商品マスターのダウンロードを行っています。しばらくボタンの色が変わり、完了メッセージが" +
                        "表示されるまで、\n*** このままお待ちください。***");


                //------- OK の時の処理 ----------//
                bilder.setPositiveButton("メッセージを確認したので閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //----------------- ダイアログを閉じる
                        dialog.dismiss();
                        return;

                    }
                });

                // ダイアログの表示
                AlertDialog dialog = bilder.create();
                dialog.show();

            }
        });





        //------------------ 設定　削除　ボタン ------------------

        delete_btn_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //--------------- アラートダイヤログ　削除　設定 ---------------//
                // タイトル
                TextView titleView;
                titleView = new TextView(Configuration.this);
                titleView.setText("取得ファイルを全て削除する");
                titleView.setTextSize(20);
                titleView.setTextColor(Color.WHITE);
                titleView.setBackgroundColor(getResources().getColor(R.color.back_color_03));
                titleView.setPadding(20, 20, 20, 20);
                titleView.setGravity(Gravity.CENTER);


                //-------------- アラートログの表示 開始 -------------- //
                AlertDialog.Builder bilder = new AlertDialog.Builder(Configuration.this);

                // ダイアログの項目
                bilder.setCustomTitle(titleView);
                //  bilder.setTitle("削除");
                bilder.setMessage("ダウンロードした設定ファイルを全て削除しますか？");

                //------- OK の時の処理 ----------//
                bilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //------------ 削除　処理　END　----------------

                    }
                });

                bilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });


                // ダイアログの表示
                AlertDialog dialog = bilder.create();
                dialog.show();

            }
        });

    }

    // コンポーネント　接続
    private void finde() {

        // ヘッダーボタン
        back_btn_002 = (ImageButton) findViewById(R.id.back_btn_002);

        master_download_btn =(MaterialButton) findViewById(R.id.master_download_btn);
        h_master_download_btn = (MaterialButton) findViewById(R.id.h_master_download_btn);
        db_create_btn = (MaterialButton) findViewById(R.id.db_create_btn);
        delete_btn_02 = (MaterialButton) findViewById(R.id.delete_btn_02);


    }






}

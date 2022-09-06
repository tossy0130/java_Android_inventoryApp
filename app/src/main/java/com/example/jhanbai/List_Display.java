package com.example.jhanbai;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class List_Display extends AppCompatActivity {

   // private ListViewAdapter listViewAdapter;
    private ListView listView;
    private String selectedItem;

    private ListView mListView;

    ArrayList<ListData> objects = new ArrayList<ListData>();

    //----------DB 接続用 ----------
    private TestOpenHelper helper;
    private SQLiteDatabase db;

    //---------- アカウントデータ　送受信用 ----------//
    private ImageButton back_btn_03;
    private TextView user_num_03;

    private String rr_accont;
    private String rr_acount_02;

    private ListData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__display);

        //    View header = (View)getLayoutInflater().inflate(R.layout.heder_01,null);

        //--------------- 矢印　戻るボタン　処理 --------------
        back_btn_03 = (ImageButton) findViewById(R.id.back_btn_03);
        user_num_03 = (TextView) findViewById(R.id.user_num_03);

        if(getIntent() != null) {
            rr_accont = getIntent().getStringExtra("rr_id");
            rr_acount_02 = getIntent().getStringExtra("rr_name");
        }

        if(rr_acount_02 != null) {
            user_num_03.setText(rr_accont + " " + "：" + " " + rr_acount_02);
        } else {
            user_num_03.setText(rr_accont + " " + "：" + " " + "未登録ユーザー");
        }

        //--------------- 矢印　戻るボタン　処理 END -------------->

        //------------------------------------- CSV 読み込み処理 ------------------------- Start

        final CsvReader parser = new CsvReader();
        parser.reader(getApplicationContext());

        //listView = new ListView(this);

        mListView = (ListView) findViewById(R.id.listView_01);

        final ListViewAdapter listViewAdapter = new ListViewAdapter(this, R.layout.list_item, parser.objects);
        mListView.setAdapter(listViewAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 引数　取得
                System.out.println("リスト　：　View" + view);
                System.out.println("リスト　：　position" + position);
                System.out.println("リスト　：　id" + id);


                listViewAdapter.remove(position);

                listViewAdapter.notifyDataSetChanged();

            }
        });



        //--------------- 矢印　戻るボタンを押したときの処理 ------------------

        back_btn_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アカウント ID ,name　を返す
                Intent intent = new Intent();

                if(user_num_03.getText() != null) {
                    String str = user_num_03.getText().toString();

                    intent.putExtra("ld_id",str);

                    // 返却したい結果ステータスをセットする
                    setResult(Activity.RESULT_OK, intent);

                    // activity　終了
                    finish();
                }

            }
        });

    }



}

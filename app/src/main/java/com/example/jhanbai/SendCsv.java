package com.example.jhanbai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SendCsv extends AppCompatActivity
                implements View.OnClickListener{

    //--------- Send_TB_02 テーブル　から　データ取得用 ---------//
    // DBAdapter コンストラクタ　用
    private DBAdapter dbAdapter;
    // 参照する DB のカラムを入れる
    private String[] columns = null;

    private Common common;

    // リスト　
    private MyListItem myListItem;

    //------------- ヘッダー処理 ---------------
    private ImageButton back_btn_10; // 戻る　矢印　ボタン
    private TextView user_num_10; // 担当者コード　表示用　ビュー
    private String ac_id;
    private String ac_name;

    private String send_num;



    //------------- SQL 出力用　テスト
    private Button se_btn;
    private TextView test_send_view;

   //-------------- カレンダー用　変数
    private String csv_date_01;
    public static String send_csv_file; // ---------- 保存　& 送信　CSV ファイル　格納
    private static String send_csv_file_path;
    private static String save_csv_file;


    //------------ ディレクトリ
    // ディレクトリ　取得
    private  File dir;
    private  File target_csv;

    private boolean file_Flg;

    private String file_num;

    private Button pos_go_btn;
    private TextView test_send_view_03;

    //------------- UploadTask オブジェクト
    private UploadTask task;

    private String send_csv_filename;

    //******* 担当コードから、部署コード取得　ハッシュ
    private HashMap<String, String> Tantou_GET_Busho_H = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_csv);

        // コンポーネント　findeviewbyid  関数
        findView();

        // ヘッダー情報　担当者コード　取得
        header_ac();

        // イベントリスナー　登録
        findViewById(R.id.back_btn_10).setOnClickListener(this);
        findViewById(R.id.se_btn).setOnClickListener(this);

        // グローバル　変数
        common = (Common) getApplication();


        // DBAdapter コンストラクタ　作成
        dbAdapter = new DBAdapter(getApplicationContext());

        // ディレクトリ
        dir = new File(String.valueOf(getFilesDir()));

        file_Flg = true;

    }

    public void onClick(View v) {
        switch (v.getId()) {

            //---------- 戻るボタン 処理 ---------
            case R.id.back_btn_10:

                Intent intent = new Intent();
                //----- 担当名が空じゃなかった処理
                if(ac_name != null) {
                    intent.putExtra("send_csv", ac_name);
                    intent.putExtra("send", file_num); // ------- ファイルが送信されてなかったら 0:  1: 送信されている。

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

                break;

            //---------------- DB から　ファイル　作成　--------------
            case R.id.se_btn:

                // CSV ファイル名　作成
                send_csv_name();

                // CSV ファイル　保存
                ReadSend_tb();

                    if(target_csv.length() != 0) {


                        //---------------- 送信　処理　--------------
                        String jim = "http://192.168.254.226/tana_phppost_file/UploadToServer.php";  // JIM　社内 OK *****
                        // http://192.168.254.51/tana_phppost_file/UploadToServer.php ****** 外山工業さん VPN テスト
                        String tm = "http://192.168.50.253/tana_phppost_file/UploadToServer.php"; // **** 外山工業さん　本番 *****
                        String uploadURL = jim;

                        String title = send_csv_file;
                        String uploadFile = getFilesDir() + "/" + send_csv_file;

                        //***************************************** コールバックタスク　実行
                        PostAsyncTask task = new PostAsyncTask();
                        task.setOnCallBack(new PostAsyncTask.CallBackTask(){

                            @Override
                            public void CallBack(Object result) {
                                super.CallBack(result);

                                String obj = (String) result;

                                //************** PHP からの　コールバックを表示
                                test_send_view.setText(obj);

                                System.out.println("obj 値::::" + obj);

                                String call_back_str = test_send_view.getText().toString();

                                if(call_back_str.startsWith("0:")) {
                                    toastMake("データ送信に失敗しました。時間をおいてもう一度送信してください。",0, -200);

                                } else {

                                    //************ ファイル送信　成功処理

                                    //************** 削除処理
                                    // データ　全件　削除
                                    DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                                    dbAdapter.openDB();

                                    dbAdapter.allDelete(); // 全件　削除　クラスメソッド

                                    dbAdapter.closeDB(); //------- DB を閉じる

                                    //************** 削除処理　END


                                    //--------------- アラートダイヤログ　削除　設定 ---------------//
                                    // タイトル
                                    TextView titleView;
                                    titleView = new TextView(SendCsv.this);
                                    titleView.setText("送信完了");
                                    titleView.setTextSize(20);
                                    titleView.setTextColor(Color.WHITE);
                                    titleView.setBackgroundColor(getResources().getColor(R.color.back_color_01));
                                    titleView.setPadding(20, 20, 20, 20);
                                    titleView.setGravity(Gravity.CENTER);


                                    //-------------- アラートログの表示 開始 -------------- //
                                    AlertDialog.Builder bilder = new AlertDialog.Builder(SendCsv.this);

                                    // ダイアログの項目
                                    bilder.setCustomTitle(titleView);
                                    //  bilder.setTitle("削除");
                                    bilder.setMessage("「棚卸お疲れ様でした」\nファイル送信が完了しました。");

                                    //------- OK の時の処理 ----------//
                                    bilder.setPositiveButton("棚卸アプリを終了する", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            toastMake("「棚卸　お疲れ様でした。」", 0, -200);
                                            finish();

                                        }
                                    });

                                    bilder.setNegativeButton("棚卸作業を続ける", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent1 = new Intent(getApplication(), ReadBerCode.class);

                                            startActivity(intent1);

                                            finish();
                                        }
                                    });


                                    // ダイアログの表示
                                    AlertDialog dialog = bilder.create();
                                    dialog.show();

                                }

                            }

                        });


                            //⑧PostAsyncTaskに渡すパラメータをObject配列に設定
                            Object[] postParams = new Object[3];
                            postParams[0] = uploadURL;
                            postParams[1] = title;
                            postParams[2] = uploadFile;

                            //⑨PostAsyncTaskを実行
                            //   new PostAsyncTask(this).execute(postParams);
                            task.execute(postParams);

                        /*
                        // データ削除
                        deleteDatabase("Send.db");
                           */

                            file_Flg = false;

                            file_num = "send"; // 送信されたら 1

                            common.g_flg = 1;
                            System.out.println(common.g_flg + "グローバル変数　フラグ");



                    } else {

                        test_send_view.setText("棚卸データが空です。送信できません。");

                        toastMake("棚卸データが空です。送信できません。",0, -200);

                        file_num = "kara";
                        return;
                    }

                break;

            default:
                break;

        }
    }



    // データベースから読んで　CSV ファイルとして、　ファイルを　Android 端末内に保存
    private void ReadSend_tb() {

        dbAdapter.openDB();

        //-------- DB　格納用 ----------
        // Send_TB_02 の　データ取得
        Cursor cursor = dbAdapter.getDB(columns);

     //   int id;  // Send_TB_02 カラム　2
        String [] arr_item = new String[4]; // Send_TB_02 カラム　2 ～ 10
        ArrayList<String> send_item = new ArrayList<String>();

        String place; // カラム　２　場所コード　格納用変数
        String place_get = "";

        //---------- 合計　4 カラム ------------//

        try {

            // 外部ストレージ用　パス
            send_csv_file_path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();

            target_csv = new File(send_csv_file_path + "/" + send_csv_file);

            // 書き込み用　ストリーム
         FileOutputStream outStream = openFileOutput(String.valueOf(target_csv), MODE_PRIVATE);
            // ファイル書き込み　準備
          OutputStreamWriter out = new  OutputStreamWriter(outStream, "Shift_JIS");


          PrintWriter printWriter = new PrintWriter(out);

            if (cursor.moveToFirst()) {

                do {

                    //----- カラム 1  （担当者コード）
                    arr_item[0] = cursor.getString(1);

                    System.out.println("出力テスト" + arr_item[0]);

                    //----- カラム 2  （倉庫コード）
                     place = cursor.getString(2); // 場所コード　格納

                    //----- カラム 3 （商品コード）
                    arr_item[1] = cursor.getString(3);
                    System.out.println("出力テスト" + arr_item[1]);

                    //----- カラム 3 書き込み
                    arr_item[2] = cursor.getString(4);
                    // 書き込み
             //       out.write(arr_item[2]);
               //     out.write(",");
                    System.out.println("出力テスト" + arr_item[2]);

                    //----- カラム 4 書き込み
                    arr_item[3] = cursor.getString(5);
                    // 書き込み
                //    out.write(arr_item[3]);
                //    out.write(",");
                    System.out.println("出力テスト" + arr_item[3]);

                    String record = arr_item[0] + "," + place_get + "," + arr_item[1] + "," +
                            arr_item[2] + "," + arr_item[3] + "," + arr_item[4];

                    printWriter.println(record);

                    // 書き込み

                    printWriter.flush();

                } while (cursor.moveToNext()); //-----END while

                // 閉じる
                printWriter.close();

            }

            cursor.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();

            // フォルダへのアクセス権限がない場合の表示
            Toast ts = Toast.makeText(this, "アクセス権限がありません", Toast.LENGTH_SHORT);
            ts.show();

        }catch (Exception e) {
            Toast ts = Toast.makeText(this, "CSV出力が失敗しました", Toast.LENGTH_SHORT);
            ts.show();

        } finally {

            if(dbAdapter != null) {
                dbAdapter.closeDB();
            }

            /*
            Toast ts = Toast.makeText(this, "出力データ作成成功。", Toast.LENGTH_LONG);
            ts.show();

             */

        }

    }//------------------------- END SQLliste => csv ファイル


    private void findView() {

        //---- ヘッダーコンポーネント
                back_btn_10 = findViewById(R.id.back_btn_10);
                user_num_10 = findViewById(R.id.user_num_10);

                test_send_view_03 = findViewById(R.id.test_send_view_03);

                //------- テストビュー
                test_send_view = findViewById(R.id.test_send_view);
                se_btn = (MaterialButton)findViewById(R.id.se_btn);

                //------- ボタン
         //       pos_go_btn = findViewById(R.id.pos_go_btn);
    }

    private void header_ac() {

        if(getIntent() != null) {
            ac_id = getIntent().getStringExtra("send_id");
            ac_name = getIntent().getStringExtra("send_name");

            send_num = getIntent().getStringExtra("send_flg");

            user_num_10.setText(ac_name);

            file_num = send_num;

            System.out.println("送られてきた" + send_num);

        } else {
            toastMake("アカウント情報 取得 エラー", 0, -200);
        }
    }


    //---------------- csv　保存 & 送信用　データ名　作成 TN + yyyyMMdd L HHmmss -> csv_date_01 に格納
    private void send_csv_name() {

        //------------- CSV ファイル名用　時間取得
        //Calendarクラスのオブジェクトを生成する
        Calendar cl = Calendar.getInstance();

        // SimpleDateFormatクラスを使用して、パターンを設定する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd" + "-" + "HHmmss");
        csv_date_01 = sdf.format(cl.getTime());

        // 出力　ファイル名
        send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + ac_id + ".csv";

    }


    // トーストメッセージ　表示用
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        // 位置調整
        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();

    }


    // AsyncTask 用
    /*
    @Override
    protected void onDestroy() {
        task.setListener(null);
        super.onDestroy();
    }

     */

    // AsyncTask 用
    private UploadTask.Listener  createListener() {
        return new UploadTask.Listener () {
            @Override
            public void onSuccess(String result) {
              //  textView.setText(result); 送信　結果
            }
        };

    }


    @Override
    public void onBackPressed() {


        Intent intent = new Intent();

        intent.putExtra("send_csv", ac_name);
        intent.putExtra("send", file_num); // ------- ファイルが送信されてなかったら 0:  1: 送信されている。

        setResult(Activity.RESULT_OK, intent);
        finish();

    }


    /**
     *  担当者コード　9999 から　担当部署コード B0882 を取得
     */

    private void Tantou_Busho_SELECT_01 () {

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase Ta_BU_db = helper.getReadableDatabase();

        Cursor cursor = null;

        String [] arr_item = new String[2];

        try {

            cursor = Ta_BU_db.rawQuery("select employee_tb_c_01, employee_tb_c_04 from Employee_Tb", null);

            if(cursor != null || cursor.getCount() > 0) {

                if(cursor.moveToFirst()) {

                    do{

                        arr_item[0] = cursor.getString(0);
                        arr_item[1] = cursor.getString(1);

                        Tantou_GET_Busho_H.put(arr_item[0], arr_item[1]);

                    } while (cursor.moveToNext());

                } // ------ END cursor

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }






}

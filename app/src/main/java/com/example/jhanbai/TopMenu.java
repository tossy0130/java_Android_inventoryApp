package com.example.jhanbai;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TopMenu extends AppCompatActivity {

    private final int REQUESTED_LIST_TOP = 1;
    private final int REQUESTED_SEND_TOP = 2;

    private DBAdapter dbAdapter;

    // word テスト　データ　入力用
    private EditText editText;

    private Button test_nyuuko;

    private Button List_test;

    // アカウント　入力用　ボタン
    private Button user_go;

    // 棚卸し　入力画面へ移動
    private Button menu_btn_01;

    // アカウント　受け取り
    private String gg_accont; // id
    private String gg_accont_02; // name
    private String gg_accont_code; // カラム　04

    // ＊＊＊　倉庫コード用　格納変数 ＊＊＊
    private String gg_souko_code;

    // アカウント　バーコード読み取り画面
    private String bb_accont; // id
    private String bb_accont_02; // name

    private TextView post_view;
    private TextView user_num;

    //＊＊＊　倉庫コード取得 ＊＊＊
    private TextView souko_num;


    private String company_code_c_01_num;
    private String company_code_c_02_num;

    static final int RESULT_SUBACTIVITY = 1000;

    private String get_num;

    private String put_num;

    private WebView webView;
    private Button kousin_btn; // webview 用

    // イメージボタン　追加
    private ImageButton user_icon, souko_icon;

    //****** 日付取得用
    private String Now_date_str,get_year,get_month,get_day;
    // 表示用　テキストビュー
    private TextView date_view;

    private String csv_date_01;

   // private int file_flg;
    private static int file_flg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_menu);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //------- 送信画面からの　戻り　フラグ　取得
        get_flg();

        //------------ AsyncTask 送信用

        // DBAdapter の　コントラクタを呼ぶ
        dbAdapter = new DBAdapter(this);

        //　＊＊＊＊＊＊＊　コンポーネント　初期化　＊＊＊＊＊＊＊
        test_nyuuko = (Button) findViewById(R.id.test_nyuuko);
  //      List_test = (Button) findViewById(R.id.List_test);

        // ユーザー入力画面　へ移動　ボタン
  //      user_go = (Button) findViewById(R.id.user_go);
        // 棚卸し入力画面　へ移動　ボタン
        menu_btn_01 = (Button) findViewById(R.id.menu_btn_01);
        post_view = (TextView) findViewById(R.id.post_view);
        user_num = (TextView) findViewById(R.id.user_num);


        //　＊＊＊　イメージアイコン　追加
        user_icon = (ImageButton) findViewById(R.id.user_icon); // 担当者アイコン
        souko_icon = (ImageButton) findViewById(R.id.souko_icon); // 倉庫アイコン

        //　＊＊＊　日付　日時　取得
        date_view = (TextView) findViewById(R.id.date_view);
        Now_date_str = getNowDate();

        get_year = Now_date_str.substring(0,4); //yyyy
        get_month = Now_date_str.substring(4,6); // MM
        get_day = Now_date_str.substring(6,8); // dd

        String get_week = Now_date_str.substring(8,9);

      //  date_view.setText(get_year + "年" + get_month + "月" + get_day + "日");
        date_view.setText(get_year + "年" + get_month + "月" + get_day + "日" + '(' + get_week + ')');

        /***
         *  /log/ フォルダに　ファイルがなかった場合　削除実行
         */
        File_Del();


        //---------- putExtra の値を取得 (アカウント用)----------------------------------------------
        if(getIntent() != null) {
            gg_accont = getIntent().getStringExtra("account_id");

            //　８桁 ０　埋め用
       //     bb_accont = String.format("%8s", gg_accont).replace(" ", "0");
            // 通常　０　埋めなし
            bb_accont = gg_accont;
            // アカウント　名 チェック
            user_num.setText(bb_accont);

        }
        //---------- putExtra の値を取得 (アカウント用) END ------->

        //　＊＊＊　イメージボタン　イベント（担当者コード）
        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toastMake("担当者コードです。", 0 , -200);

            }
        });


        // -------------- 戻る矢印
        final ImageButton back_btn = (ImageButton) findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //-------- 戻る
                Intent intent = new Intent();

                    intent.putExtra("file_flg_top", get_num); // ------- ファイルが送信されてなかったら 0:  1: 送信されている。

                    setResult(Activity.RESULT_OK, intent);

                    /**
                     *  端末内　ファイル削除　チェック
                     */
                    File_Del();

                    finish();

            }
        });


        menu_btn_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //　＊＊＊　倉庫コード　入力画面へ遷移
                Intent intent = new Intent(getApplication(), Souko_Input.class);

                //　＊＊＊　倉庫コード　へ　アカウント コード を　渡す
                intent.putExtra("get_account",bb_accont);

                startActivity(intent);
            //    startActivityForResult(intent , RESULT_SUBACTIVITY);

           //     finish();

            }
        });

        // 一覧表示
        test_nyuuko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 一覧　表示 ・　修正画面へ　移動
                //　＊＊＊　倉庫コード　入力画面へ遷移
                Intent intent = new Intent(getApplication(), SelectSheetListView.class);

                //　＊＊＊　倉庫コード　へ　アカウント コード を　渡す
                intent.putExtra("get_account",bb_accont);

                startActivity(intent);

             //   finish();

            }
        });


        //------------------------------------ データ送信画面　へ　遷移 ---------------
       /*
        List_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // データベース　一覧表示画面　テスト
                Intent intent = new Intent(getApplication(), SendCsv.class);

                // 担当者　データ　渡す
                intent.putExtra("send_id", bb_accont); // 担当者コード
                intent.putExtra("send_name", bb_accont_02); // 担当者名

                intent.putExtra("send_flg", put_num);

                startActivityForResult(intent, REQUESTED_SEND_TOP); // int 2 定数

              //  finish();

            }
        });
        */


        /*
        //-------- 最新アプリ　ダウロード -------------//
        kousin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----- アプリ　ダウンロード ----
                String jim = "http://192.168.254.87/JimApk/index.php";  // JIM　社内 OK *****
                // http://192.168.254.51/JimApk/index.php ****** 外山工業さん VPN テスト
                 String tm = "http://192.168.50.253/JimApk/index.php";// **** 外山工業さん　本番 *****

                setContentView(webView);
                // jim テスト　URL:
                webView.loadUrl(tm);

            }
        });

         */

    }


    /*
    @Override
    protected void onDestroy() {
        task.setListener(null);
        super.onDestroy();
    }

     */

    //---------- ReadBerCode から　返却値を受け取る ----------
    protected void onActivityResult(int requestCode, int resultCode,Intent  intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            case RESULT_SUBACTIVITY :
                if(requestCode == RESULT_OK) {
                    String str = intent.getStringExtra("re_id");

                    user_num.setText(str);

                    System.out.println("back" + str);

                }
                break;

            case REQUESTED_LIST_TOP :
                if(requestCode == RESULT_OK) {
                    String str = intent.getStringExtra("re_id");

                    user_num.setText(str);
                    System.out.println("リストから  back" + str);

                }
                break;

            case REQUESTED_SEND_TOP :
                if(requestCode == RESULT_OK) {
                    String str = intent.getStringExtra("send_csv");
                    String str_2 = intent.getStringExtra("send");

                    System.out.println("送信back" + str);
                    System.out.println("送信back" + str_2);

                    user_num.setText(str);
                }
        }

    }


    /*----------- 課　紐づけ　------------------*/

    private void getAcount() {

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase Qr_sql = helper.getReadableDatabase();

        String [] arr_item = new String[2];

        ArrayList<String> Qr_item = new ArrayList<>();

        try {

            Cursor cursor = Qr_sql.rawQuery("SELECT company_code_c_01, company_code_c_02 FROM " +
                    "Company_code WHERE company_code_c_01 = " + gg_accont_code + " LIMIT 1", null);

            if(cursor.moveToNext()) {

                int idx = cursor.getColumnIndex("company_code_c_01");
                arr_item[0] = cursor.getString(idx);

                System.out.println(arr_item[0]);

                idx = cursor.getColumnIndex("company_code_c_02");
                arr_item[1] = cursor.getString(idx);

                System.out.println(arr_item[1]);

                company_code_c_01_num = arr_item[0];
                company_code_c_02_num = arr_item[1];

            }

        } finally {
            if(Qr_sql != null) {
                Qr_sql.close();
            }
        }

    }

    // --------------------- トーストメッセージ表示 -----------------------
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this,message, Toast.LENGTH_SHORT);

        // 位置調整
        toast.setGravity(Gravity.CENTER, x,y);

        View v = toast.getView();
        // トースト背景色
        v.setBackgroundColor(Color.rgb(58, 58, 60));
        TextView  view1=(TextView)v.findViewById(android.R.id.message);
        view1.setTextColor(Color.parseColor("#ffffff"));
        toast.show();
    }

    private void get_flg() {

        if(getIntent() != null) {
            get_num = getIntent().getStringExtra("send");
            System.out.println("送信back" + get_num);
        } else {
            System.out.println("送信back" + get_num);
        }

    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.
     */
    public static String getNowDate() {

        final DateFormat df = new SimpleDateFormat("yyyyMMddE");
        final Date date = new Date(System.currentTimeMillis());

        return df.format(date);
    }


    /**
     *   home 復帰処理
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        //  user_input.setText("");
        //  user_view.setText("");

        // onRestartのログを表示.
        Log.v("ログ現在のactivity：：：", getLocalClassName());	// Log.vで"onRestart()"と出力.
    }




    /**
     *   log ディレクト　が　存在している　かつ　、ファイルがあって、ファイル名が TN- から始まっていたら、
     *   全削除する。
     */
    private void File_Del() {

        //************************** ファルダ　作成 **************************
        String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/log/";
        String r_path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();



        //************************* フォルダ　リネーム用 **********************
        //Calendarクラスのオブジェクトを生成する
        Calendar cl = Calendar.getInstance();
        // SimpleDateFormatクラスを使用して、パターンを設定する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd" + "_" + "HHmmss");
        csv_date_01 = sdf.format(cl.getTime());

        String re_name_path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() +
                "/" + csv_date_01 + "_log/";

        File log_f = new File(path);
        File root = new File(r_path);

        //*** リネーム用
        File re_nama_d = new File(re_name_path);

        //listFilesメソッドを使用して一覧を取得する
        File[] list_File = root.listFiles();

        File[] list_File_log = log_f.listFiles();

        // /log/ ファイルの中を取得
        File[] list_log = log_f.listFiles();

        System.out.println("ファイル & ディレクトリ数 if文前" + list_File.length);

        //***** ディレクトリを取得
        String dirname = log_f.getParent() + "/log/";

        // 0  1 = 削除
        file_flg = 0;

        for(int i = 0; i < list_File.length; i++) {

            if (list_File[i].isFile()) {

                System.out.println("ファイル" + list_File[i]);
                System.out.println("ファイル名" + list_File[i].getName());

                //*** ファイル名チェック TN-
                if (list_File[i].getName().contains("TN-")) {
                    System.out.println("ファイルあり" + list_File[i].getName());

                    file_flg = 0;

                    break;

                } else {

                    //*** 削除　フラグ on
                    file_flg = 1;
                    System.out.println("削除フラグ　on" + String.valueOf(file_flg));


                }

            } else {
                //*** 削除　フラグ on
                file_flg = 1;
                System.out.println("削除フラグ　on" + String.valueOf(file_flg));


            }
        } // ************** end loop ***********


        System.out.println("if 文前 削除フラグ　on" + String.valueOf(file_flg));

        if(log_f.exists() && file_flg == 1 && dirname.contains("log")) {

            System.out.println("ファイル & ディレクトリ数" + list_File.length);

            //****** データ　全件　削除 ******
            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
            dbAdapter.openDB();

            dbAdapter.allDelete(); // 全件　削除　クラスメソッド

            dbAdapter.closeDB(); //------- DB を閉じる

            System.out.println("ファイルなし　全削除");

            //*************** /log/  フォルダを　リネーム *****************
            if (log_f.renameTo(re_nama_d)) {
                //正常に名前が変更された場合
                System.out.println("名前変更成功" + re_nama_d.getName());
            }

        } else {

            System.out.println("ファイル & ディレクトリ数 return" + list_File.length);

            return;

        }  //************ END if


    } //*********** END function

    /**
     *   フォルダ　全削除
     */

    /*
    public static void delete(String path) {
        File filePath = new File(path);
        String[] list = filePath.list();
        for(String file : list) {
            File f = new File(path + File.separator + file);
            if(f.isDirectory()) {
                delete(path + File.separator + file);
            }else {
                f.delete();
            }
        }
        filePath.delete();
    }

     */

    /**
     *  バックグランドになったとき 発火
     */
    @Override
    public void onUserLeaveHint(){

        /**
         *  /log/ ファイル　リネーム　&  端末内データ削除　実行
         */
        File_Del();

    }

    /**
     *   バックキーを押した処理
     */
    @Override
    public void onBackPressed(){
        // 行いたい処理

        File_Del();

        finish();

    }


}

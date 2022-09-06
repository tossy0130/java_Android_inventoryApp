package com.example.jhanbai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private int DB_version = 2;

    /**
     *  DB 接続用
     */
    private TestOpenHelper helper;
    private SQLiteDatabase db;

    private Common common;      // グローバル変数を扱うクラス

    // ユーザーテキスト　入力用
    private EditText user_input;
    private EditText souko_input;

    // アカウント名　表示用　テキストビュー
    private TextView user_view;

    //------------- アセッツマネージャー --------//
    private AssetManager ass;
    private AssetManager User_as;
    private AssetManager Bu_as;


    //------------- コンテンツ　バリュー ---------//
    private ContentValues value;


    //---- employee テーブル　取得用 -------//
    private String employee_tb_c_04_num;

    //------------- イベント　ボタン -----------//
    private Button account_put_btn;  // アカウント　確定ボタン
    private String g_account;  // アカウント　番号　格納用

    //＊＊＊　倉庫コード　追加 ＊＊＊
    private String souko_code;

    private Button setting_01; // DB 作成
    private Button setting_02; // CSV インポート
    private MaterialButton kousin_w; //　最新ボタン　


    // アカウント名　格納用
    private String g_account_02;

    private String send_account;

    private ProgressBar progressBar;

    private int val;

    private String [] Row_tnmf = null;

    //------------- 判別　フラグ
    private int Ac_Flg;
    private int db_Flg;

    private int Item_tb_Flg;

    //------------- 担当コード　格納　
    private ArrayList<String> arr_col = new ArrayList<>();

    //------------- btn
    private Button master_btn;

    //------------- 効果音　用
    SoundPool soundPool;    // 効果音を鳴らす本体（コンポ）
    int mp3a;          // 効果音データ（mp3）

    private Button db_delete_btn;

    private WebView webView;

    //-------- トースト用 メッセージ ----------//
    private String str_01 = "「DB設定」が完了しました。　「ファイル受信」に進んでください。";

    // 日付表示用
    private TextView date_view_top;
    private String get_date_str, get_year,get_month,get_day;

    private MaterialButton start_btn;


    //-------- home キー　処理
  //  private HomeButtonReceive homeButtonReceiver;

    private Context context;
    private int waitperiod;

    // 端末 ID 表示用
    private TextView t_id;
    private String target_csv, csv_date_01;
    private static int file_flg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // グローバル変数を扱うクラスを取得する
        common = (Common) getApplication();

        context = getApplicationContext();
        waitperiod = 500; // 5sec

        // グローバル変数の初期化
        common.g_flg = 0;

        // コンポーネント 初期化
        init();

        //　＊＊＊　日付設定 表示
        get_date_str = getNowDate();
        get_year = get_date_str.substring(0,4); //yyyy
        get_month = get_date_str.substring(4,6); // MM
        get_day = get_date_str.substring(6,8); // dd

        //String get_week = get_date_str.substring(8,10);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        date_view_top.setText(get_year + "年" + get_month + "月" + get_day + "日" + '(' + dayOfTheWeek + ')');

        // ＃＃＃　担当者コード　へ　フォーカスを当てる
        user_input.requestFocus();

        g_account = user_input.getText().toString();


        //------- 判別　フラグ　初期設定
        Ac_Flg = 0;
        db_Flg = 0;

        Item_tb_Flg = 0; // 0: 未作成 1: 作成済み

        // 端末の固有識別番号の取得
        String androidId = Settings.Secure.getString(
                this.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        //******** 端末 ID 表示
        t_id = (TextView) findViewById(R.id.t_id);

        t_id.setText("端末ID：" + androidId);


        /**
         *  ファイル数　取得
         */
        File_Del();

        //----------- アカウント　作成ボタン フォーカスを受けとった後の　処理 ------------------
        user_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // TODO 自動生成されたメソッド・スタブ
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ## フォーカスを受け取ったとき
                if (hasFocus) {
                    // ソフトキーボードの表示
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    user_input.setRawInputType(Configuration.KEYBOARD_QWERTY); // ソフトキーボード　の　デフォルト設定

                } else {
                    // ## フォーカスが外れた時
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });


        //**************** 担当コード　入力　テキストエディット  キーボード　エンター処理が押された処理 *************
        // ************************************************************************************//
        user_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // エンターボタンが押されたら
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                        //　***********  エラーチェック **************
                        if (user_input.length() == 0) {

                            Snackbar.make(v,"入力欄が空白です。「担当者コード」を入力してください。",Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();

                            return false;

                        }

                        // ソフトキーボードを隠す
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);

                    /**
                     *   エディットテキスト　値　取得 （ユーザー ID）
                     */
                        g_account = user_input.getText().toString();

                        // **********  ログイン用　情報 SELECT

                        //　＊＊＊ 画面遷移　＊＊＊
                        // putExtra で　データを渡す ---------------------------------
                        Intent intent = new Intent(getApplication(), TopMenu.class);
                        intent.putExtra("account_id", g_account); // エディットテキスト　account ID

                        startActivity(intent);

                        //************* ログイン　OK
                    //    User_Conf();

                    return true;
                }

                return false;
            }
        });


        /**
         *  現品票　CSV 読みとりボタン
         */
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 *  　＊＊＊＊＊＊＊　データベース　作成　＊＊＊＊＊＊＊＊
                 */

                if(helper == null) {
                    // DB　の作成
                    helper = new TestOpenHelper(getApplicationContext());
                }

                // DB が空なら　作成
                if(db == null) {
                    db = helper.getWritableDatabase();
                    toastMake("初期セッティング 設定完了", 0, -200);
                    db.close();

                } else if (db == null && DB_version > 1) {
                    // アップグレード処理
                    db = helper.getWritableDatabase();
                    toastMake("アップグレード 処理", 0, -200);
                    db.close();

                } else {

                }

                /**
                 *  　＊＊＊＊＊＊＊　データベース　作成　END　＊＊＊＊＊＊＊＊
                 */


                /**
                 *  現品票 CSV インサート処理
                 */
                CSV_in_SQL_01();

            }
        });


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // ここに３秒後に実行したい処理



            }
        }, 3000);
    }

    //---------- アカウント作成ボタン　イベント処理 END ----------------->

    private void toastMake (String message,int x, int y){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

        // 位置調整
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }

    private void User_Conf() {

        helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        String [] arr_item = new String[1];


        try {

            Cursor cursor = db.rawQuery("SELECT employee_tb_c_01 FROM Employee_Tb WHERE employee_tb_c_01 NOTNULL",null);

            if(cursor.moveToFirst()) {

                do {

                    int idx = cursor.getColumnIndex("employee_tb_c_01");
                    arr_item[0] = cursor.getString(idx);

                    arr_col.add(arr_item[0]);

                } while(cursor.moveToNext()); //------ END while

            } //------ END if


            //--- 出力テスト ---
            for(String a:arr_col) {
                System.out.println(a);
            }

        } finally {

            if(db != null) {
                db.close();
            }

        }

    }


    @Override
    public void onBackPressed() {

        /**
         *  ファイル削除処理
         */

        File_Del();


        //-------------- アラートログ　削除　-----------
        // タイトル
        TextView textView;
        textView = new TextView(MainActivity.this);
        textView.setText("J-販売アプリケーション");
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(getResources().getColor(R.color.back_color_03));
        textView.setPadding(20, 20, 20, 20);
        textView.setGravity(Gravity.CENTER);

        //--------------　アラートログの表示　開始
        AlertDialog.Builder bilder = new AlertDialog.Builder(MainActivity.this);

        //--------- カスタムタイトル　セット
        bilder.setCustomTitle(textView);
        //--------- メッセージのセット
        bilder.setMessage("「棚卸」アプリケーションを終了しますか？");

        bilder.setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //------------------　「いいえ」　の処理
                return;
            }
        });

        bilder.setNegativeButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

                //------------ 削除　処理　END　----------------
            }
        });


        AlertDialog dialog = bilder.create();
        dialog.show();

        //******************************************* ボタン　配色　変更
        //********* ボタン はい **********
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF4081"));
        //   dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red));

        //********* ボタン いいえ **********
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF4081"));
        //   dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red));



    }

    private void init() {

        // 現品票　CSV 読みとり　ボタン
        start_btn = (MaterialButton)findViewById(R.id.start_btn);

        date_view_top = (TextView) findViewById(R.id.date_view_top);

        // アカウント名　表示用　テキストビュー

        //*** ユーザーテキスト　入力用　エディットテキスト ***
        user_input = (EditText) findViewById(R.id.user_input);
        //------- エディットテキスト　インプットタイプ設定
        user_input.setInputType(InputType.TYPE_CLASS_NUMBER);

        user_input.setText("");


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
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.
     */
    public static String getNowDate() {

        final DateFormat df = new SimpleDateFormat("yyyyMMddHH");
        final Date date = new Date(System.currentTimeMillis());

        return df.format(date);
    }


    /**
     *  CSV ファイルを　DB へ　挿入
     */
    private void CSV_in_SQL_01 () {

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        // トランザクション　開始
        db.beginTransaction();

        try {

            db.delete("Genpin_T", null,null);

            // トランザクション　完了
            db.setTransactionSuccessful();

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //------------- トランザクション　完了
            db.endTransaction();
        }

        /**
         *  現品票　CSV 読み込み
         */

        String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();

     //   target_csv = path + "/Genpinhyou_File/test.csv";

        target_csv = path + "/SHMF.csv";

        System.out.println("CSVファイル SHMF:::パス:::" + target_csv);

        String line = "";

        // CSV 取得データ数
        String Row_data [] = new String[4];

        // トランザクション開始
        db.beginTransaction();

        try {

            FileInputStream fi = new FileInputStream(target_csv);

    //        InputStreamReader is = new InputStreamReader(fi, "UTF-8");
            InputStreamReader is = new InputStreamReader(fi, "Shift-JIS");


            BufferedReader reader = new BufferedReader(is);

            while((line = reader.readLine()) != null) {

                Row_data = line.split("," , -1);

                ContentValues values = new ContentValues();

                // ****** CSV データ １ 項目目　取得
                values.put(TestOpenHelper.COLUMN_01, Row_data[0]);
                System.out.println("テスト出力::CSVデータ 1：：：" + Row_data[0]);

                // ****** CSV データ ２ 項目目　取得

                // "" ダブルクォーテーションを取る
                String row_str_02 = Row_data[1].replace("\"", "").trim();
                values.put(TestOpenHelper.COLUMN_02, row_str_02);
                System.out.println("テスト出力::CSVデータ 2：：：" + row_str_02);

                /**
                 * ＊＊＊＊＊＊ 仕入単価　追加　＊＊＊＊＊＊
                 */
                values.put(TestOpenHelper.COLUMN_03, Row_data[2]);
                System.out.println("テスト出力::CSVデータ 3：：：" + Row_data[2]);

                /**
                 * ＊＊＊＊＊＊ 仕入先名　追加　＊＊＊＊＊＊
                 */
                values.put(TestOpenHelper.COLUMN_04, Row_data[3]);
                System.out.println("テスト出力::CSVデータ 4：：：" + Row_data[3]);

                //******* インサート処理
                db.insert("Genpin_T", null, values);

            } //***** END while

            // トランザクション　成功
            db.setTransactionSuccessful();

            toastMake("「マスター データセット」　完了しました。 ", 0, -200);

        } catch (Exception e) {
            e.printStackTrace();
            toastMake("「マスター データセット」　読み込みエラー。CSVファイルがセットされているか確認してください。", 0, -200);

        } finally {

            // トランザクション　完了　閉じる
            db.endTransaction();

        }


    } //================= END CSV_in_SQL_01


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



}

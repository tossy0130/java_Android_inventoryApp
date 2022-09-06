package com.example.jhanbai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Souko_Input extends AppCompatActivity {

    private TextView date_view_top_souko;

    //******* 日付用
    //****** 日付取得用
    private String Now_date_str_souko,get_year,get_month,get_day;
    // 表示用　テキストビュー
    private TextView date_view_souko, user_num_03;

    private EditText souko_input;
    private String get_souko_input, get_account_num;

    private ImageButton back_btn_03;

    private String csv_date_02;
    // private int file_flg;
    private static int file_flg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souko__input);

   //     getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        init();

        //***** 時間表示　取得
        Now_date_str_souko = getNowDate();

        get_year = Now_date_str_souko.substring(0,4); //yyyy
        get_month = Now_date_str_souko.substring(4,6); // MM
        get_day = Now_date_str_souko.substring(6,8); // dd

        String get_week = Now_date_str_souko.substring(8,9);

     //   date_view_souko.setText(get_year + "年" + get_month + "月" + get_day + "日");
        date_view_souko.setText(get_year + "年" + get_month + "月" + get_day + "日" + '(' + get_week + ')');

        //***  TopMenu.java から 値を受け取る
        if(getIntent() != null) {

            //*** アカウント　コード　取得
            get_account_num = getIntent().getStringExtra("get_account");

            //*** アカウント　コードセット
            user_num_03.setText(get_account_num);

            //***

        }

        //***** 時間表示　取得 END

        /***
         *  /log/ フォルダに　ファイルがなかった場合　削除実行
         */
        File_Del();

        //----------- 倉庫　入力 エディットテキスト　作成ボタン フォーカスを受けとった後の　処理 ------------------
        souko_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // TODO 自動生成されたメソッド・スタブ
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ## フォーカスを受け取ったとき
                if (hasFocus) {
                    // ソフトキーボードの表示
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    souko_input.setRawInputType(Configuration.KEYBOARD_QWERTY); // ソフトキーボード　の　デフォルト設定

                } else {
                    // ## フォーカスが外れた時
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });

        //----------- 倉庫　入力 エディットテキスト　作成ボタン フォーカスを受けとった後の　処理 END ------------------


        //**************** 倉庫コード　入力　テキストエディット  キーボード　エンター処理が押された処理 *************
        // ************************************************************************************//
        souko_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // エンターボタンが押されたら
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                    // ソフトキーボードを隠す
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // エディットテキスト　値　取得
                    get_souko_input = souko_input.getText().toString();


                    if (get_souko_input.length() == 0) {

                        Snackbar.make(v,"入力欄が空白です。「倉庫コード」を入力してください。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();

                        return false;

                    } else {


                        // **********  ログイン用　情報 SELECT

                        //　＊＊＊ 画面遷移　＊＊＊
                        // putExtra で　データを渡す ---------------------------------
                        Intent intent = new Intent(getApplication(), ReadBerCode.class);

                        intent.putExtra("get_account_num", get_account_num); // アカウントコード　を渡す
                        intent.putExtra("souko_num", get_souko_input); // 倉庫コード

                        startActivity(intent);

                        //************* ログイン　OK

                        //    User_Conf();

                    }

                    return true;
                }

                return false;
            }
        });


        //---------- back btn バックボタン　を押されたら　値を返す
        back_btn_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    /**
                     *  端末内　ファイル削除　チェック
                     */
                    File_Del();

                   // アクティビティを終了させる
                    finish();
                }
        });


    }//*****************************************  END onCreate

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.
     */
    public static String getNowDate() {

        final DateFormat df = new SimpleDateFormat("yyyyMMddE");
        final Date date = new Date(System.currentTimeMillis());

        return df.format(date);
    }

    private void init() {

        //****** コンポーネント 初期化処理
        date_view_souko = (TextView) findViewById(R.id.date_view_souko);

        // 倉庫コード　入力用
        souko_input = (EditText) findViewById(R.id.souko_input);
        //------- エディットテキスト　インプットタイプ設定
        souko_input.setInputType(InputType.TYPE_CLASS_NUMBER);

        back_btn_03 = (ImageButton) findViewById(R.id.back_btn_03);

        user_num_03 = (TextView) findViewById(R.id.user_num_03);


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
        csv_date_02 = sdf.format(cl.getTime());

        String re_name_path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() +
                "/" + csv_date_02 + "_log/";

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
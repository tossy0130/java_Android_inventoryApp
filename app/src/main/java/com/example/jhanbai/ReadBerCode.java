package com.example.jhanbai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReadBerCode extends AppCompatActivity {

    // View 動的　追加用
    private ConstraintLayout constraintLayout_01;
    private LayoutInflater inflater;

    // 商品複数リスト　スピナー
    private ArrayList<String> arr_genpin_item = new ArrayList<>();
    private ArrayList<String> arr_shouhin_item = new ArrayList<>();
    private ArrayList<String> arr_spiner_item = new ArrayList<>();

    // インサート用 リストarr_spiner_item
    private ArrayList<String> Arr_Insert_Item = new ArrayList<>();

    //---------- 画面　全体 ----------
    private ScrollView mainLayout_ber;

    private IntentIntegrator integrator;

    //--------- Send_TB_02 テーブル　から　データ取得用 ---------//
    // DBAdapter コンストラクタ　用
    private DBAdapter dbAdapter;
    // 参照する DB のカラムを入れる
    private String[] columns = null;

    //---------- インテント用　リクエストコード -------
    private final int REQUESTED_LIST = 11; // リスト一覧　へ　遷移
    private final int REQUESTED_QR = 100; // カメラ　起動画面へ遷移
    private final int GET_LIST = 111; // リスト一覧　から　戻ってくる

    private Snackbar snackbar;

    //---------- リストアダプター
   private SelectSheetListView.MyBaseAdapter myBaseAdapter;

    //--------- DB 関連 -----------
    private TestOpenHelper helper;
    private SQLiteDatabase db;

    private DBAdapter DBHelper;

    private ContentValues values; // Send_db インサート 用

    //id
    private long id;

    //---------- header ----------//
    private ImageButton back_btn_02;
    private TextView user_num_02;

    // ユーザーアカウント
    private String gg_accont; // id
    private String gg_accont_02; // name

    //----------- スピナー
    private Spinner top_category_spi;
    private Spinner hinmoku_spi;

    private Spinner spinner_shouhin;

    private TextView tantou_spi_text;

    private String spi_item;

    //------------ スピナー　判別用
    // 品区
    private Map<String, String> spi_map = new HashMap<>();

    // 場所
    private Map<String ,String> somf_map = new HashMap<>();

    private TextView max_data_view; // max データ
    private int max_num;

    private String br_val_01_num;

    //---------- header END ----------//

    //---------- スナックバー　設定 ----//
    private Snackbar snackbar_01;
    private Snackbar snackbar_02;

    //---------- コンテンツ　部分 -------//

    private String br_val_01; // 品目区分


    // 所属部署　判別用
    private String bs_code;
    private String Comp_01_num;
    private String Comp_02_num;

    private String In_Comp_01_num;

    private TextView test_view, syouhin_mei;

    // エディットテキスト QR 手打ち　用　取得
    private String edit_qr;


    // バーコード　取得用 テキストビュー
    private TextView br_text;
    // 現品票コード用　（バーコード）
    private EditText qr_edit;

    //　バーコード　取得用 DB 変数 ------------------
    private String SH_col_2; // カラム 02
    private String SH_col_3; // カラム 03
    private String SH_col_6; // カラム 06
    private String SH_col_7; // カラム 07
    private String SH_col_8; // カラム 08
    private String SH_col_9; // カラム 09

    public String test_t;

    private String spi_str;

    private String pre_text;

    //---------- コンテンツ　部分 END -------//

    //---------- 棚卸し　ボトム コンテンツ　部分 Start -------//

    // ボタン
    private Button sql_load;
    private Button sql_read;

    private Button hide_btn;

    // エディットテキスト---------
    private EditText tana_edit;


    private String [] qr_text = new String[1];
    private TextView test_sql_label;
    private StringBuilder sb;
    private String [] arr;
    private String tmp_tanasuu;

    //---------- 棚卸し　ボトム コンテンツ　部分 END -------//

    //---------- 読み込み　書き込み　部分 start ----------
    private int item_num;
    private int count_num = 1;

    //---------- 読み込み　書き込み　部分 END ------------>

    //---------- アカウントデータ　送受信用 ----------------

    static final int RESULT_SUBACTIVITY = 1000;

    //---------- カレンダー用 テキストビュー ------------------
    private TextView tokei_view;

    //--------- エディットテキスト　フォーカス　外しよう VIew ------
    private TextView focusView_01;

    //--------- バーコード　読み取り　起動用　フローティングアクションボタン ----
    private FloatingActionButton fab_btn;

    private String tmp;
    private String tmp_qr;

    //-------- 比較用
    private String hinmoku_num; // 品目コード
    private String get_souko_num;

    private TextView souko_view;

    // 追加　ビュー
    private TextView add_view;

    //-------------- カレンダー用　変数
    private String csv_date_01, csv_date_02;
    public static String send_csv_file; // ---------- 保存　& 送信　CSV ファイル　格納
    private static String send_csv_file_path;
    private static String save_csv_file;


    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 480;
    private static final int MAX_FRAME_HEIGHT = 360;


    //------------ ディレクトリ
    // ディレクトリ　取得
    private  File dir;
    private  File target_csv;
    private  File new_csv;
    private File last_file;
    private File target_csv_new;
    private File csv_copy, csv_ppp, csv_desc, csv_desc_02,csv_desc_03;

    private String last_file_name;

    private String qr_edit_str;
    private String Shouhin_Name;

    // private int file_flg;
    private static int file_flg;

    private String get_QR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ber_code);

        //  -----   ソフトキーボードを表示させる
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // 初期設定 findViewById
        findViews();

        init();

        //-------------------- アカウント情報を受け取る-------------
        if(getIntent() != null) {
            gg_accont = getIntent().getStringExtra("get_account_num");
            // *** 倉庫　コード取得
            get_souko_num = getIntent().getStringExtra("souko_num");

            //＊＊＊　　担当者コード　取得　表示
            user_num_02.setText(gg_accont);

            //＊＊＊＊　　倉庫コード　取得　表示
            souko_view.setText(get_souko_num);

        }

        //------- エディットテキスト　インプットタイプ設定
        tana_edit.setInputType(InputType.TYPE_CLASS_NUMBER); // # 数字　入力
     //   qr_edit.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS); // # 数字　入力
       // qr_edit.setInputType(InputType.TYPE_CLASS_NUMBER); // 「JAN、現品票、」　数字　入力

        /**
         *  修正
         */
        // エディットボタンの　「完了」にする 　
        qr_edit.setImeOptions(RESULT_CANCELED);

        //******* 商品検索エディット フォーカス移動
        qr_edit.requestFocus();

        //---------- 時刻取得 END ---------------//

        // DBAdapter コンストラクタ　作成
        dbAdapter = new DBAdapter(getApplicationContext());

        /**
         *  ファイル TN- を　移動したら　/log/ をリネームして、　端末データ削除
         */
        File_Del();


        /**
         *  商品複数　スピナー
         */
        arr_shouhin_item.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                arr_spiner_item
        );
      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);


        /***
         *   商品コード　エディットテキスト　「確定」　ボタン　を押したら　Insert
         */

        qr_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //******* 確定ボタン　処理
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                    qr_edit_str = qr_edit.getText().toString();

                    // ****** エラーチェック
                    if (qr_edit_str.length() != 0) {

                        /**
                         *  現品票　検索　Function 実行
                         */
                        System.out.println("現品票　検索　開始");
                        // ========== 商品名　edittext
                        syouhin_mei.setText("");
                        arr_shouhin_item.clear();
                        spinner_shouhin.setVisibility(View.GONE);

                        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
                        SQLiteDatabase g_db = helper.getReadableDatabase();

                        // 現品票コード　edittext　取得

                        String[] arr_item = new String[4];

                        try {

                            Cursor cursor = g_db.rawQuery("SELECT Genpin_T_column_01, Genpin_T_column_02, Genpin_T_column_03, Genpin_T_column_04 " +
                                    " FROM Genpin_T WHERE Genpin_T_column_01=" + "\"" + qr_edit_str + "\"", null);

                            // ============================================================
                            // ==================== 商品検索結果が １　だった場合
                            // ============================================================
                            if(cursor != null && cursor.getCount() == 1) {

                                if (cursor.moveToFirst()) {

                                    //************* 商品名　取得
                                    int idx = cursor.getColumnIndex("Genpin_T_column_01");
                                    arr_item[0] = cursor.getString(idx);
                                    System.out.println("現品票コード出力テスト：：：" + arr_item[0]);
                                    //=== インサート用　リスト挿入
                                    Arr_Insert_Item.add(arr_item[0]); // 現品票番号

                                    idx = cursor.getColumnIndex("Genpin_T_column_02");
                                    arr_item[1] = cursor.getString(idx);
                                    System.out.println("現品票コード出力テスト：：：" + arr_item[1]);

                                    //=== インサート用　リスト挿入
                                    Arr_Insert_Item.add(arr_item[1]); // 商品名

                                    //****** 商品名表示
                                    syouhin_mei.setText(arr_item[1]);

                                    if (syouhin_mei.length() != 0) {
                                        // 商品名　格納
                                        Shouhin_Name = syouhin_mei.getText().toString();
                                    } else {
                                        toastMake("商品名が空です。", 0, -200);
                                        return false;
                                    }

                                    idx = cursor.getColumnIndex("Genpin_T_column_03");
                                    arr_item[2] = cursor.getString(idx);
                                    System.out.println("仕入先単価 出力テスト：：：" + arr_item[2]); // 仕入先単価

                                    //=== インサート用　リスト挿入
                                    Arr_Insert_Item.add(arr_item[2]); // 仕入先名

                                    idx = cursor.getColumnIndex("Genpin_T_column_04");
                                    arr_item[3] = cursor.getString(idx);
                                    System.out.println("仕入先名 出力テスト：：：" + arr_item[3]); // 仕入先名

                                    //=== インサート用　リスト挿入
                                    Arr_Insert_Item.add(arr_item[3]); // 仕入先名

                                } //****** moveToNext

                                tana_edit.requestFocus();

                           // ============================================================
                           // ================== 商品検索結果が 2 以上　だった場合
                           // ============================================================
                            } else if(cursor != null && cursor.getCount() >= 2){

                                toastMake("複数商品有り。対象商品をリストから選択してください。", 0,-200);

                                arr_spiner_item.clear();

                                if (cursor.moveToFirst()) {

                                 //   arr_shouhin_item.add("複数商品から選択してください。")

                                    int idx = 0;
                                    do {
                                 //       arr_genpin_item.add(cursor.getString(0));
                                        //************* 商品名　取得
                                  //      arr_shouhin_item.add(cursor.getString(1));

                                        arr_spiner_item.add(cursor.getString(0) + ":" + cursor.getString(1));
                                    } while (cursor.moveToNext());

                                } //****** moveToNext

                                for(String s : arr_spiner_item) {
                                    System.out.println(s);
                                }

                                //============== 複数商品 リスト表示 ==============
                                spinner_shouhin.setVisibility(View.VISIBLE);
                                spinner_shouhin.setFocusable(false);
                                spinner_shouhin.setAdapter(adapter);
                                setSpinner(arr_spiner_item);



                            } else {

                                toastMake("商品マスタに登録がありません。", 0,-200);
                                arr_shouhin_item.clear();
                                spinner_shouhin.setVisibility(View.GONE);
                                // ========== 商品名　edittext
                                syouhin_mei.setText("");

                                System.out.println("SELECT できない。");
                                return false;

                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            if(g_db != null) {
                                g_db.close();
                            }
                        }


                    } else {

                        /**
                         *  エラー処理
                         */
                        syouhin_mei.setText("");
                        arr_shouhin_item.clear();

                        Snackbar.make(v,"入力欄が空白です。「商品コード」を入力してください。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();

                        return false;
                    }
                }
                return true;
            }
        });


        fab_btn = (FloatingActionButton) findViewById(R.id.fab_btn);


        //---------- back btn バックボタン　を押されたら　値を返す
        back_btn_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 *  端末内　ファイル削除　チェック
                 */
                File_Del();

                // アカウント　ID , name を返す
                Intent intent = new Intent();
                if(user_num_02.getText() != null) {
                    String str = user_num_02.getText().toString();

                    // intentへ添え字付で値を保持させる
                    intent.putExtra("re_id",str);

                    // 返却したい結果ステータスをセットする
                    setResult( Activity.RESULT_OK, intent);

                    // アクティビティを終了させる
                    finish();
                }

            }
        });


        /**
         *  fab_btn が　タップされた時の処理 ---------------------------------
         */
        //--------------------- QR 読み取り　-------------------------//
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 integrator = new IntentIntegrator(ReadBerCode.this);


                // Fragmentで呼び出す場合
                //IntentIntegrator integrator = IntentIntegrator.forFragment(this);

                //---------------- スナックバー　処理 -----------------

                // キャプチャ画面の下方にメッセージを表示
                integrator.setPrompt("戻る　ボタン タップで「キャンセル」できます。");

                // キャプチャ画面起動
                integrator.initiateScan();

                //   new IntentIntegrator(ReadBerCode).initiateScan(IntentIntegrator.QR_CODE_TYPES);

            }
        });
        //--------------------- QR 読み取り END　-------------------------//


        //------------ 棚卸用　エディットテキスト　イベント --------------------

        tana_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // TODO 自動生成されたメソッド・スタブ
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ## フォーカスを受け取ったとき
                if(hasFocus) {
                    // ソフトキーボードの表示
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    tana_edit.setRawInputType(Configuration.KEYBOARD_QWERTY); // ソフトキーボード　の　デフォルト設定

                } else {
                    // ## フォーカスが外れた時
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

            }
        });

        /**
         *   棚卸　エディットテキスト　「確定ボタン」　が押されたら　インサート する
         */
        //---------------- 棚卸　エディットテキストで　ボタンを押された時の処理 ---------------------
        tana_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // エンターボタンが押されたら
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){

                        // ソフトキーボード 非表示
                    if (getCurrentFocus() != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    //**************　エラーチェック
                    //**** 商品コード　空チェック
                    if(qr_edit.length() == 0) {
                        Snackbar.make(v,"「商品コード」入力欄が空です。商品コードを入力してください。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                        return false;
                    }

                    if(syouhin_mei.length() == 0) {
                        Snackbar.make(v,"「商品名」が空です。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                        return false;
                    }

                    String s_name_valid_01 = syouhin_mei.getText().toString();
                    if (s_name_valid_01.equals("複数商品から選択してください。")) {
                        Snackbar.make(v,"「商品名」複数リストから選択されていません。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                        return false;
                    }

                    //**** 棚卸し数　空チェック
                    if(tana_edit.length() == 0) {

                        Snackbar.make(v,"「棚卸し数」入力欄が空です。棚卸し数を入力してください。",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();

                        return false;
                    }

                    /**
                     *  インサート処理  saveList()
                     */

                        saveList();

                    /**
                     *   CSVファイルに書き込みする  Write_CSV()
                     */
                        Write_CSV();

                    if(CollectionUtils.isEmpty(arr_shouhin_item)) {

                    } else {
                        arr_shouhin_item.clear();
                        spinner_shouhin.setVisibility(View.GONE);
                    }

                    Toast.makeText(getApplicationContext(), "「棚卸し数」を保存しました。", Toast.LENGTH_LONG).show();

                    // 初期化処理
                    init();

                    // エディットテキスト　値　取得
                    tmp_tanasuu = tana_edit.getText().toString();

                }

                return false;
            }
        });


        //------------ QR コード、　現品票コード 、　バーコード　エディットテキスト　イベント --------------------

        qr_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // TODO 自動生成されたメソッド・スタブ
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // ## フォーカスを受け取ったとき
                if(hasFocus) {
                    // ソフトキーボードの表示
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
               //    qr_edit.setRawInputType(Configuration.KEYBOARD_QWERTY); // ソフトキーボード　の　デフォルト設定

                } else {
                    // ## フォーカスが外れた時 非表示にする
                    /*
                    InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                     */

                    // ## フォーカスが外れた時
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

            }
        });


        //-------------- 一覧表示　ボタン　（読み込み）　-------------------
        sql_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 一覧　表示
               // Intent intent = new Intent(getApplication(), List_Display.class);
                Intent intent = new Intent(getApplication(), SelectSheetListView.class);

                // ユーザーコード
                String user_num = user_num_02.getText().toString();
                // 倉庫コード
                String souko_view_str = souko_view.getText().toString();

                intent.putExtra("get_account", user_num);
                intent.putExtra("souko_view_str", souko_view_str);

             //   startActivityForResult(intent,REQUESTED_LIST);
                startActivity(intent);

            //    finish();

            }
        });


    }  //------------- onCrate END ********************************************************
     // ******************************************************************************
    // -------------------------------------------------------------------------->

    //----------- エディットテキスト　QR、　バーコード、　現品票コード　非表示用　タッチイベント　
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        focusView_01.requestFocus();
        return super.onTouchEvent(event);
    }

    //----------------- メニュー　追加
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.qr_menu_01, menu);

        return true;
    }

    //----------------- メニューボタンが押された時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.qr_menu_01_btn:

                integrator = new IntentIntegrator(ReadBerCode.this);

                // キャプチャ画面の下方にメッセージを表示
                integrator.setPrompt("戻る　ボタン タップで「キャンセル」できます。");

                // キャプチャ画面起動
                integrator.initiateScan();

        }

        return true;
    }


    //------------------------- 端末が　横を向いても値を保存　しておく処理 -----------------------------------
    // 上記は、画面の回転やアプリケーションがバックグラウンドになった後メモリの不足等でインスタンスが破棄される場合など、
    // 情報の保存が必要になった際に呼び出されるメソッドです。

/*
    @Override
    protected void onSaveInstanceState(Bundle outSate) {
        super.onSaveInstanceState(outSate);

        // 表示用ラベル
        qr_edit = (EditText) findViewById(R.id.qr_edit);
        h_moku_m_text = (TextView) findViewById(R.id.h_moku_m_text);
        h_moku_b_text = (TextView) findViewById(R.id.h_moku_b_text);
        h_moku_text = (TextView) findViewById(R.id.h_moku_text);

        h_moku_text_area = (TextView) findViewById(R.id.h_moku_text_area); // 品目コード
        location_text = (TextView) findViewById(R.id.location_text); // 在庫場所

        //------------- tmp 変数に格納 -------------------
        h_value_01 = qr_edit.getText().toString();
        h_value_02 = h_moku_m_text.getText().toString();
        h_value_03 = h_moku_b_text.getText().toString();
        h_value_04 = h_moku_text.getText().toString();

        outSate.putString("TEXT_VIEW_STR_1", h_value_01);
        outSate.putString("TEXT_VIEW_STR_2", h_value_02);
        outSate.putString("TEXT_VIEW_STR_3", h_value_03);
        outSate.putString("TEXT_VIEW_STR_4", h_value_04);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        String h_in_value_01 = savedInstanceState.getString("TEXT_VIEW_STR_1");
        String h_in_value_02 = savedInstanceState.getString("TEXT_VIEW_STR_2");
        String h_in_value_03 = savedInstanceState.getString("TEXT_VIEW_STR_3");
        String h_in_value_04 = savedInstanceState.getString("TEXT_VIEW_STR_4");

        // 表示用ラベル
        qr_edit = (EditText) findViewById(R.id.qr_edit);
        h_moku_m_text = (TextView) findViewById(R.id.h_moku_m_text);
        h_moku_b_text = (TextView) findViewById(R.id.h_moku_b_text);
        h_moku_text = (TextView) findViewById(R.id.h_moku_text);

        br_text.setText(h_in_value_01);
        h_moku_m_text.setText(h_in_value_02);
        h_moku_b_text.setText(h_in_value_03);
        h_moku_text.setText(h_in_value_04);

    }
*/


    //------------------------- 端末が　横を向いても値を保存　しておく処理 END -----------------------------------


    //＊＊＊＊＊＊ コード読み取り　読み取った結果の取得　。　表示　＊＊＊＊＊＊
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanResult.getContents() == null) {
            return;
        }

        /**
         /* ********** QR データ　取得 **********
         */

        if (scanResult != null) {

            /**　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
             *  ＊＊＊＊＊＊＊＊＊ 現品票　読み込み　＊＊＊＊＊＊＊＊＊＊
             *  ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
             */
         //   if (get_QR.matches("^.*[A-Za-z0-9].*$]") || get_QR.length() < 13) {
         //   if (scanResult.getContents().length() < 13) {

                get_QR = (scanResult.getContents());

                Log.d("QR読みとり", "get_QR:::値:::" + get_QR);

                qr_edit.setText(get_QR);
                add_view.setVisibility(View.VISIBLE);

                add_view.setText("商品コード：" + scanResult.getContents());

                /**
                 *  現品票　検索　Function 実行
                 */
                System.out.println("＊＊＊ 現品票　検索　開始 ＊＊＊");

                TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
                SQLiteDatabase g_db = helper.getReadableDatabase();

                // 現品票コード　edittext　取得

                String[] arr_item = new String[4];

                try {

                    Cursor cursor = g_db.rawQuery("SELECT Genpin_T_column_01, Genpin_T_column_02, " +
                            "Genpin_T_column_03, Genpin_T_column_04 FROM Genpin_T WHERE Genpin_T_column_01=" + "\"" + get_QR + "\"", null);

                    if(cursor != null && cursor.getCount() == 1) {

                        if (cursor.moveToFirst()) {

                            Shouhin_List_Clear();

                            //************* 商品名　取得
                            int idx = cursor.getColumnIndex("Genpin_T_column_01");
                            arr_item[0] = cursor.getString(idx);
                            System.out.println("現品票コード出力テスト：：：" + arr_item[0]);

                            // === インサート用 Arraylist 挿入
                            Arr_Insert_Item.add(arr_item[0]);

                            idx = cursor.getColumnIndex("Genpin_T_column_02");
                            arr_item[1] = cursor.getString(idx);
                            System.out.println("現品票コード出力テスト：：：" + arr_item[1]);

                            // === インサート用 Arraylist 挿入
                            Arr_Insert_Item.add(arr_item[1]);

                            //****** 商品名表示
                            syouhin_mei.setText(arr_item[1]);

                            if (syouhin_mei.length() != 0) {
                                // 商品名　格納
                                Shouhin_Name = syouhin_mei.getText().toString();
                            } else {
                                toastMake("商品名が空です。", 0, -200);
                                return;
                            }

                            /**
                             *   仕入単価 , 仕入先名  ＊＊＊ 追加 ＊＊＊
                             */

                            idx = cursor.getColumnIndex("Genpin_T_column_03");
                            arr_item[2] = cursor.getString(idx);
                            System.out.println("仕入単価 出力テスト：：：" + arr_item[2]);

                            // === インサート用 Arraylist 挿入
                            Arr_Insert_Item.add(arr_item[2]);

                            idx = cursor.getColumnIndex("Genpin_T_column_04");
                            arr_item[3] = cursor.getString(idx);
                            System.out.println("仕入先名 出力テスト：：：" + arr_item[3]);

                            // === インサート用 Arraylist 挿入
                            Arr_Insert_Item.add(arr_item[3]);

                        } //****** moveToNext

                        tana_edit.requestFocus();


                    } else if (cursor != null && cursor.getCount() > 1) {

                        toastMake("JANコード。", 0,-200);

                        // ========== 商品名　edittext
                        syouhin_mei.setText("");

                        System.out.println("SELECT できない。");
                        return;

                    } else {

                        toastMake("商品マスタに登録がありません。", 0,-200);

                        // ========== 商品名　edittext
                        syouhin_mei.setText("");

                        System.out.println("SELECT できない。");
                        return;

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if(g_db != null) {
                        g_db.close();
                    }
                }

                tana_edit.requestFocus();

                /**　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
                 *  ＊＊＊＊＊＊＊＊＊ JAN コード　１３桁　読み込み　＊＊＊＊＊＊＊＊＊＊
                 *  ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
                 */
          //  } else if(get_QR.matches("[0-9]{13}]") && get_QR.startsWith("49")) {

            /*
            } else if(get_QR.matches("[0-9]{13}]") || get_QR.matches("[0-9]{8}")) {

                    toastMake("JANコード検索",0, -200);
                    System.out.println("JAN コード　１３桁　読み込み GOGO：：：");
                    Log.d("QR読みとり", "else if ::: get_QR:::値:::" + get_QR);

            } //===================== END IF　（）

             */

        } //======================================== END IF scanResult != null

    }// ----------------------- END

    //＊＊＊＊＊＊ コード読み取り　読み取った結果の取得　。　表示　END　＊＊＊＊＊＊


    // --------------------- トーストメッセージ表示 -----------------------
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this,message, Toast.LENGTH_LONG);

        // 位置調整
        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }
    // --------------------- トーストメッセージ表示 END ----------------------->



    /***
     *   品目　
     */

    /*
    private void Spiiner_03() {

        // ヘルパー　メソッド
        helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] arr_item = new String[2];

        ArrayList<String> Item_tb_item = new ArrayList<>();

        try {

            Cursor cursor = db.rawQuery("SELECT * from Item_tb;", null);

            while(cursor.moveToNext()) {

                int idx = cursor.getColumnIndex("item_tb_c_01");
                arr_item[0] = cursor.getString(idx);

                idx = cursor.getColumnIndex("item_tb_c_02");
                arr_item[1] = cursor.getString(idx);

                // 品目コード　テーブル　値取得
                Item_tb_item.add(arr_item[0] + ":" + arr_item[1]);

                //------ 比較用　ハッシュマップに　格納 ------
                spi_map.put(arr_item[0], arr_item[1]);

            }

            for(Map.Entry<String, String> entry : spi_map.entrySet()) {
                System.out.println("spi_map マップキー" + entry.getKey() + " : " + entry.getValue());
            }



        } finally {
            if(db != null) {
                db.close();
            }
        }

    }

     */


    /**
     *  品目　スピナー　アダプター　セット
     */
    private void Hinmoku_Spinner_set() {

        /*------------ スピナー 　処理 ----------------*/
        /*
        //    hinmoku_spi = (Spinner) findViewById(R.id.hinmoku_spi);

        //---------- ArrayAdapter -------------//
    //    ArrayAdapter<String> spi_3adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,Item_tb_item);
    //    spi_3adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // スピナーセット  品目コード　テーブル　
        hinmoku_spi.setAdapter(spi_3adapter);

         */

    }

    //    hinmoku_spi.setVisibility(View.GONE);


    /*
    private void getPr() {

        // ヘルパー　メソッド

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase Qr_hr = helper.getReadableDatabase();


        String [] arr_item =  new String[2];

        ArrayList<String> Qr_item = new ArrayList<>();

        try {

            Cursor cursor = Qr_hr.rawQuery("SELECT * FROM Somf_tb;", null);

            while(cursor.moveToNext()) {

                int idx = cursor.getColumnIndex("Somf_tb_01");

            //    int idx = cursor.getColumnIndex(TestOpenHelper.SONF_DB_C_01);
                arr_item[0] = cursor.getString(idx);
                System.out.println("マップテスト 文字" +  arr_item[0]);

             //   idx = cursor.getColumnIndex(TestOpenHelper.SONF_DB_C_02);
                idx = cursor.getColumnIndex("Somf_tb_02");
                arr_item[1] = cursor.getString(idx);

                System.out.println("マップテスト 文字" +  arr_item[1]);

                Qr_item.add(arr_item[0] + ":" + arr_item[1]);

                // 比較用　ハッシュマップ
                somf_map.put(arr_item[0],arr_item[1]);

            }

            for(Map.Entry<String, String> entry : somf_map.entrySet()) {
                System.out.println("マップキー" + entry.getKey() + " : " + entry.getValue());
            }

            System.out.println("マップテスト 文字");


        } finally {
            if(Qr_hr != null) {
                Qr_hr.close();
            }
        }

    }

     */



    /**
     * 初期値設定 (EditTextの入力欄は空白、※印は消す)
     * init()
     */
    private void init() {

        tana_edit.setText(""); // 商品コード
        qr_edit.setText(""); //棚卸し数


        qr_edit.requestFocus();      // 商品コードへ　カーソルを合わせる

        // *** 商品コード　タグ　非表示
        add_view.setVisibility(View.GONE);
        add_view.setText("");

        // ========== 商品名　edittext
        syouhin_mei.setText("");


    }


    /**
     * EditTextに入力したテキストをDBに登録
     * saveDB()
     *
     * 五十嵐プライヤー
     *
     * １: 担当者コード
     * ２：倉庫コード
     * ３：商品コード
     * ４：棚卸し数
     * ５　商品名
     * ６　仕入先名
     * ７　仕入単価
     */

    private void saveList() {


        //------- 棚卸し数が　空の　場合の処理 ------>
        // エラー処理
        if(tana_edit.length() == 0) {

           Toast.makeText(ReadBerCode.this, "入力エラー「棚卸し数」が空です。", Toast.LENGTH_SHORT).show();

        } else {
            // エディットテキストが　空じゃ　ない場合

            //---------- その他の項目の　NUll チェック ------------//

            br_val_01_num = br_val_01; // 品区 取得

            /**
             *   商品コード　&  棚卸し数　取得
             */

            // 商品コード
            String in_tana_edit = tana_edit.getText().toString();
            // 棚卸し数
            String in_qr_edit = qr_edit.getText().toString();
            // 商品名
            String in_qe_shouhin = syouhin_mei.getText().toString();

            // DB への　登録
            DBAdapter dbAdapter = new DBAdapter(this);

            dbAdapter.openDB();

            /**
             *  １：担当者コード : gg_accont
             *  ２：倉庫コード：get_souko_num
             *  ３：商品コード：qr_edit
             *  ４：棚卸し数：tana_edit
             *  ５　商品名
             *  ６　仕入先名
             *  ７　仕入単価
             */

            dbAdapter.saveDB(gg_accont,get_souko_num,in_qr_edit,in_tana_edit,in_qe_shouhin
                    ,Arr_Insert_Item.get(2), Arr_Insert_Item.get(3));   // DBに登録

            System.out.println("インサート　完了");

            dbAdapter.closeDB();  // DBを閉じる

            //　複数選択 スピナー　非表示
            spinner_shouhin.setVisibility(View.GONE);

            // インサート用　arraylist 初期化
            Arr_Insert_Item.clear();

            init();     // 初期値設定
        }

    } //------------ END saveList ------->


    /*-------------- findeVIewById 紐づけよう 関数 ---------------------*/
    private void findViews() {

        //---------- 画面　全体を　取得 ----------//
        mainLayout_ber = (ScrollView) findViewById(R.id.mainLayout_ber);

        //---------- header ----------//
        back_btn_02 = (ImageButton) findViewById(R.id.back_btn_02);
        user_num_02 = (TextView) findViewById(R.id.user_num_02);



        //---------- ボトム　コンテンツ部分 Start ----------//
        //------- エディットテキスト
        tana_edit = (EditText) findViewById(R.id.tana_edit);

        //------- ボタン
        sql_load = (Button) findViewById(R.id.sql_load);

        //---------- ボトム　コンテンツ部分 END ----------//

        //------ QR コード　取得　編集用　エディットテキスト ------------
        qr_edit = (EditText) findViewById(R.id.qr_edit);

        //-------- エディットテキスト　フォーカス　外し用 Veiw ----------
        focusView_01 = (TextView) findViewById(R.id.focusView_01);


        //----テスト用　ビュー
       // test_view = (TextView) findViewById(R.id.test_view);

        // 倉庫コード　表示用
        souko_view = (TextView) findViewById(R.id.souko_view);

        //　追加ビュー
        add_view = (TextView) findViewById(R.id.add_view);

        //*** 商品名　表示用
        syouhin_mei = findViewById(R.id.syouhin_mei);

        // スピナー （商品名　複数用）
        spinner_shouhin = findViewById(R.id.spinner_shouhin);
        spinner_shouhin.setVisibility(View.GONE);

    }


    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
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
        send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + gg_accont + ".csv";

    }


    // データベースから読んで　CSV ファイルとして、　ファイルを　Android 端末内に保存
    private void Write_CSV() {

        // 端末の固有識別番号の取得
        String androidId = Settings.Secure.getString(
                this.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        //------------- CSV ファイル名用　時間取得
        //Calendarクラスのオブジェクトを生成する
        Calendar cl = Calendar.getInstance();

        // SimpleDateFormatクラスを使用して、パターンを設定する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd" + "-" + "HHmmss");
        csv_date_01 = sdf.format(cl.getTime());

        // 出力　ファイル名
    //    send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + gg_accont + ".csv";
        send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + gg_accont + '-' + androidId + ".csv";

        //************************** ファルダ　作成 **************************
        String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/log/";
        File root = new File(path);

        if(!root.exists()) {
            root.mkdir();
        }


        target_csv = new File( getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+ "/log/" + send_csv_file);

        target_csv_new = new File( getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+ "/" + send_csv_file);


        MediaScannerConnection.scanFile(this, new String[]{target_csv.getAbsolutePath()}, null, null);

        MediaScannerConnection.scanFile(this, new String[]{target_csv_new.getAbsolutePath()}, null, null);

        Uri contentUri = Uri.fromFile(target_csv);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);

     //   target_csv.getParentFile().mkdir();

        dbAdapter.openDB();

        //-------- DB　格納用 ----------
        // Send_TB_02 の　データ取得
        Cursor cursor = dbAdapter.getDB(columns);

        //   int id;  // Send_TB_02 カラム　2
        String [] arr_item = new String[7]; // Send_TB_02 カラム　2 ～ 10
        ArrayList<String> send_item = new ArrayList<String>();

        String place; // カラム　２　場所コード　格納用変数
        String place_get = "";

        //---------- 合計　4 カラム ------------//
        FileOutputStream fileOutputStream;

        try {

            // 書き込み用　ストリーム
            fileOutputStream = new FileOutputStream(target_csv, true);
       //     FileOutputStream outStream = openFileOutput(target_csv, MODE_PRIVATE);
            // ファイル書き込み　準備
            OutputStreamWriter out = new  OutputStreamWriter(fileOutputStream, "Shift_JIS");

            PrintWriter printWriter = new PrintWriter(out);

            if (cursor.moveToFirst()) {

                do {

                    //----- カラム 1  （担当者コード）
                    arr_item[0] = cursor.getString(1);

                    System.out.println("出力テスト" + arr_item[0]);

                    //----- カラム 2  （倉庫コード）
                    arr_item[1] = cursor.getString(2);

                    //----- カラム 3 （商品コード）
               //     arr_item[2] = cursor.getString(3);
                    arr_item[2] = cursor.getString(3);
                    System.out.println("出力テスト" + arr_item[2]);

                    //----- カラム 4 （棚卸し 個数）
                    arr_item[3] = cursor.getString(4);

                    /**
                     *  商品名 , 仕入先名 , 仕入単価  ＊＊＊ 追加 ＊＊＊
                     */
                    //----- カラム 5 （商品名）
                    arr_item[4] = cursor.getString(5);

                    //----- カラム 5 （仕入単価）
                    arr_item[5] = cursor.getString(6);

                    //----- カラム 5 （仕入先名）
                    arr_item[6] = cursor.getString(7);

                    // , 区切り （カンマ）
             //      String record = arr_item[0] + "," + arr_item[1] + "," + arr_item[2] + "," + arr_item[3];
                    // タブ区切り
                    // ========== ユーザーID [0]  倉庫番号 [1]  現品票コード [2]  商品名 [3]  棚卸し数 [4]  仕入先名 [6]  仕入単価 [5]
                    String record = arr_item[0] + "\t" + arr_item[1] + "\t" + arr_item[2] + "\t" + arr_item[3] +
                            "\t" + arr_item[4] + "\t" + arr_item[6] + "\t" + arr_item[5];

                    /**
                     *  空白行の削除
                     */
                    if(record.substring(record.length() - 1).equals("\n")) {
                        record = record.substring(0, record.length() - 1);
                    }

                    String get_record;
                    get_record = record.trim();

                    printWriter.println(get_record);

                    // 書き込み
                    printWriter.flush();

                } while (cursor.moveToNext()); //-----END while

                // 閉じる
                printWriter.close();

            }

            cursor.close();
        /*
        }catch (FileNotFoundException e) {
            e.printStackTrace();

            // フォルダへのアクセス権限がない場合の表示
            Toast ts = Toast.makeText(this, "アクセス権限がありません", Toast.LENGTH_SHORT);
            ts.show();

         */

        }catch (Exception e) {
            Toast ts = Toast.makeText(this, "CSV出力が失敗しました", Toast.LENGTH_SHORT);
            ts.show();

        } finally {

            if(dbAdapter != null) {
                dbAdapter.closeDB();
            }

            // Androidに認識させる
            MediaScannerConnection.scanFile(this,
                    new String[]{Environment.DIRECTORY_DOCUMENTS +"/log/" + send_csv_file},
                    new String[]{"application/octet-stream"}, null);

            // Androidに認識させる
            MediaScannerConnection.scanFile(this,
                    new String[]{Environment.DIRECTORY_DOCUMENTS +"/" + send_csv_file},
                    new String[]{"application/octet-stream"}, null);

            MediaScannerConnection.scanFile(this, new String[]{target_csv.getAbsolutePath()}, null, null);

            MediaScannerConnection.scanFile(this, new String[]{target_csv_new.getAbsolutePath()}, null, null);


            /**
             *  ファイルのソート
             */

           // String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/log/";
            File diffDirectory = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/log/");

            // listFiles() で　ファイル一覧　を　取得
            File[] targetFiles = diffDirectory.listFiles();

            Arrays.sort(targetFiles, new Comparator<File>(){

                public int compare(File file1, File file2) {
                    return file1.getName().compareTo(file2.getName());
                }
            });

            if (targetFiles != null) {
                for(int i = 0; i < targetFiles.length; i++) {
                    System.out.println(targetFiles[i].getName());
                }

                /**
                 *  降順　ファイル名取得
                 */
                last_file = targetFiles[targetFiles.length - 1];
                last_file_name = targetFiles[targetFiles.length - 1].getName();
            }

            System.out.println(targetFiles[targetFiles.length - 1] + "最新");
            System.out.println("last_file:名" + last_file);
            System.out.println("last_file_name:名" + last_file_name);

            //**********  最新ファイルを取得
            Get_Last_FILE();

        }



    }//------------------------- END SQLliste => csv ファイル


    private void Get_Last_FILE() {

        csv_copy = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/log/" + last_file_name);
        csv_ppp = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + last_file_name);

        try {

            FileInputStream fileIn = new FileInputStream(csv_copy);
            FileOutputStream fileOut = new FileOutputStream(csv_ppp);

            byte[] buf = new byte[1024];
            int len;

            while ((len = fileIn.read(buf)) != -1) {
                fileOut.write(buf, 0, len);
            }

            // ファイル内容に書き込み
            fileOut.flush();

            System.out.println("ファイルコピー最新：：：" + fileOut);

            fileOut.close();
            fileIn.close();

            MediaScannerConnection.scanFile(this, new String[]{csv_ppp.getAbsolutePath()}, null, null);

            // Androidに認識させる
            MediaScannerConnection.scanFile(this,
                    //    new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + last_file},
                    new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + last_file_name},
                    new String[]{"application/octet-stream"}, null);

            Uri contentUri = Uri.fromFile(csv_ppp);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            getApplicationContext().sendBroadcast(mediaScanIntent);

            /**
             * 　ファイル削除
             */
            File diffDirectory = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/");

            System.out.println("diffDirectory" + diffDirectory);

            // listFiles() で　ファイル一覧　を　取得
            File[] targetFiles_02 = diffDirectory.listFiles();

            for(File f : targetFiles_02) {

                if (f.getName().equals(last_file_name)) {
                    continue;
                }
                f.delete();
            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    } //================= END function


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


    // リスナーを登録
    private void setSpinner(final ArrayList<String> spinner){
        spinner_shouhin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {

                // 初回の動作
                /*
                if (spinner_shouhin.isFocusable() == false) {
                    spinner_shouhin.setFocusable(true);
                    return;
                }
                 */

                // 初回以降の動作
                // --- 処理 ---
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                System.out.println("アイテムitem:::" + item);



                String spinner_text = (String) parent.getAdapter().getItem(position);
                System.out.println("テキストtext:::" + spinner_text);

                String [] arr_spi_Item = spinner_text.split(":");

                syouhin_mei.setText(arr_spi_Item[1]);

                String Genpin_Tmp = arr_spi_Item[0];

                if (spinner_text.length() > 0) {

                    /**
                     *   検索処理 => 商品名で検索
                     */
                    TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
                    SQLiteDatabase spiner_db = helper.getReadableDatabase();

                    // 現品票コード　edittext　取得

                    String[] arr_item = new String[4];

                    try {

                        String syouhin_mei_TEXT = syouhin_mei.getText().toString();
                        System.out.println("スピナー商品名 テキスト syouhin_mei_TEXT:::" + syouhin_mei_TEXT);

                        Cursor cursor = spiner_db.rawQuery("SELECT Genpin_T_column_01, Genpin_T_column_02, Genpin_T_column_03, Genpin_T_column_04 " +
                                " FROM Genpin_T WHERE Genpin_T_column_02=" + "\"" + syouhin_mei_TEXT + "\"", null);

                        if(cursor != null && cursor.getCount() > 0) {

                            if (cursor.moveToFirst()) {

                                //************* 商品名　取得
                                int idx = cursor.getColumnIndex("Genpin_T_column_01");
                                arr_item[0] = cursor.getString(idx);
                                System.out.println("スピナー 出力テスト 01：：：" + arr_item[0]);

                                // === インサート用 Arraylist 挿入
                                Arr_Insert_Item.add(arr_item[0]);

                                idx = cursor.getColumnIndex("Genpin_T_column_02");
                                arr_item[1] = cursor.getString(idx);
                                System.out.println("スピナー 出力テスト 02：：：" + arr_item[1]);

                                // === インサート用 Arraylist 挿入
                                Arr_Insert_Item.add(arr_item[1]);

                                //****** 商品名表示
                                syouhin_mei.setText(arr_item[1]);

                                if (syouhin_mei.length() != 0) {
                                    // 商品名　格納
                                    Shouhin_Name = syouhin_mei.getText().toString();
                                } else {
                                    toastMake("商品名が空です。", 0, -200);
                                    return;
                                }

                                /**
                                 *   仕入単価 , 仕入先名  ＊＊＊ 追加 ＊＊＊
                                 */

                                idx = cursor.getColumnIndex("Genpin_T_column_03");
                                arr_item[2] = cursor.getString(idx);
                                System.out.println("スピナー 出力テスト 03：：：" + arr_item[2]);

                                // === インサート用 Arraylist 挿入
                                Arr_Insert_Item.add(arr_item[2]);

                                idx = cursor.getColumnIndex("Genpin_T_column_04");
                                arr_item[3] = cursor.getString(idx);
                                System.out.println("スピナー 出力テスト 04：：：" + arr_item[3]);

                                // === インサート用 Arraylist 挿入
                                Arr_Insert_Item.add(arr_item[3]);

                            }

                        } else {

                            // 該当データなし
                            toastMake("該当データがありません。", 0, -200);

                        }


                    }catch (SQLException e) {
                        e.printStackTrace();
                    }catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if(spiner_db != null) {
                            spiner_db.close();
                        }
                    }

                    // フォーカス移動
                    tana_edit.requestFocus();

                }

            }


            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    /**
     *  複数商品　リスト　初期化
     */
    private void Shouhin_List_Clear() {

        syouhin_mei.setText("");
        arr_shouhin_item.clear();
        spinner_shouhin.setVisibility(View.GONE);

    }

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

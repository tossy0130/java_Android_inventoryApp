package com.example.jhanbai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SelectSheetListView extends AppCompatActivity {

    private DBAdapter dbAdapter;
    private MyBaseAdapter myBaseAdapter;
    private List<MyListItem> items;
    private ListView mListView03;
    private MyListItem myListItem; // マイリストアイテム　の　オブジェクト

    private final int R_BACK = 2000;

    private TestOpenHelper helper; // テスト　ヘルパー　オブジェクト

    /*---- header アカウント情報 取得 ----------*/
    private String sr_account_num;
    private String sr_account_name;
    private ImageButton back_btn_05;
    private TextView user_num_05;
    /*---- header アカウント情報 取得 END ----------*/

    // 参照する DB のカラムを入れる
    private String[] columns = null;

    // text UPDATE 用　変数 -----------------
    private TextInputLayout list_update_box;

    private EditText list_update;
    private String list_update_num;
    private int listId_up;

    private String update_text;

    //------------- スワイプ　テスト

    // X軸最低スワイプ距離
    private static final int SWIPE_MIN_DISTANCE = 50;
    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 250;
    // タッチイベントを処理するためのインタフェース
    private GestureDetector mGestureDetector;

    //-------------- アップデート　変更用　画面
    private FrameLayout contentsBackground_03;

    private TextView l_01;
    private TextView l_02;
    private TextView l_03;
    private TextView l_04;
    private TextView l_05;
    private TextView l_06;
    private TextView l_07;

    private TextView d_01;
    private TextView d_02;
    private TextView d_03;
    private TextView d_04;
    private TextView d_05;
    private TextView d_06;
    private TextView d_07;

    // 商品名ラベル、　商品名
    private TextView text10Product, text10_02;


    //*********** 追加  アップデート時　「商品コード」　「修正」ラベル　「棚卸し数」 「商品名」
    private TextView shouhin_c_01_num, tana_label, tanaoroshi_update,tanaoroshi_update_name;

    private TextView item_count_view;

    private LinearLayout linear_01;
    private LinearLayout linear_02;
    private LinearLayout linear_03;
    private LinearLayout linear_04;
    private LinearLayout linear_05;



    private View list_line_col_02;

    // ユーザー id 取得用　ハッシュ
    private HashMap<String, String> list_user_H = new HashMap<>();
    // 場所　取得用　ハッシュ
    private HashMap<String, String> Place_code_H = new HashMap<>();

    // リスト　フレームレイアウト
    private FrameLayout header_account_view;

    //-------------- カレンダー用　変数
    private String csv_date_01, csv_date_02;
    public static String send_csv_file; // ---------- 保存　& 送信　CSV ファイル　格納
    private static String send_csv_file_path;
    private static String save_csv_file;

    // ユーザーアカウント
    private String gg_accont; // id

    private  File dir;
    private  File target_csv;
    private  File new_csv;
    private File last_file;
    private File target_csv_new;
    private File csv_copy, csv_ppp, csv_desc, csv_desc_02,csv_desc_03;
    private String last_file_name;

    private String tmp_user,tmp_user_02;

    private String get_souko_c;
    private TextView souko_num_003;

    // private int file_flg;
    private static int file_flg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sheet_list_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //------- user 説明
      //  User_hint();

        // DBAdapter の　コントラクタを呼ぶ
        dbAdapter = new DBAdapter(this);
        // Mylist items の list 作成
        items = new ArrayList<>();
        //  MyBaseAdapte の　コンストラクタを呼び出す
        myBaseAdapter = new MyBaseAdapter(this, items);
        // ListVIew の結び付け

          mListView03 = (ListView) findViewById(R.id.listView03);
      //  mListView03 = (EnhancedListView) findViewById(R.id.listView03);

        // 件数用　
        item_count_view = (TextView) findViewById(R.id.item_count_view);

 //      loadMyList(); // DBを読み込む＆更新する処理   2021-07-26 移動

        // ユーザーコード取得用
        user_num_05 = (TextView)  findViewById(R.id.user_num_05);
        // 倉庫コード取得用
        souko_num_003 = (TextView) findViewById(R.id.souko_num_003);


        back_btn_05 = (ImageButton) findViewById(R.id.back_btn_05);

        //------- テキスト　アップデート ---------------------------

        list_update_box = (TextInputLayout) findViewById(R.id.list_update_box);

        list_update = (EditText) findViewById(R.id.list_update);

        //******* 「完了」に変更
    //    list_update.setImeOptions(DEFAULT_KEYS_DISABLE);

        // 非表示
        list_update_box.setVisibility(View.GONE);
        list_update.setVisibility(View.GONE);
        // 初期設定
        list_update.setInputType(InputType.TYPE_CLASS_NUMBER);
       // list_update.setImeOptions(EditorInfo.IME_ACTION_SEND);
        list_update.setText("");

        header_account_view = (FrameLayout) findViewById(R.id.header_account_view);

        //--------------- スワイプ　テスト -------------------

        //--------------- アップデート　更新画面 コンポーネント　一覧 --------------
        contentsBackground_03 = (FrameLayout) findViewById(R.id.contentsBackground_03);

        contentsBackground_03.setVisibility(View.GONE);

        l_01 = (TextView) findViewById(R.id.l_01);
        l_02 = (TextView) findViewById(R.id.l_02);
        l_03 = (TextView) findViewById(R.id.l_03);

        l_06 = (TextView) findViewById(R.id.l_06);


        d_01 = (TextView) findViewById(R.id.d_01);
        d_02 = (TextView) findViewById(R.id.d_02);
        d_03 = (TextView) findViewById(R.id.d_03);

        // 商品名　追加
        l_04 = (TextView) findViewById(R.id.l_04);
        d_04 = (TextView) findViewById(R.id.d_04);

        d_06 = (TextView) findViewById(R.id.d_06);

        //*********** 追加
        shouhin_c_01_num = (TextView) findViewById(R.id.shouhin_c_01_num);
        tana_label = (TextView) findViewById(R.id.tana_label);
        tanaoroshi_update = (TextView) findViewById(R.id.tanaoroshi_update);

        //**** 商品名追加  2021-07-26
        tanaoroshi_update_name = (TextView) findViewById(R.id.tanaoroshi_update_name);


        // 非表示
        l_01.setVisibility(View.GONE);
        l_02.setVisibility(View.GONE);
        l_03.setVisibility(View.GONE);
        l_06.setVisibility(View.GONE);

        d_01.setVisibility(View.GONE);
        d_02.setVisibility(View.GONE);
        d_03.setVisibility(View.GONE);
        d_06.setVisibility(View.GONE);

        // 商品名追加 2021-07-26
        l_04.setVisibility(View.GONE);
        d_04.setVisibility(View.GONE);

        //********** 追加
        shouhin_c_01_num.setVisibility(View.GONE);
        tana_label.setVisibility(View.GONE);
        tanaoroshi_update.setVisibility(View.GONE);

        // 商品名追加 2021-07-26
        tanaoroshi_update_name.setVisibility(View.GONE);



        if(getIntent() != null) {

            /**
             *  ユーザーコード取得
             */
            tmp_user = getIntent().getStringExtra("get_account");
            user_num_05.setText(tmp_user);
            gg_accont = (String) user_num_05.getText();

            // ユーザーコード取得
    //        tmp_user_02 = getIntent().getStringExtra("user_num");
            // 倉庫コード取得
            get_souko_c = getIntent().getStringExtra("souko_view_str");

            //******** 倉庫コードのセット
            souko_num_003.setText(get_souko_c);

        }

        // 2021-07-26 移動
        loadMyList(); // DBを読み込む＆更新する処理

        int item = items.size();

        /**
         *  ファイル TN- を　移動したら　/log/ をリネームして、　端末データ削除
         */
        File_Del();

        //---------- header END ----------//

        back_btn_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 戻る　ボタンの処理

                /**
                 *  端末内　ファイル削除　チェック
                 */
                File_Del();

                // アカウント　ID , name を返す
                Intent intent = new Intent();
                if(user_num_05.getText() != null) {
                    String str = user_num_05.getText().toString();

                    // intentへ添え字付で値を保持させる
                    intent.putExtra("re_id", str);

                    // 返却したい結果ステータスをセットする
                    setResult(ReadBerCode.RESULT_OK, intent);

                    setResult(TopMenu.RESULT_OK, intent);

                    // アクティビティを終了させる
                    finish();
                }

            }
        });


        /**
         *  アイテムロング　クリックリスナー　
         *
         *   アップデート update 処理
         */
        mListView03.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                //--------------- アラートダイヤログ　棚卸し数変更　設定 ---------------//
                // タイトル
                TextView titleView;
                titleView = new TextView(SelectSheetListView.this);
                titleView.setText("【 棚 卸 数 】 の変更");
                titleView.setTextSize(20);
                titleView.setTextColor(Color.WHITE);
                titleView.setBackgroundColor(getResources().getColor(R.color.back_color_01));
                titleView.setPadding(20, 20, 20, 20);
                titleView.setGravity(Gravity.CENTER);

                //-------------- アラートログの表示 開始 -------------- //
                AlertDialog.Builder bilder = new AlertDialog.Builder(SelectSheetListView.this);

                view.setBackgroundColor(getResources().getColor(R.color.back_color_01));



                // ダイアログの項目
             //   bilder.setTitle("【 棚 卸 数 】 の変更");
                bilder.setCustomTitle(titleView);
                bilder.setMessage("棚卸数を変更しますか？");

                bilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // ID を取得する
                        myListItem = items.get(position);
                        listId_up = myListItem.getId(); // listId_up => int

                        dbAdapter.openDB();

                        view.setBackgroundColor(getResources().getColor(R.color.back_color_01));

                        //------　棚卸し　アップデート用 コンポーネント------------

                        contentsBackground_03.setVisibility(View.VISIBLE);

                        list_update_box.setVisibility(View.VISIBLE); // 表示
                        list_update.setVisibility(View.VISIBLE); // 表示

                        list_update.requestFocus(); // EditTextにフォーカスを移動


                        //------ アップデート用　レイアウト 表示

                   //     contentsBackground_03.setVisibility(View.VISIBLE);

                        fadein();

                        l_01.setVisibility(View.VISIBLE);
                        l_02.setVisibility(View.VISIBLE);
                        l_03.setVisibility(View.VISIBLE);


                        l_04.setVisibility(View.VISIBLE);

                        l_06.setVisibility(View.VISIBLE);


                        d_01.setVisibility(View.VISIBLE);
                        d_02.setVisibility(View.VISIBLE);
                        d_03.setVisibility(View.VISIBLE);

                        // 商品名 表示
                        l_04.setVisibility(View.VISIBLE);
                        d_04.setVisibility(View.VISIBLE);

                        d_06.setVisibility(View.VISIBLE);



                        //*********** 追加
                        //------ 商品コード表示 （アップデート時）
                        shouhin_c_01_num.setVisibility(View.VISIBLE);
                        shouhin_c_01_num.setText("【 商品コード 】：" + myListItem.Send_Num_03);

                        tana_label.setVisibility(View.VISIBLE);

                        //------ 棚卸し数表示　（アップデート時）
                        tanaoroshi_update.setVisibility(View.VISIBLE);
                        tanaoroshi_update.setText("【 棚卸し数 】：" + myListItem.Send_Num_04);

                        // 商品名 （アップデート時）
                        tanaoroshi_update_name.setVisibility(View.VISIBLE);
                        tanaoroshi_update_name.setText("【　商品名　】：" + myListItem.Send_Num_05);

                        //************** データ格納 *******************

                        //****************** 担当者コード　表示
                        d_01.setText(myListItem.getSend_Num_01());

                        // 倉庫コード
                        d_02.setText(myListItem.getSend_Num_02());
                        // 商品コード
                        d_03.setText(myListItem.getSend_Num_03());
                        // 棚卸し　数
                        d_06.setText(myListItem.getSend_Num_04());

                        // 商品名 追加
                        d_04.setText(myListItem.getSend_Num_05());

                        list_update.setText("");
                     //   list_update.setText(myListItem.getSend_Num_08());

                        // 変更用　エディットテキスト　値　取得
                        update_text = list_update.getText().toString();

                        list_update.setFocusable(true);

                        list_update.requestFocus();

                        list_update.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {

                                // TODO 自動生成されたメソッド・スタブ
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                // ## フォーカスを受け取ったとき
                                if(hasFocus) {

                            // ソフトキーボードの表示
                            inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                            list_update.setRawInputType(Configuration.KEYBOARD_QWERTY); // ソフトキーボード　の　デフォルト設定


                                } else {
                                    // ## フォーカスが外れた時
                                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                                }

                            }
                        });


                        //---------------- 棚卸　エディットテキストで　ボタンを押された時の処理 ---------------------
                        list_update.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                                // エンターボタンが押されたら
                                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){

                                    // ソフトキーボード 非表示
                                    if (getCurrentFocus() != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                    }

                                    // エディットテキスト　値　取得
                                    list_update_num = list_update.getText().toString();

                                    String up_num = myListItem.getSend_Num_03();

                                    if(list_update_num.equals("") || list_update_num.length() == 0 || list_update_num == null) {

                                     //   list_update.setText(myListItem.getSend_Num_08());

                                        toastMake("入力エラー「商品コード」が空です。",-200,0);

                                        loadMyList();

                                        contentsBackground_03.setVisibility(View.GONE);
                                        list_update_box.setVisibility(View.GONE);
                                        list_update.setVisibility(View.GONE);


                                        fadeout();

                                        l_01.setVisibility(View.GONE);
                                        l_02.setVisibility(View.GONE);
                                        l_03.setVisibility(View.GONE);
                                        l_06.setVisibility(View.GONE);

                                        d_01.setVisibility(View.GONE);
                                        d_02.setVisibility(View.GONE);
                                        d_03.setVisibility(View.GONE);
                                        d_06.setVisibility(View.GONE);

                                        //　商品名　非表示
                                        l_04.setVisibility(View.GONE);
                                        d_04.setVisibility(View.GONE);
                                        tanaoroshi_update_name.setVisibility(View.GONE);

                                        //****** 追加
                                        shouhin_c_01_num.setVisibility(View.GONE);
                                        tana_label.setVisibility(View.GONE);
                                        tanaoroshi_update.setVisibility(View.GONE);

                                        return false;

                                    } else {

                                        // アップデート処理
                                        dbAdapter.selectUpdate(list_update_num, String.valueOf(listId_up));

                                        System.out.println("棚卸し数　アップデート完了");

                                        Toast.makeText(getApplicationContext(), "棚卸し数を変更しました。", Toast.LENGTH_LONG).show();

                                        dbAdapter.closeDB();    // DBを閉じる
                                        loadMyList();

                                        // 入力された棚卸し数

                                        /**
                                         * CSV 出力処理
                                         */
                                        Write_CSV();


                                        // エディットテキスト　初期化
                                        //    list_update.setText("");
                                        // 非表示
                                        contentsBackground_03.setVisibility(View.GONE);
                                        list_update_box.setVisibility(View.GONE);
                                        list_update.setVisibility(View.GONE);


                                        fadeout();

                                        l_01.setVisibility(View.GONE);
                                        l_02.setVisibility(View.GONE);
                                        l_03.setVisibility(View.GONE);
                                        l_06.setVisibility(View.GONE);


                                        d_01.setVisibility(View.GONE);
                                        d_02.setVisibility(View.GONE);
                                        d_03.setVisibility(View.GONE);
                                        d_06.setVisibility(View.GONE);

                                        //　商品名　非表示
                                        l_04.setVisibility(View.GONE);
                                        d_04.setVisibility(View.GONE);
                                        tanaoroshi_update_name.setVisibility(View.GONE);

                                        //****** 追加
                                        shouhin_c_01_num.setVisibility(View.GONE);
                                        tana_label.setVisibility(View.GONE);
                                        tanaoroshi_update.setVisibility(View.GONE);


                                        return true;
                                    }
                                }

                                return false;
                            }
                        });
                    }
                });


                bilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadMyList();
                    }
                });


                // ダイアログの表示
                AlertDialog dialog = bilder.create();
                dialog.show();

            }
        });


        /**
         *  アイテムロング　クリックリスナー　
         *
         *   アップデート delete 処理
         */

        mListView03.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                /**
                 * @param parent ListView
                 * @param view 選択した項目
                 * @param position 選択した項目の添え字
                 * @param id 選択した項目のID
                 */

                //--------------- アラートダイヤログ　削除　設定 ---------------//
                // タイトル
                TextView titleView;
                titleView = new TextView(SelectSheetListView.this);
                titleView.setText("削除");
                titleView.setTextSize(20);
                titleView.setTextColor(Color.WHITE);
                titleView.setBackgroundColor(getResources().getColor(R.color.back_color_03));
                titleView.setPadding(20, 20, 20, 20);
                titleView.setGravity(Gravity.CENTER);


                //-------------- アラートログの表示 開始 -------------- //
                AlertDialog.Builder bilder = new AlertDialog.Builder(SelectSheetListView.this);

                view.setBackgroundColor(getResources().getColor(R.color.back_color_03));

                // ダイアログの項目
                bilder.setCustomTitle(titleView);
              //  bilder.setTitle("削除");
                bilder.setMessage("削除しますか？");

                //------- OK の時の処理 ----------//
                bilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // ID を取得する
                        myListItem = items.get(position);
                        int listId = myListItem.getId();

                        //------------ 削除　処理　START　----------------
                        dbAdapter.openDB(); // データベース　読み書き　OK

                        // DBから取得したIDが入っているデータを削除する
                        dbAdapter.selectDelete(String.valueOf(listId));

                        Log.d("ログ： アイテムクリック削除　OK",String.valueOf(listId));

                        dbAdapter.closeDB();    // DBを閉じる
                        //　データを更新して、画面に表示
                        loadMyList();

                        /**
                         *  CSV 作成　出力
                         */
                        Write_CSV();

                        //------------ 削除　処理　END　----------------

                    }
                });

                bilder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadMyList();
                    }
                });
                // ダイアログの表示
                AlertDialog dialog = bilder.create();
                dialog.show();

                return true; //　***** false を返すと　onItemLongClick()後、onItemClick()が動いてしまうので注意 *******
            }
    });


    } //------------------------------------------------------------ END Oncreate


    /**
     *   menu ボタン　delete 追加
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu_01, menu);

        return true;

    }

    //************** メニューボタンが押された時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch(itemId) {

            /**
             *  メニュー　リストアイテム　全件削除　ボタン
             */
            case R.id.delete_01_btn :

                //--------------- アラートダイヤログ　削除　設定 ---------------//
                // タイトル
                TextView titleView;
                titleView = new TextView(SelectSheetListView.this);
                titleView.setText("棚卸しデータを全て削除する");
                titleView.setTextSize(20);
                titleView.setTextColor(Color.WHITE);
                titleView.setBackgroundColor(getResources().getColor(R.color.back_color_03));
                titleView.setPadding(20, 20, 20, 20);
                titleView.setGravity(Gravity.CENTER);


                //-------------- アラートログの表示 開始 -------------- //
                AlertDialog.Builder bilder = new AlertDialog.Builder(SelectSheetListView.this);

                // ダイアログの項目
                bilder.setCustomTitle(titleView);
                //  bilder.setTitle("削除");
                bilder.setMessage("ダウンロードした設定ファイルを全て削除しますか？");

                //------- OK の時の処理 ----------//
                bilder.setNegativeButton("削除する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // データ　全件　削除
                        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                        dbAdapter.openDB();

                        dbAdapter.allDelete(); // 全件　削除　クラスメソッド

                        dbAdapter.closeDB(); //------- DB を閉じる

                        //　データを更新して、画面に表示
                        loadMyList();

                        toastMake("棚卸しデータを削除しました。", 0, -200);

                        //------------ 削除　処理　END　----------------

                    }
                });


                bilder.setPositiveButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // アラートダイアログ　を　閉じる
                        dialog.dismiss();
                        return;
                    }
                });


                // ダイアログの表示
                AlertDialog dialog = bilder.create();
                dialog.show();


        } //****************** END delete_01_btn

        return true;
    }


    /**
     * DBを読み込む＆更新する処理
     * loadMyList()
     */
    private void loadMyList() {

        //ArrayAdapterに対してListViewのリスト(items)の更新
        items.clear();

        dbAdapter.openDB();

        // DB データを取得
        Cursor c = dbAdapter.getDB(columns);

        if(c.moveToFirst()) {

            do {

                // MyListItemのコンストラクタ呼び出し(myListItemのオブジェクト生成)
                myListItem = new MyListItem(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5));

                //------- ログの取得
                Log.d("取得したCursor(ID):", String.valueOf(c.getInt(0)));
                Log.d("取得したCursor(担当者コード):", c.getString(1));
                Log.d("取得したCursor(倉庫コード):", c.getString(2));
                Log.d("取得したCursor(商品コード):", c.getString(3));
                Log.d("取得したCursor(棚卸し数):", c.getString(4));
                Log.d("取得したCursor(商品名):", c.getString(5));
                Log.d("取得したCursor(仕入単価):", c.getString(6));
                Log.d("取得したCursor(仕入先名):", c.getString(7));


                items.add(myListItem);

            } while(c.moveToNext());


        }

        c.close();
        dbAdapter.closeDB();

        mListView03.setAdapter(myBaseAdapter);

        //****************** 棚卸し　数　合計　表示 ***************//
        // 棚卸し合計数　取得
        int size_num = items.size();

        String tana_num = item_count_view.getText().toString();

        if(items.isEmpty()) {
            item_count_view.setText("件数　： " + 0);
        } else {
            item_count_view.setText("件数　： " + String.valueOf(size_num));
        }

        //****************** END 棚卸し　数　合計　表示 ***************//

        myBaseAdapter.notifyDataSetChanged();   // Viewの更新




    }

    /**
     * BaseAdapterを継承したクラス
     * MyBaseAdapter
     */

    public class MyBaseAdapter extends BaseAdapter {

        private Context context;
        private List<MyListItem> items;

        // 毎回findViewByIdをする事なく、高速化が出来るようするholderクラス
        private class ViewHolder {
            TextView list_ta_d; // 担当者

            TextView text05Product;
            TextView text06Product;
            TextView text07Product;

            TextView text05MadeIn;
            // 商品名
            TextView text10Product;

            ImageButton list_image_icon_delete;

        }


        // コンストラクタの生成
        public MyBaseAdapter(Context context, List<MyListItem> items) {
            this.context = context;
            this.items = items;
        }

        // Listの数
        @Override
        public int getCount() {
            return items.size();
        }

        // index or オブジェクトを返す
        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        // index を　他の index に返す
        @Override
        public long getItemId(int position) {
            return position;
        }

        //------- 新しいデータが表示されるタイミングで呼び出される -------
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder;

            // リスト 降順   int item_last = items.size() - 1  - position;

            // データ取得
            myListItem = items.get(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_sheet_listview, parent, false);

                // 担当者コード
                TextView list_ta_d = (TextView) view.findViewById(R.id.list_ta_d);
                // 倉庫コード
                TextView text05Product = (TextView) view.findViewById(R.id.text05Product);
                // 商品コード
                TextView text06Product = (TextView) view.findViewById(R.id.text06Product);
                // 棚卸し数
                TextView text05MadeIn = (TextView) view.findViewById(R.id.text05MadeIn);

                //*** 商品名 ***
                TextView text10Product = (TextView) view.findViewById(R.id.text10Product);


                //******** バケツアイコン　イメージボタン
                ImageButton list_image_icon_delete = (ImageButton) view.findViewById(R.id.list_image_icon_delete);

                // holder に　view を持たせておく
                holder = new ViewHolder();

                // 担当者
                holder.list_ta_d = list_ta_d;
                // 倉庫コード
                holder.text05Product = text05Product;
                // 商品コード
                holder.text06Product = text06Product;
                // 棚卸し数
                holder.text05MadeIn = text05MadeIn;

                //*** 商品名
                holder.text10Product = text10Product;


                //**** バケツアイコン　追加
                holder.list_image_icon_delete = list_image_icon_delete;

                view.setTag(holder);

            } else {
                // 初めて表示されるときにつけておいたtagを元にviewを取得する
                holder = (ViewHolder) view.getTag();
                convertView.setBackgroundColor(getResources().getColor(R.color.back_color_02));
            }

            // holder の　コンポーネント　と　データ　を紐づける


            //**************** 担当者表示 ********************************************
            //******* 担当名　取得
            holder.list_ta_d.setText(myListItem.getSend_Num_01());

            // 倉庫コード
            holder.text05Product.setText(myListItem.getSend_Num_02());
            // 商品コード
            holder.text06Product.setText(myListItem.getSend_Num_03());
            // 棚卸し数
            holder.text05MadeIn.setText(myListItem.getSend_Num_04());

            // *** 商品名 ***
            holder.text10Product.setText(myListItem.getSend_Num_05());

            /**
             *  バケツ　ボタン　削除　
             */
            holder.list_image_icon_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //-------------- アラートログ　削除　-----------
                    // タイトル
                    TextView textView;
                    textView = new TextView(SelectSheetListView.this);
                    textView.setText("タスクの削除");
                    textView.setTextSize(20);
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(getResources().getColor(R.color.back_color_03));
                    textView.setPadding(20, 20, 20, 20);
                    textView.setGravity(Gravity.CENTER);

                    //--------------　アラートログの表示　開始
                    AlertDialog.Builder bilder = new AlertDialog.Builder(SelectSheetListView.this);

                    //--------- カスタムタイトル　セット
                    bilder.setCustomTitle(textView);
                    //--------- メッセージのセット
                    bilder.setMessage("選択された「棚卸アイテム」を削除しますか？");

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

                            // ID を取得する
                            myListItem = items.get(position);
                            int listId = myListItem.getId();

                            //------------ 削除　処理　START　----------------
                            dbAdapter.openDB(); // データベース　読み書き　OK

                            // DBから取得したIDが入っているデータを削除する
                            dbAdapter.selectDelete(String.valueOf(listId));

                            Log.d("ログ： アイテムクリック削除　OK", String.valueOf(listId));

                            dbAdapter.closeDB();    // DBを閉じる
                            //　データを更新して、画面に表示
                            loadMyList();

                            /**
                             * CSV 作成
                             */
                            Write_CSV();

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

                    //******************************************* END ボタン　配色　変更


                }
            });

            return view;
        }

}

    // --------------------- トーストメッセージ表示 -----------------------
    private void toastMake(String message, int x, int y) {
        Toast toast = Toast.makeText(this,message, Toast.LENGTH_SHORT);

        // 位置調整
        toast.setGravity(Gravity.CENTER, x,y);
        toast.show();
    }
    // --------------------- トーストメッセージ表示 END ----------------------->

    @Override
    public void onBackPressed() {
        // 行いたい処理
        //  finish(); // 終了

        /**
         *  ファイル削除　処理
         */
        File_Del();

        // 戻る　ボタンの処理
        // アカウント　ID , name を返す
        Intent intent = new Intent();
        if (user_num_05.getText() != null) {
            String str = user_num_05.getText().toString();

            // intentへ添え字付で値を保持させる
            intent.putExtra("re_id", str);

            // 返却したい結果ステータスをセットする
            setResult(ReadBerCode.RESULT_OK, intent);

            setResult(TopMenu.RESULT_OK, intent);

            // アクティビティを終了させる
            finish();

        }

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    private void fadeout(){
        // 透明度を1から0に変化
        AlphaAnimation alphaFadeout = new AlphaAnimation(1.0f, 0.0f);
        // animation時間 msec
        alphaFadeout.setDuration(950);
        // animationが終わったそのまま表示にする
        alphaFadeout.setFillAfter(true);

        contentsBackground_03.startAnimation(alphaFadeout);
    }

    private void fadein(){
        // 透明度を0から1に変化
        AlphaAnimation alphaFadeIn = new AlphaAnimation(0.0f, 1.0f);
        // animation時間 msec
        alphaFadeIn.setDuration(950);
        // animationが終わったそのまま表示にする
        alphaFadeIn.setFillAfter(true);

        contentsBackground_03.startAnimation(alphaFadeIn);

        /*
        l_01.startAnimation(alphaFadeIn);
        l_02.startAnimation(alphaFadeIn);
        l_03.startAnimation(alphaFadeIn);
        l_04.startAnimation(alphaFadeIn);
        l_05.startAnimation(alphaFadeIn);
        l_06.startAnimation(alphaFadeIn);
        l_07.startAnimation(alphaFadeIn);

         */

    } //--------------- END function

    /**
     *   ************ ユーザー名　取得用　SELECT
     */
    private void List_User_SELECT () {

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase list_user_db = helper.getReadableDatabase();

        Cursor cursor = null;

        String [] arr_item = new String[2];

        try {

            cursor = list_user_db.rawQuery("select employee_tb_c_01, employee_tb_c_02 from Employee_Tb",
                    null);

            if(cursor != null || cursor.getCount() > 0) {

                if(cursor.moveToFirst()) {

                    do {

                        arr_item[0] = cursor.getString(0);
                        arr_item[1] = cursor.getString(1);

                        // key  3,  11, 9999   ||  value  名前
                        list_user_H.put(arr_item[0], arr_item[1]);

                    } while (cursor.moveToNext());

                }

            } else {

                return;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(list_user_db != null) {
                list_user_db.close();
            }
        }

    } // ================= end function


    /**
     *   ************ 場所コード, 場所名　取得用　SELECT
     */
    private void Place_code_SELECT () {

        TestOpenHelper helper = new TestOpenHelper(getApplicationContext());
        SQLiteDatabase place_code_db = helper.getReadableDatabase();

        Cursor cursor = null;

        String [] arr_item = new String[2];

        try {

            cursor = place_code_db.rawQuery("select Somf_tb_01, Somf_tb_02 from Somf_tb",
                    null);

            if(cursor != null || cursor.getCount() > 0) {

                if(cursor.moveToFirst()) {

                    do {

                        arr_item[0] = cursor.getString(0);
                        arr_item[1] = cursor.getString(1);

                        // key  Z0010   ||  value 第四工場材料置場
                        Place_code_H.put(arr_item[0], arr_item[1]);

                    } while (cursor.moveToNext());

                }

            } else {

                return;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(place_code_db != null) {
                place_code_db.close();
            }
        }

    } // ================= end function


    // データベースから読んで　CSV ファイルとして、　ファイルを　Android 端末内に保存
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

        // send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + gg_accont + ".csv";

        send_csv_file = "TN-" + csv_date_01 + "-ANDROID-" + gg_accont + '-' + androidId + ".csv";

        // 外部ストレージ用　パス
        /*
        send_csv_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/csv_file/" + send_csv_file;

            target_csv = new File(send_csv_file_path);
         */

        // 内部ストレージ
        //target_csv = new File(getFilesDir() + "/" + send_csv_file);

        // 外部ストレージ
        // ### getExternalFilesDir


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

            //  target_csv = new File(send_csv_file_path + "/" + send_csv_file);

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
                    arr_item[1] = cursor.getString(2); // 場所コード　格納

                    //----- カラム 3 （商品コード）
                    arr_item[2] = cursor.getString(3);
                    System.out.println("出力テスト" + arr_item[1]);

                    //----- カラム 3 書き込み
                    arr_item[3] = cursor.getString(4);
                    // 書き込み

                    /**
                     *
                     */
                    //----- カラム 5 （商品名）
                    arr_item[4] = cursor.getString(5);
                    //----- カラム 5 （仕入単価）
                    arr_item[5] = cursor.getString(6);
                    //----- カラム 5 （仕入先名）
                    arr_item[6] = cursor.getString(7);

                    // , カンマ区切り
                    // String record = arr_item[0] + "," + arr_item[1] + "," + arr_item[2] + "," + arr_item[3];

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

            /*
            Toast ts = Toast.makeText(this, "出力データ作成成功。", Toast.LENGTH_LONG);
            ts.show();

             */

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

            /* ソート処理
            java.util.Arrays.sort(targetFiles_02, new java.util.Comparator<File>() {

                public int compare(File file1, File file2) {
                    return file1.getName().compareTo(file2.getName());
                }
            });

             */

            for(File f : targetFiles_02) {

                if (f.getName().equals(last_file_name)) {
                    continue;
                }
                f.delete();
            }


            /*

                if (targetFiles_02 != null) {

                    for (int i = 0; i < targetFiles_02.length; i++) {
                        System.out.println(i + "targetFiles_02 i番：" + targetFiles_02[i]);
                    }

                //    if (targetFiles_02.length == 3) {
                    if (targetFiles_02.length == 3) {

                        targetFiles_02[1].delete();
                        File temp = targetFiles_02[2];

                        // 移動処理
                        targetFiles_02[1] = temp;
                        targetFiles_02[2].delete();

                        for (int i = 0; i < targetFiles_02.length; i++) {
                            System.out.println("targetFiles_02 削除後ファイル：" + i + "i番：" + targetFiles_02[i]);
                        }

                        // Androidに認識させる
                        MediaScannerConnection.scanFile(this,
                                //    new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + last_file},
                                new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + targetFiles_02[1].getName()},
                                new String[]{"application/octet-stream"}, null);

                        csv_desc = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + targetFiles_02[1].getName());
                        MediaScannerConnection.scanFile(this, new String[]{csv_desc.getAbsolutePath()}, null, null);


                        // Androidに認識させる
                        MediaScannerConnection.scanFile(this,
                                //    new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + last_file},
                                new String[]{Environment.DIRECTORY_DOCUMENTS + "/" + targetFiles_02[2].getName()},
                                new String[]{"application/octet-stream"}, null);

                        csv_desc_02 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + targetFiles_02[2].getName());
                        MediaScannerConnection.scanFile(this, new String[]{csv_desc_02.getAbsolutePath()}, null, null);


                    } else {

                        return;
                    }

                }//========= END if

             */


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

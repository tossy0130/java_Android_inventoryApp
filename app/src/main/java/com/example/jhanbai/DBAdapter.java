package com.example.jhanbai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Databaseに関連するクラス
 * DBAdapter
 */

public class DBAdapter {

    private final static String DB_NAME = "Send.db"; // DB 名
    private final static String DB_TABLE = "Send_TB_02"; // テーブル名
    private final static int DB_VERSION = 1; // データベース　バージョン

    /**
     * DBのカラム名
     */
    public final static String COL_ID = "id"; // 項目　01
    public final static String SEND_COL_01 = "send_col_01"; // 項目　02 担当者コード,
    public final static String SEND_COL_02 = "send_col_02"; // 項目　03 倉庫コード,
    public final static String SEND_COL_03 = "send_col_03"; // 項目　04 商品コード,
    public final static String SEND_COL_04 = "send_col_04"; // 項目　05 数量,
    public final static String SEND_COL_05 = "send_col_05"; // 項目　06 商品名,
    public final static String SEND_COL_06 = "send_col_06"; // 項目　06 仕入先名,
    public final static String SEND_COL_07 = "send_col_07"; // 項目　07 仕入単価,

    //***************** 削除 (使わない) *******************
    /*
    public final static String SEND_COL_05 = "send_col_05"; // 項目　06 品目コード,
    public final static String SEND_COL_06 = "send_col_06"; // 項目　07 品目名称,
    public final static String SEND_COL_07 = "send_col_07"; // 項目　08 品目備考,
    public final static String SEND_COL_08 = "send_col_08"; // 項目　09 数量,
    public final static String SEND_COL_09 = "send_col_09"; // 項目　10 場所
    //***************** 削除 (使わない) END *******************

     */

    public SQLiteDatabase db = null; // SQLiteDatabase
    public DBHelper dbHelper = null;           // DBHepler
    private Context context;

    // コンストラクタ
    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト
     */
    public DBAdapter openDB() {
        db = dbHelper.getWritableDatabase(); // DB の読み書き
        return this;
    }

    /**
     * DBの読み込み 今回は未使用
     * readDB()
     *
     * @return this 自身のオブジェクト
     */

    public DBAdapter readDB() {
        db = dbHelper.getReadableDatabase(); // DB の　読み込み
        return this;
    }


    /**
     * DBを閉じる
     * closeDB()
     */
    public void closeDB() {
        db.close(); // DB を閉じる
        db = null;
    }


    /**
     * DBのレコードへ登録
     * saveDB()
     *
     * @param send_col_num_01 担当者コード,
     * @param send_col_num_02 倉庫コード,
     * @param send_col_num_03 商品コード,
     * @param send_col_num_04 個数,
     * @param send_col_num_05 商品名,
     *

     */

    public void saveDB(String send_col_num_01, String send_col_num_02, String send_col_num_03,
                       String send_col_num_04, String send_col_num_05,String send_col_num_06,
                       String send_col_num_07) {

        // トランザクション　開始
        db.beginTransaction();

        try {
            // ContentValuesでデータを設定していく
            ContentValues values = new ContentValues();
            values.put(SEND_COL_01, send_col_num_01);
            values.put(SEND_COL_02, send_col_num_02);
            values.put(SEND_COL_03, send_col_num_03);
            values.put(SEND_COL_04, send_col_num_04);

            values.put(SEND_COL_05, send_col_num_05);
            values.put(SEND_COL_06, send_col_num_06); // 仕入先名
            values.put(SEND_COL_07, send_col_num_07); // 仕入先単価

            // 削除対象　使わない
            /*
            values.put(SEND_COL_05, send_col_num_05);
            values.put(SEND_COL_06, send_col_num_06);
            values.put(SEND_COL_07, send_col_num_07);
            values.put(SEND_COL_08, send_col_num_08);
            values.put(SEND_COL_09, send_col_num_09);
             */

            // insertメソッド データ登録
            // 第1引数：DBのテーブル名
            // 第2引数：更新する条件式
            // 第3引数：ContentValues
            db.insert(DB_TABLE, null, values);      // レコードへ登録
            System.out.println("DB_TABLE インサート　完了");
            db.setTransactionSuccessful(); // トランザクションへコミット
            System.out.println("インサート　トランザクション　完了");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); //------------------> トランザクション終了
        }

    }

    /**
     * DBのデータを取得
     * getDB()
     *
     * @param columns String[] 取得するカラム名 nullの場合は全カラムを取得
     * @return DBのデータ
     */
    public Cursor getDB(String[] columns) {

        // queryメソッド DBのデータを取得
        // 第1引数：DBのテーブル名
        // 第2引数：取得するカラム名
        // 第3引数：選択条件(WHERE句)
        // 第4引数：第3引数のWHERE句において?を使用した場合に使用
        // 第5引数：集計条件(GROUP BY句)
        // 第6引数：選択条件(HAVING句)
        // 第7引数：ソート条件(ODERBY句)
        return db.query(DB_TABLE, columns, null, null, null, null, "id DESC");
    }

    /**
     * DBの検索したデータを取得
     * searchDB()
     *
     * @param columns String[] 取得するカラム名 nullの場合は全カラムを取得
     * @param column  String 選択条件に使うカラム名
     * @param name    String[]
     * @return DBの検索したデータ
     */

    public Cursor searchDB(String[] columns, String column, String[] name) {
        return db.query(DB_TABLE, columns, column + " like ?", name, null,null,null);
    }

    /**
     * DBのレコードを全削除
     * allDelete()
     */
    public void allDelete() {

        db.beginTransaction(); // トランザクションの開始

        try {
            // deleteメソッド DBのレコードを削除
            // 第1引数：テーブル名
            // 第2引数：削除する条件式 nullの場合は全レコードを削除
            // 第3引数：第2引数で?を使用した場合に使用

            db.delete(DB_TABLE, null,null); // DB のレコードを全削除

            db.setTransactionSuccessful(); // トランザクションへのコミット
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); //----------------------------> トランザクション終了
        }
    }

    /**
     * DBのレコードの単一削除
     * selectDelete()
     *
     * @param position String
     */
    public void selectDelete(String position) {

        db.beginTransaction(); //------ トランザクション開始

        try {

            db.delete(DB_TABLE, COL_ID + "=?", new String[]{position});

            db.setTransactionSuccessful(); // トランザクションへコミット

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); //-----------------------------> トランザクション　終了
        }
    }

    /**
     *  アップデート処理
     */
    public void selectUpdate(String send_col_num_04, String position) {

        db.beginTransaction(); //------ トランザクジョン開始

        try {
            // ContentValuesでデータを設定していく
            ContentValues values = new ContentValues();
            values.put(SEND_COL_04,send_col_num_04);

            db.update(DB_TABLE, values,COL_ID + "=?",new String[]{position});
            System.out.println("DB_TABLE アップデート　完了");

            db.setTransactionSuccessful(); // トランザクションへコミット
            System.out.println("DB_TABLE アップデート　トランザクション　完了");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); //-----------------------------> トランザクション　終了
        }

    }



    /**
     * データベースの生成やアップグレードを管理するSQLiteOpenHelperを継承したクラス
     * DBHelper
     */
    static class DBHelper extends SQLiteOpenHelper {

        // コンストラクタ
        public DBHelper(Context context) {
            //第1引数：コンテキスト
            //第2引数：DB名
            //第3引数：factory nullでよい
            //第4引数：DBのバージョン
            super(context, DB_NAME, null, DB_VERSION);
        }

        /**
         * DB生成時に呼ばれる
         * onCreate()
         *
         * @param db SQLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            // テーブルの作成 ※　スペースに気を付ける 最初の( &&  カラムの頭にスペース
            String createTb1 = "CREATE TABLE " + DB_TABLE +" ("
                    + COL_ID + " INTEGER  primary key,"
                    + SEND_COL_01 + " TEXT,"
                    + SEND_COL_02 + " TEXT,"
                    + SEND_COL_03 + " TEXT,"
                    + SEND_COL_04 + " TEXT,"
                    + SEND_COL_05 + " TEXT,"
                    + SEND_COL_06 + " TEXT,"
                    + SEND_COL_07 + " TEXT"
                    + ");";

            System.out.println("テーブル Send_TB_02 作成完了");
            db.execSQL(createTb1);  //SQL文の実行

        }

        /**
         * DBアップグレード(バージョンアップ)時に呼ばれる
         *
         * @param db         SQLiteDatabase
         * @param oldVersion int 古いバージョン
         * @param newVersion int 新しいバージョン
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // DBからテーブル削除
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            // テーブル生成
            onCreate(db);
        }
    }//------------------------------------ END sub class


}

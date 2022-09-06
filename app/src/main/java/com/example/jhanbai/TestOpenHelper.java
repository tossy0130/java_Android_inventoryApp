package com.example.jhanbai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static java.sql.Types.INTEGER;

public class TestOpenHelper extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    private TestOpenHelper dbHelper = null;

    // データーベースのバージョン
    public static final int DATABASE_VERSION = 2;

    /**
     *   // データベース名
     */
    public static final String DATABASE_NAME = "Shouhin.db";

    /**
     *   // テーブル名
     */
    public static final String TABLE_NAME = "Genpin_T";

    /*
    // DB_User テーブル
    public static final String CREATE_TABLE_DB_USER = "create table DB_User( db_user_c_01 text, db_user_c_02 text);";
    // Item_tb テーブル
    public static final String CREATE_TABLE_ITEM_TB = "create table Item_tb( item_tb_c_01 text, item_tb_c_02 text);";
    */

    /**
     *  // カラム名 一覧  　テーブル Genpin_T
     */
    public static final String COLUMN_00 = "Genpin_T_column_00"; // ID
    public static final String COLUMN_01 = "Genpin_T_column_01"; // 現品票番号
    public static final String COLUMN_02 = "Genpin_T_column_02"; // 商品名
    public static final String COLUMN_03 = "Genpin_T_column_03"; // *** 追加 仕入単価 ***
    public static final String COLUMN_04 = "Genpin_T_column_04"; // *** 追加 仕入仕入先名 ***


    //------------ テーブル　定義　メソッド
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +  TABLE_NAME + "(" +
                    COLUMN_00 + " INTEGER  primary key," +
                    COLUMN_01 + " TEXT," +
                    COLUMN_02 + " TEXT," +
                    COLUMN_03 + " TEXT," +
                    COLUMN_04 + " TEXT)";

    //------------ テーブルが存在していたら　削除する
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // ----------- コンストラクタ ------------>
    TestOpenHelper(Context context) {
    // TestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * DBの読み書き
     * openDB()
     *
     * @return this 自身のオブジェクト
     */
    public TestOpenHelper openDB() {
        db = dbHelper.getWritableDatabase(); // DB の読み書き
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

    //------------- テーブル作成 実行
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                SQL_CREATE_ENTRIES
        );
        System.out.println("SQL_CREATE_ENTRIES テーブル 作成完了");

        Log.d("debug", "execSQL 実行完了");
    }

    //------------- アップデート判別
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        // アップデート判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        System.out.println("SQL_CREATE_ENTRIES テーブル 作成完了");

        if(oldVersion == 1 && newVersion == 2){
            try{
                db.execSQL(
                        "CREATE TABLE " +  TABLE_NAME + "(" +
                                COLUMN_00 + " INTEGER  primary key," +
                                COLUMN_01 + " TEXT," +
                                COLUMN_02 + " TEXT," +
                                COLUMN_03 + " TEXT," +
                                COLUMN_04 + " TEXT)"
                );
            }catch(SQLiteException err){
                err.printStackTrace();
            }
        }



    }

    //-------------- アップグレードメソッドの実行のタイミング
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);


    }

    //-------------- トランザクション　メソッド 開始 ------------//
    public void beginTransaction() {
        if(this.db != null) {
            this.db.beginTransaction();
        }
    }

    //-------------- トランザクション　メソッド 終了 ------------//
    public void endTransaction() {
        if(this.db != null) {
            this.db.endTransaction();
        }
    }

    //------------- トランザクション　コミット 完了 -------------//
    public void setTransactionSuccessful() {
        if (this.db != null) {
            db.setTransactionSuccessful();
        }

    }

    /**
     * DBのデータを取得
     * getDB()
     *
     * @param columns String[] 取得するカラム名 nullの場合は全カラムを取得
     * @return DBのデータ
     */
    /*
    public Cursor getDB(String[] columns) {

        // queryメソッド DBのデータを取得
        // 第1引数：DBのテーブル名
        // 第2引数：取得するカラム名
        // 第3引数：選択条件(WHERE句)
        // 第4引数：第3引数のWHERE句において?を使用した場合に使用
        // 第5引数：集計条件(GROUP BY句)
        // 第6引数：選択条件(HAVING句)
        // 第7引数：ソート条件(ODERBY句)
        return db.query(Genpin_T, columns, null, null, null, null, null);
    }

     */




}

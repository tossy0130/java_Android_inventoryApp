package com.example.jhanbai;

import android.content.ContentValues;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class CsvReader {

    private SQLiteDatabase db;

    List<ListData> objects = new ArrayList<ListData>();

    String line = "";
    String [] RowData = new String [9];
    public void reader(Context context) {


        try {
            FileInputStream in = context.openFileInput("test_r.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

            Integer tmp = 0; // 初期化
            
            while((line = reader.readLine()) != null) {

                // DB 値　格納用　変数
               ContentValues value = new ContentValues();

                RowData = line.split("," , -1);

                // カンマ区切りで１つづつ　配列に入れる
                ListData data = new ListData();

                if(RowData[0] == null || isEmpty(RowData[0])) { // ID
                    data.setId("ID：" + "null");


                } else {
                    data.setId("ID：" + RowData[0]);
                    //tmp = Integer.parseInt(RowData[0]);
                  //  value.put(TestOpenHelper.SEND_DB_C_01, tmp); // Send_db　テーブル へ　カラム 01 へ　インサート (integer)
                }


                if(RowData[1] == null || isEmpty(RowData[1])) { // 担当者コード
                    data.setCol_02("担当者コード：" + "null");

                } else {
                    data.setCol_02("担当者コード：" + RowData[1]);
                  //  value.put(TestOpenHelper.SEND_DB_C_02, RowData[1]); // Send_db　テーブル へ　カラム 02 へ　インサート
                }


                if(RowData[2] == null || isEmpty(RowData[2])) {  // 部署コード
                    data.setCol_03("部署コード：" + "null");

                } else {
                    data.setCol_03("部署コード：" + RowData[2]);
                  //  value.put(TestOpenHelper.SEND_DB_C_03, RowData[2]); // Send_db　テーブル へ　カラム 03 へ　インサート
                }

                if(RowData[3] == null || isEmpty(RowData[3])) { // "現品票コード：" +
                    data.setCol_04("現品票コード：" + "null");

                } else {
                    data.setCol_04("現品票コード：" + RowData[3]);
                 //   value.put(TestOpenHelper.SEND_DB_C_04, RowData[3]); // Send_db　テーブル へ　カラム 04 へ　インサート
                }

                if(RowData[4] == null || isEmpty(RowData[4])) { // 品目コード
                    data.setCol_05("品目コード：" +  "null");

                } else {
                    data.setCol_05("品目コード：" + RowData[4]);

                }

                if(RowData[5] == null || isEmpty(RowData[5])) { // 品目名称コード
                    data.setCol_06("品目名称コード：" + "null");
                 //   value.put(TestOpenHelper.SEND_DB_C_06, "該当データがありません。"); //  null 処理
                } else {
                    data.setCol_06("品目名称コード：" + RowData[5]);

                }

                if(RowData[6] == null || isEmpty(RowData[6])) { // 品目備考
                    data.setCol_07("棚卸数：" + "null");

                } else {
                    data.setCol_07("棚卸数：" + RowData[6]);
                  // value.put(TestOpenHelper.SEND_DB_C_07, RowData[6]); // Send_db　テーブル へ　カラム 07 へ　インサート
                }

                if(RowData[7] == null || isEmpty(RowData[7])) { // 場所
                    data.setCol_08("ロケーション：" + "null");

                } else {
                    data.setCol_08("ロケーション：" + RowData[7]);
                  //  value.put(TestOpenHelper.SEND_DB_C_08, RowData[7]); // Send_db　テーブル へ　カラム 08 へ　インサート
                }
/*
                if(RowData[8] == null || isEmpty(RowData[8])) {
                    data.setCol_09("null");
                } else {
                    data.setCol_09(RowData[8]);
                }

 */

                objects.add(data);

                // テーブル　へ　INSERT
           //     db.insert("Send_db", null, value);

                for(ListData d : objects) {
                    System.out.println("リストデータ　一覧　表示：" + d);
                }
                
                System.out.println("カラム 01 データ" + tmp);

            }
            reader.close();

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}

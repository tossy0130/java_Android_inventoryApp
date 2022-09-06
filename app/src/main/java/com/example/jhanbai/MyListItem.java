package com.example.jhanbai;

import android.util.Log;

public class MyListItem {

    protected int id;   // id
    protected String Send_Num_01;   // 担当者コード
    protected String Send_Num_02;   // 倉庫コード
    protected String Send_Num_03;   // 商品コード
    protected String Send_Num_04;   // 棚卸し数量

    protected String Send_Num_05;   // 商品名
    protected String Send_Num_06;   // 仕入単価
    protected String Send_Num_07;   // 仕入先名



    /*---- セッター　まとめ */
    public MyListItem(int id, String Send_Num_01, String Send_Num_02, String Send_Num_03,
                      String Send_Num_04, String Send_Num_05) {

        this.id = id;
        this.Send_Num_01 = Send_Num_01;
        this.Send_Num_02 = Send_Num_02;
        this.Send_Num_03 = Send_Num_03;
        this.Send_Num_04 = Send_Num_04;

        // 商品名
        this.Send_Num_05 = Send_Num_05;
        // 仕入単価
        this.Send_Num_06 = Send_Num_06;
        // 仕入先名
        this.Send_Num_07 = Send_Num_07;

    }

    /*------------------------------- ゲッター設定----------------------------*/
    /**
     * IDを取得
     * getId()
     *
     * @return id int ID
     */
    public int getId() {
        Log.d("ログ: 取得したID：", String.valueOf(id));
        return id;
    }


    public String getSend_Num_01() {
        return Send_Num_01;
    }

    public String getSend_Num_02() {
        return Send_Num_02;
    }

    public String getSend_Num_03() {
        return Send_Num_03;
    }

    public String getSend_Num_04() {
        return Send_Num_04;
    }

    // 商品名取得
    public String getSend_Num_05() {
        return Send_Num_05;
    }

    // 仕入単価
    public String getSend_Num_06() {
        return Send_Num_06;
    }
    // 仕入先名
    public String getSend_Num_07() {
        return Send_Num_07;
    }




}

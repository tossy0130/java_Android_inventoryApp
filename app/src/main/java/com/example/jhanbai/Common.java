package com.example.jhanbai;

import android.app.Application;

public class Common extends Application {

    // グローバルに扱う変数
    int g_flg;        // 画面遷移した回数
    String fromActivityName;    // 遷移元の画面名称

    /**
     * 変数を初期化する
     */
    public void init(){
        g_flg = 0;
        fromActivityName = "";
    }

}

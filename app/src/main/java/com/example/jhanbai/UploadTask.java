package com.example.jhanbai;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UploadTask extends AsyncTask<String, Void, String> {

    private Listener listener;

            // 非同期処理　
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {

        // ファイル　送信　対象パス
        String urlst = "http://tokotoko6667777.hippy.jp/tana_phppost_file/index.php";

        HttpURLConnection httpConn = null;
        String result = null;
        String target = "target=" + params[0];

        try {

            // URL の設定
            URL url = new URL(urlst);

            // HttpURLConnection
            httpConn = (HttpURLConnection) url.openConnection();

            // request POST
            httpConn.setRequestMethod("POST");

            // no Redirects
            httpConn.setInstanceFollowRedirects(false);

            // データを書き込む
            httpConn.setDoOutput(true);

            // 時間制限
            httpConn.setReadTimeout(10000); // 10秒
            httpConn.setConnectTimeout(20000); // 20秒

            // 接続
            httpConn.connect();

            try(// POST データ送信
                    OutputStream outputStream = httpConn.getOutputStream()) {
                    outputStream.write(target.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    Log.d("debug", "POST 送信 OK");
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
                result = "POST送信エラー";

            }

            //-------　レスポンスを受け取る処理
            final int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // レスポンスを受け取る処理等
                result="HTTP_OK";
            }
            else{
                result="status="+String.valueOf(status);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpConn != null) {
                // コネクトを切る
                httpConn.disconnect();
            }
        }
        return result;

        }

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String result);
    }

}

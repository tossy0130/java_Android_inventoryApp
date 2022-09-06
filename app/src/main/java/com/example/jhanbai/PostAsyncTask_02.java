package com.example.jhanbai;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PostAsyncTask_02 extends AsyncTask<Object, Void, Object> {


    private final WeakReference<Activity> w_Activity;

    // csv ファイル名　取得
    private String get_csv_name;

    // ファイル 削除用
    private File delete_file;
    private File delete_file_02;

    //②コンストラクタで、 呼び出し元Activityを弱参照で変数セット
    PostAsyncTask_02(Activity activity) {
        this.w_Activity =  new WeakReference<>(activity);
    }

    //③バックグラウンド処理
    @Override
    protected Object doInBackground(Object[] data) {

        //Object配列でパラメータを持ってこれたか確認
        String url = (String) data[0];
        String description = (String) data[1];
        get_csv_name = description;

        String filePath = (String) data[2];

        //④HTTP処理用オプジェクト作成
        OkHttpClient client = new OkHttpClient();

        //⑤送信用POSTデータを構築（Multipart)
        MediaType MEDIA_TYPE_CSV = MediaType.parse("text/csv; charset=Shift_JIS");


        // リクエストボディの作成
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"description\""),
                        RequestBody.create(MEDIA_TYPE_CSV, description)
                )
                .addFormDataPart(
                        "uploaded_file",
                        get_csv_name, //****** 　送信形式　ファイル名に　入れ替え ******
                        RequestBody.create(MediaType.parse("application/octet-stream"), new File(filePath))
                )
                .build();

        delete_file = new File(filePath);
        delete_file_02 = new File(description);

        //⑥送信用リクエストを作成
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();


        //⑦受信用オブジェクトを作成 call back 用
        Call call = client.newCall(request);
        String result = "";

        //⑧送信と受信
        try {

            Response response = call.execute();
            ResponseBody body = response.body();

            if(body != null) {
                result = body.string();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError(e + "例外：送信タスクで例外エラーが発生しました。");
        }

        //⑨結果を返し、onPostExecute で受け取る
        return result;

    }

    //****** ⑩バックグラウンド完了処理 ********
    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        Log.i("onPostExecute　POST 送信結果", (String) result + "送信結果 POST");
        System.out.println("送信結果【" + result + "】");

        //⑪画面表示
        Activity activity = w_Activity.get();
        if (activity == null || activity.isFinishing()) {
            // activity is no longer valid, don't do anything!
            return;
        }




        //---------------- ファイル削除 ---------------
        delete_file.delete();
        delete_file_02.delete();

        System.out.println("削除用　ファイルパス" + delete_file);
        System.out.println("削除用　ファイルパス" + delete_file_02);
    }



}

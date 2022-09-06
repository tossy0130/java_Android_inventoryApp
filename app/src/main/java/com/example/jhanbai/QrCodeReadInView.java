package com.example.jhanbai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class QrCodeReadInView extends AppCompatActivity {

    private CompoundBarcodeView mBarcodeView;

    //--------　ヘッダー　アカウント用
    private ImageButton back_btn_001;
    private TextView user_num_001;

    //------ バーコード　結果データ用
    private String code_num;

    //----- アカウント　受け取り用
    private String qr_user_01;
    private String qr_user_02;

    private Intent qr_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_read_in_view);

        //---------------- header start ----------------
        back_btn_001 = (ImageButton) findViewById(R.id.back_btn_001);
        user_num_001 = (TextView) findViewById(R.id.user_num_001);

        // ----- アカウントデータ受け取り
        if(getIntent() != null) {
            qr_user_01 = getIntent().getStringExtra("user_go_num");
            qr_user_02 = getIntent().getStringExtra("user_go_num_02");

            user_num_001.setText(qr_user_01 + "：" + qr_user_02);
        }


        qr_intent = new Intent();


        //---------------- header END ----------------

        // 読み込んだ結果を　エディットテキストに表示
        mBarcodeView = findViewById(R.id.barcodeView);
        mBarcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult barcodeResult) {

                // ----- バーコード　値取得
                code_num = barcodeResult.getText();

                EditText output_bar = (EditText) findViewById(R.id.output_bar);
                output_bar.setText(code_num);

                if(code_num != null) {
                    finish();
                }


                }



            @Override
            public void possibleResultPoints(List<ResultPoint> list) {}
        });


        // -----------------　戻るボタン ----------------
        back_btn_001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent();

               intent.putExtra("qr_ak_num", qr_user_01);
               intent.putExtra("qr_ak_num_02", qr_user_02);

               setResult(RESULT_CANCELED, intent);

               System.out.println(qr_user_01 + "アカウント　インテント　テスト01");
               System.out.println(qr_user_02 + "アカウント　インテント　テスト02");

               finish();
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        mBarcodeView.resume();
    }


    @Override
    public void onPause() {

        if(code_num != null) {
            // アカウント　ID , name を返す


            // intentへ添え字付で値を保持させる
            qr_intent.putExtra("get_qr_data",code_num);

            // 返却したい結果ステータスをセットする
            setResult(RESULT_OK, qr_intent);

            // アクティビティを終了させる
            System.out.println(code_num + "QR テスト 01");

        } // end if

        super.onPause();
        mBarcodeView.pause();
    }

}




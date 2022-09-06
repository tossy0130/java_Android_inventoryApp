package com.example.jhanbai;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DigitalClock extends androidx.appcompat.widget.AppCompatTextView {

    private Calendar mCalendar;
    private String mFormat = "H:mm:ss";

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;
    private SimpleDateFormat sdf;

    /**
     * コンストラクタ
     * @param context
     */
    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    /**
     * 初期処理
     * @param context
     */
    private void initClock(Context context) {
        sdf  =  new SimpleDateFormat(mFormat);
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(sdf.format(mCalendar.getTime()));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 -now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    public void setFormat(String fmt) {
        mFormat = fmt;
    }
}




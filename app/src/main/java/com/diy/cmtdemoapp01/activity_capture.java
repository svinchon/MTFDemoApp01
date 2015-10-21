package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class activity_capture extends Activity {

    private String TAG = this.getClass().getSimpleName();

    TextView tvCaptureLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_capture);
        tvCaptureLog = (TextView)this.findViewById(R.id.tvCaptureLog);
        tvCaptureLog.setText(TAG + " - onCreate called");
    }

}

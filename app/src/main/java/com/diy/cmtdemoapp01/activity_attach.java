package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class activity_attach extends Activity {

    private String TAG = this.getClass().getSimpleName();

    TextView tvAttachLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_attach);
        tvAttachLog = (TextView)this.findViewById(R.id.tvAttachLog);
        tvAttachLog.setText(TAG + " - onCreate called");
    }

}

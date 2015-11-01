package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

public class activity_proceed extends Activity {

    private String TAG = this.getClass().getSimpleName();

    TextView tvProceedLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_proceed);
        tvProceedLog = (TextView)this.findViewById(R.id.tvProceedLog);
        tvProceedLog.setText(TAG + " - onCreate called");
    }

    public void getFromMain(View view) {
        tvProceedLog.append("\nxxx"+((activity_main)this.getParent()).currentConfigFile +"xxx");
    }
}

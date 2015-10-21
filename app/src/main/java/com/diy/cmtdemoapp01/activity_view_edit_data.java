package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diy.helpers.android.v1.AndroidHelper;
import com.diy.helpers.v1.RESTHelper;
import com.diy.helpers.v1.RESTHelperCallRequest;
import com.diy.helpers.v1.RESTHelperCallResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class activity_view_edit_data extends Activity {

    private String TAG = this.getClass().getSimpleName();

    TextView tvViewEditLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_view_edit_data);
        tvViewEditLog = (TextView)this.findViewById(R.id.tvViewEditLog);
        tvViewEditLog.setText(TAG + " - onCreate called");
    }

}

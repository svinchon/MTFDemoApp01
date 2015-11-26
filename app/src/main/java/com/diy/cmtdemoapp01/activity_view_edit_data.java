package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diy.helpers.android.v1.AndroidHelper;
import com.diy.helpers.v1.RESTHelper;
import com.diy.helpers.v1.RESTHelperCallRequest;
import com.diy.helpers.v1.RESTHelperCallResult;
import com.diy.helpers.v1.XMLHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.style.TextAppearance_Medium;

public class activity_view_edit_data extends Activity {

    private String TAG = this.getClass().getSimpleName();

    TextView tvViewEditLog;
    activity_main rootActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        rootActivity = (activity_main)this.getParent();
        this.setContentView(R.layout.activity_view_edit_data);
        drawUI();
    }

    public void drawUI() {
        LinearLayout llEdit = (LinearLayout)this.findViewById(R.id.llEdit);
        llEdit.removeAllViews();
        HashMap<String, utils_edit_config_field> editingHashMap = rootActivity.currentEditingHashMap;
        for (Map.Entry<String, utils_edit_config_field> entry : editingHashMap.entrySet()) {
            LinearLayout llField = new LinearLayout(this);
            llField.setPadding(dip(10), dip(10), dip(10), dip(10));
            llField.setGravity(Gravity.CENTER_VERTICAL);
            llField.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            TextView tvLabel = new TextView(this);
            tvLabel.setText(entry.getValue().fieldName);
            tvLabel.setTextAppearance(this, TextAppearance_Medium);
            tvLabel.setWidth(dip(100));
            EditText etValue = new EditText(this);
            String currentValue = XMLHelper.getValueFromXML(rootActivity.currentDataXML, entry.getValue().fieldXPath);
            etValue.setText(currentValue);
            etValue.setLayoutParams(
                    new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            llField.addView(tvLabel);
            llField.addView(etValue);
            llEdit.addView(llField);
        }
    }

    public int dip(int pixels) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

}

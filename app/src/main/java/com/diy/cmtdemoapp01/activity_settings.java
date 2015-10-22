package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

public class activity_settings extends Activity {

    private String TAG = this.getClass().getSimpleName();

    RadioGroup rg;

    EditText etServerIP;
    Spinner spServerScenarios, spServerConfigFiles, spLocalScenarios, spLocalConfigFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        etServerIP = (EditText)findViewById(R.id.etServerIP);
        spServerScenarios = (Spinner)findViewById(R.id.spScenarios);
        spServerConfigFiles = (Spinner)findViewById(R.id.spConfigFiles);
        spLocalScenarios = (Spinner)findViewById(R.id.spLocalScenarios);
        spLocalScenarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getLocalConfigFilesList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spLocalConfigFiles = (Spinner)findViewById(R.id.spLocalConfigFiles);
        rg = (RadioGroup) findViewById(R.id.rg);
    }

    public void getServerScenariosList(View view) {
        Log.i(TAG, "button was pressed"); // write info message to log
        try {
            String ip = etServerIP.getText().toString(); // get server ip from text field
            String url ="http://"+ip+":80/MTFServer01/rest/MTFServer01REST/getServerScenariosList";
            Log.i(TAG, "url: "+url); // write info message to log
            RESTHelperCallRequest req = new RESTHelperCallRequest(); // prepare request
            req.setURL(url);
            req.setRequestType("GET");
            RESTHelper rh = new RESTHelper(); // prepare helper
            rh.setReq(req);
            rh.execute(); // run request
            RESTHelperCallResult res = rh.getRESTCallResult(); // process result
            Log.i(TAG, res.getStatus()); // write info message to log
            if (res.getStatus()!= "error") {
                Log.i(TAG, res.getContent()); // write info message to log
                List<String> list=new ArrayList<String>(); // underlying list to feed spinner
                JSONObject jo = new JSONObject(res.getContent());
                JSONArray scenarios = jo.getJSONArray("scenarios");
                for (int i=0; i<scenarios.length(); i++) {
                    list.add((String) scenarios.get(i));
                }
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show(); // display message
                ArrayAdapter<String> adapter=new ArrayAdapter<String>( // adapter to link string array or array list to spinner
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spServerScenarios.setAdapter(adapter); // associate adapter to spinner
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConfigFilesList(View view) {
        Log.i(TAG, "button was pressed");
        try {
            String ip = etServerIP.getText().toString();
            String scenario = spServerScenarios.getSelectedItem().toString();
            String url ="http://"+ip+":18080/MTFServer01/rest/MTFServer01REST/getConfigFilesList";
            Log.i(TAG, "url: "+url);
            RESTHelperCallRequest req = new RESTHelperCallRequest();
            req.setURL(url);
            req.setRequestType("GET");
            req.addParameter("Scenario", scenario);
            RESTHelper rh = new RESTHelper();
            rh.setReq(req);
            rh.execute();
            RESTHelperCallResult res = rh.getRESTCallResult();
            Log.i(TAG, res.getStatus());
            if (res.getStatus()!= "error") {
                Log.i(TAG, res.getContent());
               List<String> list=new ArrayList<String>();
               JSONObject jo = new JSONObject(res.getContent());
                JSONArray configFiles = jo.getJSONArray("configFiles");
                for (int i=0; i<configFiles.length(); i++) {
                    list.add((String) configFiles.get(i));
                }
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spServerConfigFiles.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConfigFile(View view) {
        Log.i(TAG, "button was pressed");
        try {
            String ip = etServerIP.getText().toString();
            String scenario = spServerScenarios.getSelectedItem().toString();
            String configFile = spServerConfigFiles.getSelectedItem().toString();
            String url ="http://"+ip+":18080/MTFServer01/rest/MTFServer01REST/getConfigFile";
            Log.i(TAG, "url: "+url);
            RESTHelperCallRequest req = new RESTHelperCallRequest();
            req.setURL(url);
            req.setRequestType("GET");
            req.addParameter("Scenario", scenario);
            req.addParameter("ConfigFile", configFile);
            RESTHelper rh = new RESTHelper();
            rh.setReq(req);
            rh.execute();
            RESTHelperCallResult res = rh.getRESTCallResult();
            Log.i(TAG, res.getStatus());
            if (res.getStatus()!= "error") {
                //Log.i(TAG, res.getContent());
                String content = res.getContent();
                //((activity_main)this.getParent()).configFile = content;
                if (AndroidHelper.checkExternalMedia().indexOf("W")>=0) {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File (root.getAbsolutePath() + "/MTFLocal");
                    dir.mkdirs();
                    File file = new File(dir, configFile);
                    try {
                        FileOutputStream f = new FileOutputStream(file);
                        PrintWriter pw = new PrintWriter(f);
                        pw.println(content);
                        pw.flush();
                        pw.close();
                        f.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i(TAG, "" +
                                "******* File not found. Did you" +
                                " add a WRITE_EXTERNAL_STORAGE permission" +
                                " to the manifest?");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocalConfigFile(View view) {
        ((activity_main)this.getParent()).configFile = "test";
    }

    public void activateLocal(View view) {
        View v2 = (View)findViewById(R.id.tab2);
        View v1 = (View)findViewById(R.id.tab1);
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.VISIBLE);
        getLocalScenariosList();
    }

    public void activateServer(View view) {
        View v2 = (View)findViewById(R.id.tab2);
        View v1 = (View)findViewById(R.id.tab1);
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
    }

    public void getLocalScenariosList() {
        try {
            List<String> list=new ArrayList<String>();
            if (AndroidHelper.checkExternalMedia().indexOf("R")>=0) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/MTFLocal/Scenarios");
                String[] scenarios = dir.list();
                for (int i = 0; i < scenarios.length; i++) {
                    list.add((String) scenarios[i]);
                }
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show(); // display message
                ArrayAdapter<String> adapter = new ArrayAdapter<String>( // adapter to link string array or array list to spinner
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spLocalScenarios.setAdapter(adapter); // associate adapter to spinner
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       //Spinner spLocalScenarios = (Spinner)findViewById();
    }

    public void getLocalConfigFilesList() {
        try {
            List<String> list=new ArrayList<String>();
            String scenario = spLocalScenarios.getSelectedItem().toString();
            if (AndroidHelper.checkExternalMedia().indexOf("R")>=0) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/MTFLocal/Scenarios/"+scenario);
                String[] scenarios = dir.list();
                for (int i = 0; i < scenarios.length; i++) {
                    list.add((String) scenarios[i]);
                }
                Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show(); // display message
                ArrayAdapter<String> adapter = new ArrayAdapter<String>( // adapter to link string array or array list to spinner
                        this,
                        android.R.layout.simple_list_item_1,
                        list
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spLocalConfigFiles.setAdapter(adapter); // associate adapter to spinner
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       //Spinner spLocalScenarios = (Spinner)findViewById();
    }
}
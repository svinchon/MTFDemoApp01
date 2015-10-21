package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    EditText etServerIP;
    Spinner spScenarios, spConfigFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        etServerIP = (EditText)findViewById(R.id.etServerIP);
        spScenarios = (Spinner)findViewById(R.id.spScenarios);
        spConfigFiles = (Spinner)findViewById(R.id.spConfigFiles);
    }

    public void getScenariosList(View view) {
        Log.i(TAG, "button was pressed"); // write info message to log
        try {
            String ip = etServerIP.getText().toString(); // get server ip from text field
            String url ="http://"+ip+":18080/MTFServer01/rest/MTFServer01REST/getScenariosList";
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
                spScenarios.setAdapter(adapter); // associate adapter to spinner
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
            String scenario = spScenarios.getSelectedItem().toString();
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
                spConfigFiles.setAdapter(adapter);
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
            String scenario = spScenarios.getSelectedItem().toString();
            String configFile = spConfigFiles.getSelectedItem().toString();
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

}

//public static final String PREFS_NAME = "MyPrefsFile";
//// Restore preferences
//SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//boolean silent = settings.getBoolean("silentMode", false);
//// We need an Editor object to make preference changes.
//// All objects are from android.context.Context
//SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//SharedPreferences.Editor editor = settings.edit();
//editor.putBoolean("silentMode", mSilentMode);
//// Commit the edits!
//editor.commit();

//            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(
//                    MediaType.parse("application/json; charset=utf-8"),
//                    "{ toto: tutu }"
//            );
//            Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//            Response response = client.newCall(request).execute();
//            Log.i(TAG, "response body: " + response.body().string());

// FROM XCP MOBILE BANKING APP
//        android {
//            useLibrary 'org.apache.http.legacy'
//        }//
//        import org.apache.http.HttpResponse;
//        import org.apache.http.client.ClientProtocolException;
//        import org.apache.http.client.methods.HttpGet;
//        import org.apache.http.impl.client.DefaultHttpClient;
//        import org.apache.http.util.EntityUtils;

//        DefaultHttpClient client = new DefaultHttpClient();
//        HttpPost request = new HttpPost(CBatchURI);
//        request.addHeader("Content-Type", "application/vnd.emc.captiva+json; charset=utf-8");
//        request.addHeader("Accept", "application/vnd.emc.captiva+json, application/json");
//        request.addHeader("Cookie", "CPTV-TICKET=" + ticket);
//        //Serialise the JSON
//        String json = gson.toJson(BatchRequest);
//        //Now Try to post
//        boolean IsValidJSON = true;
//        request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
//        HttpResponse response = client.execute(request);
//        delay(2);
//        String strResponse = EntityUtils.toString(response.getEntity(), "UTF8");

// FROM WEB EXAMPLE
//        URL url = new URL(urlString);
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        in = new BufferedInputStream(urlConnection.getInputStream());


// FROM ECIPSE
//        RESTHelper rh = new RESTHelper();
//        RESTHelperCallRequest req = rh.getReq();
//        req.setURL("http://192.168.0.22:18080/MTFServer01/rest/MTFServer01REST/getConfigFilesList");
//        try {
//            rh.execute();
//            RESTHelperCallResult res = rh.getRESTCallResult();
//            Log.v("DEBUG", "fff" + res.getContent());
//        } catch (Exception e) {
//            Log.v("ERROR", e.getMessage() + " - probably unable to access url");
//            e.printStackTrace();
//        }



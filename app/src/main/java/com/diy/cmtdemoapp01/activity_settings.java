package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.diy.helpers.android.v1.AndroidHelper;
import com.diy.helpers.v1.HelperException;
import com.diy.helpers.v1.RESTHelper;
import com.diy.helpers.v1.RESTHelperCallRequest;
import com.diy.helpers.v1.RESTHelperCallResult;
import com.diy.helpers.v1.Utils;
import com.diy.helpers.v1.XMLHelper;
import com.diy.helpers.v1.XPath2XmlCreator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class activity_settings extends Activity {

    private String TAG = this.getClass().getSimpleName();

    RadioGroup rg;

    TextView tvSettingsLog;
    EditText etServerIP;
    Spinner spServerScenarios, spServerConfigFiles, spLocalScenarios, spLocalConfigFiles;
    activity_main rootActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        rootActivity = (activity_main)this.getParent();
        this.setContentView(R.layout.activity_settings);
        etServerIP = (EditText)findViewById(R.id.etServerIP);
        etServerIP.setText(XMLHelper.getValueFromXML(rootActivity.currentPreferencesXML, "/Preferences/ServerIP"));
        spServerScenarios = (Spinner)findViewById(R.id.spServerScenarios);
        spServerScenarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getServerConfigFilesList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spServerConfigFiles = (Spinner)findViewById(R.id.spServerConfigFiles);
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
        getLocalScenariosList();
    }

    // show appropriate tab base on radio button
    public void activateLocal(View view) {
        View v2 = (View)findViewById(R.id.localTab);
        View v1 = (View)findViewById(R.id.serverTab);
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.VISIBLE);
    }
    public void activateServer(View view) {
        View v2 = (View)findViewById(R.id.localTab);
        View v1 = (View)findViewById(R.id.serverTab);
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
    }

    public void getServerScenariosList(View view) {
        String ip = etServerIP.getText().toString(); // get server ip from text field
        rootActivity.currentServerIP = ip;
        rootActivity.updatePreferencesAndSave("ServerIP", ip);
        getServerScenariosList();
    }

    public void getServerScenariosList() {
        try {
            String url ="http://"+rootActivity.currentServerIP+":"+rootActivity.portNumber+"/MTFServer01/rest/MTFServer01REST/getScenariosList";
            RESTHelperCallRequest req = new RESTHelperCallRequest(); // prepare request
            req.setURL(url); req.setRequestType("GET");
            RESTHelper rh = new RESTHelper(); // prepare helper
            rh.setReq(req); rh.execute(); // run request
            RESTHelperCallResult res = rh.getRESTCallResult(); // process result
            if (res.getStatus()!= "error") {
                List<String> list=new ArrayList<String>(); // underlying list to feed spinner
                JSONObject jo = new JSONObject(res.getContent());
                JSONArray scenarios = jo.getJSONArray("scenarios");
                for (int i=0; i<scenarios.length(); i++) { list.add((String) scenarios.get(i)); }
                String[] array = list.toArray(new String[list.size()]);
                AndroidHelper.populateSpinner(this, spServerScenarios, array);
            } else {
                AndroidHelper.showAlertDialog("Error", res.getErrorMessage(), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getServerConfigFilesList() {
        try {
            String ip = etServerIP.getText().toString();
            String scenario = spServerScenarios.getSelectedItem().toString();
            rootActivity.currentScenario = scenario;
            String url ="http://"+ip+":"+rootActivity.portNumber+"/MTFServer01/rest/MTFServer01REST/getConfigFilesList";
            RESTHelperCallRequest req = new RESTHelperCallRequest();
            req.setURL(url); req.setRequestType("GET"); req.addParameter("Scenario", scenario);
            RESTHelper rh = new RESTHelper();
            rh.setReq(req); rh.execute();
            RESTHelperCallResult res = rh.getRESTCallResult();
            if (res.getStatus()!= "error") {
                List<String> list=new ArrayList<String>();
                JSONObject jo = new JSONObject(res.getContent());
                JSONArray configFiles = jo.getJSONArray("configFiles");
                for (int i=0; i<configFiles.length(); i++) { list.add((String) configFiles.get(i)); }
                String[] array = list.toArray(new String[list.size()]);
                AndroidHelper.populateSpinner(this, spServerConfigFiles, array);
            } else {
                AndroidHelper.showAlertDialog("Error", "hhh"+res.getErrorMessage(), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO not finished
    public void getServerConfigFile(View view) {
        try {
            String ip = etServerIP.getText().toString();
            String scenario = spServerScenarios.getSelectedItem().toString();
            String configFileName = spServerConfigFiles.getSelectedItem().toString();
            RESTHelperCallRequest req = new RESTHelperCallRequest();
            RESTHelper rh = new RESTHelper();
            RESTHelperCallResult res;
            String url;
            url = "http://"+ip+":"+rootActivity.portNumber+"/MTFServer01/rest/MTFServer01REST/getConfigFile";
            req.setURL(url); req.setRequestType("GET"); req.addParameter("Scenario", scenario); req.addParameter("ConfigFile", configFileName);
            rh.setReq(req); rh.execute();
            res = rh.getRESTCallResult();
            if (res.getStatus()!= "error") {
                String configFileXML = res.getContent();
                rootActivity.currentConfigFileXML = configFileXML;
                rootActivity.currentScenario = scenario;
                rootActivity.currentConfigFileName = configFileName;
                rootActivity.refreshCaptureTab();
                AndroidHelper.displayMessage("config file '" + configFileName + "' downloaded, loaded, & saved", this);
                createEditingHashMaps();
                createSampleDataXML();
                rootActivity.rbCamera.toggle();
                String color = XMLHelper.getValueFromXML(configFileXML, "/MTFDemoSpecs/CorporateID/MainColor");
                String brand = XMLHelper.getValueFromXML(configFileXML, "/MTFDemoSpecs/CorporateID/Brand");
                rootActivity.updateLookAndFeel(color, brand);
                if (AndroidHelper.checkExternalMedia().indexOf("W")>=0) {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File (root.getAbsolutePath() + "/MTFLocal/Scenarios/"+scenario);
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, configFileName);
                    try {
                        FileOutputStream f = new FileOutputStream(file);
                        f.write(configFileXML.getBytes());
                        f.close();
                        url = "http://"+ip+":"+rootActivity.portNumber+"/MTFServer01/rest/MTFServer01REST/getIcon";
                        req.setURL(url); req.setRequestType("GET"); req.addParameter("Scenario", scenario);
                        rh.setReq(req); rh.execute();
                        res = rh.getRESTCallResult();
                        if (res.getStatus()!="error") {
                            String icon = res.getContent();
                            File iconFile = new File(dir, "Icon.png");
                            FileOutputStream iconFIS = new FileOutputStream(iconFile);
                            byte[] iconBytes;
                            iconBytes = icon.getBytes("UTF-8");
                            iconBytes = Base64.decode(iconBytes, Base64.DEFAULT);
                            iconFIS.write(iconBytes);
                            iconFIS.close();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        AndroidHelper.showAlertDialog("ERROR", e.getMessage(), this);
                    } catch (IOException e) {
                        e.printStackTrace();
                        AndroidHelper.showAlertDialog("ERROR", e.getMessage(), this);
                    }
                }
            } else {
                AndroidHelper.showAlertDialog("ERROR", "erreur", this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocalScenariosList() {
        try {
            List<String> list=new ArrayList<String>();
            if (AndroidHelper.checkExternalMedia().indexOf("R")>=0) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/MTFLocal/Scenarios");
                String[] scenarios = dir.list();
                for (int i = 0; i < scenarios.length; i++) { list.add((String) scenarios[i]); }
                String[] array = list.toArray(new String[list.size()]);
                AndroidHelper.populateSpinner(this, spLocalScenarios, array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocalConfigFilesList() {
        try {
            List<String> list=new ArrayList<String>();
            String scenario = spLocalScenarios.getSelectedItem().toString();
            if (AndroidHelper.checkExternalMedia().indexOf("R")>=0) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/MTFLocal/Scenarios/"+scenario);
                String[] scenarios = dir.list();
                for (int i = 0; i < scenarios.length; i++) { list.add((String) scenarios[i]); }
                String[] array = list.toArray(new String[list.size()]);
                AndroidHelper.populateSpinner(this, spLocalConfigFiles, array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocalConfigFile(View view) {
        String scenario = this.spLocalScenarios.getSelectedItem().toString();
        String configFileName = this.spLocalConfigFiles.getSelectedItem().toString();
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File (root.getAbsolutePath() + "/MTFLocal/Scenarios/"+scenario+"/"+configFileName);
        try {
            String configFileXML = Utils.convertFile2String(file.getCanonicalPath());
            rootActivity.currentConfigFileXML = configFileXML;
            rootActivity.currentScenario = scenario;
            rootActivity.currentConfigFileName = configFileName;
            rootActivity.refreshCaptureTab();
            rootActivity.rbEdit.setVisibility(View.GONE);
            AndroidHelper.displayMessage("config file '" + configFileName + "' loaded", this);
            createEditingHashMaps();
            createSampleDataXML();
            rootActivity.rbCamera.toggle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createEditingHashMaps() {
        String configFileXML = rootActivity.currentConfigFileXML;
        String currentScenario = rootActivity.currentScenario;
        String[] fieldIds = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldId");
        String[] fieldNames = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldLabel");
        String[] fieldTypes = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldType");
        String[] fieldXPaths = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldXPath");
        String[] fieldValidValuess = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldValidValues");
        String[] fieldDefaultValues = XMLHelper.getValuesFromXML(configFileXML, "/MTFDemoSpecs/Edit/Field/FieldDefaultValue");
        HashMap<String, utils_edit_config_field> editingHashMap = new LinkedHashMap<>();
            for (int i=0; i < fieldNames.length; i++) {
                utils_edit_config_field f = new utils_edit_config_field();
                f.fieldId = fieldIds[i];
                f.fieldName = fieldNames[i];
                f.fieldType = fieldTypes[i];
                f.fieldXPath = fieldXPaths[i];
                f.fieldValidValues = fieldValidValuess[i];
                f.fieldDefaultValue = fieldDefaultValues[i];
                editingHashMap.put(fieldNames[i], f);
            }
        rootActivity.currentEditingHashMap = editingHashMap;
    }

    public void createSampleDataXML() {
        String currentScenario = rootActivity.currentScenario;
        HashMap<String, utils_edit_config_field> editingHashMap = rootActivity.currentEditingHashMap;
        if (AndroidHelper.checkExternalMedia().contains("W")) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dataFolder = new File(root.getAbsolutePath() + "/MTFLocal/Scenarios/" + currentScenario + "/Data");
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dataFile = new File(dataFolder, "newDataFile.xml");
            try {
                FileOutputStream f = new FileOutputStream(dataFile);
                String sampleXml = "<Documents></Documents>";
                XPath2XmlCreator x2x = new XPath2XmlCreator();
                x2x.initDocumentFromString(sampleXml);
                for (Map.Entry<String, utils_edit_config_field> entry : editingHashMap.entrySet()) {
                    utils_edit_config_field cf = entry.getValue();
                    x2x.addElement(cf.fieldXPath, cf.fieldDefaultValue);
                }
                sampleXml = x2x.getResultingDocument();
               rootActivity.currentDataXML = sampleXml;
                x2x = null;
                f.write(sampleXml.getBytes());
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                AndroidHelper.showAlertDialog("ERROR", e.getMessage(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

//        this would be safer in case nodes are missing in config file
//        try {
//            String xq;
//            xq =    "declare variable $doc external; " +
//                    "string-join( " +
//                    "   for $Field in $doc/MTFDemoSpecs/Edit/Field " +
//                    "   return " +
//                    "       concat( " +
//                    "           $Field/FieldId/text(), " +
//                    "           ':', " +
//                    "           $Field/FieldLabel/text(), " +
//                    "           ':', " +
//                    "           $Field/FieldType/text(), " +
//                    "           ':', " +
//                    "           $Field/FieldXPath/text(), " +
//                    "           ':', " +
//                    "           $Field/FieldValidValues/text(), " +
//                    "           ':', " +
//                    "           $Field/FieldDefaultValue/text()" +
//                    "       )" +
//                    "   ," +
//                    "   ';'" +
//                    ")";
//            String r = XMLHelper.runXQueryAgainstXML_saxon9(configFileXML, xq);
//        } catch (HelperException e) {
//            e.printStackTrace();
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        tvSettingsLog.append("\nonStart");
//    }
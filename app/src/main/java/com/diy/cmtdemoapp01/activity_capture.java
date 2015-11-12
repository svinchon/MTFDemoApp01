package com.diy.cmtdemoapp01;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.diy.helpers.android.v1.AndroidHelper;
import com.diy.helpers.v1.HelperException;
import com.diy.helpers.v1.JSONHelper;
import com.diy.helpers.v1.Utils;
import com.diy.helpers.v1.XMLHelper;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import emc.captiva.mobile.sdk.CaptureImage;
import emc.captiva.mobile.sdk.PictureCallback;

public class activity_capture extends Activity implements PictureCallback {

    private String TAG = this.getClass().getSimpleName();

    LinearLayout llImages;
    TextView tvSpinner1, tvSpinner2;
    Spinner spSpinner1, spSpinner2;

    utils_capture_mask[] masks;
    Map<String, utils_capture_mask> masksMap;

    int currentImageCount;
    String configFileXML, configFileJSON, currentMaskName, filePath;
    String[] maskNames;
    List<String> currentImages;

    activity_main rootActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        rootActivity = (activity_main)this.getParent();
        drawUI();
    }

    public void TakePicture(View view) {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> appParameters = new HashMap<>();
        parameters = AndroidHelper.getTakePictureParameters(this, appParameters);
        utils_capture_custom_window cust_win;
        cust_win = new utils_capture_custom_window(this, "none", appParameters, masksMap.get(currentMaskName));
        parameters.put(CaptureImage.PICTURE_CAPTUREWINDOW, cust_win);
        CaptureImage.takePicture(this, parameters);
    }

    @Override
    public void onPictureTaken(byte[] bImage) {
        //tvCaptureLog.append("\nPictureTaken");
        File fullPath = new File(AndroidHelper.getImageGalleryPath(), AndroidHelper.getUniqueFilename("Img", ".JPG"));
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bImage);
            AndroidHelper.saveFile(inputStream, fullPath);
            Uri uri = Uri.fromFile(fullPath);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            //Uri contentURI = Uri.parse(imageReturnedIntent.getDataString());
            ContentResolver cr = getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=8;
            Bitmap thumb = BitmapFactory.decodeStream(in,null,options);
            currentImageCount ++;
            int id = 10000+currentImageCount;
            //LinearLayout llimage = new LinearLayout(this);
            RelativeLayout llimage = new RelativeLayout(this);
            llimage.setId(id);
            //llimage.setOrientation(LinearLayout.HORIZONTAL);
            ImageView iv = new ImageView(this);
            iv.setImageBitmap(thumb);
            llimage.addView(iv);
            ImageView iv_icon = new ImageView(this);
            iv_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_white_24dp));
            iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View v = findViewById(
                            ((View)view.getParent()).getId()
                    );
                    ViewGroup vg = (ViewGroup)v.getParent();
                    vg.removeView(v);
                    // TODO delete picture from file system
                    currentImageCount--;
                    View b2 = findViewById(R.id.btnCaptureData);
                    View b1 = findViewById(R.id.btnTakePicture);
                    if (Integer.parseInt(masksMap.get(currentMaskName).requiredImageCount) == currentImageCount) {
                        b1.setVisibility(View.GONE);
                        b2.setVisibility(View.VISIBLE);
                    } else {
                        b1.setVisibility(View.VISIBLE);
                        b2.setVisibility(View.GONE);
                    }
                }
            });
            llimage.addView(iv_icon);
            llImages.addView(llimage);
            AndroidHelper.displayMessage("Picture taken ans saved to '" + fullPath + "'", this);
            currentImages.add(fullPath.toString());
            AndroidHelper.displayMessage(""+currentImageCount, this);
            View b2 = findViewById(R.id.btnCaptureData);
            View b1 = findViewById(R.id.btnTakePicture);
            if (Integer.parseInt(masksMap.get(currentMaskName).requiredImageCount) == currentImageCount) {
                b1.setVisibility(View.GONE);
                b2.setVisibility(View.VISIBLE);
            } else {
                b1.setVisibility(View.VISIBLE);
                b2.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            //Android.displayError(this, "Could not save the image to the gallery.");
        }
    }

    @Override
    public void onPictureCanceled(int i) { }

    public void getUpfrontChoicesLevel1() {
        String configFileXML = ((activity_main)this.getParent()).currentConfigFileXML;
        //String xq = "declare variable $doc external;<xdata>{for $i in $doc/root/item return $i}</xdata>";
        try {
            String xq;
            xq =    "declare variable $doc external; " +
                    "let $d:=$doc/MTFDemoSpecs/Capture " +
                    "return " +
                    "   concat(" +
                    "       $d/UpfrontChoices/*[1]/name(), " +
                    "       ';'," +
                    "       fn:string-join(" +
                    "           $d/UpfrontChoices/*[1]/Choice/Label, " +
                    "           ':'" +
                    "       )" +
                    "   )";
            String r = XMLHelper.runXQueryAgainstXML_saxon9(configFileXML, xq);
            String[] rr = r.split(";");
            String label1 = rr[0];
            String[] choices1 = rr[1].split(":");
            tvSpinner1.setText(label1);
            AndroidHelper.populateSpinner(this, spSpinner1, choices1);
        } catch (HelperException e) {
            AndroidHelper.displayMessage(e.message, this);
        }
    }

    public void getUpfrontChoicesLevel2() {
        configFileXML = ((activity_main)this.getParent()).currentConfigFileXML;
        String choice1 = spSpinner1.getSelectedItem().toString();
        configFileJSON = XMLHelper.convertToJSON(configFileXML);
        try {
            String xq;
            xq =    "declare variable $doc external; " +
                    "let $d:=$doc/MTFDemoSpecs/Capture " +
                    "return " +
                    "   concat(" +
                    "       $d/UpfrontChoices/*[1]/Choice[Label='"+choice1+"']/*[2]/name(), " +
                    "       ';'," +
                    "       fn:string-join(" +
                    "           $d/UpfrontChoices/*[1]/Choice[Label='"+choice1+"']/*[2]/Choice/Label, " +
                    "           ':'" +
                    "       )," +
                    "       ';'," +
                    "       fn:string-join(" +
                    "           $d/UpfrontChoices/*[1]/Choice[Label='"+choice1+"']/*[2]/Choice/Mask, " +
                    "           ':'" +
                    "       )" +
                    "   )";
            String r = XMLHelper.runXQueryAgainstXML_saxon9(configFileXML, xq);
            String[] rr = r.split(";");
            String label1 = rr[0];
            String[] choices2 = rr[1].split(":");
            maskNames = rr[2].split(":");
            masksMap = null;
            masksMap = new HashMap<String, utils_capture_mask>();
            masks = null;
            masks = new utils_capture_mask[maskNames.length];
            for (int i=0; i<maskNames.length; i++) {
                int boxesCount = Integer.parseInt(
                        XMLHelper.runXQueryAgainstXML_saxon9(
                                configFileXML,
                                "declare variable $doc external; " +
                                "let $c := count($doc/MTFDemoSpecs/Capture/Masks/Mask[Label='" + maskNames[i] + "']/Boxes/Box) " +
                                "return $c"
                        )
                );
                int ovalsCount = Integer.parseInt(
                        XMLHelper.runXQueryAgainstXML_saxon9(
                                configFileXML,
                                "declare variable $doc external; " +
                                "let $c := count($doc/MTFDemoSpecs/Capture/Masks/Mask[Label='" + maskNames[i] + "']/Ovals/Oval) " +
                                "return $c"
                        )
                );
                String maskDef = JSONHelper.runJPath(
                        configFileJSON,
                        "/MTFDemoSpecs/Capture/Masks/Mask["+(i+1)+"]"
                );
                masks[i] = new utils_capture_mask();
                masks[i].label = JSONHelper.runJPath(maskDef, "/Label");
                masks[i].orientation = JSONHelper.runJPath(maskDef, "/Orientation");
                masks[i].requiredImageCount = JSONHelper.runJPath(maskDef, "/RequiredImageCount");
                masks[i].boxes = new utils_capture_mask_box[boxesCount];
                for (int j=0;j<masks[i].boxes.length;j++) {
                    String boxDef = JSONHelper.runJPath(maskDef, "/Boxes/Box["+(j+1)+"]");
                    masks[i].boxes[j] = new utils_capture_mask_box();
                    masks[i].boxes[j].width_vs_canvas_width_in_percent = Integer.parseInt(JSONHelper.runJPath(boxDef, "/WidthVsCanvasWidthPercent"));
                    masks[i].boxes[j].width_vs_height_ratio= Float.parseFloat(JSONHelper.runJPath(boxDef, "/WidthVsHeightRatio"));
                    masks[i].boxes[j].color = JSONHelper.runJPath(boxDef, "/Color");
                    masks[i].boxes[j].comment = JSONHelper.runJPath(boxDef, "/Comment");
                    masks[i].boxes[j].commentPosition = JSONHelper.runJPath(boxDef, "/CommentPosition");
                }
                masks[i].ovals = new utils_capture_mask_oval[ovalsCount];
                for (int j=0;j<masks[i].ovals.length;j++) {
                    String ovalDef = JSONHelper.runJPath(maskDef, "/Ovals/Oval["+(j+1)+"]");
                    masks[i].ovals[j] = new utils_capture_mask_oval();
                    masks[i].ovals[j].width_vs_canvas_width_in_percent = Integer.parseInt(JSONHelper.runJPath(ovalDef, "/WidthVsCanvasWidthPercent"));
                    masks[i].ovals[j].width_vs_height_ratio= Float.parseFloat(JSONHelper.runJPath(ovalDef, "/WidthVsHeightRatio"));
                    masks[i].ovals[j].color = JSONHelper.runJPath(ovalDef, "/Color");
                    masks[i].ovals[j].comment = JSONHelper.runJPath(ovalDef, "/Comment");
                    masks[i].ovals[j].commentPosition = JSONHelper.runJPath(ovalDef, "/CommentPosition");
                    masks[i].ovals[j].center_x_vs_canvas_width_in_percent = Integer.parseInt(JSONHelper.runJPath(ovalDef, "/CenterXVsCanvasWidthPercent"));
                    masks[i].ovals[j].center_y_vs_canvas_height_in_percent = Integer.parseInt(JSONHelper.runJPath(ovalDef, "/CenterYVsCanvasHeightPercent"));
                }
                masksMap.put(maskNames[i], masks[i]);
            }
            tvSpinner2.setText(label1);
            AndroidHelper.populateSpinner(this, spSpinner2, choices2);
        } catch (HelperException e) {
            AndroidHelper.showAlertDialog("Error", e.getMessage(), this);
        }
    }

    public void drawUI() {
        this.setContentView(R.layout.activity_capture);
        spSpinner1 = (Spinner)this.findViewById(R.id.spSpinner1);
        spSpinner2 = (Spinner)this.findViewById(R.id.spSpinner2);
        tvSpinner1 = (TextView)this.findViewById(R.id.tvSpinner1);
        tvSpinner2 = (TextView)this.findViewById(R.id.tvSpinner2);
        spSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getUpfrontChoicesLevel2();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentMaskName = maskNames[i];
                currentImageCount = 0;
                currentImages = null;
                currentImages = new ArrayList<String>();
                llImages.removeAllViews();
                View b2 = findViewById(R.id.btnCaptureData);
                View b1 = findViewById(R.id.btnTakePicture);
                if (Integer.parseInt(masksMap.get(currentMaskName).requiredImageCount) == currentImageCount) {
                    b1.setVisibility(View.GONE);
                    b2.setVisibility(View.VISIBLE);
                } else {
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        llImages = (LinearLayout)this.findViewById(R.id.llImages);
        getUpfrontChoicesLevel1();
        currentImageCount = 0;
        currentImages = null;
        currentImages = new ArrayList<String>();
    }

    public void captureData(View view) {
        UploadFileToServer ufts = new UploadFileToServer();
        ufts.execute();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            //<editor-fold desc="add code for progress bar here">
            // setting progress bar to zero
            //progressBar.setProgress(0);
            //</editor-fold>
            super.onPreExecute();
            AndroidHelper.displayMessage(currentImages.toString(), getApplicationContext());
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //<editor-fold desc="add code for progress bar here">
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            //progressBar.setProgress(progress[0]);
            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
            //</editor-fold>
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        private String uploadFile() {
            String responseString = null;
            MultipartBuilder mb = new MultipartBuilder().type(MultipartBuilder.FORM);
                        //<editor-fold desc="add code to add regular fields here ">
                        //                .addFormDataPart("field", "value")
                        //</editor-fold>
            for (String cf : currentImages) {
                File sourceFile = new File(cf);
                mb.addFormDataPart(
                        "image_file",
                        cf,
                        RequestBody.create(
                                MediaType.parse("image/png"),
                                sourceFile //has to return a file object
                        )
                );
            }
            mb.addFormDataPart("Scenario", rootActivity.currentScenario);
            RequestBody requestBody =  mb.build();
            Request request = new Request.Builder()
                .url("http://" + rootActivity.currentServerIP + ":18080/MTFServer01/rest/MTFServer01REST/uploadAndProcessImages")
                        //<editor-fold desc="add code for authentication here">
                        //                .addHeader("X-Auth-Token", "user")
                        //</editor-fold>
                .post(requestBody)
                .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                responseString = "ERROR";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showAlertDialog(result);
            rootActivity.currentOCRResult = result;
            UpdateCurrentDataXML();

        }

    }

    private void UpdateCurrentDataXML() {
        String currentDataXML = rootActivity.currentDataXML;
        String currentOCRResult = rootActivity.currentOCRResult;
        String[] OCRResultFieldNames = XMLHelper.getValuesFromXML(currentOCRResult, "/root/field/name");
        String[] OCRResultFieldValues = XMLHelper.getValuesFromXML(currentOCRResult, "/root/field/value");
        Map<String, String> fieldsMap = new LinkedHashMap<>();
        for (int i=0;i<OCRResultFieldNames.length;i++) {
            fieldsMap.put(OCRResultFieldNames[i], OCRResultFieldValues[i]);
        }
        HashMap<String, utils_edit_config_field> editingHashMap = rootActivity.currentEditingHashMap;
        for (Map.Entry<String, utils_edit_config_field> entry : editingHashMap.entrySet()) {
            String xPath =  "/"+entry.getValue().fieldXPath;
            String fieldName = entry.getValue().fieldName;
            if (fieldsMap.get(fieldName)!=null) currentDataXML = XMLHelper.updateNodeInXMLString(currentDataXML, xPath, fieldsMap.get(fieldName));
        }
        rootActivity.currentDataXML = currentDataXML;

    }

    public void onOCRFeedbackReceived(View view) {
        onOCRFeedbackReceivedJSON();
        onOCRFeedbackReceivedXML();
    }

    public void onOCRFeedbackReceivedXML() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String fullPath = root.getAbsolutePath()
                + "/MTFLocal/Scenarios/"
                + rootActivity.currentScenario
                + "/Data/"
                + "S1a.xml";
        String data = Utils.convertFile2String(fullPath);
    }

    public void onOCRFeedbackReceivedJSON() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String fullPath = root.getAbsolutePath()
                + "/MTFLocal/Scenarios/"
                + rootActivity.currentScenario
                + "/Data/"
                + "data_success.json";
        String data = Utils.convertFile2String(fullPath);
        try {
            JSONObject response = new JSONObject(data);
            if (response.getString("status").indexOf("error")<0) {
                AndroidHelper.displayMessage("success!", this);
//                String fields = JSONHelper.runJPath(data,"/MTFDemoSpecs/Capture/Masks/Mask["+(i+1)+"]");
                JSONArray fields = response.getJSONArray("data");
                int fieldCount = fields.length();
                Map<Integer, Map<String, String>> map = new HashMap<Integer, Map<String,String>>();
//                LinkedHashMap
                for (int i=0;i<fieldCount;i++) {
                    JSONObject field = fields.getJSONObject(i);
                    Map<String, String> fmap = new HashMap<String, String>();
                    for (int j=0;j<field.names().length();j++) {
                        String n = field.names().getString(j);
                        String v = field.getString(n);
                        fmap.put(n, v);
                    }
                    map.put(i, fmap);
                }
                AndroidHelper.displayMessage("map size: "+map.size(),this);
                AndroidHelper.displayMessage("fmap size: "+map.get(0).size(),this);
//                Iterator i = map.entrySet().iterator();
//                while (i.hasNext()) {
//                }
               for (Map.Entry<Integer,Map<String, String>> entry : map.entrySet()) {
                   int i = entry.getKey();
                   String s = entry.getValue().get("fieldName");
                   AndroidHelper.displayMessage("fn: " + s, this);
                }
//                GsonBuilder builder = new GsonBuilder();
//                Gson gson = builder.create();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showAlertDialog(String str) {
        AndroidHelper.showAlertDialog("showAlertDialog", str, this);
    }

}

//<editor-fold desc="Old code">
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            Log.i(TAG, "hello world");
//        }
//        catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//            //CoreHelper.displayError(this, e);
//        }
//    }
//
//    public String parse(String jsonLine) {
//        JsonElement jelement = new JsonParser().parse(jsonLine);
//        JsonObject  jobject = jelement.getAsJsonObject();
//        jobject = jobject.getAsJsonObject("data");
//        JsonArray jarray = jobject.getAsJsonArray("translations");
//        jobject = jarray.get(0).getAsJsonObject();
//        String result = jobject.get("translatedText").toString();
//        return result;
//    }
//
//        try {
//            JSONObject jsonObject = new JSONObject(jsonConfigFile);
//        } catch (JSONException je){
//            AndroidHelper.displayMessage(je.getMessage(), this);
//        }
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = new File(filePath);
//
//                // Adding file data to http body
//                entity.addPart("image", new FileBody(sourceFile));
//
//                // Extra parameters if you want to pass to server
//                entity.addPart("website",
//                        new StringBody("www.androidhive.info"));
//                entity.addPart("email", new StringBody("abc@gmail.com"));
//
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//</editor-fold>
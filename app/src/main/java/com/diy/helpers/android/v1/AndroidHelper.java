package com.diy.helpers.android.v1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emc.captiva.mobile.sdk.CaptureImage;

public class AndroidHelper {

    private static int _imageCounter = 0;

    public static String checkExternalMedia() {
        String rights = "";
        //boolean mExternalStorageAvailable = false;
        //boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //mExternalStorageAvailable = mExternalStorageWriteable = true;
            rights = "RW";
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            //mExternalStorageAvailable = true;
            //mExternalStorageWriteable = false;
            rights = "RO";
        } else {
            //mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return rights;
    }

    public static void saveFile(InputStream inputStream, File fullPath) throws IOException {
        byte[] buffer = new byte[1000];
        OutputStream outputStream = new FileOutputStream(fullPath, false);
        int ret;
        while (true) {
            ret = inputStream.read(buffer, 0, 1000);
            if (ret <= 0) {
                break;
            }
            outputStream.write(buffer, 0, ret);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public static void populateSpinner(Context ctx, Spinner spinner, String[] choices) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < choices.length; i++) {
            list.add((String) choices[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ctx,
                android.R.layout.simple_list_item_1,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //@SuppressLint("SimpleDateFormat")
    public static String getUniqueFilename(String prefix, String extension) {
        if (prefix == null) {
            prefix = "";
        }
        if (extension == null) {
            extension = "";
        }
        String timeStamp = new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date());
        return prefix + timeStamp + String.valueOf(++_imageCounter) + extension;
    }

    public static File getImageGalleryPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public static HashMap<String, Object> getTakePictureParameters(Context ctx, Map<String, Object> appParams) {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put(CaptureImage.PICTURE_CONTEXT, ctx);

        if (ctx != null) {
            // Retrieve user's preferences.
            SharedPreferences gprefs = PreferenceManager.getDefaultSharedPreferences(ctx);

            boolean lightSensor = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_LIGHT), true);
            boolean motionSensor = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_MOTION), true);

            //appParams.put(MainActivity.USE_MOTION, motionSensor);

            boolean focusSensor = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_FOCUS), true);
            boolean qualitySensor = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_QUALITY), false);
            boolean guideLines = true; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_GUIDELINES), false);
            boolean pictureCrop = true; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_PICTURE_CROP), false);
            String pictureCropColor = "green"; //gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_PICTURE_CROP_COLOR), "green");
            String pictureCropWarningColor = "red"; // = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_PICTURE_CROP_WARNING_COLOR), "red");
            float pictureCropAspectWidth = 8.5f; //String temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_PICTURE_CROP_ASPECT_WIDTH), "8.5"); //getFloat(temp, 8.5f);
            float pictureCropAspectHeight = 11f;//temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_PICTURE_CROP_ASPECT_HEIGHT), "11"); //getFloat(temp, 11f);
            boolean optimalCondReq = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_OPTIMALCONDREQ), false);
            boolean cancelBtn = true; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_CANCEL), false);
            boolean torchBtn = true; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_TORCH_BUTTON), true);
            boolean torch = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_TORCH), false);
            Integer lightSensitivity = 50;//temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_LIGHT_VALUE), "50"); //getInteger(temp, 50);
            Float motionSensitivity = 0.3f;//temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_MOTION_VALUE), ".30");//getFloat(temp, .30f);
            Integer qualityGlare = 0; //temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_QUALITY_GLARE_VALUE), "0");//getInteger(temp, 0);
            Integer qualityPerspective = 0;//temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_QUALITY_PERSPECTIVE_VALUE), "0");//getInteger(temp, 0);
            Integer qualityQuadrilateral = 0; //temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_SENSOR_QUALITY_QUADRILATERAL_VALUE), "0");//getInteger(temp, 0);
            Integer captureDelayMs = 500;  //temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_CAPTUREDELAY), "");//getInteger(temp, 500);
            Integer continuousCaptureFrameDelayMs = 500; //temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_CONTINUOUSCAPTUREFRAMEDELAY), "");//getInteger(temp, 500);
            Integer captureTimeoutMs = 0; //temp = gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_CAPTURETIMEOUT), "");//getInteger(temp, 0);
            boolean captureImmediately = false; //gprefs.getBoolean(CoreHelper.getStringResource(ctx, R.string.GPREF_AUTOCAPTURE), false);
            String edgeLabel = ""; //gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_EDGELABEL), "");
            String centerLabel = ""; //gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_CENTERLABEL), "");
            String captureLabel = ""; //gprefs.getString(CoreHelper.getStringResource(ctx, R.string.GPREF_CAPTURINGLABEL), "");
            String pictueSensors = "";
            pictueSensors = lightSensor ? pictueSensors.concat("l") : pictueSensors;
            pictueSensors = motionSensor ? pictueSensors.concat("m") : pictueSensors;
            pictueSensors = focusSensor ? pictueSensors.concat("f") : pictueSensors;
            pictueSensors = qualitySensor ? pictueSensors.concat("q") : pictueSensors;

            parameters.put(CaptureImage.PICTURE_LABEL_EDGE, edgeLabel);
            parameters.put(CaptureImage.PICTURE_LABEL_CENTER, centerLabel);
            parameters.put(CaptureImage.PICTURE_LABEL_CAPTURE, captureLabel);
            parameters.put(CaptureImage.PICTURE_SENSORS, pictueSensors);
            parameters.put(CaptureImage.PICTURE_SENSITIVITY_LIGHT, lightSensitivity);
            parameters.put(CaptureImage.PICTURE_SENSITIVITY_MOTION, motionSensitivity);

            boolean useQuadView = false;
            if (qualitySensor) {
                HashMap<String, Object> measures = new HashMap<>();
                if (qualityGlare > 0 && qualityGlare <= 101) {
                    measures.put(CaptureImage.MEASURE_GLARE, qualityGlare);
                }
                if (qualityPerspective > 0 && qualityPerspective <= 100) {
                    measures.put(CaptureImage.MEASURE_PERSPECTIVE, qualityPerspective);
                    useQuadView = true;
                }
                if (qualityQuadrilateral > 0 && qualityQuadrilateral <= 100) {
                    measures.put(CaptureImage.MEASURE_QUADRILATERAL, qualityQuadrilateral);
                    useQuadView = true;
                }
                parameters.put(CaptureImage.PICTURE_QUALITY_MEASURES, measures);
            }

            //appParams.put(MainActivity.USE_QUADVIEW, useQuadView);

            parameters.put(CaptureImage.PICTURE_GUIDELINES, guideLines); // GUIDELINES
            parameters.put(CaptureImage.PICTURE_CROP, pictureCrop); // PICTURECROP
            parameters.put(CaptureImage.PICTURE_CROP_COLOR, pictureCropColor); // PICTURECROP
            parameters.put(CaptureImage.PICTURE_CROP_WARNING_COLOR, pictureCropWarningColor); // PICTURECROP
            parameters.put(CaptureImage.PICTURE_CROP_ASPECT_WIDTH, pictureCropAspectWidth); // PICTURECROP
            parameters.put(CaptureImage.PICTURE_CROP_ASPECT_HEIGHT, pictureCropAspectHeight); // PICTURECROP
            parameters.put(CaptureImage.PICTURE_CAPTURE_DELAY, captureDelayMs);// CAPTUREDELAY
            parameters.put(CaptureImage.PICTURE_CONTINUOUS_CAPTURE_FRAME_DELAY, continuousCaptureFrameDelayMs);// CONTINUOUSCAPTUREFRAMEDELAY
            parameters.put(CaptureImage.PICTURE_CAPTURE_TIMEOUT, captureTimeoutMs);// CAPTURETIMEOUT
            parameters.put(CaptureImage.PICTURE_IMMEDIATE, captureImmediately);// CAPTUREIMMEDIATELY
            parameters.put(CaptureImage.PICTURE_OPTIMAL_CONDITIONS, optimalCondReq);// OPTIMALCONDREQ
            parameters.put(CaptureImage.PICTURE_BUTTON_CANCEL, cancelBtn);// CANCEL BUTTON
            parameters.put(CaptureImage.PICTURE_BUTTON_TORCH, torchBtn);// TORCH BUTTON
            parameters.put(CaptureImage.PICTURE_TORCH, torch); // Initial torch mode.
        }

        // Return parameters.
        return parameters;
    }

    public static void displayMessage(String str, Context ctx) {
        Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
        Log.i("MESSAGE", str);
    }

    public static void showAlertDialog(String Title, String msg, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setTitle(Title);
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

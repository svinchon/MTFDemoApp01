package com.diy.cmtdemoapp01;

import android.app.Activity;
//import android.hardware.Camera;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.diy.helpers.android.v1.AndroidHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import emc.captiva.mobile.sdk.CaptureImage;
import emc.captiva.mobile.sdk.PictureCallback;

public class activity_capture extends Activity implements PictureCallback {

    private String TAG = this.getClass().getSimpleName();

    TextView tvCaptureLog;
    LinearLayout llImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_capture);
        tvCaptureLog = (TextView)this.findViewById(R.id.tvCaptureLog);
        llImages = (LinearLayout)this.findViewById(R.id.llImages);
        tvCaptureLog.setText(TAG + " - onCreate called");
    }

    public void TakePicture(View view) {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> appParameters = new HashMap<>();
        parameters = AndroidHelper.getTakePictureParameters(this, appParameters);
        CaptureImage.takePicture(this, parameters);
    }

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

    @Override
    public void onPictureTaken(byte[] bImage) {
        tvCaptureLog.append("\nPictureTaken");
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
            ImageView iv1 = new ImageView(this);
            iv1.setImageBitmap(thumb);
            llImages.addView(iv1);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            //Android.displayError(this, "Could not save the image to the gallery.");
        }

    }

    @Override
    public void onPictureCanceled(int i) { }

 }

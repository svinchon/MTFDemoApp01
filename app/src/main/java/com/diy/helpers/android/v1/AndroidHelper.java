package com.diy.helpers.android.v1;

import android.os.Environment;

public class AndroidHelper {

    public static String checkExternalMedia(){
        String rights = "";
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
            rights = "RW";
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            rights = "RO";
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return rights;
    }

}

package com.diy.cmtdemoapp01;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.diy.helpers.v1.Utils;
import com.diy.helpers.v1.XMLHelper;

import java.io.File;
import java.util.HashMap;

public class activity_main extends TabActivity implements TabHost.TabContentFactory, TabHost.OnTabChangeListener {

    Resources r;
    TabHost tabHost;
    String color_background_primary;
    String color_background_secondary;
    String color_text_primary;
    TextView tt;
    ImageView ti1, ti2;
    LinearLayout tl;
    String  rootPath,
            currentServerIP,
            currentScenario,
            currentConfigFileName,
            currentConfigFileXML,
            currentDataXML,
            currentOCRResult,
            currentPreferencesXML;
    RadioGroup rg;
    RadioButton rbCamera, rbSearch, rbEdit, rbAttach, rbProcess, rbSettings;
    HashMap<String, utils_edit_config_field> currentEditingHashMap;
    int portNumber = 18080;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MTFLocal";

        String preferencePath = rootPath + "/Preferences.xml";
        File preferencesFile =  new File(preferencePath);
        if (!preferencesFile.exists()) {
            String defaultPreferencesXML = "<Preferences><ServerIP/></Preferences>";
            currentPreferencesXML = defaultPreferencesXML;
            Utils.convertByte2File(currentPreferencesXML.getBytes(), preferencePath);
        } else {
            currentPreferencesXML = Utils.convertFile2String(preferencePath);
        }

        currentConfigFileXML = "";

        r = getResources();
        color_background_primary = r.getString(R.string.color_background_primary);
        color_background_secondary = r.getString(R.string.color_background_secondary);
        color_text_primary = r.getString(R.string.color_text_primary);

        this.setContentView(R.layout.activity_main);

        tt = (TextView) findViewById(R.id.appTitle);
        ti1 = (ImageView)findViewById(R.id.appTitleImageView1);
        ti2 = (ImageView)findViewById(R.id.appTitleImageView2);
        tl = (LinearLayout)findViewById(R.id.appTitleLayout);
        tt.setText(R.string.app_name);
        tt.setTextColor(Color.parseColor(color_text_primary));
        ti1.setImageDrawable(r.getDrawable(R.drawable.ic_settings_white_48dp));
        ti2.setImageDrawable(r.getDrawable(R.drawable.ic_settings_white_48dp));
        tl.setBackgroundColor(Color.parseColor(color_background_primary));

        tabHost = getTabHost();

        TabHost.TabSpec ts_capture = tabHost.newTabSpec("capture");
        TabHost.TabSpec ts_search = tabHost.newTabSpec("search");
        TabHost.TabSpec ts_edit = tabHost.newTabSpec("edit");
        TabHost.TabSpec ts_attach = tabHost.newTabSpec("attach");
        TabHost.TabSpec ts_proceed = tabHost.newTabSpec("proceed");
        TabHost.TabSpec ts_settings = tabHost.newTabSpec("settings");

        tabHost.addTab(ts_capture.setIndicator("", r.getDrawable(R.drawable.ic_camera)).setContent(this));
        tabHost.addTab(ts_search.setIndicator("", r.getDrawable(R.drawable.ic_search)).setContent(this));
        tabHost.addTab(ts_edit.setIndicator("", r.getDrawable(R.drawable.ic_edit)).setContent(this));
        tabHost.addTab(ts_attach.setIndicator("", r.getDrawable(R.drawable.ic_attach)).setContent(this));
        tabHost.addTab(ts_proceed.setIndicator("", r.getDrawable(R.drawable.ic_flight_takeoff)).setContent(this));
        tabHost.addTab(ts_settings.setIndicator("", r.getDrawable(R.drawable.ic_settings)).setContent(this));

        Intent settingsIntent = new Intent(this, activity_settings.class);
        Intent captureIntent = new Intent(this, activity_capture.class);
        Intent searchIntent = new Intent(this, activity_search.class);
        Intent editIntent = new Intent(this, activity_view_edit_data.class);
        Intent attachIntent = new Intent(this, activity_attach.class);
        Intent proceedIntent = new Intent(this, activity_proceed.class);

        ts_settings.setContent(settingsIntent);
        ts_capture.setContent(captureIntent);
        ts_search.setContent(searchIntent);
        ts_edit.setContent(editIntent);
        ts_attach.setContent(attachIntent);
        ts_proceed.setContent(proceedIntent);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color_background_primary));
        }
        tabHost.getTabWidget().getChildAt(5).setBackgroundColor(Color.parseColor("#FFFFFF"));
        tabHost.setOnTabChangedListener(this);
        tabHost.setCurrentTab(5);

        RadioButton rbSettings = (RadioButton) findViewById(R.id.rbSettings);

        rbCamera = (RadioButton) findViewById(R.id.rbCamera);
        rbSearch = (RadioButton) findViewById(R.id.rbSearch);
        rbEdit = (RadioButton) findViewById(R.id.rbEdit);
        rbAttach = (RadioButton) findViewById(R.id.rbAttach);
        rbProcess = (RadioButton) findViewById(R.id.rbProcess);

        rbCamera.setVisibility(View.GONE);
        rbEdit.setVisibility(View.GONE);
        rbSearch.setVisibility(View.GONE);
        //rbAttach.setVisibility(View.GONE);
        rbProcess.setVisibility(View.GONE);

        int backgroundColor = Color.parseColor("#FFFFFF");
        StateListDrawable sldCamera = new StateListDrawable();
        sldCamera.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroundColor));
        rbCamera.setBackground(sldCamera);
        StateListDrawable sldSearch = new StateListDrawable();
        sldSearch.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroundColor));
        rbSearch.setBackground(sldSearch);
        StateListDrawable sldEdit = new StateListDrawable();
        sldEdit.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroundColor));
        rbEdit.setBackground(sldEdit);
        StateListDrawable sldAttach = new StateListDrawable();
        sldAttach.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroundColor));
        rbAttach.setBackground(sldAttach);
        StateListDrawable sldProcess = new StateListDrawable();
        sldProcess.addState( new int[]{android.R.attr.state_checked},  new ColorDrawable(backgroundColor) );
        rbProcess.setBackground(sldProcess);
        StateListDrawable sldSettings = new StateListDrawable();
        sldSettings.addState( new int[]{android.R.attr.state_checked}, new ColorDrawable(backgroundColor) );
        rbSettings.setBackground(sldSettings);

        //RadioGroup
        rg = (RadioGroup) findViewById(R.id.states);

        rg.setBackgroundColor(Color.parseColor(color_background_primary));

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
            switch (checkedId) {
                case R.id.rbCamera:
                    getTabHost().setCurrentTab(0);
                    break;
                case R.id.rbSearch:
                    getTabHost().setCurrentTab(1);
                    break;
                case R.id.rbEdit:
                    getTabHost().setCurrentTab(2);
                    break;
                case R.id.rbAttach:
                    getTabHost().setCurrentTab(3);
                    break;
                case R.id.rbProcess:
                    getTabHost().setCurrentTab(4);
                    break;
                case R.id.rbSettings:
                    getTabHost().setCurrentTab(5);
                    break;
            }
            }
        });
    }

    @Override
    public View createTabContent(String tag) {
        TextView textView = new TextView(this);
        return textView;
    }

    @Override
    public void onTabChanged(String tabId) {
        for(
                int i=0;
                i<tabHost.getTabWidget().getChildCount();
                i++
        ) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color_background_primary));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF"));
        //createTabContent(tabId);
    }

    public void refreshCaptureTab() {
        rbCamera.setVisibility(View.VISIBLE);
        activity_capture activity = (activity_capture)(getLocalActivityManager().getActivity("capture"));
        if (activity!=null) {
            activity.drawUI();
        }
    }

    public void refreshEditTab() {
        rbEdit.setVisibility(View.VISIBLE);
        activity_view_edit_data activity = (activity_view_edit_data)(getLocalActivityManager().getActivity("edit"));
        if (activity!=null) {
            activity.drawUI();
        }
    }

    public void updatePreferencesAndSave(String item, String value) {
        currentPreferencesXML = XMLHelper.updateNodeInXMLString(currentPreferencesXML, "/Preferences/" + item, value);
        Utils.convertByte2File(currentPreferencesXML.getBytes(), rootPath + "/Preferences.xml");
    }

    public void updateLookAndFeel(String color, String title) {
        color_background_primary = color;
        tl.setBackgroundColor(Color.parseColor(color_background_primary));
        rg.setBackgroundColor(Color.parseColor(color_background_primary));
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color_background_primary));
            if (tabHost.getTabWidget().getChildAt(i).isSelected()) {
                tabHost.getTabWidget().getChildAt(5).setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color_background_primary)));
        bar.setTitle(title);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_main);
        TextView tv = (TextView) findViewById(R.id.abtext);
        ImageView iv = (ImageView) findViewById(R.id.abImage);
        tv.setText(title);
        File imgFile = new  File("/sdcard/MTFLocal/Scenarios/"+currentScenario+"/Icon.png");
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        iv.setImageBitmap(myBitmap);
    }

//        tabHost.addTab(
//                ts_capture.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_camera))
//                ).setContent(this)
//        );
//        tabHost.addTab(
//                ts_search.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_search))
//                ).setContent(this)
//        );
//        tabHost.addTab(
//                ts_edit.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_edit))
//                ).setContent(this)
//        );
//        tabHost.addTab(
//                ts_attach.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_attach))
//
//                ).setContent(this)
//        );
//        tabHost.addTab(
//                ts_proceed.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_flight_takeoff))
//                ).setContent(this)
//        );
//        tabHost.addTab(
//                ts_settings.setIndicator(
//                        createIndicatorView(tabHost, "tab title", getResources().getDrawable(R.drawable.ic_settings))
//                ).setContent(this)
//        );
//        tabHost.getTabWidget().getChildAt(5).setVisibility(View.GONE);
//        tabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
//        TabWidget tw = tabHost.getTabWidget();
//        tw.setOrientation(LinearLayout.VERTICAL);
//        int decode = Integer.decode("FF6666");
//        ColorDrawable colorDrawable = new ColorDrawable(decode);
//
//   private void shortcutAdd(String name, int number) {
//        // Intent to be send, when shortcut is pressed by user ("launched")
//        Intent shortcutIntent = new Intent(getApplicationContext(), Play.class);
//        shortcutIntent.setAction(SyncStateContract.Constants.ACTION_PLAY);
//
//        // Create bitmap with number in it -> very default. You probably want to give it a more stylish look
//        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//        Paint paint = new Paint();
//        paint.setColor(0xFF808080); // gray
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setTextSize(50);
//        new Canvas(bitmap).drawText(""+number, 50, 50, paint);
//        ((ImageView) findViewById(R.id.icon)).setImageBitmap(bitmap);
//
//        // Decorate the shortcut
//        Intent addIntent = new Intent();
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
//
//        // Inform launcher to create shortcut
//        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//        getApplicationContext().sendBroadcast(addIntent);
//    }
//
//    private void shortcutDel(String name) {
//        // Intent to be send, when shortcut is pressed by user ("launched")
//        Intent shortcutIntent = new Intent(getApplicationContext(), Play.class);
//        shortcutIntent.setAction(SyncStateContract.Constants.ACTION_PLAY);
//
//        // Decorate the shortcut
//        Intent delIntent = new Intent();
//        delIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//        delIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//
//        // Inform launcher to remove shortcut
//        delIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
//        getApplicationContext().sendBroadcast(delIntent);
//    }
//
//    private View createIndicatorView(TabHost tabHost, CharSequence label, Drawable icon) {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View tabIndicator = inflater.inflate(R.layout.tab_indicator,
//                tabHost.getTabWidget(), // tab widget is the parent
//                false); // no inflate params
//        final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
//        tv.setText(label);
//        final ImageView iconView = (ImageView) tabIndicator.findViewById(R.id.icon);
//        iconView.setImageDrawable(icon);
//        return tabIndicator;
//    }

//    <activity
//    android:name=".MyActivity"
//    android:label="@string/app_name"
//    <intent-filter>
//    <action android:name="com.mycompany.myApp.DO_SOMETHING" />
//    </intent-filter>
//    </activity>
//    public class MyActivity extends Activity {
//
//        private MyListener listener = null;
//        private Boolean MyListenerIsRegistered = false;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreated(savedInstanceState);
//
//            listener = new MyListener();
//        }
//
//        @Override
//        protected void onResume() {
//            super.onResume();
//
//            if (!MyListenerIsRegistered) {
//                registerReceiver(listener, new IntentFilter("com.mycompany.myApp.DO_SOMETHING"));
//                MyListenerIsRegisterd = true;
//            }
//        }
//
//        @Override
//        protected void onPause() {
//            super.onPause();
//
//            if (MyListenerIsRegistered) {
//                unregisterReceiver(listener);
//                MyListenerIsRegistered = false;
//            }
//        }
//
//        // Nested 'listener'
//        protected class MyListener extends BroadcastReceiver {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // No need to check for the action unless the listener will
//                // will handle more than one - let's do it anyway
//                if (intent.getAction().equals("com.mycompany.myApp.DO_SOMETHING")) {
//                    // Do something
//                }
//            }
//        }
//    }
//
//    private void MakeChildDoSomething() {
//
//        Intent i = new Intent();
//        i.setAction("com.mycompany.myApp.DO_SOMETHING");
//        sendBroadcast(i);
//    }

}



package com.diy.cmtdemoapp01;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class activity_main extends TabActivity implements TabHost.TabContentFactory, TabHost.OnTabChangeListener {

    TabHost tabHost;
    Resources r;
    String color_background_primary;
    String color_background_secondary;
    String color_text_primary;
    TextView tt;
    ImageView ti1, ti2;
    LinearLayout tl;
    String configFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        configFile = "";

        r = getResources();
        color_background_primary = r.getString(R.string.color_background_primary);
        color_background_secondary = r.getString(R.string.color_background_secondary);
        color_text_primary = r.getString(R.string.color_text_primary);

        //this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        this.setContentView(R.layout.activity_main);

        //this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        //tt = (TextView) findViewById(R.id.titlebartext);
        //ti = (ImageView)findViewById(R.id.titlebarimage);
        //tl = (LinearLayout)findViewById(R.id.titlebarlayout);
        //tt.setText(R.string.app_name);
        //ti.setImageDrawable(r.getDrawable(R.drawable.ic_attach_file_black_48dp));
        //tl.setBackgroundColor(Color.parseColor(color_primary));

        tt = (TextView) findViewById(R.id.appTitle);
        ti1 = (ImageView)findViewById(R.id.appTitleImageView1);
        ti2 = (ImageView)findViewById(R.id.appTitleImageView2);
        tl = (LinearLayout)findViewById(R.id.appTitleLayout);
        tt.setText(R.string.app_name);
        tt.setTextColor(Color.parseColor(color_text_primary));
        ti1.setImageDrawable(r.getDrawable(R.drawable.ic_settings_white_48dp));
        ti2.setImageDrawable(r.getDrawable(R.drawable.ic_settings_white_48dp));
        tl.setBackgroundColor(Color.parseColor(color_background_primary));

        // create tabhost to contain tabs
        tabHost = getTabHost();

//        tabHost.addTab(tabHost.newTabSpec("camera").setIndicator("", getResources().getDrawable(R.drawable.ic_camera)).setContent(this));
//        tabHost.addTab(tabHost.newTabSpec("edit").setIndicator("", getResources().getDrawable(R.drawable.ic_edit)).setContent(this));
//        tabHost.addTab(tabHost.newTabSpec("attach").setIndicator("", getResources().getDrawable(R.drawable.ic_attach)).setContent(this));
//        tabHost.addTab(tabHost.newTabSpec("proceed").setIndicator("", getResources().getDrawable(R.drawable.ic_flight_takeoff)).setContent(this));

        TabHost.TabSpec ts_capture = tabHost.newTabSpec("capture");
        TabHost.TabSpec ts_search = tabHost.newTabSpec("search");
        TabHost.TabSpec ts_edit = tabHost.newTabSpec("edit");
        TabHost.TabSpec ts_attach = tabHost.newTabSpec("attach");
        TabHost.TabSpec ts_proceed = tabHost.newTabSpec("proceed");
        TabHost.TabSpec ts_settings = tabHost.newTabSpec("settings");

        Intent captureIntent = new Intent(this, activity_capture.class);
        Intent searchIntent = new Intent(this, activity_search.class);
        Intent editIntent = new Intent(this, activity_view_edit_data.class);
        Intent attachIntent = new Intent(this, activity_attach.class);
        Intent proceedIntent = new Intent(this, activity_proceed.class);
        Intent settingsIntent = new Intent(this, activity_settings.class);

        tabHost.addTab(ts_capture.setIndicator("", getResources().getDrawable(R.drawable.ic_camera)).setContent(this));
        tabHost.addTab(ts_search.setIndicator("", getResources().getDrawable(R.drawable.ic_search)).setContent(this));
        tabHost.addTab(ts_edit.setIndicator("", getResources().getDrawable(R.drawable.ic_edit)).setContent(this));
        tabHost.addTab(ts_attach.setIndicator("", getResources().getDrawable(R.drawable.ic_attach)).setContent(this));
        tabHost.addTab(ts_proceed.setIndicator("", getResources().getDrawable(R.drawable.ic_flight_takeoff)).setContent(this));
        tabHost.addTab(ts_settings.setIndicator("", getResources().getDrawable(R.drawable.ic_settings)).setContent(settingsIntent));

        ts_capture.setContent(captureIntent);
        ts_search.setContent(searchIntent);
        ts_edit.setContent(editIntent);
        ts_attach.setContent(attachIntent);
        ts_proceed.setContent(proceedIntent);
        ts_settings.setContent(settingsIntent);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color_background_primary));
        }
        tabHost.getTabWidget().getChildAt(5).setBackgroundColor(Color.parseColor("#FFFFFF"));

        tabHost.setOnTabChangedListener(this);

        init();

        //tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
        //tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
        //tabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
        //tabHost.getTabWidget().getChildAt(3).setVisibility(View.GONE);

        //tabHost.getTabWidget().getChildAt(5).setVisibility(View.GONE);
        //tabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);

        //tabHost.getTabWidget().setCurrentTab(5);
        tabHost.setCurrentTab(5);
        //onTabChanged("settings");
        //createTabContent("settings");
    }

    @Override
    public View createTabContent(String tag) {
        TextView textView = new TextView(this);
//        textView.setTextColor(Color.BLACK);
//        textView.setTextSize(20);
//        switch (tag) {
//            case "settings":
//                textView.setText(R.string.flickr);
//                break;
//            case "ebay":
//                textView.setText(R.string.ebay);
//                break;
//            case "skype":
//                textView.setText(R.string.skype);
//                break;
//            case "you_tube":
//                textView.setText(R.string.you_tube);
//                break;
//        }
        return textView;
    }

    private void init() {
    }

    @Override
    public void onTabChanged(String tabId) {
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color_background_primary));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF"));
        //createTabContent(tabId);
    }

}

//<editor-fold desc="standard way to create a tab">
// text has to be empty for icon to show up
// TabHost.TabSpec t1 = tabHost.newTabSpec("ebay");
// t1.setContent(this);
// t1.setIndicator("", getResources().getDrawable(R.drawable.ic_camera));
// tabHost.addTab(t1);
//</editor-fold>

//<editor-fold desc="Create a tab with a drawable">
// that allows to get both text and icon
// but creates issue with selected/unselected
// TabHost.TabSpec t2 = tabHost.newTabSpec("flickr");
// View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
// TextView title = (TextView) tabIndicator.findViewById(R.id.title);
// title.setText("flickr");
// ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
// icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_attach));
// icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
// t2.setIndicator(tabIndicator);
// t2.setContent(this);
// tabHost.addTab(t2);
//</editor-fold>

//<editor-fold desc="hide a tab">
// hide tab
// tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
//</editor-fold>

//<editor-fold desc="change tab background">
// change tab indicator background
// tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundColor(Color.BLUE);
//</editor-fold>

//<editor-fold desc="hide radio button">
// RadioButton myRadioButton = (RadioButton) findViewById(R.id.first);
// myRadioButton.setVisibility(View.GONE)
// </editor-fold>

//<editor-fold desc="usage of radio buttons to change tab">
//RadioButton rbFirst = (RadioButton) findViewById(R.id.first);
//RadioButton rbSecond = (RadioButton) findViewById(R.id.second);
//RadioButton rbThird = (RadioButton) findViewById(R.id.third);
//RadioButton rbFourth = (RadioButton) findViewById(R.id.fourth);

//rbFirst.setButtonDrawable(R.drawable.ic_edit);
//rbFirst.setScaleY(50);
//.tab_header_icon_ebay);
//rbSecond.setButtonDrawable(R.drawable.tab_header_icon_flickr);
//rbSecond.setButtonDrawable(R.drawable.ic_attach);
//rbThird.setButtonDrawable(R.drawable.tab_header_icon_skype);
//rbThird.setButtonDrawable(R.drawable.ic_camera);
//rbFourth.setButtonDrawable(R.drawable.tab_header_icon_you_tube);
//rbFourth.setButtonDrawable(R.drawable.ic_camera);

//RadioGroup rg = (RadioGroup) findViewById(R.id.states);
//rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//@Override
//public void onCheckedChanged(RadioGroup group, final int checkedId) {
//switch (checkedId) {
//case R.id.first:
//getTabHost().setCurrentTab(0);
//break;
//case R.id.second:
//getTabHost().setCurrentTab(1);
//break;
//case R.id.third:
//getTabHost().setCurrentTab(2);
//break;
//case R.id.fourth:
//getTabHost().setCurrentTab(3);
//break;
//}
//}
//});
//</editor-fold>

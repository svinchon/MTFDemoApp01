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
        tabHost.addTab(ts_settings.setIndicator("", getResources().getDrawable(R.drawable.ic_settings)).setContent(this));

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
        tabHost.setCurrentTab(5);

        //tabHost.getTabWidget().getChildAt(5).setVisibility(View.GONE);
        //tabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
    }

    @Override
    public View createTabContent(String tag) {
        TextView textView = new TextView(this);
        return textView;
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



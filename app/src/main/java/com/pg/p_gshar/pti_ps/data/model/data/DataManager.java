package com.pg.p_gshar.pti_ps.data.model.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.p_gshar.pti_ps.data.model.data.model.AdsData;

public class DataManager {
    public static final String COLOR_PICKER = "colorPicker";
    public static final String USER_NAME = "userName";
    public static final String USER_DISPLAY_NAME = "userDisplayName";
    public static final String USER_AVATAR = "userAvatar";

    private static DataManager singleton;
    private final SharedPreferences sharedPrefs;
    private AdsData adsData;

    private DataManager(Activity context) {
        this.sharedPrefs = context.getPreferences(Context.MODE_PRIVATE);
    }

    public static DataManager getInstance(Activity context) {
        if (singleton == null) {
            singleton = new DataManager(context);
        }
        return singleton;
    }

    public static DataManager getInstance() {
        return singleton;
    }

    public void setAdsData(String result) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            adsData = mapper.readValue(result, AdsData.class);
        }catch (Exception e) {
            System.err.println(e);
        }
    }

    public AdsData getAdsData() {
        return adsData;
    }

    public SharedPreferences getSharedPrefs() {
        return this.sharedPrefs;
    }
}

package com.pg.p_gshar.pti_ps.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.p_gshar.pti_ps.data.model.AdsData;

public class DataManager {
    public static final int NETWORK_ERROR = -3;
    public static final int JSON_ERROR = -2;
    public static final int READY = -1;

    public static final String COLOR_PICKER = "colorPicker";
    public static final String USER_NAME = "userName";
    public static final String USER_DISPLAY_NAME = "userDisplayName";
    public static final String USER_AVATAR = "userAvatar";

    private static DataManager singleton;
    private final SharedPreferences sharedPrefs;
    private AdsData adsData;
    private int status;

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
        if (isNetworkError()) {
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            adsData = mapper.readValue(result, AdsData.class);
            status = READY;
            // validate data
            if (adsData.getAdScreens()==null || adsData.getAdScreens().size()==0) {
                status = JSON_ERROR;
            }
        }catch (Exception e) {
            System.err.println(e.getMessage());
            status = JSON_ERROR;
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isReady() {
        return status == READY;
    }

    public boolean isNetworkError() {
        return status == NETWORK_ERROR;
    }

    public boolean isJSONError() {
        return status == JSON_ERROR;
    }

    public AdsData getAdsData() {
        return adsData;
    }

    public SharedPreferences getSharedPrefs() {
        return this.sharedPrefs;
    }
}

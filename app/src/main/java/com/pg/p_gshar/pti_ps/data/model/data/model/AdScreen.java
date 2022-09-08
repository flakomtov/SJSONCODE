package com.pg.p_gshar.pti_ps.data.model.data.model;

import java.util.List;

public class AdScreen {
    private int screenIndex;
    private String description;
    private List<AdScreenData> adScreenData;

    public AdScreen() {
    }

    public AdScreen(int screenIndex, String description, List<AdScreenData> adScreenData) {
        this.screenIndex = screenIndex;
        this.description = description;
        this.adScreenData = adScreenData;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScreenIndex(int screenIndex) {
        this.screenIndex = screenIndex;
    }

    public void setAdScreenData(List<AdScreenData> adScreenData) {
        this.adScreenData = adScreenData;
    }

    public String getDescription() {
        return description;
    }

    public int getScreenIndex() {
        return screenIndex;
    }

    public List<AdScreenData> getAdScreenData() {
        return adScreenData;
    }

    @Override
    public String toString() {
        return "AdScreen{" +
                "screenIndex=" + screenIndex +
                ", description='" + description + '\'' +
                ", adScreenData=" + adScreenData +
                '}';
    }
}

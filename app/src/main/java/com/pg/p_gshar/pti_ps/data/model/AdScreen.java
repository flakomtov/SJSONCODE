package com.pg.p_gshar.pti_ps.data.model;

import java.util.ArrayList;
import java.util.List;

public class AdScreen {
    private int screenIndex;
    private String description;
    private List<AdScreenData> adScreenData = new ArrayList<>();
    private String background;
    private boolean video;
    private Carousel carousel;

    public AdScreen() {
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public void setCarousel(Carousel carousel) {
        this.carousel = carousel;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public boolean isVideo() {
        return video;
    }

    public Carousel getCarousel() {
        return carousel;
    }

    public String getBackground() {
        return background;
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

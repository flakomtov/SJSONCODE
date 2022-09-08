package com.pg.p_gshar.pti_ps.data.model;

public class AdScreenData {
    private String provider;
    private String type;
    private String id;

    public AdScreenData() {
    }

    public AdScreenData(String provider, String type, String id) {
        this.provider = provider;
        this.type = type;
        this.id = id;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AdScreenData{" +
                "provider='" + provider + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

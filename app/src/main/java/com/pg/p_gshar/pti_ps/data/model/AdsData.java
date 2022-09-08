package com.pg.p_gshar.pti_ps.data.model;

import java.util.ArrayList;
import java.util.List;

public class AdsData {
    private String defaultProvider;
    private String code;
    private String codeLink;
    private String accessLink;
    private List<AdScreen> adScreens = new ArrayList<>();
    private String admobAppOpenAdId;
    private String requestCodeText;
    private boolean verificationPage;
    private String disabledPageDescription;

    public AdsData() {
    }

    public AdsData(String defaultProvider, String code, String codeLink, String accessLink, List<AdScreen> adScreens, String admobAppOpenAdId, String requestCodeText, boolean verificationPage, String disabledPageDescription) {
        this.defaultProvider = defaultProvider;
        this.code = code;
        this.codeLink = codeLink;
        this.accessLink = accessLink;
        this.adScreens = adScreens;
        this.admobAppOpenAdId = admobAppOpenAdId;
        this.requestCodeText = requestCodeText;
        this.verificationPage = verificationPage;
        this.disabledPageDescription = disabledPageDescription;
    }

    public boolean isVerificationPage() {
        return verificationPage;
    }

    public String getDisabledPageDescription() {
        return disabledPageDescription;
    }

    public String getAdmobAppOpenAdId() {
        return admobAppOpenAdId;
    }

    public String getRequestCodeText() {
        return requestCodeText;
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public String getCode() {
        return code;
    }

    public String getCodeLink() {
        return codeLink;
    }

    public String getAccessLink() {
        return accessLink;
    }

    public List<AdScreen> getAdScreens() {
        return adScreens;
    }

    public void setVerificationPage(boolean verificationPage) {
        this.verificationPage = verificationPage;
    }

    public void setDisabledPageDescription(String disabledPageDescription) {
        this.disabledPageDescription = disabledPageDescription;
    }

    public void setAdmobAppOpenAdId(String admobAppOpenAdId) {
        this.admobAppOpenAdId = admobAppOpenAdId;
    }

    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public void setRequestCodeText(String requestCodeText) {
        this.requestCodeText = requestCodeText;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeLink(String codeLink) {
        this.codeLink = codeLink;
    }

    public void setAccessLink(String accessLink) {
        this.accessLink = accessLink;
    }

    public void setAdScreens(List<AdScreen> adScreens) {
        this.adScreens = adScreens;
    }

    @Override
    public String toString() {
        return "AdsData{" +
                "defaultProvider='" + defaultProvider + '\'' +
                ", code='" + code + '\'' +
                ", codeLink='" + codeLink + '\'' +
                ", accessLink='" + accessLink + '\'' +
                ", adScreens=" + adScreens +
                '}';
    }
}

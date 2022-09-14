package com.pg.p_gshar.pti_ps.data.model;

import java.util.List;

public class Carousel {
    private boolean advancedView;
    private List<String> items;

    public Carousel() {
    }

    public boolean isAdvancedView() {
        return advancedView;
    }

    public void setAdvancedView(boolean advancedView) {
        this.advancedView = advancedView;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}

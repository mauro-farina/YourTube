package it.units.sim.yourtube.model;

import java.io.Serializable;

import it.units.sim.yourtube.R;

public enum CategoryIcon implements Serializable {
    ICON_CATEGORY_DEFAULT(R.drawable.icon_category_default),
    ICON_CATEGORY_BASKETBALL(R.drawable.icon_category_basketball),
    ICON_CATEGORY_CAR(R.drawable.icon_category_car),
    ICON_CATEGORY_TRAVEL(R.drawable.icon_category_travel),
    ICON_CATEGORY_VIDEOGAME(R.drawable.icon_category_videogame),
    ICON_CATEGORY_COMPUTER(R.drawable.icon_category_computer),
    ICON_CATEGORY_PODCAST(R.drawable.icon_category_podcast);

    private final int resourceId;

    CategoryIcon(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}

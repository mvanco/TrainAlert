package cz.intesys.trainalert.entity;

import android.support.annotation.DrawableRes;

import cz.intesys.trainalert.utility.Utility;

public class Category {
    private static int sCount = 0;
    //TODO: add getter from shared preferences
    private @Utility.CategoryId int id;
    private String title;
    private @DrawableRes int iconRes;

    public Category(int id, String title, int iconRes) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

}

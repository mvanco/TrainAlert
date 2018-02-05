package cz.intesys.trainalert.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import cz.intesys.trainalert.repository.DataHelper;

public class Category {
    private static int sCount = 0;
    //TODO: Add getter from shared preferences.
    private @DataHelper.CategoryId int id;
    private @StringRes int titleRes;
    private @DrawableRes
    int iconRes;

    public Category(@DataHelper.CategoryId int id, @StringRes int titleRes, @DrawableRes int iconRes) {
        this.id = id;
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public int getId() {
        return id;
    }

    public @DataHelper.CategoryId
    int getTitleRes() {
        return titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

}

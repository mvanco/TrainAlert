package cz.intesys.trainalert.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class NavItem {
    private @DrawableRes int iconRes;
    private @StringRes int titleRes;

    /**
     * @param titleRes string resource id of item title shown in navigation bar, works also like id for click listener
     * @param iconRes
     */
    public NavItem(@StringRes int titleRes, @DrawableRes int iconRes) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getTitleRes() {
        return titleRes;
    }
}

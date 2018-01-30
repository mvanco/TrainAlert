package cz.intesys.trainalert.utility;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import cz.intesys.trainalert.repository.DataHelper;

public final class DataBindingAdapters {

    public DataBindingAdapters() {
    }


    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @BindingAdapter("category")
    public static void setImageCategory(ImageView imageView, int category) {
        imageView.setImageResource(DataHelper.getInstance().getCategories().get(category).getIconRes());
    }

//    @BindingAdapter("android:textRes")
//    public static void setTextResource(TextView textView, int resId) {
//        textView.setText(textView.getContext().getResources().getString(resId));
//    } POI_TYPE_CROSSING, POI_TYPE_SPEED_LIMITATION_50, POI_TYPE_SPEED_LIMITATION_70, POI_TYPE_TRAIN_STATION, POI_TYPE_TURNOUT, POI_TYPE_BRIDGE
}
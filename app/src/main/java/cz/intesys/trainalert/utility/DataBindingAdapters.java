package cz.intesys.trainalert.utility;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

public final class DataBindingAdapters {

    public DataBindingAdapters() {
    }


    @BindingAdapter ("android:src")
    public static void setImageResource(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }


//    @BindingAdapter("android:textRes")
//    public static void setTextResource(TextView textView, int resId) {
//        textView.setText(textView.getContext().getResources().getString(resId));
//    }
}
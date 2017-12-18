package cz.intesys.trainalert.utility;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

public final class DataBindingAdapters {

    public DataBindingAdapters() {
    }


    @BindingAdapter ("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}

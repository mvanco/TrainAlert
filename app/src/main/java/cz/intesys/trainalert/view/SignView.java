package cz.intesys.trainalert.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ViewSignBinding;
import cz.intesys.trainalert.repository.DataHelper;

import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_BLACK_SQUARE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_BLUE_CIRCLE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_BLUE_RING;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_BLUE_SQUARE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_GREY_SQUARE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_RED_CIRCLE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_RED_RING;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_RED_SQUARE;
import static cz.intesys.trainalert.repository.DataHelper.GRAPHICS_YELLOW_GREY_SQARE;

public class SignView extends FrameLayout {
    private int mGraphics;
    private String mText;

    private ViewSignBinding mBinding;

    public SignView(@NonNull Context context) {
        super(context);
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        parseAttrs(context, attrs, 0, 0);
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        parseAttrs(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setText(String text) {
        mText = text;

        mBinding.fragmentMainNotificationText.setText(mText);
        invalidate();
        requestLayout();
    }

    public void setGraphics(@DataHelper.GraphicsId int graphics) {
        mGraphics = graphics;

        mBinding.fragmentMainSign.setImageResource(getDrawable());
        mBinding.fragmentMainNotificationText.setTextColor(getTextColor()); // Changed graphics, possibly text color must be changed as well.
        invalidate();
        requestLayout();
    }

    private void init() {
        mBinding = ViewSignBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    /**
     * @param context      mandatory
     * @param attrs        mandatory
     * @param defStyleAttr optional, 0 if missing
     * @param defStyleRes  optional, 0 if missing
     */
    private void parseAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SignView, defStyleAttr, defStyleRes);

        try {
            mGraphics = a.getInteger(R.styleable.SignView_graphics, 0);
            mText = a.getString(R.styleable.SignView_text);

            mBinding.fragmentMainSign.setImageResource(getDrawable());
            mBinding.fragmentMainNotificationText.setText(mText);
            mBinding.fragmentMainNotificationText.setTextColor(getTextColor());
            invalidate();
            requestLayout();
        } finally {
            a.recycle();
        }
    }

    private @ColorInt int getTextColor() {
        switch (mGraphics) {
            case GRAPHICS_BLACK_SQUARE:
                return ContextCompat.getColor(getContext(), android.R.color.white);
            default:
                return ContextCompat.getColor(getContext(), android.R.color.black);
        }
    }

    private @DrawableRes int getDrawable() {
        switch (mGraphics) {
            case GRAPHICS_BLACK_SQUARE:
                return R.drawable.alarm_black_square;
            case GRAPHICS_BLUE_CIRCLE:
                return R.drawable.alarm_blue_circle;
            case GRAPHICS_BLUE_RING:
                return R.drawable.alarm_blue_ring;
            case GRAPHICS_BLUE_SQUARE:
                return R.drawable.alarm_blue_square;
            case GRAPHICS_GREY_SQUARE:
                return R.drawable.alarm_grey_square;
            case GRAPHICS_RED_CIRCLE:
                return R.drawable.alarm_red_circle;
            case GRAPHICS_RED_RING:
                return R.drawable.alarm_red_ring;
            case GRAPHICS_RED_SQUARE:
                return R.drawable.alarm_red_square;
            case GRAPHICS_YELLOW_GREY_SQARE:
                return R.drawable.alarm_yellow_grey_square;
            default:
                return R.drawable.alarm_black_square;
        }
    }
}
package cz.intesys.trainalert.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.databinding.ViewSignBinding;
import cz.intesys.trainalert.entity.Alarm;
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
    private boolean mAnimationEnabled = true;
    private boolean mAnimating = false;
    private ViewPropertyAnimator mAnimator;

    private ViewSignBinding mBinding;

    public SignView(@NonNull Context context) {
        super(context);
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public SignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
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

    public void showAlarm(Alarm alarm) {
        setVisibility(View.VISIBLE);
        setText(alarm.getMessage());
        setGraphics(alarm.getGraphics());
        if (mAnimationEnabled) {
            mAnimating = true;
            setAlpha(0f);
            setScaleX(0.5f);
            setScaleY(0.5f);
            mAnimator = animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(getResources().getInteger(R.integer.show_animation_duration))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimating = false;
                        }
                    });

            new Handler().postDelayed(() -> {
                mAnimating = true;
                mAnimator = animate()
                        .alpha(0f)
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(getResources().getInteger(R.integer.hide_animation_duration))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                setVisibility(View.GONE);
                                mAnimating = false;
                            }
                        });
            }, TaConfig.SIGN_VIEW_SHOW_DURATION);
        }
        else {
            setAlpha(1f);
            setScaleX(1f);
            setScaleY(1);

            new Handler().postDelayed(() -> {
                setVisibility(View.GONE);
            }, TaConfig.SIGN_VIEW_SHOW_DURATION);
        }
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        if (mAnimationEnabled == true && animationEnabled == false && mAnimating) {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            setVisibility(View.GONE);
            invalidate();
            requestLayout();
        }
        mAnimationEnabled = animationEnabled;
    }

    /**
     * @param attrs        mandatory
     * @param defStyleAttr optional, 0 if missing
     * @param defStyleRes  optional, 0 if missing
     */
    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        initLayout();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SignView, defStyleAttr, defStyleRes);

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

    private void initLayout() {
        mBinding = ViewSignBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    private @ColorInt int getTextColor() {
        switch (mGraphics) {
            case GRAPHICS_BLACK_SQUARE:
            case GRAPHICS_BLUE_RING:
            case GRAPHICS_GREY_SQUARE:
            case GRAPHICS_RED_RING:
            case GRAPHICS_YELLOW_GREY_SQARE:
                return ContextCompat.getColor(getContext(), android.R.color.black);
            case GRAPHICS_BLUE_CIRCLE:
            case GRAPHICS_BLUE_SQUARE:
            case GRAPHICS_RED_CIRCLE:
            case GRAPHICS_RED_SQUARE:
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
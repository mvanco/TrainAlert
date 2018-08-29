package cz.intesys.trainalert.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.databinding.ViewSpeedBinding;
import cz.intesys.trainalert.repository.DataHelper;

public class SpeedView extends FrameLayout {
    private boolean mAnimationEnabled = true;
    private int mSpeed;
    private ViewSpeedBinding mBinding;
    private AnimatorSet mAnimSet;
    private boolean mExceededSpeed;

    public SpeedView(@NonNull Context context) {
        super(context);
    }

    public SpeedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public SpeedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SpeedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        if (mAnimationEnabled == true && animationEnabled == false && mAnimSet != null) {
            mAnimSet.cancel();
            Log.d("cancelinganim", "true");
        }
        mAnimationEnabled = animationEnabled;
    }

    public void setSpeed(int speed) {
        setSpeedAndLimit(speed, DataHelper.SPEED_LIMIT_NO_LIMIT);
    }

    public void setSpeedAndLimit(int speed, int speedLimit) {
        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(TaConfig.SPEED_VIEW_ENABLED_KEY, TaConfig.SPEED_VIEW_ENABLED_DEFAULT)) {
            return;
        }

        AnimationType animType;

        if (mSpeed == 0 && speed == 0) {
            animType = AnimationType.HIDDEN;
        } else if (mSpeed == 0 && speed != 0) {
            animType = AnimationType.SHOW;
        } else if (mSpeed != 0 && speed != 0) {
            animType = AnimationType.VISIBLE;
        } else {  // mSpeed != 0 && speed == 0
            animType = AnimationType.HIDE;
        }

        boolean exceededSpeed = speedLimit > 0 && speed > speedLimit;

        if (animType == AnimationType.SHOW || animType == AnimationType.VISIBLE) {
            mBinding.speedText.setText(String.valueOf(speed));

            if (exceededSpeed) {
                mBinding.speedSign.setBackgroundResource(R.drawable.speed_background_red);
                mBinding.speedText.setTextColor(getResources().getColor(android.R.color.white));
                mBinding.speedUnitText.setTextColor(getResources().getColor(android.R.color.white));
            }
            else {
                mBinding.speedSign.setBackgroundResource(R.drawable.speed_background);
                mBinding.speedText.setTextColor(getResources().getColor(R.color.text_grey));
                mBinding.speedUnitText.setTextColor(getResources().getColor(R.color.text_grey));
            }

            if (animType == AnimationType.VISIBLE && mExceededSpeed == false && exceededSpeed == true) {
                this.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(getResources().getInteger(R.integer.show_animation_duration) / 2)
                        .setListener(null);

                new Handler().postDelayed(() -> {
                    this.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(getResources().getInteger(R.integer.show_animation_duration) / 2)
                            .setListener(null);
                }, getResources().getInteger(R.integer.show_animation_duration) / 2);
            }
        }

        if (animType == AnimationType.SHOW) {
            if (mAnimationEnabled) {
                mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.speed_limit_view_animator);
                mAnimSet.setTarget(this);
                mAnimSet.start();
            }
            this.setVisibility(View.VISIBLE);
        } else if (animType == AnimationType.HIDE) {
            if (mAnimationEnabled) {
                mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.speed_limit_view_animator_reversed);
                mAnimSet.setTarget(this);
                mAnimSet.start();
                new Handler().postDelayed(() -> {
                    this.setVisibility(View.INVISIBLE);
                }, getResources().getInteger(R.integer.hide_animation_duration));
            } else {
                this.setVisibility(View.INVISIBLE);
            }
        }

        invalidate();
        requestLayout();
        mSpeed = speed;
        mExceededSpeed = exceededSpeed;
    }

    /**
     * @param attrs        mandatory
     * @param defStyleAttr optional, 0 if missing
     * @param defStyleRes  optional, 0 if missing
     */
    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        initLayout();
    }

    private void initLayout() {
        mBinding = ViewSpeedBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    enum AnimationType {HIDDEN, SHOW, VISIBLE, HIDE}
}
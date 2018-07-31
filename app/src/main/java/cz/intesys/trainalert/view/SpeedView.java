package cz.intesys.trainalert.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ViewSpeedBinding;

public class SpeedView extends FrameLayout {
    private boolean mAnimationEnabled = true;
    private int mSpeed;
    private ViewSpeedBinding mBinding;
    private AnimatorSet mAnimSet;

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
        mBinding.speedText.setText(String.valueOf(speed));

        if (speed != 0) {
            mBinding.getRoot().setVisibility(View.VISIBLE);
            this.setVisibility(View.VISIBLE);
        } else {  // Hide icon with or without animation.
            if (this.getVisibility() == View.VISIBLE) {
                if (mAnimationEnabled) {
                    mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.speed_limit_view_animator_reversed);
                    mAnimSet.setTarget(this);
                    mAnimSet.start();
                    new Handler().postDelayed(() -> {
                        mBinding.getRoot().setVisibility(View.INVISIBLE);
                        this.setVisibility(View.INVISIBLE);
                    }, mAnimSet.getDuration());
                } else {
                    mBinding.getRoot().setVisibility(View.INVISIBLE);
                    this.setVisibility(View.INVISIBLE);
                }
            }
        }

        invalidate();
        requestLayout();

        if (mAnimationEnabled && mSpeed != 0) {  // Animation is shown only when previous speed was zero and icon has been hidden.
            mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.speed_limit_view_animator);
            mAnimSet.setTarget(this);
            mAnimSet.start();
        }

        mSpeed = speed;
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
}
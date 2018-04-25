package cz.intesys.trainalert.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ViewSpeedLimitBinding;
import cz.intesys.trainalert.repository.DataHelper;

import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_20;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_30;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_40;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_50;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_SPEED_LIMITATION_70;

public class SpeedLimitView extends FrameLayout {
    private boolean mAnimationEnabled = true;
    private String mText;
    private @DataHelper.CategoryId int mCategoryId;
    private ViewSpeedLimitBinding mBinding;
    private AnimatorSet mAnimSet;

    public SpeedLimitView(@NonNull Context context) {
        super(context);
    }

    public SpeedLimitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public SpeedLimitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SpeedLimitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    public void setText(String text) {
        mText = text;

        mBinding.speedLimitText.setText(mText);
        invalidate();
        requestLayout();
    }

    public void setCategory(@DataHelper.CategoryId int categoryId) {
        switch (categoryId) {
            case POI_TYPE_SPEED_LIMITATION_20:
                mText = "20";
                break;
            case POI_TYPE_SPEED_LIMITATION_30:
                mText = "30";
                break;
            case POI_TYPE_SPEED_LIMITATION_40:
                mText = "40";
                break;
            case POI_TYPE_SPEED_LIMITATION_50:
                mText = "50";
                break;
            case POI_TYPE_SPEED_LIMITATION_70:
                mText = "70";
                break;
            default:
                mText = "-";
                break;
        }

        mBinding.speedLimitText.setText(mText);

        if (DataHelper.getInstance().isSpeedLimitCategory(categoryId)) {
            mBinding.getRoot().setVisibility(View.VISIBLE);
            this.setVisibility(View.VISIBLE);
        } else {
            mBinding.getRoot().setVisibility(View.INVISIBLE);
            this.setVisibility(View.INVISIBLE);
        }

        invalidate();
        requestLayout();

        boolean shouldShowAnimation = getCategoryId() != categoryId;
        mCategoryId = categoryId;

        if (mAnimationEnabled && shouldShowAnimation) {
            mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.speed_limit_view_animator);
            mAnimSet.setTarget(this);
            mAnimSet.start();
        }
    }

    public int getCategoryId() {
        return mCategoryId;
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
        mBinding = ViewSpeedLimitBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }
}
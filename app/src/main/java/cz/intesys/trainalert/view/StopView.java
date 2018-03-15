package cz.intesys.trainalert.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.util.Date;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.databinding.ViewStopBinding;
import cz.intesys.trainalert.entity.Stop;

public class StopView extends FrameLayout {

    public static final int LIST_POSITION_START = 1;
    public static final int LIST_POSITION_END = 2;
    public static final int LIST_POSITION_MIDDLE = 3;

    public static final int TYPE_PREVIOUS_STOP = 0;
    public static final int TYPE_CLOSEST_NEXT_STOP = 1;
    public static final int TYPE_NEXT_STOP = 2;
    public static final int TYPE_LATEST_NEXT_STOP = 3;
    public static final int TYPE_FINAL_STOP = 4;
    public static final int TYPE_TRAIN_MARKER = 5;
    private ViewStopBinding mBinding;
    private int mListPosition;
    private String mLabel;
    private String mName;
    private Date mArrival;
    private int mDelay;
    private int mColor;
    private @StopType
    int mType;
    private boolean mFinalStage;
    private boolean mButtonPressed;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_PREVIOUS_STOP, TYPE_CLOSEST_NEXT_STOP, TYPE_NEXT_STOP, TYPE_LATEST_NEXT_STOP, TYPE_FINAL_STOP, TYPE_TRAIN_MARKER})
    public @interface StopType {
    }

    public StopView(Context context) {
        super(context);
        init(null, 0);
    }

    public StopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setFinalStage(boolean mFinalStage) {
        this.mFinalStage = mFinalStage;
    }

    public int getListPosition() {
        return mListPosition;
    }

    public void setListPosition(int listPosition) {
        mListPosition = listPosition;
        invalidateStopView();
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
        invalidateStopView();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        invalidateStopView();
    }

    public Date getArrival() {
        return mArrival;
    }

    public void setArrival(String arrival) {
        if (arrival == null) {
            return;
        }

        try {
            mArrival = TaConfig.BASIC_DATE_FORMAT.parse(arrival);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setArrival(Date arrival) {
        mArrival = arrival;
    }

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int delay) {
        mDelay = delay;
        invalidateStopView();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        invalidateStopView();
    }

    public int getType() {
        return mType;
    }

    public void setType(@StopType int type) {
        mType = type;
        invalidateStopView();
    }

    public void setStop(Stop stop) {
        mName = stop.getName();
        mArrival = stop.getArrival();
        mDelay = stop.getDelay();
    }

    public void setAsTrainMarker(boolean value) {
        if (value) {
            mBinding.stopViewCurrentLocation.setVisibility(View.VISIBLE);
            mBinding.stopViewCard.setVisibility(View.INVISIBLE);
            mBinding.stopViewName.setVisibility(View.INVISIBLE);
            mBinding.stopViewArrival.setVisibility(View.INVISIBLE);
            mBinding.stopViewDelay.setVisibility(View.INVISIBLE);
            mBinding.stopViewHand.setVisibility(INVISIBLE);
            mBinding.stopViewTimePoint.setImageResource(R.drawable.time_point_now);

            mButtonPressed = false;

            ViewGroup.LayoutParams lp = mBinding.stopViewRoot.getLayoutParams();
            lp.height = getContext().getResources().getDimensionPixelSize(R.dimen.stopView_height_train);
            mBinding.stopViewRoot.setLayoutParams(lp);
        } else {
            mBinding.stopViewCurrentLocation.setVisibility(View.INVISIBLE);
            mBinding.stopViewCard.setVisibility(View.VISIBLE);
            mBinding.stopViewName.setVisibility(View.VISIBLE);
            mBinding.stopViewArrival.setVisibility(View.VISIBLE);
            mBinding.stopViewDelay.setVisibility(View.VISIBLE);
            mBinding.stopViewTimePoint.setImageResource(R.drawable.time_point);

            ViewGroup.LayoutParams lp = mBinding.stopViewRoot.getLayoutParams();
            lp.height = getContext().getResources().getDimensionPixelSize(R.dimen.stopView_height);
            mBinding.stopViewRoot.setLayoutParams(lp);
        }
    }

    public void setButtonPressed(boolean buttonPressed) {
        mButtonPressed = buttonPressed;
        invalidateStopView();
    }

    private void init(AttributeSet attrs, int defStyle) {
        initLayout();

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StopView, defStyle, 0);
        mListPosition = a.getInt(R.styleable.StopView_listPosition, 3);
        mLabel = a.getString(R.styleable.StopView_label);
        mName = a.getString(R.styleable.StopView_name);
        setArrival(a.getString(R.styleable.StopView_arrival));
        mDelay = a.getInt(R.styleable.StopView_delay, 0);
        mColor = a.getColor(R.styleable.StopView_color, Color.WHITE);

        invalidateStopView();
    }

    private void initLayout() {
        mBinding = ViewStopBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    private void invalidateStopView() {
        if ((mListPosition & 0x1) != 0) { // start flag
            mBinding.stopViewBottomLine.setVisibility(View.VISIBLE);
        } else {
            mBinding.stopViewBottomLine.setVisibility(View.INVISIBLE);
        }
        if ((mListPosition & 0x2) != 0) { // end flag
            mBinding.stopViewTopLine.setVisibility(View.VISIBLE);
        } else {
            mBinding.stopViewTopLine.setVisibility(View.INVISIBLE);
        }

        if (mFinalStage) {
            mBinding.stopViewBottomLine.setBackgroundResource(R.color.trip_grey);
            mBinding.stopViewTopLine.setBackgroundResource(R.color.trip_grey);
        } else {
            if (mType == TYPE_LATEST_NEXT_STOP) {
                mBinding.stopViewBottomLine.setBackgroundResource(R.drawable.dashed_line_bottom);
            } else {
                mBinding.stopViewBottomLine.setBackgroundResource(R.color.trip_grey);
            }

            if (mType == TYPE_FINAL_STOP) {
                mBinding.stopViewTopLine.setBackgroundResource(R.drawable.dashed_line_top);
            } else {
                mBinding.stopViewTopLine.setBackgroundResource(R.color.trip_grey);
            }
        }

        mBinding.stopViewName.setText(mName);

        if (mArrival != null) {
            mBinding.stopViewArrival.setText(TaConfig.ARRIVAL_DATE_FORMAT.format(mArrival));
        }

        int hours = mDelay / 3600;
        int minutes = (mDelay - hours * 3600) / 60;
        int onlyMinutes = mDelay / 60;
        mBinding.stopViewDelay.setText(String.format("%d", Math.abs(onlyMinutes)));
        if (onlyMinutes < 0) {
            mBinding.stopViewDelay.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        } else if (onlyMinutes == 0) {
            mBinding.stopViewDelay.setTextColor(ContextCompat.getColor(getContext(), R.color.trip_grey));
        } else {
            mBinding.stopViewDelay.setTextColor(ContextCompat.getColor(getContext(), R.color.trip_delay_red));
        }
        mBinding.stopViewCard.setBackgroundColor(mColor);

        switch (mType) {
            case TYPE_PREVIOUS_STOP:
                mBinding.stopViewCard.setBackgroundResource(R.drawable.stop_background_green);
                break;
            case TYPE_CLOSEST_NEXT_STOP:
                mBinding.stopViewCard.setBackgroundResource(R.drawable.stop_background_orange);
                break;
            case TYPE_NEXT_STOP:
            case TYPE_LATEST_NEXT_STOP:
                mBinding.stopViewCard.setBackgroundResource(R.drawable.stop_background_red);
                break;
            case TYPE_FINAL_STOP:
                mBinding.stopViewCard.setBackgroundResource(R.drawable.stop_background_blue);
                break;
        }

        ViewGroup.LayoutParams lp = mBinding.stopViewHand.getLayoutParams();
        if (mType == TYPE_CLOSEST_NEXT_STOP) {
            lp.width = getResources().getDimensionPixelOffset(R.dimen.stopView_bigHand_width);
            lp.height = getResources().getDimensionPixelSize(R.dimen.stopView_bigHand_height);
            mBinding.stopViewHand.setLayoutParams(lp);
        } else {
            lp.width = getResources().getDimensionPixelOffset(R.dimen.stopView_hand_width);
            lp.height = getResources().getDimensionPixelSize(R.dimen.stopView_hand_height);
            mBinding.stopViewHand.setLayoutParams(lp);
        }

        if (mButtonPressed) {
            mBinding.stopViewHand.setVisibility(View.VISIBLE);
        } else {
            mBinding.stopViewHand.setVisibility(View.GONE);
        }


        invalidate();
        requestLayout();
    }
}

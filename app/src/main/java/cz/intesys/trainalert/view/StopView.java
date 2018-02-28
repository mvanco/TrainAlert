package cz.intesys.trainalert.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ViewStopBinding;

import static cz.intesys.trainalert.TaConfig.ARRIVAL_DATE_FORMAT;
import static cz.intesys.trainalert.TaConfig.DATE_FORMAT;

public class StopView extends FrameLayout {

    private ViewStopBinding mBinding;

    private int mListPosition;
    private String mLabel;
    private String mName;
    private Date mArrival;
    private int mDelay;
    private int mColor;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private SimpleDateFormat mArrivalDateFormat = new SimpleDateFormat(ARRIVAL_DATE_FORMAT);

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

    public void setArrival(Date arrival) {
        mArrival = arrival;
    }

    public void setArrival(String arrival) {
        try {
            mArrival = mSimpleDateFormat.parse(arrival);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            mBinding.imageView.setVisibility(View.VISIBLE);
        }
        if ((mListPosition & 0x2) != 0) { // end flag
            mBinding.imageView2.setVisibility(View.VISIBLE);
        }

        mBinding.stopViewName.setText(mName);
        mBinding.stopViewArrival.setText(mArrivalDateFormat.format(mArrival));

        int hours = mDelay / 3600;
        int minutes = (mDelay - hours * 3600) / 60;
        mBinding.stopViewDelay.setText(String.format("%d h %d m", hours, minutes));
        mBinding.stopViewCard.setBackgroundColor(mColor);

        invalidate();
        requestLayout();
    }

}

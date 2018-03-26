package cz.intesys.trainalert.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import cz.intesys.trainalert.R;

public class FinishAnimationView extends View {
    final float DP_PORTION = 0.01f;

    int framesPerSecond = 60;
    long animationDuration = 1000; // 10 seconds

    float mWidth;
    float mHeight;
    float mMyDp;
    float mInnerSquareDimension;

    Paint paint = new Paint();    // your paint

    long startTime;

    public FinishAnimationView(Context context) {
        super(context);
        init(null, 0);
    }

    public FinishAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FinishAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long elapsedTime = System.currentTimeMillis() - startTime;
        float animationFraction = (float) elapsedTime / animationDuration;
        Log.d("animationFraction", "fraction:" + String.valueOf(animationFraction));

        mWidth = (float) getWidth();
        mHeight = (float) getHeight();
        if (mWidth > mHeight) {
            mMyDp = mHeight * DP_PORTION;
            mInnerSquareDimension = mHeight;
        } else {
            mMyDp = mWidth * DP_PORTION;
            mInnerSquareDimension = mWidth;
        }

        float strokeWidth = 5 * mMyDp;
        float padding = strokeWidth / 2;

        float ovalLeftMargin = padding;
        float ovalWidth = 60 * mMyDp; //mInnerSquareDimension / 1.5f  - (2 * padding);

        float ovalTopMargin = padding;
        float ovalHeight = 2.5f * ovalWidth;

        float moveRight = 8 * mMyDp; //mInnerSquareDimension / 2.5f - (ovalWidth / 2);
        float moveBottom = -(62 * mMyDp); //-(mInnerSquareDimension / 3 - (ovalWidth / 2));

        canvas.rotate(30f, mInnerSquareDimension / 2, mInnerSquareDimension / 2);

        paint.setColor(getResources().getColor(R.color.green));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        RectF oval = new RectF(ovalLeftMargin + moveRight, ovalTopMargin + moveBottom, ovalLeftMargin + ovalWidth + moveRight, ovalTopMargin + ovalHeight + moveBottom);

        AccelerateInterpolator interpolator = new AccelerateInterpolator(4f);

        float mLength = 112f * interpolator.getInterpolation(animationFraction);
        float mStartAngle = 15f + 112f - mLength;
        float mSweepAngle = mLength;


        Log.d("drawArc", "startAngle:" + mStartAngle + "sweepAngle:" + mSweepAngle);
        if (elapsedTime < animationDuration) {
            canvas.drawArc(oval, mStartAngle, mSweepAngle, false, paint);
        } else {
            canvas.drawArc(oval, 15f, 112f, false, paint);
        }

        if (elapsedTime < animationDuration) {
            postInvalidateDelayed(1000 / framesPerSecond);
        }
    }

    public void startAnimation() {
        startTime = System.currentTimeMillis();
        postInvalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.FinishAnimationView, defStyle, 0);
//
//        mExampleString = a.getString(
//                R.styleable.FinishAnimationView_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.FinishAnimationView_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.FinishAnimationView_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.FinishAnimationView_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.FinishAnimationView_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }
//
//        a.recycle();
//
//        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);
//
//        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
    }
}

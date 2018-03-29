package cz.intesys.trainalert.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;

public class FinishAnimationView extends View {
    final float DP_PORTION = 0.01f;

    public static final long ANIMATION_DURATION = 2000; // 10 seconds
    int framesPerSecond = 360;

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
        float animationFraction = (float) elapsedTime / ANIMATION_DURATION;
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

//        float ovalLeftMargin = padding;
//        float ovalWidth = 60 * mMyDp; //mInnerSquareDimension / 1.5f  - (2 * padding);
//
//        float ovalTopMargin = padding;
//        float ovalHeight = 2.5f * ovalWidth;
//
//        float moveRight = 8 * mMyDp; //mInnerSquareDimension / 2.5f - (ovalWidth / 2);
//        float moveBottom = -(62 * mMyDp); //-(mInnerSquareDimension / 3 - (ovalWidth / 2));

//        canvas.rotate(30f, mInnerSquareDimension / 2, mInnerSquareDimension / 2);

        paint.setColor(getResources().getColor(R.color.green));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

//        RectF oval = new RectF(ovalLeftMargin + moveRight, ovalTopMargin + moveBottom, ovalLeftMargin + ovalWidth + moveRight, ovalTopMargin + ovalHeight + moveBottom);


//        float mLength = 112f * interpolator.getInterpolation(animationFraction);
//        float mStartAngle = 15f + 112f - mLength;
//        float mSweepAngle = mLength;
//
//
//        Log.d("drawArc", "startAngle:" + mStartAngle + "sweepAngle:" + mSweepAngle);
//        if (elapsedTime < ANIMATION_DURATION) {
//            canvas.drawArc(oval, mStartAngle, mSweepAngle, false, paint);
//        } else {
//            canvas.drawArc(oval, 15f, 112f, false, paint);
//        }

        float animationFractionStage1;
        if (animationFraction < 0f) {
            animationFractionStage1 = 0f;
        } else if (animationFraction < 0.35f) {
            animationFractionStage1 = animationFraction / 0.35f;
        } else {
            animationFractionStage1 = 1f;
        }

        float animationFractionStage2;
        if (animationFraction < 0.35f) {
            animationFractionStage2 = 0f;
        } else if (animationFraction < 1f) {
            animationFractionStage2 = (animationFraction - 0.35f) / 0.65f;
        } else {
            animationFractionStage2 = 1f;
        }

        Path path = new Path();
        path.moveTo(10f * mMyDp, 50f * mMyDp);

        if (animationFractionStage1 > 0f) {
            float x = 10f + animationFractionStage1 * 20f;
            float y = 50f + animationFractionStage1 * 30f;
            path.lineTo(x * mMyDp, y * mMyDp);
            Log.d("draww", "stage1:" + animationFractionStage1);
        }

        if (animationFractionStage2 > 0f) {
            float x = 30f + animationFractionStage2 * 60f;
            float y = 80f - animationFractionStage2 * 60f;
            path.lineTo(x * mMyDp, y * mMyDp);
            Log.d("draww", "stage2:" + animationFractionStage2);
        }


        if (elapsedTime < ANIMATION_DURATION) {
            canvas.drawPath(path, paint);
        } else {
            Path finalPath = new Path();
            finalPath.moveTo(10f * mMyDp, 50f * mMyDp);
            finalPath.lineTo(30f * mMyDp, 80f * mMyDp);
            finalPath.lineTo(90f * mMyDp, 20f * mMyDp);
            canvas.drawPath(finalPath, paint);
        }

        if (elapsedTime < ANIMATION_DURATION) {
            postInvalidateDelayed(1000 / framesPerSecond);
        }
    }

    public void startAnimation() {
        long newStartTime = System.currentTimeMillis();
        if ((newStartTime - startTime) < (ANIMATION_DURATION + TaConfig.TRIP_FRAGMENT_TIME_PADDING)) {
            return;
        }
        startTime = newStartTime;
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

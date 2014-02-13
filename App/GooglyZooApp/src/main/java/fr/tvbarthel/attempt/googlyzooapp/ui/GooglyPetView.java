package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyPetListener;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyEye;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;

/**
 * Created by tbarthel on 06/02/14.
 */
public class GooglyPetView extends ImageView {

    /**
     * Log cat
     */
    private static final String TAG = GooglyPetView.class.getName();

    /**
     * radius used for eye navigation
     */
    private static final float EYE_RADIUS = 10f;

    /**
     * Paint used to draw eyes
     */
    private Paint mPaint;

    /**
     * View model
     */
    private GooglyPet mGooglyPetModel;

    /**
     * Googly pet event listener to update view on pet events
     */
    private GooglyPetListener mListener;


    public GooglyPetView(Context context, GooglyPet model) {
        super(context);

        //init paint
        mPaint = new Paint();
        mPaint.setStrokeWidth(15f);
        mPaint.setColor(Color.BLACK);

        //init model
        mGooglyPetModel = model;

        //init pet event listener
        mListener = new GooglyPetListener() {

            @Override
            public void onAwake() {
                moveUp();
            }

            @Override
            public void onFallAsleep() {
                moveDown();
            }
        };

        //add listener
        mGooglyPetModel.addListener(mListener);

        this.setImageDrawable(getResources().getDrawable(mGooglyPetModel.getPetRes()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float middleW = canvas.getWidth();
        float middleH = canvas.getHeight();

        final GooglyEye leftEye = mGooglyPetModel.getLeftEye();
        final GooglyEye rightEye = mGooglyPetModel.getRightEye();

        if (leftEye != null && leftEye.isOpened()) {
            //draw left eye
            canvas.drawCircle(leftEye.getCenterX() * middleW + leftEye.getOrientationX(),
                    leftEye.getCenterY() * middleH + leftEye.getOrientationY(),
                    EYE_RADIUS, mPaint);
        }

        if (rightEye != null && rightEye.isOpened()) {
            //draw right eye
            canvas.drawCircle(rightEye.getCenterX() * middleW + rightEye.getOrientationX(),
                    rightEye.getCenterY() * middleH + rightEye.getOrientationY(),
                    EYE_RADIUS, mPaint);
        }

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        moveDown();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mGooglyPetModel.removeListener(mListener);
    }

    /**
     * animate the view to move down from 70% of its height
     */
    private void moveDown() {
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * 0.70f);
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }


    /**
     * animate the view to move up to 25% of its height
     */
    private void moveUp() {
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * 0.25f);
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }
}
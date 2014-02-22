package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
     * duration of eye transition between two known position
     */
    private static final int EYE_ANIMATION_DURATION_IN_MILLI = 300;

    /**
     * radius used for eye navigation
     */
    private static final float EYE_RADIUS = 15f;

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

    /**
     * Animator for pet eyes
     */
    private ObjectAnimator mEyesAnimator;


    public GooglyPetView(Context context, GooglyPet model) {
        super(context);

        //init paint
        mPaint = new Paint();
        mPaint.setStrokeWidth(15f);
        mPaint.setColor(Color.BLACK);

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

        mGooglyPetModel = model;


        //init animator with custom evaluator
        mEyesAnimator = ObjectAnimator.ofObject(mGooglyPetModel, "EyesOrientation",
                new FloatArrayEvaluator(2), 0);

        //add listener to update the view during the animation
        mEyesAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                GooglyPetView.this.invalidate();
            }
        });

        //set animation duration
        mEyesAnimator.setDuration(EYE_ANIMATION_DURATION_IN_MILLI);

        //init model
        initModel();


    }

    public void setModel(GooglyPet model) {
        if (mGooglyPetModel != null) {
            mGooglyPetModel.removeListener(mListener);
            mGooglyPetModel = null;
        }
        mGooglyPetModel = model;
        initModel();
    }

    private void initModel() {
        //add listener
        mGooglyPetModel.addListener(mListener);
        this.setImageResource(mGooglyPetModel.getPetRes());
        mEyesAnimator.setTarget(mGooglyPetModel);
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
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * mGooglyPetModel.getBodyProportion());
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }


    /**
     * animate the view to move up to 25% of its height
     */
    private void moveUp() {
        ObjectAnimator trans = ObjectAnimator.ofFloat(GooglyPetView.this, "translationY", GooglyPetView.this.getHeight() * mGooglyPetModel.getHeadProportion());
        trans.setInterpolator(new DecelerateInterpolator());
        trans.setDuration(300);
        trans.start();
    }


    /**
     * Animate eyes from last position to new position
     *
     * @param newDirection  new target position
     * @param lastDirection last target position
     */
    public void animatePetEyes(float[] newDirection, float[] lastDirection) {
        if (newDirection.length != 2 || lastDirection.length != 2) {
            return;
        }
        final GooglyEye leftEye = mGooglyPetModel.getLeftEye();
        final GooglyEye rightEye = mGooglyPetModel.getRightEye();

        if (mEyesAnimator.isRunning()) {
            mEyesAnimator.end();
        }

        if (leftEye.isOpened()) {
            leftEye.blink();
        } else {
            leftEye.open();
        }

        if (rightEye.isOpened()) {
            rightEye.blink();
        } else {
            rightEye.open();
        }

        //clone array as gradual values are going to be evaluated
        mEyesAnimator.setObjectValues(lastDirection.clone(), newDirection.clone());
        mEyesAnimator.start();
    }
}
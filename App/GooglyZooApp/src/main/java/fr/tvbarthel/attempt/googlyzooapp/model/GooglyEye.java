package fr.tvbarthel.attempt.googlyzooapp.model;

import android.os.CountDownTimer;

import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyEyeListener;

/**
 * Created by tbarthel on 06/02/14.
 */
public class GooglyEye {

    /**
     * duration after which eye will be closed
     */
    private static final int EYE_STAY_OPENED_DURATION_IN_MILLI = 500;

    /**
     * fixed x center
     */
    private float mCenterX;

    /**
     * fixed y center
     */
    private float mCenterY;

    /**
     * diameter of eye used for retina motion
     */
    private float mDiameter;

    /**
     * x orientation use to move retina around the center
     */
    private float mOrientationX;

    /**
     * y orientation used to move retina around the center
     */
    private float mOrientationY;

    /**
     * is eye opened ?
     */
    private boolean mIsOpened;

    /**
     * count down timer used to close eye
     */
    private CountDownTimer mCloseCountDownTimer;

    /**
     * eye event listener
     */
    private GooglyEyeListener mListener;


    /**
     * round eye model
     *
     * @param centerX  fixed eye center X
     * @param centerY  fixed eye center Y
     * @param diameter diameter of the eye
     */
    public GooglyEye(float centerX, float centerY, float diameter) {
        mCenterX = centerX;
        mCenterY = centerY;
        mDiameter = diameter;
        mOrientationX = 0;
        mOrientationY = 0;
        mIsOpened = false;
        mCloseCountDownTimer = new CountDownTimer(
                EYE_STAY_OPENED_DURATION_IN_MILLI,
                EYE_STAY_OPENED_DURATION_IN_MILLI) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                GooglyEye.this.close();
            }
        };
    }

    /**
     * set listener used to catch eye event
     *
     * @param listener
     */
    public void setListener(GooglyEyeListener listener) {
        mListener = listener;
    }


    /**
     * open eye. It will be closed automatically after a while unless it blinks.
     */
    public void open() {
        mIsOpened = true;

        //restart the count down timer for automatic closure
        mCloseCountDownTimer.start();

        //throw open event
        if (mListener != null) {
            mListener.onOpened();
        }
    }

    /**
     * blink eye in order to stay opened
     */
    public void blink() {
        //cancel running count down timer
        mCloseCountDownTimer.cancel();

        //restart the count down timer for automatic closure
        mCloseCountDownTimer.start();

        //throw blink event
        if (mListener != null) {
            mListener.onBlinked();
        }
    }

    /**
     * close eye
     */
    public void close() {
        mIsOpened = false;

        //throw close event
        if (mListener != null) {
            mListener.onClosed();
        }
    }


    //////////////////////////////////
    //////////SETTER&GETTER///////////
    //////////////////////////////////
    public void setCenterX(float mCenterX) {
        this.mCenterX = mCenterX;
    }

    public void setCenterY(float mCenterY) {
        this.mCenterY = mCenterY;
    }

    public void setDiameter(float mDiameter) {
        this.mDiameter = mDiameter;
    }

    public boolean isOpened() {
        return mIsOpened;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getDiameter() {
        return mDiameter;
    }

    public float getOrientationX() {
        return mOrientationX;
    }

    public float getOrientationY() {
        return mOrientationY;
    }

    /**
     * set x eye orientation
     *
     * @param x
     */
    public void setOrientationX(float x) {
        mOrientationX = x * mDiameter;
    }

    /**
     * set y eye orientation
     *
     * @param y
     */
    public void setOrientationY(float y) {
        mOrientationY = y * mDiameter;
    }
}
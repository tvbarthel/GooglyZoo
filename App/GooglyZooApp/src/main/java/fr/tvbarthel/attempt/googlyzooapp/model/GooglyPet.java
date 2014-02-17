package fr.tvbarthel.attempt.googlyzooapp.model;

import java.util.ArrayList;

import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyEyeListener;
import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyPetListener;

/**
 * Created by tbarthel on 10/02/14.
 */
public class GooglyPet {

    /**
     * pet's left eye
     */
    private GooglyEye mLeftEye;

    /**
     * pet's right eye
     */
    private GooglyEye mRightEye;

    /**
     * eyes event listener
     */
    private GooglyEyeListener mEyesListener;

    /**
     * pet resource
     */
    private int mPetRes;

    /**
     * proportion of the body compare to the whole pet (body + head)
     */
    private float mBodyProportion;


    /**
     * listeners list for pet event
     */
    private ArrayList<GooglyPetListener> mListenerList;

    /**
     * Pet with two eyes
     *
     * @param petResId       res id
     * @param leftRelativeX  left eye relative X
     * @param leftRelativeY  left eye relative Y
     * @param rightRelativeX right eye relative X
     * @param rightRelativeY right eye relative Y
     * @param eyeDiameter    diameter for both eye
     */
    public GooglyPet(int petResId, float leftRelativeX, float leftRelativeY, float rightRelativeX,
                     float rightRelativeY, float eyeDiameter, float bodyProportion) {
        mPetRes = petResId;
        mBodyProportion = bodyProportion;
        //init pet's eyes
        mLeftEye = new GooglyEye(leftRelativeX, leftRelativeY, eyeDiameter);
        mRightEye = new GooglyEye(rightRelativeX, rightRelativeY, eyeDiameter);

        //init eyes event listener
        mEyesListener = new GooglyEyeListener() {
            @Override
            public void onOpened(GooglyEye src) {
                GooglyEye otherEye = getSecondEye(src);
                if (otherEye.isOpened()) {
                    //pet awakes
                    fireAwakeEvent();
                }

            }

            @Override
            public void onBlinked(GooglyEye src) {
                if (src.equals(mLeftEye)) {
                    //pet's left eye blink
                } else if (src.equals(mRightEye)) {
                    //pet's right eye blink
                }
            }

            @Override
            public void onClosed(GooglyEye src) {
                GooglyEye otherEye = getSecondEye(src);
                if (otherEye.isClosed()) {
                    //pet fall asleep
                    fireFallAsleepEvent();
                }
            }
        };

        //set listener
        mLeftEye.setListener(mEyesListener);
        mRightEye.setListener(mEyesListener);
    }

    /**
     * add listener to catch pet event
     *
     * @param listener
     */
    public void addListener(GooglyPetListener listener) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<GooglyPetListener>();
        }

        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    /**
     * remove listener to stop event callback
     *
     * @param listener
     */
    public void removeListener(GooglyPetListener listener) {
        if (mListenerList != null && mListenerList.contains(listener)) {
            mListenerList.remove(listener);
        }
    }

    /**
     * Set orientation for both eyes
     * Used by ObjectAnimator
     *
     * @param orientations
     */
    public void setEyesOrientation(float[] orientations) {
        if (orientations.length != 2) {
            return;
        }
        mLeftEye.setOrientationX(orientations[0]);
        mLeftEye.setOrientationY(orientations[1]);
        mRightEye.setOrientationX(orientations[0]);
        mRightEye.setOrientationY(orientations[1]);
    }

    /**
     * retrieve the second eye of the pet
     *
     * @param eye first eye
     * @return second eye
     */
    private GooglyEye getSecondEye(GooglyEye eye) {
        return eye.equals(mLeftEye) ? mLeftEye : mRightEye;
    }

    /**
     * call onAwake of all registered listeners
     */
    private void fireAwakeEvent() {
        if (mListenerList != null && mListenerList.size() > 0) {
            for (GooglyPetListener listener : mListenerList) {
                listener.onAwake();
            }
        }
    }

    /**
     * call onFallAsleep of all registered listeners
     */
    private void fireFallAsleepEvent() {
        if (mListenerList != null && mListenerList.size() > 0) {
            for (GooglyPetListener listener : mListenerList) {
                listener.onFallAsleep();
            }
        }
    }

    /**
     * Getter & Setter
     */

    public GooglyEye getLeftEye() {
        return mLeftEye;
    }

    public GooglyEye getRightEye() {
        return mRightEye;
    }

    public int getPetRes() {
        return mPetRes;
    }

    public float getBodyProportion() {
        return mBodyProportion;
    }

    public float getHeadProportion() {
        return 1.0f - mBodyProportion;
    }

}

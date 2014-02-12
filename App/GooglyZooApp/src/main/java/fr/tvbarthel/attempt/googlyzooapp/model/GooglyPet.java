package fr.tvbarthel.attempt.googlyzooapp.model;

import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyEyeListener;

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
                     float rightRelativeY, float eyeDiameter) {
        mPetRes = petResId;

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
                }
            }
        };

        //set listener
        mLeftEye.setListener(mEyesListener);
        mRightEye.setListener(mEyesListener);
    }

    /**
     * Set orientation for both eyes
     *
     * @param xOrientation
     * @param yOrientation
     */
    public void setEyeOrientation(float xOrientation, float yOrientation) {
        mLeftEye.setOrientationX(xOrientation);
        mLeftEye.setOrientationY(yOrientation);
        mRightEye.setOrientationX(xOrientation);
        mRightEye.setOrientationY(yOrientation);
    }

    public GooglyEye getLeftEye() {
        return mLeftEye;
    }

    public GooglyEye getRightEye() {
        return mRightEye;
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


}

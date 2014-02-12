package fr.tvbarthel.attempt.googlyzooapp.model;

/**
 * Created by tbarthel on 10/02/14.
 */
public class GooglyPet {

    //left eye
    private GooglyEye mLeftEye;

    //righ eye
    private GooglyEye mRightEye;

    //pet resource
    private int mPetRes;

    /**
     * Pet constructor
     *
     * @param petResId
     */
    public GooglyPet(int petResId) {
        mPetRes = petResId;
        mLeftEye = null;
        mRightEye = null;
    }

    /**
     * Pet constructor
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
        mLeftEye = new GooglyEye(leftRelativeX, leftRelativeY, eyeDiameter);
        mRightEye = new GooglyEye(rightRelativeX, rightRelativeY, eyeDiameter);
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


}

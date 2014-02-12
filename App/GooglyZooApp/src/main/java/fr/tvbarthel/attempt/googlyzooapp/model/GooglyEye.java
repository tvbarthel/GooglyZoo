package fr.tvbarthel.attempt.googlyzooapp.model;

/**
 * Created by tbarthel on 06/02/14.
 */
public class GooglyEye {

    private float mCenterX;
    private float mCenterY;
    private float mDiameter;
    private float mOrientationX;
    private float mOrientationY;

    public GooglyEye(float centerX, float centerY, float diameter) {
        mCenterX = centerX;
        mCenterY = centerY;
        mDiameter = diameter;
        mOrientationX = 0;
        mOrientationY = 0;
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

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getDiameter() {
        return mDiameter;
    }

    public float getOrientationX(){
        return mOrientationX;
    }

    public float getOrientationY(){
        return mOrientationY;
    }

    /**
     * set x eye orientation
     * @param x
     */
    public void setOrientationX(float x){
        mOrientationX = x * mDiameter;
    }

    /**
     * set y eye orientation
     * @param y
     */
    public void setOrientationY(float y){
        mOrientationY = y * mDiameter;
    }
}
package fr.tvbarthel.attempt.googlyzooapp.listener;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.hardware.Camera;

import fr.tvbarthel.attempt.googlyzooapp.ui.FloatArrayEvaluator;

/**
 * Created by tbarthel on 22/02/14.
 * Listener for face position with smooth motion
 * Support only one face
 */
public abstract class SmoothFaceDetectionListener implements Camera.FaceDetectionListener {

    /**
     * listener for smooth face detection
     * support only one face
     *
     * @param smoothFacePosition smooth position of the detected face
     */
    public abstract void onSmoothFaceDetection(float[] smoothFacePosition);

    /**
     * last face detected position
     */
    private float[] mLastFaceDetectedPosition;

    /**
     * current smooth position
     */
    private float[] mCurrentFacePosition;

    /**
     * Animator for smooth face motion
     */
    private ObjectAnimator mSmoothPositionAnimator;

    /**
     * duration of face transition between two known positions
     */
    private static final int FACE_MOTION_DURATION_IN_MILLI = 100;

    /**
     * listener which apply smooth motion between old face position and the new one
     * support only one face
     */
    public SmoothFaceDetectionListener() {
        mLastFaceDetectedPosition = new float[]{0, 0};
        mCurrentFacePosition = new float[]{0, 0};

        //init animator with custom evaluator
        mSmoothPositionAnimator = ObjectAnimator.ofObject(this, "CurrentFacePosition",
                new FloatArrayEvaluator(2), 0);

        //add listener to update the position during smooth motion
        mSmoothPositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SmoothFaceDetectionListener.this.onSmoothFaceDetection(mCurrentFacePosition);
            }
        });

        //set animation duration
        mSmoothPositionAnimator.setDuration(FACE_MOTION_DURATION_IN_MILLI);
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces.length > 0) {
            //apply smooth motion between old position and the new detected one
            processSmoothDetection(faces[0]);
        }
    }

    /**
     * apply smooth motion on face position
     *
     * @param face newly detected face
     */
    private void processSmoothDetection(Camera.Face face) {
        float[] newDetectedFacePosition = new float[2];
        newDetectedFacePosition[0] = (float) face.rect.centerX();
        newDetectedFacePosition[1] = (float) face.rect.centerY();

        //if smooth motion already running, cancel it
        if (mSmoothPositionAnimator.isRunning()) {
            mSmoothPositionAnimator.end();
        }

        //set new detected position as end value
        //clone array as gradual values are going to be evaluated
        mSmoothPositionAnimator.setObjectValues(mLastFaceDetectedPosition.clone(), newDetectedFacePosition.clone());

        //restart smooth motion
        mSmoothPositionAnimator.start();

        //store as last knows position
        mLastFaceDetectedPosition[0] = newDetectedFacePosition[0];
        mLastFaceDetectedPosition[1] = newDetectedFacePosition[1];
    }

    /**
     * used by object animator to update current position during smooth motion
     *
     * @param smoothPosition position calculated by the animator
     */
    public void setCurrentFacePosition(float[] smoothPosition) {
        if (smoothPosition.length != 2) {
            return;
        }
        mCurrentFacePosition[0] = smoothPosition[0];
        mCurrentFacePosition[1] = smoothPosition[1];
    }


}

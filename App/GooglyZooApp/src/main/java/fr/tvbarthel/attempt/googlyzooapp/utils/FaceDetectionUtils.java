package fr.tvbarthel.attempt.googlyzooapp.utils;

import android.view.Surface;
import android.view.View;

/**
 * Created by tbarthel on 10/02/14.
 */
public final class FaceDetectionUtils {

    /**
     * process values from FaceDetectionListener to retrieve relative position
     * based on cameraPreview dimension and current device rotation
     *
     * @param facePosition       detected face
     * @param cameraPreview      camera preview used for faceDectection
     * @param currentOrientation current device rotation
     * @return normalized relative position in a direct orthonormal system
     */
    public static float[] getRelativeHeadPosition(
            float[] facePosition, View cameraPreview, int currentOrientation) {

        float[] relativePosition = new float[2];

        final float cameraCoordinateX = facePosition[0] / ((float) cameraPreview.getMeasuredWidth());
        final float cameraCoordinateY = facePosition[1] / ((float) cameraPreview.getMeasuredHeight());

        switch (currentOrientation) {
            case Surface.ROTATION_0:
                relativePosition[0] = -cameraCoordinateY;
                relativePosition[1] = -cameraCoordinateX;
                break;
            case Surface.ROTATION_90:
                relativePosition[0] = -cameraCoordinateX;
                relativePosition[1] = cameraCoordinateY;
                break;
            case Surface.ROTATION_180:
                break;
            case Surface.ROTATION_270:
                relativePosition[0] = cameraCoordinateX;
                relativePosition[1] = -cameraCoordinateY;
                break;
        }

        return relativePosition;
    }

    //Non instantiable class.
    private FaceDetectionUtils() {
    }
}

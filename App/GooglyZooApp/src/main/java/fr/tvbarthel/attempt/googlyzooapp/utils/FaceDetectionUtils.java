package fr.tvbarthel.attempt.googlyzooapp.utils;

import android.hardware.Camera;
import android.view.Surface;
import android.view.View;

/**
 * Created by tbarthel on 10/02/14.
 */
public class FaceDetectionUtils {

    /**
     * process values from FaceDetectionListener to retrieve relative position
     *
     * @param face
     * @param cameraPreview
     * @param currentOrientation
     * @return
     */
    public static float[] getRelativeHeadPosition(
            Camera.Face face, View cameraPreview, int currentOrientation) {

        float[] relativePosition = new float[2];

        final float cameraCoordinateX = (float) face.rect.centerX() /
                ((float) cameraPreview.getMeasuredWidth());
        final float cameraCoordinateY = (float) face.rect.centerY() /
                ((float) cameraPreview.getMeasuredHeight());

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
}

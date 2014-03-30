package fr.tvbarthel.attempt.googlyzooapp.utils;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by tbarthel on 30/03/14.
 */
public final class CameraUtils {

    private static final double ASPECT_TOLERANCE = 0.15;

    /**
     * Calculate the optimal size of camera preview according to layout used to display this preview
     *
     * @param sizes        Available camera preview sizes
     * @param layoutWidth  width of layout used to display the preview
     * @param layoutHeight height of the layout used to display the preview
     * @param isPortrait   indicate if current orientation is portrait since the camera sizes are all in landscape mode
     * @return best camera preview size which fit layout used to display the preview
     */
    public static Camera.Size getBestPreviewSize(List<Camera.Size> sizes, int layoutWidth, int layoutHeight, boolean isPortrait) {
        if (isPortrait) {
            // Inverse surfaceWidth and surfaceHeight since the sizes are all in landscape mode.
            layoutHeight = layoutHeight + layoutWidth;
            layoutWidth = layoutHeight - layoutWidth;
            layoutHeight = layoutHeight - layoutWidth;
        }
        double targetRatio = (double) layoutWidth / layoutHeight;
        Camera.Size optimalSize = null;
        double optimalArea = 0;

        // Try to find the size that matches the target ratio and has the max area.
        for (Camera.Size candidateSize : sizes) {
            double candidateRatio = (double) candidateSize.width / candidateSize.height;
            double candidateArea = candidateSize.width * candidateSize.height;
            double ratioDifference = Math.abs(candidateRatio - targetRatio);
            if (ratioDifference < ASPECT_TOLERANCE && candidateArea > optimalArea) {
                optimalSize = candidateSize;
                optimalArea = candidateArea;
            }
        }

        // Cannot find a size that matches the target ratio.
        // Try to find the size that has the max area.
        if (optimalSize == null) {
            optimalArea = 0;
            for (Camera.Size candidateSize : sizes) {
                double candidateArea = candidateSize.width * candidateSize.height;
                if (candidateArea > optimalArea) {
                    optimalSize = candidateSize;
                    optimalArea = candidateArea;
                }
            }
        }

        return optimalSize;
    }

    /**
     * calculate proportional layout dimension for displaying a camera preview according to a given camera preview size
     *
     * @param size       Camera preview size used {@see CameraUtils.getBestPreviewSize}
     * @param targetW    current width of the layout which will host camera preview
     * @param targetH    current height of the layout which will host camera preview
     * @param isPortrait indicate if current orientation is portrait since the camera sizes are all in landscape mode
     * @return dimension which matches camera preview size ratio
     */
    public static double[] getProportionalDimension(Camera.Size size, int targetW, int targetH, boolean isPortrait) {
        double[] adaptedDimension = new double[2];
        double previewRatio;

        if (isPortrait) {
            previewRatio = (double) size.height / size.width;
        } else {
            previewRatio = (double) size.width / size.height;
        }

        if (((double) targetW / targetH) > previewRatio) {
            adaptedDimension[0] = targetW;
            adaptedDimension[1] = adaptedDimension[0] / previewRatio;
        } else {
            adaptedDimension[1] = targetH;
            adaptedDimension[0] = adaptedDimension[1] * previewRatio;
        }

        return adaptedDimension;
    }


    //Non instantiable class.
    private CameraUtils() {
    }
}

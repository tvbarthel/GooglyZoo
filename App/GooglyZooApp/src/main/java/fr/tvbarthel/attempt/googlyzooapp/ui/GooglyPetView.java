package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

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
     * radius used for eye navigation
     */
    private static final float EYE_RADIUS = 10f;

    /**
     * Paint used to draw eyes
     */
    private Paint mPaint;

    /**
     * View model
     */
    private GooglyPet mGooglyPet;


    public GooglyPetView(Context context, Drawable drawable) {
        super(context);

        mPaint = new Paint();
        mPaint.setStrokeWidth(15f);
        mPaint.setColor(Color.BLACK);

        this.setImageDrawable(drawable);
    }

    public void setEyesModel(GooglyPet pet) {
        mGooglyPet = pet;
    }

    @Override
    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);

        if (hint == VISIBLE) {
            Log.e(TAG, "VISIBLE");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float middleW = canvas.getWidth();
        float middleH = canvas.getHeight();

        final GooglyEye leftEye = mGooglyPet.getLeftEye();
        final GooglyEye rightEye = mGooglyPet.getRightEye();

        if (leftEye != null) {
            //draw left eye
            canvas.drawCircle(leftEye.getCenterX() * middleW + leftEye.getOrientationX(),
                    leftEye.getCenterY() * middleH + leftEye.getOrientationY(),
                    EYE_RADIUS, mPaint);
        }

        if (rightEye != null) {
            //draw right eye
            canvas.drawCircle(rightEye.getCenterX() * middleW + rightEye.getOrientationX(),
                    rightEye.getCenterY() * middleH + rightEye.getOrientationY(),
                    EYE_RADIUS, mPaint);
        }

    }
}
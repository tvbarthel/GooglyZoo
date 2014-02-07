package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import fr.tvbarthel.attempt.googlyzooapp.model.Eye;

/**
 * Created by tbarthel on 06/02/14.
 */
public class GooglyPet extends ImageView {

    private static final String TAG = GooglyPet.class.getName();

    private Paint mPaint;
    private Eye mLeftEye;
    private Eye mRightEye;


    public GooglyPet(Context context, Drawable drawable) {
        super(context);

        mPaint = new Paint();
        mPaint.setStrokeWidth(15f);
        mPaint.setColor(Color.BLACK);

        this.setImageDrawable(drawable);
    }

    public void setEyesModel(Eye left, Eye right) {
        mLeftEye = left;
        mRightEye = right;
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

        if (mLeftEye != null) {
            //draw left eye
            canvas.drawPoint(mLeftEye.getCenterX() * middleW + mLeftEye.getOrientationX(),
                    mLeftEye.getCenterY() * middleH + mRightEye.getOrientationY(), mPaint);
        }

        if (mRightEye != null) {
            //draw right eye
            canvas.drawPoint(mRightEye.getCenterX() * middleW + mRightEye.getOrientationX(),
                    mRightEye.getCenterY() * middleH + mRightEye.getOrientationY(), mPaint);
        }

    }
}
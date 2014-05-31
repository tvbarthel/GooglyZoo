package fr.tvbarthel.attempt.googlyzooapp.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Rounded overlay drawn over parent view
 * First attempt to mimic http://cdn.androidpolice.com/wp-content/uploads/2014/04/nexusae0_Share_Photo.mp4?_=1
 */
public class RoundedOverlay extends View {

    /**
     * default color use by the overlay
     */
    private static final int DEFAULT_OVERLAY_COLOR = 0xCC000000;

    /**
     * default duration in milli seconds for open and close animations
     */
    private static final int DEFAULT_DURATION_IN_MILLI = 300;

    /**
     * overlay radius, evaluate to match parent diagonal
     */
    private double mRadius;

    /**
     * real radius used to draw the overlay at any time
     */
    private float mCurrentRadius;

    /**
     * painter used to draw the overlay
     */
    private Paint mPaint;

    /**
     * overlay wished color
     */
    private int mColor;

    /**
     * alpha of the wished color, used during the fade in / fade out effect
     */
    private int mAlpha;

    /**
     * right position of the parent
     */
    private int mParentRight;

    /**
     * top position of the parent
     */
    private int mParentTop;

    /**
     * coordinates used to render the rounded overlay
     */
    private RectF mCircleCoordinates;

    /**
     * animator used to open the overlay
     */
    private ValueAnimator mOpenAnimator;

    /**
     * animator used to close the overlay
     */
    private ValueAnimator mCloseAnimator;

    /**
     * current listener to catch open event
     */
    private OpenListener mOpenListener;

    /**
     * current listener to catch close event
     */
    private CloseListener mCloseListener;


    public RoundedOverlay(Context context) {
        this(context, DEFAULT_OVERLAY_COLOR);
    }

    public RoundedOverlay(Context context, int color) {
        super(context);
        mCircleCoordinates = new RectF(0, 0, 1, 1);
        mRadius = 1;
        mColor = color;
        mAlpha = Color.alpha(color);

        //initialize painter used to draw overlay
        initPainter(mColor);

        //initialize animator used to display overlay
        initOpenAnimator();

        //initialize animator used to hide overlay
        initCloseAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mCircleCoordinates, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //update parent right and top in order to position overlay on the top right corner
        mParentRight = right;
        mParentTop = top;

        //evaluate parent diagonal according to Pythagorean theorem to update max circle radius
        final double widthSqr = Math.pow(right - left, 2);
        final double heightSqr = Math.pow(bottom - top, 2);

        //double radius since circle is center in corner
        mRadius = Math.sqrt(widthSqr + heightSqr) * 2;
    }

    /**
     * show circular overlay
     */
    public void open() {
        if (mCloseAnimator.isRunning()) {
            mCloseAnimator.cancel();
        }

        if (this.getVisibility() != View.VISIBLE) {
            this.setVisibility(View.VISIBLE);
        }
        mOpenAnimator.start();
    }


    /**
     * hide circular overlay
     */
    public void close() {
        if (mOpenAnimator.isRunning()) {
            mOpenAnimator.cancel();
        }
        mCloseAnimator.start();
    }

    /**
     * set duration of animation used to open the overlay
     *
     * @param duration duration in milli seconds
     */
    public void setOpenDuration(int duration) {
        mOpenAnimator.setDuration(duration);
    }

    /**
     * set duration of animation used to close the overlay
     *
     * @param duration duration in milli seconds
     */
    public void setCloseDuration(int duration) {
        mCloseAnimator.setDuration(duration);
    }

    /**
     * set listener to catch onOpen event
     *
     * @param listener OpenListener
     */
    public void setOnOpenListener(OpenListener listener) {
        mOpenListener = listener;
    }

    /**
     * set listener to catch onClose event
     *
     * @param listener CloseListener
     */
    public void setOnCloseListener(CloseListener listener) {
        mCloseListener = listener;
    }

    /**
     * update {@link android.graphics.RectF} which define circle edges according to a given radius
     *
     * @param radius radius used to evaluate circle position
     */
    private void upDateCircle(float radius) {
        mCircleCoordinates =
                new RectF(
                        mParentRight - radius / 2,
                        mParentTop - radius / 2,
                        mParentRight + radius / 2,
                        mParentTop + radius / 2
                );
        RoundedOverlay.this.invalidate();
    }

    /**
     * initialize painter used to draw the overlay
     *
     * @param color color use for texture
     */
    private void initPainter(int color) {
        mPaint = new Paint();

        //set color used to draw overlay
        mPaint.setColor(color);

        //smooth edges since current overlay is circular
        mPaint.setAntiAlias(true);
    }

    /**
     * initialize animator used to show circular overlay
     */
    private void initOpenAnimator() {
        //values set from 0 to 100 in order to reused animated fraction
        mOpenAnimator = ObjectAnimator.ofInt(0, 100);

        //initialize duration with default value
        mOpenAnimator.setDuration(DEFAULT_DURATION_IN_MILLI);

        //set default interpolator to accelerate
        mOpenAnimator.setInterpolator(new AccelerateInterpolator());

        //add listener to update circle radius according to the current animated fraction
        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //increase alpha from set color up to 100% at the end
                mPaint.setAlpha((int) (mAlpha * animation.getAnimatedFraction()));

                //update radius
                mCurrentRadius = (float) mRadius * animation.getAnimatedFraction();
                upDateCircle(mCurrentRadius);
            }
        });


        //listener used to throw onOpen event
        mOpenAnimator.addListener(new Animator.AnimatorListener() {

            /**
             * since .cancel also calls end, should distinguish if animation stop normally or
             * from a .cancel call
             */
            private boolean fromCanceled = false;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOpenListener != null) {
                    if (fromCanceled) {
                        fromCanceled = false;
                    } else {
                        mOpenListener.onOpen();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                fromCanceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * initialize animator use to hide circular overlay
     */
    private void initCloseAnimator() {
        //values set from 0 to 100 in order to reused animated fraction
        mCloseAnimator = ObjectAnimator.ofInt(0, 100);

        //initialize duration with default value
        mCloseAnimator.setDuration(DEFAULT_DURATION_IN_MILLI);

        //set default interpolator to accelerate
        mCloseAnimator.setInterpolator(new AccelerateInterpolator());

        //add listener to completely hide overlay once hiding animation ends
        //as well as throwing onClose event
        mCloseAnimator.addListener(new Animator.AnimatorListener() {

            /**
             * since .cancel also calls end, should distinguish if animation stop normally or
             * from a .cancel call
             */
            private boolean fromCanceled = false;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                RoundedOverlay.this.setVisibility(View.GONE);
                if (mCloseListener != null) {
                    if (fromCanceled) {
                        fromCanceled = false;
                    } else {
                        mCloseListener.onClose();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                fromCanceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //add listener to update circle radius according to the opposite of
        // current animated fraction in order to reduce it
        mCloseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //reduce alpha from set color up to 0% at the end
                mPaint.setAlpha((int) (mAlpha * (1 - animation.getAnimatedFraction())));

                //update radius
                final float newRadius = mCurrentRadius * (1.0f - animation.getAnimatedFraction());
                upDateCircle(newRadius);
            }
        });
    }

    /**
     * callback for open event
     */
    public interface OpenListener {
        public void onOpen();
    }

    /**
     * callback for close event
     */
    public interface CloseListener {
        public void onClose();
    }

}

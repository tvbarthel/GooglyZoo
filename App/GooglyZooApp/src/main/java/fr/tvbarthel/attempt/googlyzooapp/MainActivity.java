package fr.tvbarthel.attempt.googlyzooapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import fr.tvbarthel.attempt.googlyzooapp.model.GooglyEye;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;
import fr.tvbarthel.attempt.googlyzooapp.ui.GooglyPetView;
import fr.tvbarthel.attempt.googlyzooapp.utils.FaceDetectionUtils;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    /**
     * duration of eye transition between two known position
     */
    private static final int EYE_ANIMATION_DURATION_IN_MILLI = 300;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private GooglyPetView mPet;
    private GooglyPet mGooglyPet;
    private FrameLayout.LayoutParams mPetParams;
    private FrameLayout mPreview;
    private FacePreviewDetection mFaceDetectionPreview;
    private Camera mCamera;
    private int mCurrentRotation;

    /**
     * eye animator
     */
    private AnimatorSet mEyeAnimator;
    private float mLastOrientationX;
    private float mLastOrientationY;


    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPet = new GooglyPetView(this, getResources().getDrawable(R.drawable.zebra));
        mPetParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPetParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mGooglyPet = new GooglyPet(R.drawable.zebra, 0.40f, 0.35f, 0.60f, 0.35f, 15f);
        mPet.setEyesModel(mGooglyPet);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mEyeAnimator = new AnimatorSet();
        mLastOrientationX = 0;
        mLastOrientationY = 0;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CameraAsyncTask().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePreview();
        releaseCamera();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Camera part
     */

    /**
     * used to adapt camera preview to the current device orientation
     *
     * @param camera
     */
    public void setCameraDisplayOrientation(android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int degrees = 0;
        mCurrentRotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (mCurrentRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * A safe way to get an instance of the front camera
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        }
        return c;
    }

    /**
     * remove the preview
     */
    private void releasePreview() {
        if (mFaceDetectionPreview != null) {
            mPreview.removeView(mFaceDetectionPreview);
        }
        if (mPreview != null) {
            mPreview.removeView(mPet);
        }
    }

    /**
     * release the camera for the other app
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            FrameLayout preview = (FrameLayout) findViewById(R.id.container);
            preview.removeView(mFaceDetectionPreview);
        }
    }

    /**
     * update pet model with gradually and invalidate pet view to redraw the model
     *
     * @param relativeFaceX
     * @param relativeFaceY
     */
    private void animateEye(float relativeFaceX, float relativeFaceY) {
        final GooglyEye leftEye = mGooglyPet.getLeftEye();
        final GooglyEye rightEye = mGooglyPet.getRightEye();

        if (mEyeAnimator.isRunning()) {
            mEyeAnimator.end();
        }

        ObjectAnimator leftEyeX =
                ObjectAnimator.ofFloat(leftEye, "orientationX", mLastOrientationX, relativeFaceX);
        leftEyeX.setDuration(EYE_ANIMATION_DURATION_IN_MILLI);

        ObjectAnimator leftEyeY =
                ObjectAnimator.ofFloat(leftEye, "orientationY", mLastOrientationY, relativeFaceY);
        leftEyeY.setDuration(EYE_ANIMATION_DURATION_IN_MILLI);
        leftEyeX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPet.invalidate();
            }
        });

        ObjectAnimator rightEyeX =
                ObjectAnimator.ofFloat(rightEye, "orientationX", mLastOrientationX, relativeFaceX);
        rightEyeX.setDuration(EYE_ANIMATION_DURATION_IN_MILLI);

        ObjectAnimator rightEyeY =
                ObjectAnimator.ofFloat(rightEye, "orientationY", mLastOrientationY, relativeFaceY);
        rightEyeY.setDuration(EYE_ANIMATION_DURATION_IN_MILLI);

        mEyeAnimator.playTogether(leftEyeX, leftEyeY, rightEyeX, rightEyeY);
        mEyeAnimator.start();

        mLastOrientationX = relativeFaceX;
        mLastOrientationY = relativeFaceY;
    }


    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected Camera doInBackground(Void... params) {
            return getCameraInstance();
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            mCamera = camera;

            if (mCamera == null) {
                MainActivity.this.finish();
            }

            mFaceDetectionPreview = new FacePreviewDetection(MainActivity.this, mCamera);
            mPreview = (FrameLayout) findViewById(R.id.container);

            mPreview.addView(mFaceDetectionPreview);
            mPreview.addView(mPet, mPetParams);
            mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    if (faces.length > 0) {
                        final float[] relativePosition = FaceDetectionUtils.getRelativeHeadPosition(
                                faces[0], mFaceDetectionPreview, mCurrentRotation);
                        animateEye(relativePosition[0], relativePosition[1]);
                    }
                }
            });


            setCameraDisplayOrientation(mCamera);
        }
    }

}

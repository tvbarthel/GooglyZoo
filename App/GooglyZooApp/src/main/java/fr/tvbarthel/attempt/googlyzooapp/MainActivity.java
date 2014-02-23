package fr.tvbarthel.attempt.googlyzooapp;

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

import fr.tvbarthel.attempt.googlyzooapp.fragments.MoreProjectDialogFragment;
import fr.tvbarthel.attempt.googlyzooapp.fragments.NavigationDrawerFragment;
import fr.tvbarthel.attempt.googlyzooapp.listener.SmoothFaceDetectionListener;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetEntry;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetFactory;
import fr.tvbarthel.attempt.googlyzooapp.ui.GooglyPetView;
import fr.tvbarthel.attempt.googlyzooapp.utils.FaceDetectionUtils;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Log cat
     */
    private static final String TAG = MainActivity.class.getName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to display icon from selected pet
     */
    private int mActionBarIcon;

    /**
     * Googly pet view
     */
    private GooglyPetView mGooglyPetView;

    /**
     * layout params used to center | bottom googly pet view
     */
    private FrameLayout.LayoutParams mPetParams;

    /**
     * camera preview
     */
    private FrameLayout mPreview;

    /**
     * Face detection
     */
    private FacePreviewDetection mFaceDetectionPreview;

    /**
     * camera hardware
     */
    private Camera mCamera;

    /**
     * current device rotation
     */
    private int mCurrentRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create view to display pet
        mGooglyPetView = new GooglyPetView(this);
        mPetParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPetParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();
        mActionBarIcon = R.drawable.ic_launcher;

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

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mActionBarIcon);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_more_projects) {
            (new MoreProjectDialogFragment()).show(getFragmentManager(), "dialog_more_projects");
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
            mPreview.removeView(mGooglyPetView);
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


    @Override
    public void onNavigationDrawerItemSelected(GooglyPetEntry petSelected) {
        final int googlyName = petSelected.getName();
        GooglyPet model = null;
        switch (googlyName) {
            case R.string.googly_zebra_name:
                model = GooglyPetFactory.createGooglyZebra();
                break;
            case R.string.googly_gnu_name:
                model = GooglyPetFactory.createGooglyGnu();
                break;
            case R.string.googly_hippo_name:
                model = GooglyPetFactory.createGooglyHippo();
                break;
        }
        mGooglyPetView.setModel(model);
        mTitle = getResources().getString(googlyName);
        mActionBarIcon = petSelected.getBlackAndWhiteIcon();
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
            mPreview.addView(mGooglyPetView, mPetParams);
            mCamera.setFaceDetectionListener(new SmoothFaceDetectionListener() {
                @Override
                public void onSmoothFaceDetection(float[] smoothFacePosition) {
                    //get relative position based on camera preview dimension and current rotation
                    final float[] relativePosition = FaceDetectionUtils.getRelativeHeadPosition(
                            smoothFacePosition, mFaceDetectionPreview, mCurrentRotation);
                    mGooglyPetView.animatePetEyes(relativePosition);
                }
            });


            setCameraDisplayOrientation(mCamera);
        }
    }

}

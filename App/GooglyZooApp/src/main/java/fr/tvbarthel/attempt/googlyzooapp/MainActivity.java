package fr.tvbarthel.attempt.googlyzooapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.vending.billing.tvbarthel.DonateCheckActivity;
import com.android.vending.billing.tvbarthel.SupportActivity;

import fr.tvbarthel.attempt.googlyzooapp.fragments.AboutDialogFragment;
import fr.tvbarthel.attempt.googlyzooapp.fragments.LicenseDialogFragment;
import fr.tvbarthel.attempt.googlyzooapp.fragments.MoreProjectDialogFragment;
import fr.tvbarthel.attempt.googlyzooapp.fragments.NavigationDrawerFragment;
import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyPetListener;
import fr.tvbarthel.attempt.googlyzooapp.listener.SmoothFaceDetectionListener;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetEntry;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetFactory;
import fr.tvbarthel.attempt.googlyzooapp.ui.GooglyPetView;
import fr.tvbarthel.attempt.googlyzooapp.utils.CameraUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.FaceDetectionUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.GooglyPetUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.SharedPreferencesUtils;

public class MainActivity extends DonateCheckActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnLongClickListener, View.OnTouchListener {
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
    private SpannableString mTitle;

    /**
     * Used to display icon from selected pet
     */
    private int mActionBarIcon;

    /**
     * Googly pet view
     */
    private GooglyPetView mGooglyPetView;

    /**
     * Googly pet model
     */
    private GooglyPet mGooglyPet;

    /**
     * Googly pet listener
     */
    private GooglyPetListener mGooglyPetListener;

    /**
     * layout params used to center | bottom googly pet view
     */
    private FrameLayout.LayoutParams mPetParams;

    /**
     * layout params used to render a proportional camera preview
     */
    private FrameLayout.LayoutParams mPreviewParams;

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

    /**
     * id of the current pet
     */
    private int mSelectedGooglyPet;

    /**
     * Smooth listener
     */
    private SmoothFaceDetectionListener mSmoothFaceDetectionListener;

    /**
     * Basic listener
     */
    private Camera.FaceDetectionListener mBasicFaceDetectionListener;

    /**
     * current listener
     */
    private Camera.FaceDetectionListener mCurrentListener;

    /**
     * An {@link android.os.AsyncTask} used to opened the camera.
     */
    private CameraAsyncTask mCameraAsyncTask;

    /**
     * instructions to take a screenshot
     */
    private TextView mCameraInstructions;

    /**
     * layout params for camera instructions
     */
    private FrameLayout.LayoutParams mCameraInstructionsParams;

    /**
     * animation used to show instructions
     */
    private Animation mInstructionsIn;

    /**
     * animation used to hide instructions
     */
    private Animation mInstructionOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview = (FrameLayout) findViewById(R.id.container);

        //get last pet used
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSelectedGooglyPet = sp.getInt(SharedPreferencesUtils.PREF_USER_GOOGLY_PET_SELECTED,
                GooglyPetUtils.GOOGLY_PET_ZEBRA);

        //init model
        mGooglyPet = GooglyPetFactory.createGooglyPet(mSelectedGooglyPet, this);

        //init pet event listener
        mGooglyPetListener = new GooglyPetListener() {

            @Override
            public void onAwake() {
                mGooglyPetView.moveUp();
            }

            @Override
            public void onFallAsleep() {
                mGooglyPetView.moveDown();
            }
        };

        //set up instruction for sharing screen shot
        setUpInstructions();

        //set up params for googlyPetView
        mPetParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPetParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = new SpannableString(getTitle());
        mActionBarIcon = R.drawable.ic_launcher;

        mNavigationDrawerFragment.selectEntry(mSelectedGooglyPet);

        mSmoothFaceDetectionListener = new SmoothFaceDetectionListener() {
            @Override
            public void onSmoothFaceDetection(float[] smoothFacePosition) {
                //get relative position based on camera preview dimension and current rotation
                final float[] relativePosition = FaceDetectionUtils.getRelativeHeadPosition(
                        smoothFacePosition, mFaceDetectionPreview, mCurrentRotation);
                mGooglyPetView.animatePetEyes(relativePosition);
            }
        };

        mBasicFaceDetectionListener = new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    final float[] relativePosition = FaceDetectionUtils.getRelativeHeadPosition(
                            new float[]{faces[0].rect.centerX(), faces[0].rect.centerY()},
                            mFaceDetectionPreview, mCurrentRotation);
                    mGooglyPetView.animatePetEyes(relativePosition);
                }
            }
        };

        mCurrentListener = mSmoothFaceDetectionListener;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register listener on googly pet
        if (mGooglyPet != null) {
            mGooglyPet.addListener(mGooglyPetListener);
        }

        //create view to display pet
        mGooglyPetView = new GooglyPetView(this, mGooglyPet);

        //start camera in background for avoiding black screen
        mCameraAsyncTask = new CameraAsyncTask();
        mCameraAsyncTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel the Camera AsyncTask.
        mCameraAsyncTask.cancel(true);

        //remove listener from googly pet
        if (mGooglyPet != null) {
            mGooglyPet.removeListener(mGooglyPetListener);
        }

        //release preview
        releasePreview();

        //release camera for other app !
        releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //store last selected pet
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SharedPreferencesUtils.PREF_USER_GOOGLY_PET_SELECTED, mSelectedGooglyPet);
        editor.commit();
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
        if (id == R.id.action_license) {
            (new LicenseDialogFragment()).show(getFragmentManager(), "dialog_license");
            return true;
        } else if (id == R.id.action_more_projects) {
            (new MoreProjectDialogFragment()).show(getFragmentManager(), "dialog_more_projects");
            return true;
        } else if (id == R.id.action_about) {
            (new AboutDialogFragment()).show(getFragmentManager(), "dialog_about");
            return true;
        } else if (id == R.id.action_support) {
            startActivity(new Intent(this, SupportActivity.class));
            return true;
        } else if (id == R.id.action_contact_us) {
            String contactUriString = getString(R.string.contact_send_to_uri,
                    Uri.encode(getString(R.string.contact_mail)),
                    Uri.encode(getString(R.string.contact_default_subject)));
            Uri contactUri = Uri.parse(contactUriString);
            Intent sendMail = new Intent(Intent.ACTION_SENDTO);
            sendMail.setData(contactUri);
            startActivity(sendMail);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(GooglyPetEntry petSelected) {
        final int googlyName = petSelected.getName();
        mSelectedGooglyPet = petSelected.getPetId();

        final String petName = getResources().getString(googlyName);
        final String oogly = getResources().getString(R.string.googly_name_ext);
        mTitle = new SpannableString(getResources().getString(googlyName) + oogly);
        mTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, petName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTitle.setSpan(new TypefaceSpan("sans-serif-light"), petName.length() - 1, mTitle.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBarIcon = petSelected.getBlackAndWhiteIcon();

        //remove listener from the old pet
        if (mGooglyPet != null) {
            mGooglyPet.removeListener(mGooglyPetListener);
        }

        //create the selected Googly pet
        mGooglyPet = GooglyPetFactory.createGooglyPet(mSelectedGooglyPet, this);

        //register listener on the new pet
        mGooglyPet.addListener(mGooglyPetListener);

        if (mGooglyPetView != null) {
            mGooglyPetView.setModel(mGooglyPet);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mCameraInstructions.getVisibility() == View.VISIBLE) {
                    if (mInstructionOut != null) {
                        mCameraInstructions.startAnimation(mInstructionOut);
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mInstructionsIn != null) {
            mCameraInstructions.setVisibility(View.VISIBLE);
            mCameraInstructions.startAnimation(mInstructionsIn);
        }
        return false;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mActionBarIcon);
    }

    /**
     * used to check orientation
     *
     * @return
     */
    public boolean isPortrait() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
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
            mPreview.removeView(mCameraInstructions);
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
     * switch listener
     *
     * @return unused listener
     */
    private Camera.FaceDetectionListener switchListener() {
        if (mCurrentListener == mSmoothFaceDetectionListener) {
            makeToast(R.string.basic_face_detection);
            return mBasicFaceDetectionListener;
        } else {
            makeToast(R.string.smooth_face_detection);
            return mSmoothFaceDetectionListener;
        }
    }

    /**
     * build instructions components
     */
    private void setUpInstructions() {
        //set up textview
        mCameraInstructions = new TextView(this);
        mCameraInstructions.setText(R.string.camera_instructions);
        mCameraInstructions.setTextColor(getResources().getColor(R.color.white));
        mCameraInstructions.setTextSize(15.0f);
        mCameraInstructions.setGravity(Gravity.CENTER);
        mCameraInstructions.setBackgroundColor(getResources().getColor(R.color.instruction_background));
        mCameraInstructions.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                getResources().getDrawable(R.drawable.ic_camera)
        );
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics()
        );
        mCameraInstructions.setPadding(padding, padding, padding, padding);
        mCameraInstructions.setVisibility(View.GONE);

        //set up layout params
        mCameraInstructionsParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCameraInstructionsParams.gravity = Gravity.CENTER;

        //set up animation in
        mInstructionsIn = AnimationUtils.loadAnimation(this, R.anim.in_bis);

        //setup animation out
        mInstructionOut = AnimationUtils.loadAnimation(this, R.anim.out_bis);
        if (mInstructionOut != null) {
            mInstructionOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCameraInstructions.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            if (camera == null) {
                MainActivity.this.finish();
            } else {
                //configure Camera parameters
                Camera.Parameters cameraParameters = camera.getParameters();

                //get optimal camera preview size according to the layout used to display it
                Camera.Size bestSize = CameraUtils.getBestPreviewSize(
                        cameraParameters.getSupportedPreviewSizes()
                        , mPreview.getWidth()
                        , mPreview.getHeight()
                        , isPortrait());
                //set optimal camera preview
                cameraParameters.setPreviewSize(bestSize.width, bestSize.height);
                camera.setParameters(cameraParameters);

                //set camera orientation to match with current device orientation
                setCameraDisplayOrientation(camera);

                //get proportional dimension for the layout used to display preview according to the preview size used
                int[] adaptedDimension = CameraUtils.getProportionalDimension(
                        bestSize
                        , mPreview.getWidth()
                        , mPreview.getHeight()
                        , isPortrait());

                //set up params for the layout used to display the preview
                mPreviewParams = new FrameLayout.LayoutParams(adaptedDimension[0], adaptedDimension[1]);
                mPreviewParams.gravity = Gravity.CENTER;
            }
            return camera;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            // Check if the task is cancelled before trying to use the camera.
            if (!isCancelled()) {
                mCamera = camera;
                if (mCamera == null) {
                    MainActivity.this.finish();
                } else {
                    //set up face detection preview
                    mFaceDetectionPreview = new FacePreviewDetection(
                            MainActivity.this,
                            mCamera,
                            new FacePreviewDetection.FacePreviewDetectionCallback() {
                                @Override
                                public void onFaceDetectionStart(boolean supported) {
                                    if (!supported) {
                                        makeToast(R.string.face_detection_not_support);
                                    }
                                }
                            }
                    );
                    mPreview.addView(mFaceDetectionPreview, mPreviewParams);
                    mPreview.addView(mGooglyPetView, mPetParams);
                    mPreview.addView(mCameraInstructions, mCameraInstructionsParams);
                    mPreview.setOnTouchListener(MainActivity.this);
                    mPreview.setOnLongClickListener(MainActivity.this);
                    mCamera.setFaceDetectionListener(mCurrentListener);
                }
            }
        }

        @Override
        protected void onCancelled(Camera camera) {
            super.onCancelled(camera);
            if (camera != null) {
                camera.release();
            }
        }
    }
}

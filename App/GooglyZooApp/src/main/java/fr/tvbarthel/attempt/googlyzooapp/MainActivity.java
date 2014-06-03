package fr.tvbarthel.attempt.googlyzooapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.vending.billing.tvbarthel.DonateCheckActivity;
import com.android.vending.billing.tvbarthel.SupportActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import fr.tvbarthel.attempt.googlyzooapp.ui.RoundedOverlay;
import fr.tvbarthel.attempt.googlyzooapp.utils.CameraUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.FaceDetectionUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.GooglyPetUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.SharedPreferencesUtils;

/**
 * TODO refactor, 1000+ lines of codes is unacceptable!
 */
public class MainActivity extends DonateCheckActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnTouchListener {
    /**
     * Log cat
     */
    private static final String TAG = MainActivity.class.getName();

    /**
     * delay between two touch required to throw double touch event
     */
    private static final long DOUBLE_TOUCH_DELAY_IN_MILLI = 500;

    /**
     * quality of the picture
     */
    private static final int BITMAP_QUALITY = 80;

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
    private LinearLayout mCameraInstructions;

    /**
     * captured picture preview
     */
    private ImageView mCapturePreview;

    /**
     * captured picture preview layout params
     */
    private FrameLayout.LayoutParams mCapturePreviewParams;

    /**
     * rounded overlay used when camera instruction are requested
     */
    private RoundedOverlay mRoundedOverlay;

    /**
     * animation used to show additional content
     */
    private Animation mAnimationIn;

    /**
     * animation used to hide additional content
     */
    private Animation mAnimationOut;

    /**
     * animation used to show saving button
     */
    private Animation mAnimationInFromLeft;

    /**
     * animation used to hide saving button
     */
    private Animation mAnimationOutFromLeft;

    /**
     * animation used when pet isn't awake and capture is requested
     */
    private Animation mWiggleAnimation;

    /**
     * last touch timestamp use to detected double touch
     */
    private long mLastTouchTimeStamp;

    /**
     * callback used to used to capture picture
     */
    private Camera.PictureCallback mPictureCallback;

    /**
     * screen capture byte
     */
    private Bitmap mPicture;

    /**
     * layout params for saved button
     */
    private FrameLayout.LayoutParams mSaveButtonParams;

    /**
     * button used to save screen capture
     */
    private ImageButton mSaveButton;

    /**
     * boolean used to know if preview has been requested
     */
    private boolean mPreviewRequested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview = (FrameLayout) findViewById(R.id.container);

        mLastTouchTimeStamp = 0;

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

        //set up rounded overlay opened when instruction are requested
        setUpRoundedOverlay();

        //set up instruction for sharing screen shot
        setUpInstructions();

        //set up animations
        setUpAnimations();

        //set up capture preview
        setUpCapturePreview();

        //set up saving button
        setUpSavingButton();

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
        } else if (id == R.id.action_camera) {
            if (mRoundedOverlay.getVisibility() != View.VISIBLE) {
                mRoundedOverlay.open();
            } else {
                hideAdditionalContent();
            }
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
            case MotionEvent.ACTION_DOWN:
                //hide instructions
                if (!hideAdditionalContent()) {

                    //process event to throw double touch
                    if (isDoubleTouch(event)) {
                        captureScreenShot();
                    }
                }
                break;
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

        int previewResult = 0;
        int pictureResult = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            previewResult = (info.orientation + degrees) % 360;
            pictureResult = previewResult;
            previewResult = (360 - previewResult) % 360;  // compensate the mirror
        }
        final Camera.Parameters params = camera.getParameters();
        params.setRotation(pictureResult);
        camera.setParameters(params);
        camera.setDisplayOrientation(previewResult);
    }

    /**
     * Callback used when Camera.takePicture is called
     */
    private void initCameraPictureCallback() {
        mPictureCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //restart preview
                mCamera.startPreview();

                //build screen capture
                new CaptureAsyncTask().execute(data);
            }
        };
    }

    /**
     * used to take a picture of the user
     */
    private void captureScreenShot() {
        if (mGooglyPet.isAwake()) {
            //TODO implement screen shot
            if (mPictureCallback == null) {
                initCameraPictureCallback();
            }
            mCamera.takePicture(null, null, mPictureCallback);
        } else {
            //pet not awake = no face detected, don't take a screen
            if (mWiggleAnimation == null) {
                buildWiggleAnimation();
            }
            mGooglyPetView.startAnimation(mWiggleAnimation);
        }
    }

    /**
     * Save screen capture into external storage
     *
     * @param screenBytes
     * @return path of the file
     */
    private Uri writeScreenBytesToExternalStorage(byte[] screenBytes) {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_HH_ss");
            final String filePrefix = "screen_";
            final String fileSuffix = ".jpg";
            final String fileName = filePrefix + simpleDateFormat.format(new Date()) + fileSuffix;
            final File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "GooglyZoo", fileName);
            if (!f.getParentFile().isDirectory()) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            final FileOutputStream fo = new FileOutputStream(f);
            fo.write(screenBytes);
            fo.close();
            return Uri.fromFile(f);
        } catch (IOException e) {
            Log.e(TAG, "error while saving screen", e);
            return null;
        }
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
            mPreview.removeView(mRoundedOverlay);
            mPreview.removeView(mCameraInstructions);
            mPreview.removeView(mCapturePreview);
            mPreview.removeView(mSaveButton);
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
     * process motion event to detect double touch
     *
     * @param event should be an ACTION_DOWN event
     * @return true if double touch motion
     */
    private boolean isDoubleTouch(MotionEvent event) {
        boolean isDoubleTouch = false;
        long newTimeStamp = event.getEventTime();
        final long delay = newTimeStamp - mLastTouchTimeStamp;
        if (mLastTouchTimeStamp != 0 && delay < DOUBLE_TOUCH_DELAY_IN_MILLI) {
            newTimeStamp = 0;
            isDoubleTouch = true;
        }
        mLastTouchTimeStamp = newTimeStamp;
        return isDoubleTouch;
    }

    /**
     * initialize rounded overlay
     */
    private void setUpRoundedOverlay() {
        mRoundedOverlay = new RoundedOverlay(this);
        mRoundedOverlay.setOpenDuration(500);
        mRoundedOverlay.setCloseDuration(300);
        mRoundedOverlay.setVisibility(View.GONE);

        //catch onOpen event to display instructions once overlay is opened
        mRoundedOverlay.setOnOpenListener(
                new RoundedOverlay.OpenListener() {
                    @Override
                    public void onOpen() {
                        if (mPreviewRequested) {
                            mCapturePreview.setVisibility(View.VISIBLE);
                            mCapturePreview.startAnimation(mAnimationIn);
                        } else if (mCameraInstructions.getVisibility() != View.VISIBLE) {
                            mCameraInstructions.setVisibility(View.VISIBLE);
                            mCameraInstructions.startAnimation(mAnimationIn);
                        }
                    }
                }
        );
    }

    /**
     * build instructions components
     */

    private void setUpInstructions() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //set up instructions view
        mCameraInstructions = (LinearLayout) inflater.inflate(R.layout.instructions, null);
        if (mCameraInstructions != null) {
            mCameraInstructions.setVisibility(View.GONE);
        }
    }

    /**
     * load animations
     */
    private void setUpAnimations() {
        //set up animation in
        mAnimationIn = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom);
        if (mAnimationIn != null) {
            mAnimationIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mPreviewRequested) {
                        displaySavingButton();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }


        //setup animation out
        mAnimationOut = AnimationUtils.loadAnimation(this, R.anim.out_from_bottom);
        if (mAnimationOut != null) {
            mAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRoundedOverlay.close();
                    if (mPreviewRequested) {
                        hideCapturePreview();
                    } else if (mCameraInstructions.getVisibility() == View.VISIBLE) {
                        mCameraInstructions.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        mAnimationInFromLeft = AnimationUtils.loadAnimation(this, R.anim.in_from_left);

        mAnimationOutFromLeft = AnimationUtils.loadAnimation(this, R.anim.out_from_left);
        if (mAnimationOutFromLeft != null) {
            mAnimationOutFromLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mSaveButton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * build captured preview layout
     */
    private void setUpCapturePreview() {
        mCapturePreview = new ImageView(this);
        mCapturePreview.setScaleType(ImageView.ScaleType.FIT_XY);
        mCapturePreviewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCapturePreview.setVisibility(View.GONE);
        mPreviewRequested = false;
    }

    /**
     * display capture preview
     */
    private void displayCapturePreview() {
        mCapturePreview.setImageBitmap(mPicture);
        mPreviewRequested = true;
        mRoundedOverlay.open();
    }

    /**
     * hide capture preview
     */
    private void hideCapturePreview() {
        mPreviewRequested = false;
        mCapturePreview.setVisibility(View.GONE);
    }

    /**
     * used to hide additional content
     */
    private boolean hideAdditionalContent() {
        //hide save button if shown
        if (mSaveButton.getVisibility() == View.VISIBLE) {
            hideSavingButton();
        }

        //hide only if visible and animation not already running
        if (mPreviewRequested && mCapturePreview.getAnimation() != mAnimationOut) {
            mCapturePreview.startAnimation(mAnimationOut);
            return true;
        } else if (mCameraInstructions.getVisibility() == View.VISIBLE
                && mCameraInstructions.getAnimation() != mAnimationOut) {
            mCameraInstructions.startAnimation(mAnimationOut);
            return true;
        }
        return false;
    }

    /**
     * build wiggle animation
     */
    private void buildWiggleAnimation() {
        mWiggleAnimation = AnimationUtils.loadAnimation(this, R.anim.wiggle);
    }

    /**
     * set up saving button used to save screen capture
     */
    private void setUpSavingButton() {
        mSaveButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSaveButtonParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

        //set up button
        mSaveButton = new ImageButton(this);
        mSaveButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_content_save_white));
        mSaveButton.setBackgroundResource(R.drawable.support_card);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Compress the bitmap before saving and capture_preview
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                mPicture.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);
                new SaveAsyncTask().execute(bytes.toByteArray());

            }
        });
        mSaveButton.setVisibility(View.INVISIBLE);
    }

    /**
     * display save button used to save screen capture
     */
    private void displaySavingButton() {
        //display button
        if (mAnimationInFromLeft != null && mSaveButton.getAnimation() == null) {
            mSaveButton.setVisibility(View.VISIBLE);
            mSaveButton.startAnimation(mAnimationInFromLeft);
        }
    }

    /**
     * hide saved button
     */
    private void hideSavingButton() {
        if (mAnimationOutFromLeft != null) {
            mSaveButton.startAnimation(mAnimationOutFromLeft);
        }
    }

    /**
     * Async task used to configure and start camera
     */
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
                cameraParameters.setPictureSize(bestSize.width, bestSize.height);
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
                    mPreview.addView(mRoundedOverlay);
                    mPreview.addView(mCameraInstructions);
                    mPreview.addView(mCapturePreview, mCapturePreviewParams);
                    mPreview.addView(mSaveButton, mSaveButtonParams);
                    mPreview.setOnTouchListener(MainActivity.this);
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

    /**
     * async task used to build screen capture from Camera.takePicture
     */
    private class CaptureAsyncTask extends AsyncTask<byte[], Void, Void> {
        @Override
        protected Void doInBackground(byte[]... params) {
            // Recover picture from camera
            byte[] data = params[0];
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            // Process picture for mirror effect
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            mPicture = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();

            //retrieve drawing cache from pet view
            mGooglyPetView.setDrawingCacheEnabled(true);
            Bitmap pet = mGooglyPetView.getDrawingCache();

            if (pet != null) {
                //evaluate visible height due to translation
                final int visibleHeight = pet.getHeight() - (int) mGooglyPetView.getTranslationY();

                //build drawing source boundaries
                final Rect srcRect = new Rect(0, 0, pet.getWidth(), visibleHeight);

                //evaluate destination height and width according to source ration
                final float destHeight = visibleHeight / (float) mPreview.getHeight() * bitmap.getHeight();
                final float destWidth = mGooglyPetView.getWidth() / (float) mPreview.getWidth() * bitmap.getWidth();

                //evaluate destination rectangle
                final float destBottom = mGooglyPetView.getBottom() / (float) mPreview.getHeight() * bitmap.getHeight();
                final float destRight = mGooglyPetView.getRight() / (float) mPreview.getWidth() * bitmap.getWidth();
                final float destTop = destBottom - destHeight;
                final float destLeft = destRight - destWidth;

                //build drawing destination boundaries
                final RectF destRect = new RectF(destLeft, destTop, destRight, destBottom);

                //draw pet on picture
                Canvas c = new Canvas(mPicture);
                Paint paint = new Paint();
                c.drawBitmap(pet, srcRect, destRect, paint);
                mGooglyPetView.setDrawingCacheEnabled(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);

            //display capture preview after capture built
            displayCapturePreview();
        }
    }

    /**
     * async task used to save screen capture into media center
     */
    private class SaveAsyncTask extends AsyncTask<byte[], Void, Uri> {
        @Override
        protected Uri doInBackground(byte[]... params) {
            // Recover picture from camera
            byte[] data = params[0];

            final Uri uriToShare = writeScreenBytesToExternalStorage(data);
            return uriToShare;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            if (uri != null) {
                // Add the screen to the Media Provider's database.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(uri);
                sendBroadcast(mediaScanIntent);

                //inform user
                makeToast(R.string.screen_capture);
            }

            //disable saving options
            hideSavingButton();

            //free memory
            mPicture = null;
        }
    }
}

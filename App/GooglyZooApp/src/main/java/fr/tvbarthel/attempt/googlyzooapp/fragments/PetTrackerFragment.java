package fr.tvbarthel.attempt.googlyzooapp.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import fr.tvbarthel.attempt.googlyzooapp.FacePreviewDetection;
import fr.tvbarthel.attempt.googlyzooapp.R;
import fr.tvbarthel.attempt.googlyzooapp.listener.GooglyPetListener;
import fr.tvbarthel.attempt.googlyzooapp.listener.SmoothFaceDetectionListener;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPet;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetFactory;
import fr.tvbarthel.attempt.googlyzooapp.ui.GooglyPetView;
import fr.tvbarthel.attempt.googlyzooapp.utils.CameraUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.FaceDetectionUtils;

/**
 * Fragment used to display pet tracking your face
 */
public class PetTrackerFragment extends Fragment {

    /**
     * Log cat
     */
    private static final String TAG = PetTrackerFragment.class.getName();

    /**
     * bundle key to set pet saved in shared preferences
     */
    public static final String BUNDLE_KEY_GOOGLY_PET = "bundle_key_for_googly_pet";

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
     * Smooth listener
     */
    private SmoothFaceDetectionListener mSmoothFaceDetectionListener;

    /**
     * An {@link android.os.AsyncTask} used to opened the camera.
     */
    private CameraAsyncTask mCameraAsyncTask;

    /**
     * animation used when pet isn't awake and capture is requested
     */
    private Animation mWiggleAnimation;

    /**
     * Bitmap used to capture pet view before taking a picture
     * <p/>
     * On some device, takePicture stop faceDetection
     */
    private Bitmap mPetCapture;

    /**
     * callback used to used to capture picture
     */
    private Camera.PictureCallback mPictureCallback;

    /**
     * animation used to show real time preview
     */
    private Animation mIn;

    /**
     * animation used to hide real time preview
     */
    private Animation mOut;

    /**
     * dummy callback used when fragment is not attached
     */
    private static Callbacks sDummyCallabacks = new Callbacks() {
        @Override
        public void onPreviewReady(FrameLayout preview) {

        }

        @Override
        public void onPreviewReleased(FrameLayout preview) {

        }

        @Override
        public void onFaceDetectionUnsupported() {

        }

        @Override
        public void onPictureTaken(Bitmap picture) {

        }
    };

    /**
     * current callback object
     */
    private Callbacks mCallbacks = sDummyCallabacks;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieved pet saved in shared preferences passed throw arguments
        final int prefPet = getArguments().getInt(BUNDLE_KEY_GOOGLY_PET);

        //init model
        mGooglyPet = GooglyPetFactory.createGooglyPet(prefPet, getActivity());

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

        //set up params for googlyPetView
        mPetParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPetParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        mSmoothFaceDetectionListener = new SmoothFaceDetectionListener() {
            @Override
            public void onSmoothFaceDetection(float[] smoothFacePosition) {
                //get relative position based on camera preview dimension and current rotation
                final float[] relativePosition = FaceDetectionUtils.getRelativeHeadPosition(
                        smoothFacePosition, mFaceDetectionPreview, mCurrentRotation);
                mGooglyPetView.animatePetEyes(relativePosition);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pet_tracker, container, false);

        mPreview = (FrameLayout) rootView.findViewById(R.id.fragment_pet_tracker_preview);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Hosting aactivity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onResume() {
        super.onResume();

        //register listener on googly pet
        if (mGooglyPet != null) {
            mGooglyPet.addListener(mGooglyPetListener);
        }

        //create view to display pet
        mGooglyPetView = new GooglyPetView(getActivity(), mGooglyPet);

        //start camera in background for avoiding black screen
        mCameraAsyncTask = new CameraAsyncTask();
        mCameraAsyncTask.execute();
    }

    @Override
    public void onPause() {
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallabacks;
    }

    /**
     * set googly pet
     *
     * @param googlyPet googly pet id
     */
    public void setGooglyPet(int googlyPet) {
        //remove listener from the old pet
        if (mGooglyPet != null) {
            mGooglyPet.removeListener(mGooglyPetListener);
        }

        //create the selected Googly pet
        mGooglyPet = GooglyPetFactory.createGooglyPet(googlyPet, getActivity());

        //register listener on the new pet
        mGooglyPet.addListener(mGooglyPetListener);

        if (mGooglyPetView != null) {
            mGooglyPetView.setModel(mGooglyPet);
        }
    }

    /**
     * used to take a picture of the user
     */
    public void captureScreenShot() {
        if (mGooglyPet.isAwake()) {
            if (mPictureCallback == null) {
                initCameraPictureCallback();
            }

            //retrieve drawing cache from pet view before takePicture
            //since takePicture seems to stop faceDetection on some device
            mGooglyPetView.setDrawingCacheEnabled(true);
            mPetCapture = mGooglyPetView.getDrawingCache(true);

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
     * show real time preview
     */
    public void showPreview() {
        if (mIn == null) {
            //retrieve animation is not loaded yet
            mIn = AnimationUtils.loadAnimation(getActivity(), R.anim.in);
        }

        //animate preview as well as googly pet view
        mFaceDetectionPreview.setAnimation(mIn);
        mGooglyPetView.setAnimation(mIn);
        mFaceDetectionPreview.setVisibility(View.VISIBLE);
        mGooglyPetView.setVisibility(View.VISIBLE);
        mIn.start();

    }


    /**
     * used to adapt camera preview to the current device orientation
     *
     * @param camera
     */
    private void setCameraDisplayOrientation(android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int degrees = 0;
        mCurrentRotation = ((WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
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

                //restart face detection
                mCamera.startFaceDetection();

                //build screen shot
                new CaptureAsyncTask().execute(data);
            }
        };
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
            mPreview.removeView(mFaceDetectionPreview);
            mCallbacks.onPreviewReleased(mPreview);
        }
    }

    /**
     * release the camera for the other app
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * build wiggle animation
     */
    private void buildWiggleAnimation() {
        mWiggleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.wiggle);
    }

    /**
     * used to check orientation
     *
     * @return
     */
    private boolean isPortrait() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * Async task used to configure and start camera
     */
    private class CameraAsyncTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected Camera doInBackground(Void... params) {
            Camera camera = getCameraInstance();
            if (camera == null) {
                getActivity().finish();
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
                    getActivity().finish();
                } else {
                    //set up face detection preview
                    mFaceDetectionPreview = new FacePreviewDetection(
                            getActivity(),
                            mCamera,
                            new FacePreviewDetection.FacePreviewDetectionCallback() {
                                @Override
                                public void onFaceDetectionStart(boolean supported) {
                                    if (!supported) {
                                        mCallbacks.onFaceDetectionUnsupported();
                                    }
                                }
                            }
                    );
                    //add camera preview
                    mPreview.addView(mFaceDetectionPreview, mPreviewParams);

                    //add pet view
                    mPreview.addView(mGooglyPetView, mPetParams);

                    //inform preview is ready
                    mCallbacks.onPreviewReady(mPreview);

                    //set face detection listener for face tracking
                    mCamera.setFaceDetectionListener(mSmoothFaceDetectionListener);

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
    private class CaptureAsyncTask extends AsyncTask<byte[], Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            final Bitmap picture;

            // Recover picture from camera
            byte[] data = params[0];
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            // Process picture for mirror effect
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            picture = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();

            if (mPetCapture != null) {
                //evaluate visible height due to translation
                final int visibleHeight = mPetCapture.getHeight() - (int) mGooglyPetView.getTranslationY();

                //build drawing source boundaries
                final Rect srcRect = new Rect(0, 0, mPetCapture.getWidth(), visibleHeight);

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
                Canvas c = new Canvas(picture);
                Paint paint = new Paint();
                c.drawBitmap(mPetCapture, srcRect, destRect, paint);
                mGooglyPetView.setDrawingCacheEnabled(false);

                //release bitmap
                mPetCapture.recycle();
            }

            return picture;
        }

        @Override
        protected void onPostExecute(final Bitmap picture) {
            super.onPostExecute(picture);

            if (mOut == null) {
                //retrieve out animation if not loaded yet
                mOut = AnimationUtils.loadAnimation(getActivity(), R.anim.out);
            }

            mOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //send build picture to the calling activity
                    mFaceDetectionPreview.setVisibility(View.GONE);
                    mGooglyPetView.setVisibility(View.GONE);
                    mCallbacks.onPictureTaken(picture);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //hide preview as well as googly pet view
            mFaceDetectionPreview.setAnimation(mOut);
            mGooglyPetView.setAnimation(mOut);
            mOut.start();
        }
    }

    public interface Callbacks {

        /**
         * called when preview as been initialized. Allow to add child to this preview.
         * <p/>
         * Take care to remove theses children when preview is released.
         *
         * @param preview frame layout used to host camera preview
         */
        public void onPreviewReady(FrameLayout preview);

        /**
         * called when preview has been released. Added children must be removed here.
         *
         * @param preview frame layout used to host camera preview
         */
        public void onPreviewReleased(FrameLayout preview);

        /**
         * called when camera face detection isn't support by the device
         */
        public void onFaceDetectionUnsupported();

        /**
         * called when a picture has been build
         *
         * @param picture googlyPetView merged in camera picture
         */
        public void onPictureTaken(Bitmap picture);
    }

}

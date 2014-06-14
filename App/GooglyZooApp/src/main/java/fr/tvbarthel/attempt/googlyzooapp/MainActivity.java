package fr.tvbarthel.attempt.googlyzooapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.view.View;
import android.view.ViewGroup;
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
import fr.tvbarthel.attempt.googlyzooapp.fragments.PetTrackerFragment;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetEntry;
import fr.tvbarthel.attempt.googlyzooapp.ui.RoundedOverlay;
import fr.tvbarthel.attempt.googlyzooapp.utils.GooglyPetUtils;
import fr.tvbarthel.attempt.googlyzooapp.utils.SharedPreferencesUtils;

public class MainActivity extends DonateCheckActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnTouchListener, PetTrackerFragment.Callbacks {
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
     * id of the current pet
     */
    private int mSelectedGooglyPet;

    /**
     * Used to display icon from selected pet
     */
    private int mActionBarIcon;

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
     * animation used to show saving button
     */
    private Animation mAnimationInFromRight;

    /**
     * animation used to hide saving button
     */
    private Animation mAnimationOutFromRight;

    /**
     * animation used to show close button
     */
    private Animation mAnimationInFromBottom;

    /**
     * animation used to hide close button
     */
    private Animation mAnimationOutFromBottom;

    /**
     * last touch timestamp use to detected double touch
     */
    private long mLastTouchTimeStamp;

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
     * layout params for saved button
     */
    private FrameLayout.LayoutParams mSaveButtonParams;

    /**
     * button used to save screen capture
     */
    private ImageButton mSaveButton;

    /**
     * layout params for sharing button
     */
    private FrameLayout.LayoutParams mShareButtonParams;

    /**
     * button used to share screen capture
     */
    private ImageButton mShareButton;

    /**
     * layout params for sharing button
     */
    private FrameLayout.LayoutParams mCloseButtonParams;

    /**
     * button used to share screen capture
     */
    private ImageButton mCloseButton;

    /**
     * boolean used to know if preview has been requested
     */
    private boolean mPreviewRequested;

    /**
     * use temp file since apps such as Hangout delete file used as EXTRA_STREAM
     */
    private Uri mTempFileForSharing;

    /**
     * screen capture byte
     */
    private Bitmap mPicture;

    /**
     * fragment responsible for head tracking and googlyPet rendering
     */
    private PetTrackerFragment mPetTrackerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get last pet used
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSelectedGooglyPet = sp.getInt(SharedPreferencesUtils.PREF_USER_GOOGLY_PET_SELECTED,
                GooglyPetUtils.GOOGLY_PET_ZEBRA);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = new SpannableString(getTitle());
        mActionBarIcon = R.drawable.ic_launcher;

        mNavigationDrawerFragment.selectEntry(mSelectedGooglyPet);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

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

        //set up sharing button
        setUpSharingButton();

        //set up close button
        setUpCloseButton();

        mLastTouchTimeStamp = 0;

        mPetTrackerFragment = new PetTrackerFragment();

        //set saved pet in shared preferences as arguments
        final Bundle data = new Bundle();
        data.putInt(PetTrackerFragment.BUNDLE_KEY_GOOGLY_PET, mSelectedGooglyPet);
        mPetTrackerFragment.setArguments(data);

        //display fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mPetTrackerFragment)
                .commit();
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

        if (mPetTrackerFragment != null) {
            mPetTrackerFragment.setGooglyPet(mSelectedGooglyPet);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCameraInstructions.getVisibility() == View.VISIBLE) {

                    //can't perform a screen if instruction are displayed
                    hideInstructions();

                } else if (isDoubleTouch(event) && !mPreviewRequested) {

                    //capture preview requested
                    mPreviewRequested = true;

                    //process event to throw double touch
                    mPetTrackerFragment.captureScreenShot();
                }
                break;
        }
        return false;
    }

    @Override
    public void onPreviewReady(FrameLayout preview) {
        preview.addView(mRoundedOverlay);
        preview.addView(mCameraInstructions);
        preview.addView(mCapturePreview, mCapturePreviewParams);
        preview.addView(mSaveButton, mSaveButtonParams);
        preview.addView(mShareButton, mShareButtonParams);
        preview.addView(mCloseButton, mCloseButtonParams);
        preview.setOnTouchListener(MainActivity.this);
    }

    @Override
    public void onPreviewReleased(FrameLayout preview) {
        preview.removeView(mRoundedOverlay);
        preview.removeView(mCameraInstructions);
        preview.removeView(mCapturePreview);
        preview.removeView(mSaveButton);
        preview.removeView(mShareButton);
        preview.removeView(mCloseButton);
    }

    @Override
    public void onFaceDetectionUnsupported() {
        makeToast(R.string.face_detection_not_support);
    }

    @Override
    public void onPictureTaken(Bitmap picture) {
        mPicture = Bitmap.createBitmap(picture);
        picture.recycle();

        //display captured picture
        displayCapturePreview();
    }

    @Override
    public void onBackPressed() {
        if (!hideAdditionalContent()) {
            super.onBackPressed();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mActionBarIcon);
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

        mRoundedOverlay.setOnCloseListener(new RoundedOverlay.CloseListener() {
            @Override
            public void onClose() {
                if (mPreviewRequested) {

                    //show live preview
                    mPetTrackerFragment.showPreview();

                    mPreviewRequested = false;
                }
            }
        });
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
                        displaySharingButton();
                        displayCloseButton();
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
                        //hide capture preview
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

        mAnimationInFromRight = AnimationUtils.loadAnimation(this, R.anim.in_from_right);

        mAnimationOutFromRight = AnimationUtils.loadAnimation(this, R.anim.out_from_right);
        if (mAnimationOutFromRight != null) {
            mAnimationOutFromRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mShareButton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        mAnimationInFromBottom = AnimationUtils.loadAnimation(this, R.anim.in_from_bottom);

        mAnimationOutFromBottom = AnimationUtils.loadAnimation(this, R.anim.out_from_bottom);
        if (mAnimationOutFromBottom != null) {
            mAnimationOutFromBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCloseButton.setVisibility(View.INVISIBLE);
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
        mCapturePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mCapturePreviewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCapturePreviewParams.gravity = Gravity.CENTER;
        mCapturePreview.setVisibility(View.GONE);
        mPreviewRequested = false;
    }

    /**
     * display capture preview
     */
    private void displayCapturePreview() {
        mCapturePreview.setImageBitmap(mPicture);
        mRoundedOverlay.open();
    }

    /**
     * hide capture preview
     */
    private void hideCapturePreview() {
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

        //hide sharing button if shown
        if (mShareButton.getVisibility() == View.VISIBLE) {
            hideSharingButton();
        }

        //hide close button if shown
        if (mCloseButton.getVisibility() == View.VISIBLE) {
            hideCloseButton();
        }

        //hide only if visible and animation not already running
        if (mPreviewRequested && mCapturePreview.getAnimation() != mAnimationOut) {
            mCapturePreview.startAnimation(mAnimationOut);

            //reset and free memory
            mPicture = null;

            //delete temp file use for sharing
            if (mTempFileForSharing != null) {
                File temp = new File(mTempFileForSharing.getPath());
                temp.delete();
                mTempFileForSharing = null;
            }
            return true;
        } else {
            return hideInstructions();
        }
    }

    /**
     * hide camera instructions
     *
     * @return true if instruction has been hidden
     */
    private boolean hideInstructions() {
        if (mCameraInstructions.getVisibility() == View.VISIBLE
                && mCameraInstructions.getAnimation() != mAnimationOut) {
            mCameraInstructions.startAnimation(mAnimationOut);
            return true;
        }
        return false;
    }

    /**
     * set up sharing button used to share screen capture
     */
    private void setUpSharingButton() {
        mShareButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mShareButtonParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        //set up button
        mShareButton = new ImageButton(this);
        mShareButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_social_share_white));
        mShareButton.setBackgroundResource(R.drawable.rounded_left_corners);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAsyncTask().execute();
            }
        });
        mShareButton.setVisibility(View.INVISIBLE);
    }

    /**
     * set up close button used to hide capture preview
     */
    private void setUpCloseButton() {
        mCloseButtonParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCloseButtonParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

        //set up button
        mCloseButton = new ImageButton(this);
        mCloseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        mCloseButton.setBackgroundResource(R.drawable.rounded_top_corners);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAdditionalContent();
            }
        });
        mCloseButton.setVisibility(View.INVISIBLE);

    }

    /**
     * display sharing button used to share screen capture
     */
    private void displaySharingButton() {
        //display button
        if (mAnimationInFromRight != null && mShareButton.getAnimation() == null) {
            mShareButton.setVisibility(View.VISIBLE);
            mShareButton.startAnimation(mAnimationInFromRight);
        }
    }

    /**
     * hide sharing button
     */
    private void hideSharingButton() {
        if (mAnimationOutFromRight != null) {
            mShareButton.startAnimation(mAnimationOutFromRight);
        }
    }

    /**
     * display close button used to hide screen capture
     */
    private void displayCloseButton() {
        //display button
        if (mAnimationInFromBottom != null && mCloseButton.getAnimation() == null) {
            mCloseButton.setVisibility(View.VISIBLE);
            mCloseButton.startAnimation(mAnimationInFromBottom);
        }
    }

    /**
     * hide close button
     */
    private void hideCloseButton() {
        if (mAnimationOutFromBottom != null) {
            mCloseButton.startAnimation(mAnimationOutFromBottom);
        }
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
        mSaveButton.setBackgroundResource(R.drawable.rounded_right_corners);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveAsyncTask().execute();

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
     * async task used to save screen capture into media center
     */
    private class SaveAsyncTask extends AsyncTask<Void, Void, Uri> {
        @Override
        protected Uri doInBackground(Void... params) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            mPicture.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);

            return writeScreenBytesToExternalStorage(bytes.toByteArray());
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

            //hide saving options
            hideSavingButton();
        }
    }

    private class ShareAsyncTask extends AsyncTask<Void, Void, Uri> {

        @Override
        protected Uri doInBackground(Void... params) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            mPicture.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);
            mTempFileForSharing = writeScreenBytesToExternalStorage(bytes.toByteArray());
            return mTempFileForSharing;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if (uri != null) {
                // Share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(shareIntent);
            }
        }
    }
}

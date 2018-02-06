package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.gihub.varunj.sangoshthi_gallery.Adapters.TopicsListAdapter;
import io.gihub.varunj.sangoshthi_gallery.BuildConfig;
import io.gihub.varunj.sangoshthi_gallery.R;
import io.gihub.varunj.sangoshthi_gallery.utils.CommonUtils;

public class TopicsListActivity extends AppCompatActivity implements HoldingButtonLayoutListener {

    private static String userName, userPhoneNum, userEmail, userLogPath, userRecordingPath, userRecordingPathTemp, userRecordingPathFinal1, userRecordingPathFinal2, userRecordingPathFinal3;
    private Boolean userLoggedIn;
    private ArrayList<String> topicsList = new ArrayList<>();
    public static ArrayList<String> logsToDropbox = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

//    location stuff
    private static final String TAG = TopicsListActivity.class.getSimpleName();
    //    Code used in requesting runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    //    Constant used in the location settings dialog.
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    //    The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    //    The fastest rate for active location updates. Exact. Updates will never be more frequent  than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    //    Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    //    Provides access to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;
    //    Provides access to the Location Settings API.
    private SettingsClient mSettingsClient;
    //    Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    //    Stores the types of location services the client is interested in using. Used for checking settings to determine if the device has optimal location settings.
    private LocationSettingsRequest mLocationSettingsRequest;
    //    Callback for Location events.
    private LocationCallback mLocationCallback;
    //    Represents a geographical location.
    private android.location.Location mCurrentLocation;
    //    Tracks the status of the location updates request. Value changes when the user presses the Start Updates and Stop Updates buttons.
    private Boolean mRequestingLocationUpdates;
    //    Time when the location was updated represented as a String.
    private String mLastUpdateTime;

// recording stuff
    private static final DateFormat mFormatter = new SimpleDateFormat("mm:ss:SS");
    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;
    private static final long TIME_INVALIDATION_FREQUENCY = 50L;
    private HoldingButtonLayout mHoldingButtonLayout;
    private TextView mTime;
    private EditText mInput;
    private View mSlideToCancel;
    private int mAnimationDuration;
    private ViewPropertyAnimator mTimeAnimator;
    private ViewPropertyAnimator mSlideToCancelAnimator;
    private ViewPropertyAnimator mInputAnimator;
    private long mStartTime;
    private Runnable mTimerRunnable;

    private MediaRecorder mediaRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics_list);

        // read cached user data
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = pref.getString("name", "defaultName");
        userPhoneNum = pref.getString("phoneNum", "0000000000");
        userLoggedIn = pref.getBoolean("isLoggedIn", false);
        userEmail = pref.getString("googleEmail", "defaultEmail");
        userLogPath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_logs_path) + userPhoneNum + ".txt/";
        userRecordingPath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + userPhoneNum + ".mp4/";
        userRecordingPathTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + userPhoneNum + "_temp.mp4/";
        userRecordingPathFinal1 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + userPhoneNum + "_final1.ts";
        userRecordingPathFinal2 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + userPhoneNum + "_final2.ts";
        userRecordingPathFinal3 = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_recording_path) + userPhoneNum + "_final3.mp4";

        Log.d("System.out", "xxx: user creden: " + userName + "   " + userPhoneNum + "   " + userEmail + "   " + userLoggedIn);

        // log
        TopicsListActivity.addToLog("app_open", "");

        // fetch list of topics authorised to access
        topicsList.clear();
        String pathAccessFile = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.dropbox_access_path);
        File directoryAccess = new File(pathAccessFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(directoryAccess));
            String line;
            String[] splitt;
            while ((line = br.readLine()) != null) {
                splitt = line.split(",");
                if (splitt[0].equals(userPhoneNum)) {
                    for (String eachString : splitt) {
                        topicsList.add(eachString);
                    }
                    topicsList.remove(userPhoneNum);
                }
            }
            br.close();
        } catch (IOException e) {
            Toast.makeText(TopicsListActivity.this, getString(R.string.toast_access_file), Toast.LENGTH_LONG).show();
        }
        Log.d("System.out", "xxx: auth topics: " + topicsList);

        // create recycler view for listing topics
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_topics);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicsListAdapter(this, topicsList);
        mRecyclerView.setAdapter(mAdapter);


        // loacation stuff
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Kick off the process of building the LocationCallback, LocationRequest, and LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();         // xxx: needed???


        // recorder stuff
        mHoldingButtonLayout = (HoldingButtonLayout) findViewById(R.id.input_holderr);
        mHoldingButtonLayout.addListener(this);
        mTime = (TextView) findViewById(R.id.time);
        mInput = (EditText) findViewById(R.id.input);
        mSlideToCancel = findViewById(R.id.slide_to_cancel);
        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        updateUI();             // xxx: needed???
    }

//    @Override
//    protected void onPause() {
//        // Remove location updates to save battery.
//        stopLocationUpdates();
//        TopicsListActivity.addToLog("app_close", "");
//        transferToDropbox();
//        logsToDropbox.clear();
//        super.onPause();
//    }

    @Override
    public void onBackPressed() {
        // Remove location updates to save battery.
        stopLocationUpdates();
        TopicsListActivity.addToLog("app_close", "");
        transferToDropbox();
        logsToDropbox.clear();
        super.onBackPressed();
    }

//    @Override
//    public void onStop() {
//        // Remove location updates to save battery.
//        stopLocationUpdates();
//        TopicsListActivity.addToLog("app_close", "");
//        transferToDropbox();
//        logsToDropbox.clear();
//        super.onStop();
//    }

    public static void addToLog(String action, String fileName) {
        if (fileName.length() > 0) {
            String[] splitt = fileName.split("/");
            fileName = splitt[splitt.length - 2] + "/" + splitt[splitt.length - 1];
        }
        logsToDropbox.add(new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(new Date()) + "," + fileName + "," + action + "\n");
        Log.d("System.out", "xxx: logs length: " + logsToDropbox.size());
    }

    public static void transferToDropbox() {
        File file = new File(userLogPath);
        if (file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                for (String x : logsToDropbox) {
                    writer.append(x);
                }
                writer.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //    --------------------------------------

    @Override
    public void onBeforeExpand() {
        cancelAllAnimations();

        mSlideToCancel.setTranslationX(0f);
        mSlideToCancel.setAlpha(0f);
        mSlideToCancel.setVisibility(View.VISIBLE);
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.start();

        mInputAnimator = mInput.animate().alpha(0f).setDuration(mAnimationDuration);
        mInputAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInput.setVisibility(View.INVISIBLE);
                mInputAnimator.setListener(null);
            }
        });
        mInputAnimator.start();
        mTime.setTranslationY(mTime.getHeight());
        mTime.setAlpha(0f);
        mTime.setVisibility(View.VISIBLE);
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
        mTimeAnimator.start();

    }

    @Override
    public void onExpand() {
        startAudioRecording(userRecordingPathTemp);
        mStartTime = System.currentTimeMillis();
        invalidateTimer();
    }

    @Override
    public void onBeforeCollapse() {
        cancelAllAnimations();

        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideToCancel.setVisibility(View.INVISIBLE);
                mSlideToCancelAnimator.setListener(null);
            }
        });
        mSlideToCancelAnimator.start();

        mInput.setAlpha(0f);
        mInput.setVisibility(View.VISIBLE);
        mInputAnimator = mInput.animate().alpha(1f).setDuration(mAnimationDuration);
        mInputAnimator.start();

        mTimeAnimator = mTime.animate().translationY(mTime.getHeight()).alpha(0f).setDuration(mAnimationDuration);
        mTimeAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTime.setVisibility(View.INVISIBLE);
                mTimeAnimator.setListener(null);
            }
        });
        mTimeAnimator.start();
    }

    @Override
    public void onCollapse(boolean isCancel) {
        stopTimer();
        if (isCancel) {
            stopAudioRecordingAndDelete(userRecordingPathTemp);
            Toast.makeText(this, "Recording canceled! " + getFormattedTime() + " s", Toast.LENGTH_SHORT).show();
        } else {
            stopAudioRecording();
            String lenRecording = getFormattedTime();
            Toast.makeText(this, "Recording submitted! " + lenRecording + " s", Toast.LENGTH_SHORT).show();
            joinTwoRecordings(userRecordingPathTemp, userRecordingPath);
            Toast.makeText(this, "Stitched!", Toast.LENGTH_SHORT).show();
            TopicsListActivity.addToLog("record:" + lenRecording,"");
        }
    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }

    private void invalidateTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mTime.setText(getFormattedTime());
                invalidateTimer();
            }
        };

        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY);
    }

    private void stopTimer() {
        if (mTimerRunnable != null) {
            mTime.getHandler().removeCallbacks(mTimerRunnable);
        }
    }

    private void cancelAllAnimations() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

        if (mTimeAnimator != null) {
            mTimeAnimator.cancel();
        }
    }

    private String getFormattedTime() {
        long  diff = System.currentTimeMillis() - mStartTime;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        diff = diff % daysInMilli;
        diff = diff % hoursInMilli;
        long elapsedMinutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;
        long elapsedSeconds = diff / secondsInMilli;

        return elapsedMinutes + ":" + elapsedSeconds;
    }

    /**
     * This method will start the voice recording
     * @param voiceStoragePath
     *                      String to the path where the file will be stored
     */
    public void startAudioRecording(String voiceStoragePath) {
        if(mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(voiceStoragePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "Not able to prepare mediaRecorder: " + e);
            mediaRecorder.stop();
            mediaRecorder.release();
        }

    }

    /**
     * This function will stop voice recording and release the resources
     */
    public void stopAudioRecording() {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d(TAG, "xxx: recorder stopped");
        }
    }

    /**
     * This function will stop voice recording and release the resources and delete the temp file made.
     */
    public void stopAudioRecordingAndDelete(String voiceStoragePath) {
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            File file = new File(voiceStoragePath);
            file.delete();
            Log.d(TAG, "xxx: recorder stopped and file deleted" + mediaRecorder);
        }
    }

    /**
     * This function will concat two mp3
     */
    public void joinTwoRecordings(String recTempPath, String recMainPath) {
        CommonUtils.getInstance().loadFFMPEGBinary(this);   // xxx:here or in create?

        recMainPath = recMainPath.substring(0,recMainPath.length()-1);
        recTempPath = recTempPath.substring(0,recTempPath.length()-1);

        String[] join_command = {"-y", "-i", recMainPath, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", userRecordingPathFinal1};
        execFFmpegBinary(join_command);

        String[] join_command2 = {"-y", "-i", recTempPath, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", userRecordingPathFinal2};
        execFFmpegBinary(join_command2);

        String concatt = "concat:" + userRecordingPathFinal1 + "|" + userRecordingPathFinal2;
        String[] join_command3 = {"-f", "mpegts", "-i", concatt, "-c", "copy", "-bsf:a", "aac_adtstoasc", userRecordingPathFinal3};
        execFFmpegBinary(join_command3);
    }

//    --------------------------------------
    /**
     * Updates fields based on data stored in the bundle. @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }


    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(TopicsListActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                mRequestingLocationUpdates = false;
                        }

                        updateUI();
                    }
                });
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        updateLocationUI();
    }



    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            TopicsListActivity.addToLog("location:" + mCurrentLocation.getLatitude() + "*" + mCurrentLocation.getLongitude(),"");
            Log.d("System.out", "xxx: location: " + mCurrentLocation.getLatitude() + ":" + mCurrentLocation.getLongitude());
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }



    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(TopicsListActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(TopicsListActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * This will execute the ffmpeg command
     *
     * @param command
     */
    private void execFFmpegBinary(final String[] command) {
        if(CommonUtils.getInstance().ffmpeg != null) {
            try {

                CommonUtils.getInstance().ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onFailure(String s) {
                        Log.d(TAG, "xxxFAILED with output : " + s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "xxxSUCCESS with output : " + s);
                    }

                    @Override
                    public void onProgress(String s) {
                        Log.d(TAG, "xxxprogress : " + s);
                    }

                    @Override
                    public void onStart() {
                        Log.d(TAG, "xxxStarted command");
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "xxxFinished command: ");
//                        File file1 = new File(userRecordingPathFinal1);
//                        if(file1.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files1: "+ file1.delete());
//                        }
//                        File file2 = new File(userRecordingPathFinal2);
//                        if(file2.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files2: "+ file2.delete());
//                        }
//                        File file3 = new File(userRecordingPathTemp);
//                        if(file3.exists()) {
//                            Log.d(TAG, "xxxdeleting intermediate files3: "+ file3.delete());
//                        }
                        File file4 = new File(userRecordingPath);
                        if(file4.exists()) {
                            Log.d(TAG, "xxxdeleting intermediate files4: "+ file4.delete());
                        }
                        File file5 = new File(userRecordingPathFinal3);
                        File file6 = new File(userRecordingPath);
                        if(file5.exists()) {
                            Log.d(TAG, "xxxdeleting intermediate files5: "+ file5.renameTo(file6));
                        }
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
                Toast.makeText(this, "xxxFFMPEG Already running", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "xxxFFMPEG not loaded", Toast.LENGTH_SHORT).show();
        }
    }

}

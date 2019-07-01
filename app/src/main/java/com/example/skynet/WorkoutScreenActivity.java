package com.example.skynet;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lf.skynet_service.ResultCode;
import com.lf.skynet_service.SkynetServiceCallbacks;
import com.lf.skynet_service.SkynetServiceError;
import com.lf.skynet_service.SkynetServiceWrapper;
import com.lf.skynet_service.ShutdownReason;
import com.lf.skynet_service.Units;
import com.lf.skynet_service.WorkoutState;
import com.lf.skynet_service.WorkoutStreamData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//import org.apache.commons.io.IOUtils;


public class WorkoutScreenActivity extends AppCompatActivity implements SkynetServiceCallbacks{

    public static final String TAG = "WorkoutScreenActivity";

    private Button workout_proceed, cancel_btn;
    private TextView time, distance, calories, heart_rate, speed, incline, current_workout;
    private TextView skynetServiceStatusText;
    private boolean isStarted = false;
    private ValuesFormatter mValuesFormatter;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private boolean mSkynetServiceConnected;
    private boolean mReceivedShutdownRequest = false;
    private SkynetServiceWrapper mSkynetService;
    private long mSendWorkoutXmlToken = 0L;
    private static final long SUMMARY_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    private static ArrayList<Integer> HEART_RATE = new ArrayList<>();
    private static double AVERAGE_HEART_RATE;
    public static int rawAssetId = 0;


    private void StartWorkout() {
        Log.d("debug", "StartWorkout()");
        if (mSendWorkoutXmlToken != 0) {
            return;
        }
        WorkoutState workoutState = WorkoutStateDao.getInstance().getWorkoutState();
        if (workoutState != WorkoutState.IDLE_WAITING_FOR_WORKOUT) {
            return;
        }

        if (rawAssetId != 0) {
            Log.d("rawAssetID", Integer.toString(rawAssetId));
            InputStream is = null;
            Reader reader = null;
            StringBuilder sb = new StringBuilder(1024);

            try {
                is = getResources().openRawResource(rawAssetId);
                reader = new InputStreamReader(is, StandardCharsets.UTF_8);

                int readResult;
                while ((readResult = reader.read()) >= 0) {
                    sb.append((char)readResult);
                }

                String workoutXmlString = sb.toString();

                Log.d("Workout XML screen", workoutXmlString);
                mSendWorkoutXmlToken = mSkynetService.sendWorkoutXml(workoutXmlString);
                if (mSendWorkoutXmlToken != 0) {
                    setSelectWorkoutButtonStates(true);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error parsing raw workout XML.", ioe);
            } finally {
//                    IOUtils.closeQuietly(reader);
//                    IOUtils.closeQuietly(is);
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Error! No workouts selected" + workoutState.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSelectWorkoutButtonStates(boolean enabled) {
        Log.d("debug", "setSelectWorkoutButtonStates");

        findViewById(R.id.workoutscreen_proceed).setEnabled(enabled);
        findViewById(R.id.workoutscreen_proceed).setActivated(enabled);
        findViewById(R.id.workout_screen_cancel).setEnabled(enabled);
        findViewById(R.id.workout_screen_cancel).setActivated(enabled);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStarted = false;
        WorkoutStateDao.getInstance().unregisterCallback(mWorkoutStateCallback);
    }

    @Override
    public void onShutdownRequest(SkynetServiceWrapper skynetServiceWrapper, ShutdownReason shutdownReason) {
        Log.d("debug", "onShutdownRequest");
        this.mReceivedShutdownRequest = true;

        if (mSkynetServiceConnected) {
            mSkynetService.disconnect();
        } else {
            finish();
        }
    }

    @Override
    public void onSetWorkoutSpeedResult(SkynetServiceWrapper skynetServiceWrapper, long l, ResultCode resultCode) {
        Log.d("debug", "onSetWorkoutSpeedResult");
        Log.d("workout speed code: ", resultCode.toString());
    }

    @Override
    public void onSetUserInformationResult(SkynetServiceWrapper skynetServiceWrapper, long l, ResultCode resultCode) {
        Log.d("debug", "onSetUserInformationResult");
        Log.d("User Information code: ", resultCode.toString());
    }

    @Override
    public void onSendWorkoutXmlResult(SkynetServiceWrapper skynetServiceWrapper, long l, ResultCode resultCode) {
        Log.d("debug", "onSendWorkoutXmlResult");
        if (l == mSendWorkoutXmlToken) {
            Log.i(TAG, "Result from sendWorkoutXml: " + resultCode);
            mSendWorkoutXmlToken = 0;
            if (resultCode != ResultCode.SUCCESS) {
                Toast.makeText(this, "Could not load workout. Reason:" + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final Runnable mSummaryTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("debug", "mSummaryTimeoutRunnable");
            mSkynetService.resetToIdle();
        }
    };

    private final View.OnClickListener mConnectSkynetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "mConnectSkynetOnClickListener");
            skynetServiceStatusText.setText(R.string.otf_service_status_connecting);
            mSkynetService.connect(WorkoutScreenActivity.this);
            skynetServiceStatusText.setOnClickListener(null);
        }
    };

    @Override
    public void onConnected(SkynetServiceWrapper skynetServiceWrapper) {
        Log.d("debug", "onConnected");
        Log.e(TAG, "SkynetServiceWrapper onConnected");
        mSkynetServiceConnected = true;

        skynetServiceStatusText.setText(R.string.otf_service_status_connected);
        skynetServiceStatusText.setOnClickListener(mDisconnectSkynetOnClickListener);

        Units units = mSkynetService.getUnits();
        Log.d(TAG, "Got Units:" + units);
        mValuesFormatter.setUnits(units);
        WorkoutState workoutState = mSkynetService.getWorkoutState();
        WorkoutStateDao.getInstance().setWorkoutState(workoutState);
        setSelectWorkoutButtonStates(workoutState == WorkoutState.IDLE_WAITING_FOR_WORKOUT);

       // Toast.makeText(this, "Connected to Treadmill, Current workout State: " + workoutState.toString(), Toast.LENGTH_SHORT).show();
        StartWorkout();
    }

    private int mDisconnectSkynetServiceGestureCount = 0;

    private final Runnable mDisconnectSkynetServiceGestureTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("debug", "mDisconnectSkynetServiceGestureTimeoutRunnable");
            mDisconnectSkynetServiceGestureCount = 0;
        }
    };

    private final View.OnClickListener mDisconnectSkynetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "mDisconnectSkynetOnClickListener");
            sHandler.removeCallbacks(mDisconnectSkynetServiceGestureTimeoutRunnable);

            if (mDisconnectSkynetServiceGestureCount >= 2) {
                mDisconnectSkynetServiceGestureCount = 0;
                mSkynetService.disconnect();
            } else {
                mDisconnectSkynetServiceGestureCount++;
                sHandler.postDelayed(mDisconnectSkynetServiceGestureTimeoutRunnable, 400);
            }
        }
    };

    @Override
    public void onConnectionError(SkynetServiceWrapper skynetServiceWrapper, SkynetServiceError skynetServiceError) {
        Log.d("debug", "onConnectionError");
        mSkynetServiceConnected = false;
        Log.e(TAG, "SkynetServiceWrapper onConnectionError:" + skynetServiceError);

        skynetServiceStatusText.setText(getString(R.string.otf_service_status_error, String.valueOf(skynetServiceError)));
        skynetServiceStatusText.setOnClickListener(mConnectSkynetOnClickListener);
        WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.UNKNOWN);
        setSelectWorkoutButtonStates(false);
        if (skynetServiceError == SkynetServiceError.UNEXPECTED_UNBIND) {
            finish();
        }
    }

    @Override
    public void onDisconnected(SkynetServiceWrapper skynetServiceWrapper) {
        Log.d("debug", "onDisconnected");
        mSkynetServiceConnected = false;
        skynetServiceStatusText.setText(R.string.otf_service_status_disconnected);
        skynetServiceStatusText.setOnClickListener(mConnectSkynetOnClickListener);
        WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.UNKNOWN);
        if (mReceivedShutdownRequest) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.UNKNOWN);
        super.onDestroy();

    }

    @Override
    public void onWorkoutStateChanged(SkynetServiceWrapper skynetServiceWrapper, WorkoutState workoutState, WorkoutState workoutState1) {
        Log.d("debug", "onWorkoutStateChanged");
        WorkoutStateDao.getInstance().setWorkoutState(workoutState);
        setSelectWorkoutButtonStates(workoutState == WorkoutState.IDLE_WAITING_FOR_WORKOUT);
       // Toast.makeText(this, "Current WOrkout State: " + workoutState.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWorkoutStream(SkynetServiceWrapper skynetServiceWrapper, WorkoutStreamData workoutStreamData) {
        Log.d("debug", "onWorkoutStream");
        WorkoutStateDao.getInstance().updateWorkoutStream(workoutStreamData);
    }

    private void processWorkoutStream(WorkoutStreamData workoutStream, Integer antPlusHeartRate){
        Log.d("debug", "processWorkoutStream");
        if (workoutStream != null) {
            time.setText(mValuesFormatter.getTimeString(workoutStream.getTimeInSeconds()));
            calories.setText(mValuesFormatter.getCaloriesString(workoutStream.getCalories()));
            distance.setText(mValuesFormatter.getDistanceString(workoutStream.getDistanceKm()));
            speed.setText(mValuesFormatter.getSpeedString(workoutStream.getSpeedKPH()));
            incline.setText(mValuesFormatter.getInclineString(workoutStream.getInclineInPercent()));
            heart_rate.setText(mValuesFormatter.getHeartRateString(antPlusHeartRate == null ? workoutStream.getHeartRate() : antPlusHeartRate));
        } else {
            time.setText(mValuesFormatter.getTimeString(null));
            calories.setText(mValuesFormatter.getCaloriesString(null));
            distance.setText(mValuesFormatter.getDistanceString(null));
            speed.setText(mValuesFormatter.getSpeedString(null));
            incline.setText(mValuesFormatter.getInclineString(null));
            heart_rate.setText(mValuesFormatter.getHeartRateString(null));
        }

        if (workoutStream != null) {
            HEART_RATE.add(Integer.parseInt(mValuesFormatter.getHeartRateString(antPlusHeartRate == null ? workoutStream.getHeartRate() : antPlusHeartRate)));
        }

    }

    private void processWorkoutState(final WorkoutState workoutState) {
        Log.d("debug", "processWorkoutState");
        ((TextView) findViewById(R.id.workout_state)).setText("Workout State : " + WorkoutStateDao.getInstance().getWorkoutStateString());

        if (workoutState == WorkoutState.WORKOUT_SUMMARY)
        {
            calculateAverageHeartRate();
            sHandler.postDelayed(mSummaryTimeoutRunnable, SUMMARY_TIMEOUT);
        }
    }

    private final WorkoutStateDao.Callback mWorkoutStateCallback = new WorkoutStateDao.Callback() {
        @Override
        public void onWorkoutStateChanged(WorkoutState workoutState) {
            Log.d("debug", "onWorkoutStateChanged");
            processWorkoutState(workoutState);
        }

        @Override
        public void onWorkoutStreamUpdated(WorkoutStreamData workoutStream, Integer antPlusHeartRate) {
            Log.d("debug", "onWorkoutStreamUpdated");
            processWorkoutStream(workoutStream, antPlusHeartRate);

            if (workoutStream != null) {
                HEART_RATE.add(Integer.parseInt(mValuesFormatter.getHeartRateString(antPlusHeartRate == null ? workoutStream.getHeartRate() : antPlusHeartRate)));
            }
        }
    };

    @Override
    protected void onStart() {
        Log.d("debug", "onStart");
        super.onStart();

        isStarted = true;

        WorkoutStateDao.getInstance().registerCallback(mWorkoutStateCallback);
        processWorkoutStream(WorkoutStateDao.getInstance().getLatestWorkoutStream(), WorkoutStateDao.getInstance().getAntPlusHeartRate());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_screen);

        mValuesFormatter = new ValuesFormatter(this);
        mReceivedShutdownRequest = false;
        mSkynetService = SkynetServiceWrapper.getInstance(this);
        mSkynetService.connect(WorkoutScreenActivity.this);

        time = findViewById(R.id.Time_val);
        distance = findViewById(R.id.Distance_val);
        calories = findViewById(R.id.Calories_val);
        heart_rate = findViewById(R.id.heart_rate_val);
        speed = findViewById(R.id.speed_val);
        incline = findViewById(R.id.incline_val);
        current_workout = findViewById(R.id.current_workout_txtview);

        if (rawAssetId != 0) {
            switch (rawAssetId) {
                case 2131623936:
                    // Log.d("wtf_1", Integer.toString(rec[i]));
                    current_workout.setText("Belt it Out");
                    break;
                case 2131623937:
                    // Log.d("wtf_2", Integer.toString(rec[i]));
                    current_workout.setText("Burn 30");
                    break;
                case 2131623940:
                    // Log.d("wtf_3", Integer.toString(rec[i]));
                    current_workout.setText("Distance Workout");
                    break;
                case 2131623941:
                    //  Log.d("wtf_4", Integer.toString(rec[i]));
                    current_workout.setText("5K Trainer");
                    break;
                case 2131623942:
                    //Log.d("wtf_5", Integer.toString(rec[i]));
                    current_workout.setText("In in for the Long Run");
                    break;
                case 2131623943:
                    //Log.d("wtf_6", Integer.toString(rec[i]));
                    current_workout.setText("Reverse Treadmill Workout");
                case 2131623945:
                    //  Log.d("wtf_7", Integer.toString(rec[i]));
                    current_workout.setText("Runners High");
                    break;
                case 2131623946:
                    //  Log.d("wtf_8", Integer.toString(rec[i]));
                    current_workout.setText("Simple Hill Sprints");
                    break;
                case 2131623947:
                    // Log.d("wtf_9", Integer.toString(rec[i]));
                    current_workout.setText("Time Interval");
                    break;
                case 2131623948:
                    // Log.d("wtf_10", Integer.toString(rec[i]));
                    current_workout.setText("Torch");
                    break;
                case 2131623949:
                    //  Log.d("wtf_11", Integer.toString(rec[i]));
                    current_workout.setText("Treadmill Turmoil");
                    break;
                case 2131623950:
                    //  Log.d("wtf_12", Integer.toString(rec[i]));
                    current_workout.setText("Walk Jog Run");
                    break;
            }
        }

        workout_proceed = (Button) findViewById(R.id.workoutscreen_proceed);
        workout_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.RESETTING_TO_IDLE);
                Intent to_explore = new Intent(WorkoutScreenActivity.this, ExploreActivity.class);
                mSkynetService.disconnect();
                startActivity(to_explore);
            }
        });

        this.skynetServiceStatusText = (TextView) findViewById(R.id.otf_state);
        cancel_btn = findViewById(R.id.workout_screen_cancel);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSkynetService.disconnect();
                WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.RESETTING_TO_IDLE);
                Intent gg = new Intent(WorkoutScreenActivity.this, LoginActivity.class);
                startActivity(gg);
            }
        });



        if (!mSkynetServiceConnected) {
            this.skynetServiceStatusText.setText(R.string.otf_service_status_connecting);
            try {
                mSkynetService.connect(this);
            } catch (SecurityException e) {
                Log.e(TAG, "Security Exception connecting to SkynetService.", e);
                this.skynetServiceStatusText.setText(R.string.otf_service_status_error);
                this.skynetServiceStatusText.setOnClickListener(mConnectSkynetOnClickListener);
            }
        } else {
            this.skynetServiceStatusText.setText(R.string.otf_service_status_connected);
        }


    }

    public void calculateAverageHeartRate() {
        float sum = 0;
        for (Integer a : HEART_RATE) {
            if (sum == 0) {
                sum = a;
            }
            sum += a;
            AVERAGE_HEART_RATE = sum / HEART_RATE.size();
        }
        //Toast.makeText(WorkoutScreenActivity.this, "avg heart rate updated by: " + WorkoutStateDao.getInstance().getWorkoutStateString() + Double.toString(AVERAGE_HEART_RATE), Toast.LENGTH_SHORT).show();
    }
}

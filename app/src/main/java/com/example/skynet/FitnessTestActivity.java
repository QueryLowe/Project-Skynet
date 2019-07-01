package com.example.skynet;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lf.skynet_service.ResultCode;
import com.lf.skynet_service.ShutdownReason;
import com.lf.skynet_service.SkynetServiceCallbacks;
import com.lf.skynet_service.SkynetServiceError;
import com.lf.skynet_service.SkynetServiceWrapper;
import com.lf.skynet_service.Units;
import com.lf.skynet_service.WorkoutState;
import com.lf.skynet_service.WorkoutStreamData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.example.skynet.LoginActivity.User_Recommendations;
import static com.example.skynet.LoginActivity.User_Test_Result;
import static com.example.skynet.LoginActivity.Users_Finished_Test;
import static com.example.skynet.LoginActivity.user;



public class FitnessTestActivity extends AppCompatActivity implements SkynetServiceCallbacks {

    private TextView testInfo, testName;

    public static final String TAG = "FitnessTestActivity";
    private ValuesFormatter mValuesFormatter;
    private boolean mReceivedShutdownRequest;
    private SkynetServiceWrapper mSkynetService;
    private TextView skynetServiceStatusText;
    private boolean mSkynetServiceConnected;
    private int mDisconnectSkynetServiceGestureCount = 0;
    private long mSendWorkoutXmlToken = 0L;
    private Button start_test;
    public static int COOL_DOWN_TIME = 10000; //default to 5 secs, for testing purposes

    // fitness test params from UserInfoActivity
    public static int ROCKPORT_USER_GENDER;
    public static double ROCKPORT_USER_WEIGHT;
    public static int ROCKPORT_USER_AGE;
    // fitness test params from treadmill output
    private static double COOPER_DISTANCE_IN_MILES;
    private static int ROCKPORT_TIME;
    private static ArrayList<Integer> ROCKPORT_HEART_RATE = new ArrayList<>();
    private static double ROCKPORT_AVERAGE_HEART_RATE;

    private boolean isStarted = false;

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static final long SUMMARY_TIMEOUT = TimeUnit.SECONDS.toMillis(5);

    private static int rawAssetId = 0;

    public static String iJetURL = "http://ijetlab.com/api/v1/lifefitness/login/";
    private static boolean isConnectionExecuted = false;

    private View.OnClickListener mSendWorkoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "SendWorkoutOnClickListener");
            if (mSendWorkoutXmlToken != 0) {
                Log.d("fitness test clicked: ", "mSendWorkoutXmlToken != 0");
                return;
            }
            final WorkoutState workoutState = WorkoutStateDao.getInstance().getWorkoutState();
            if (workoutState != WorkoutState.IDLE_WAITING_FOR_WORKOUT) {
                return;
            }

            if (v.getId() == R.id.start_test) {
                Log.d("fitness test clicked: ", "R.id.start_test");
                if (user.USER_AGE <= 50) {
                   // Toast.makeText(getApplicationContext(),"Cooper Test",Toast.LENGTH_SHORT).show();
                    rawAssetId = R.raw.cooper_fitness_test;
                } else {
                  //  Toast.makeText(getApplicationContext(),"Rockport Test",Toast.LENGTH_SHORT).show();
                    rawAssetId = R.raw.rockport_fitness_test;
                }
            }

            if (rawAssetId != 0) {
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

                    mSendWorkoutXmlToken = mSkynetService.sendWorkoutXml(workoutXmlString);
                    Log.d("Workout XML fit test", workoutXmlString);

                    if (mSendWorkoutXmlToken != 0) {
                        setSelectWorkoutButtonStates(true);
                    }

                } catch (IOException ioe) {
                    Log.e(TAG, "Error parsing raw workout XML.", ioe);
                } finally {
//                    IOUtils.closeQuietly(reader);
//                    IOUtils.closeQuietly(is);
                    try {
                        assert reader != null;
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

                switch (rawAssetId) {
                    case R.raw.cooper_fitness_test:
                        COOL_DOWN_TIME = 5000;
                        break;
                    case R.raw.rockport_fitness_test:
                        COOL_DOWN_TIME = 3000;
                        break;
                }

                final Handler cooldown_handler = new Handler();
                if (WorkoutStateDao.getInstance().getWorkoutState() != WorkoutState.IDLE_WAITING_FOR_WORKOUT ||
                        WorkoutStateDao.getInstance().getWorkoutState() != WorkoutState.RESETTING_TO_IDLE) {
                    WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.RESETTING_TO_IDLE);
                    cooldown_handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Cool-down has Started. Please relax and walk slowly",Toast.LENGTH_SHORT).show();
//                            JSONObject test_result = new JSONObject();
//                            try {
//                                test_result = buildJsonObject();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            String server_response = "";
//                            try {
//                                server_response = httpPost(test_result, "http://ijetlab.com/api/v1/lifefitness/login/");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            Log.d("response: ", server_response);
//                            Toast.makeText(FitnessTestActivity.this, "JSON: " + test_result.toString() + "Server Response: " + (server_response.equals("")  ? "null" : server_response), Toast.LENGTH_LONG).show();
                        }
                    }, COOL_DOWN_TIME + 5000 ); // add cooldown time afterward

                }


            }
        }
    };


//    private void setWorkoutTime(boolean enabled, int selectedId) {
//        Log.d("debug", "setSelectWorkoutButtonStates");
//        for (int id : SELECT_WORKOUT_XMLS) {
//            id.
//            Button button = (Button)findViewById(id);
//            button.setEnabled(enabled);
//            button.setActivated(selectedId == id && enabled);
//        }
//    }

    private static final int[] SELECT_WORKOUT_XMLS = {
            R.raw.belt_it_out,
            R.raw.burn_30,
            R.raw.distance_workout,
            R.raw.five_k_trainer,
            R.raw.in_it_for_the_long_run,
            R.raw.reverse_treadmill_workout,
            R.raw.runners_high,
            R.raw.simple_hill_sprints,
            R.raw.time_interval,
            R.raw.torch,
            R.raw.treadmill_turmoil,
            R.raw.walk_jog_run
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_test);

//        WorkoutState workoutState = mSkynetService.getWorkoutState();
//        WorkoutStateDao.getInstance().setWorkoutState(workoutState);

//        Units units = mSkynetService.getUnits();
//        mValuesFormatter.setUnits(units);

        testInfo = findViewById(R.id.test_info);
        testName = findViewById(R.id.test_name);
        start_test = findViewById(R.id.start_test);
        start_test.setOnClickListener(mSendWorkoutOnClickListener);

        setSelectWorkoutButtonStates(true);

        if (user.USER_AGE <= 50) {
            testName.setText(" Cooper Test");
            testInfo.setText("   ");
        } else {
            testName.setText("Rockport Test");
            testInfo.setText("Please hold on to the heart rate monitors during the test's run");
        }
//
//        Button developers_magic_button = findViewById(R.id.devs_magic_button);
//
//        developers_magic_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // mSkynetService.disconnect();
//                Intent req = new Intent(FitnessTestActivity.this, SessionWorkoutActivity.class);
//                mSkynetService.disconnect();
//                startActivity(req);
//            }
//        });

        Button cancel = findViewById(R.id.fitness_test_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.RESETTING_TO_IDLE);
                mSkynetService.disconnect();
                finish();
            }
        });


        mValuesFormatter = new ValuesFormatter(this);

        this.skynetServiceStatusText = (TextView) findViewById(R.id.otf_state_Fitness_Test);

//        for (int id : SELECT_WORKOUT_BUTTON_IDS) {
//            View view = findViewById(id);
//            view.setOnClickListener(mSendWorkoutOnClickListener)
//        }

        mReceivedShutdownRequest = false;

        mSkynetService = SkynetServiceWrapper.getInstance(this);
        this.skynetServiceStatusText.setText(R.string.otf_service_status_connecting);
        try {
            mSkynetService.connect(this);
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception connecting to SkynetService.", e);
            this.skynetServiceStatusText.setText(R.string.otf_service_status_error);
            this.skynetServiceStatusText.setOnClickListener(mConnectSkynetOnClickListener);
        }
    }

    private final View.OnClickListener mConnectSkynetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "mConnectSkynetOnClickListener");
            skynetServiceStatusText.setText(R.string.otf_service_status_connecting);
            mSkynetService.connect(FitnessTestActivity.this);
            skynetServiceStatusText.setOnClickListener(null);
        }
    };

    private void setSelectWorkoutButtonStates(boolean enabled) {
        Log.d("debug", "setSelectWorkoutButtonStates");
        findViewById(R.id.fitness_test_cancel).setEnabled(enabled);
        findViewById(R.id.fitness_test_cancel).setActivated(enabled);
        findViewById(R.id.start_test).setEnabled(enabled);
        findViewById(R.id.start_test).setActivated(enabled);
    }

    @Override
    public void onConnected(SkynetServiceWrapper skynetServiceWrapper) {
        Log.e(TAG, "SkynetServiceWrapper onConnected");
        mSkynetServiceConnected = true;
        skynetServiceStatusText.setText(R.string.otf_service_status_connected);
        skynetServiceStatusText.setOnClickListener(mDisconnectSkynetOnClickListener);

        WorkoutState workoutState = mSkynetService.getWorkoutState();
        WorkoutStateDao.getInstance().setWorkoutState(workoutState);

       // Toast.makeText(this, "Connected to Treadmill, Current workout State: " + workoutState.toString(), Toast.LENGTH_SHORT).show();

        setSelectWorkoutButtonStates(workoutState == WorkoutState.IDLE_WAITING_FOR_WORKOUT);

    }

    @Override
    public void onConnectionError(SkynetServiceWrapper skynetServiceWrapper, SkynetServiceError skynetServiceError) {
        Log.d("debug", "onConnectionError");
        mSkynetServiceConnected = false;
        Log.e(TAG, "SkynetServiceWrapper onConnectionError:" + skynetServiceError);

        skynetServiceStatusText.setText(getString(R.string.otf_service_status_error, String.valueOf(skynetServiceError)));
        skynetServiceStatusText.setOnClickListener(mConnectSkynetOnClickListener);
        WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.UNKNOWN);
        if (skynetServiceError == SkynetServiceError.UNEXPECTED_UNBIND) {
            finish();
        }
        setSelectWorkoutButtonStates(false);
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
    public void onWorkoutStateChanged(SkynetServiceWrapper skynetServiceWrapper, WorkoutState workoutState, WorkoutState workoutState1) {
        Log.d("debug", "onWorkoutStateChanged");
        WorkoutStateDao.getInstance().setWorkoutState(workoutState);
        setSelectWorkoutButtonStates(workoutState == WorkoutState.IDLE_WAITING_FOR_WORKOUT);
       // Toast.makeText(this, "Current WOrkout State: " + workoutState.toString(), Toast.LENGTH_SHORT).show();

        if (rawAssetId == R.raw.rockport_fitness_test) {

            calculateAverageHeartRate();
        }
    }

    @Override
    public void onWorkoutStream(SkynetServiceWrapper skynetServiceWrapper, WorkoutStreamData workoutStreamData) {
        Log.d("debug", "onWorkoutStream");
        WorkoutStateDao.getInstance().updateWorkoutStream(workoutStreamData);
        if (rawAssetId == R.raw.rockport_fitness_test) {

            calculateAverageHeartRate();
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        isStarted = false;
        user.is_first_time_user = false;
        LoginActivity.user_not_complete_test_flag = false;
        if (rawAssetId == R.raw.rockport_fitness_test) {

            calculateAverageHeartRate();
        }

        WorkoutStateDao.getInstance().unregisterCallback(mWorkoutStateCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();

        isStarted = true;
        LoginActivity.user_not_complete_test_flag = false;
        user.is_first_time_user = false;
        WorkoutStateDao.getInstance().registerCallback(mWorkoutStateCallback);
        processWorkoutStream(WorkoutStateDao.getInstance().getLatestWorkoutStream(), WorkoutStateDao.getInstance().getAntPlusHeartRate());
    }

    private final WorkoutStateDao.Callback mWorkoutStateCallback = new WorkoutStateDao.Callback() {
        @Override
        public void onWorkoutStateChanged(WorkoutState workoutState) {
            Log.d("debug", "onWorkoutStateChanged");
            processWorkoutState(workoutState);

            if (rawAssetId == R.raw.rockport_fitness_test) {

                calculateAverageHeartRate();
            }

        }

        @Override
        public void onWorkoutStreamUpdated(WorkoutStreamData workoutStream, Integer antPlusHeartRate) {
            Log.d("debug", "onWorkoutStreamUpdated");
            processWorkoutStream(workoutStream, antPlusHeartRate);

            if (rawAssetId == R.raw.rockport_fitness_test  && workoutStream != null) {
                ROCKPORT_HEART_RATE.add(Integer.parseInt(mValuesFormatter.getHeartRateString(antPlusHeartRate == null ? workoutStream.getHeartRate() : antPlusHeartRate)));
                calculateAverageHeartRate();
            }
        }
    };

    private void processWorkoutStream(WorkoutStreamData workoutStream, Integer antPlusHeartRate){
        Log.d("debug", "processWorkoutStream");
        if (workoutStream != null) {
            ROCKPORT_TIME = workoutStream.getTimeInSeconds();
            COOPER_DISTANCE_IN_MILES = workoutStream.getDistanceKm() * 0.621371;
        } else {
            ROCKPORT_TIME = 0;
            COOPER_DISTANCE_IN_MILES = 0;
        }
        if (rawAssetId == R.raw.rockport_fitness_test && workoutStream != null) {
            ROCKPORT_HEART_RATE.add(Integer.parseInt(mValuesFormatter.getHeartRateString(antPlusHeartRate == null ? workoutStream.getHeartRate() : antPlusHeartRate)));
            calculateAverageHeartRate();
        }
    }

    private void processWorkoutState(final WorkoutState workoutState) {
        Log.d("debug", "processWorkoutState");

        if (workoutState == WorkoutState.WORKOUT_SUMMARY)
        {
            sHandler.postDelayed(mSummaryTimeoutRunnable, SUMMARY_TIMEOUT);

            write_user_fitness_test_status(user.USER_NAME);

            final Handler start_test_handler = new Handler();
            if (LoginActivity.user_not_complete_test_flag == false && (WorkoutStateDao.getInstance().getWorkoutState() != WorkoutState.IDLE_WAITING_FOR_WORKOUT ||
                    WorkoutStateDao.getInstance().getWorkoutState() != WorkoutState.RESETTING_TO_IDLE)) {
                WorkoutStateDao.getInstance().setWorkoutState(WorkoutState.RESETTING_TO_IDLE);
                start_test_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject test_result = new JSONObject();
                        try {
                            test_result = buildJsonObject();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String server_response = "";
                        try {
                            server_response = httpPost(test_result, "http://ijetlab.com/api/v1/lifefitness/login/");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("response: ", server_response);
                        // Toast.makeText(FitnessTestActivity.this, "JSON: " + test_result.toString() + "Server Response: " + (server_response.equals("")  ? "null" : server_response), Toast.LENGTH_LONG).show();

                        user.is_first_time_user = false;

                        mSkynetService.disconnect();


                        Intent req = new Intent(FitnessTestActivity.this, SessionWorkoutActivity.class);
                        startActivity(req);

                    }
                }, COOL_DOWN_TIME ); // add cooldown time afterward
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

    private final Runnable mDisconnectSkynetServiceGestureTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("debug", "mDisconnectSkynetServiceGestureTimeoutRunnable");
            mDisconnectSkynetServiceGestureCount = 0;
        }
    };

    private static String httpPost(JSONObject send, String URL) throws IOException {
        final String[] value = new String[1];
        final String sendURL= URL;
        final JSONObject send1= send;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    String result = "";
                    //HttpClient httpClient = HttpClientBuilder.create().build();
                    URL url = new URL(sendURL);
                    // 1. create HttpURLConnection
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //HttpPost conn = new HttpPost(myUrl);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json"); //maybe no utf-8
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    try {conn.connect();}
                    catch (Exception e) {}
//                    //conn.addHeader("Content-Type", "application/json");
//                    // 2. build JSON object
//                    //String test = new String("{\"action\": \"register\", \"param\":{\"age\": " + age.getText().toString() + ", \"height\": " + height.getText().toString() + ", \"weight\": " + weight.getText().toString() + ", \"currentFitnessLevel\": " + UserRequirementActivity.TIME_PER_WEEK + ", \"desiredFitnessLevel\": " + UserRequirementActivity.DESIRED_TIME + "}}");
//                    JSONObject random= new JSONObject();
//                    try{random = buildJsonObject();}
//                    catch (Exception e){}
//                    Log.d("JsonOb: ", send1.toString());
                    //conn.setEntity(test);
                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = send1.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    catch (Exception e) { }

                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                        result= response.toString();
                    }
                    catch (Exception e) { }

                    value[0]=result;

                    // 3. add JSON content to POST request body
                    //setPostRequestContent(conn, jsonObject);
                    //HttpResponse response = httpClient.execute(conn);
                    // 4. make POST request to the given URL
                    //conn.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {thread.join();}
        catch (Exception e) {};


        // 5. return response message
        //return response.toString()+"";
        return value[0] +"";
    }

    private static JSONObject buildJsonObject() throws JSONException {
        JSONObject postData = new JSONObject();
        JSONObject m = new JSONObject();

        if (rawAssetId == R.raw.cooper_fitness_test) {
            m.put("distance", COOPER_DISTANCE_IN_MILES);
        } else if (rawAssetId == R.raw.rockport_fitness_test){
            m.put("age", ROCKPORT_USER_AGE);
            m.put("weight", ROCKPORT_USER_WEIGHT);
            m.put("gender",ROCKPORT_USER_GENDER);
            m.put("elapsed time", ROCKPORT_TIME);
            m.put("heart rate", ROCKPORT_AVERAGE_HEART_RATE);
        } else {
            m.put("Error", "no fitness test selected");
        }

        postData.put("action", "User Fitness Test Results");
        postData.put("param", m);

        Log.d("Fitness Result JSON: ", postData.toString());

        return postData;
    }

    public void calculateAverageHeartRate() {
        float sum = 0;
        for (Integer a : ROCKPORT_HEART_RATE) {
            if (sum == 0) {
                sum = a;
            }
            sum += a;
            ROCKPORT_AVERAGE_HEART_RATE = sum / ROCKPORT_HEART_RATE.size();
        }
        //Toast.makeText(FitnessTestActivity.this, "avg heart rate updated by: " + WorkoutStateDao.getInstance().getWorkoutStateString() + Double.toString(ROCKPORT_AVERAGE_HEART_RATE), Toast.LENGTH_SHORT).show();
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(UserInfoActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    private void write_user_fitness_test_status(String UserName) {
        JSONObject js = new JSONObject();
        // get json object of test result
        try {
            js = buildJsonObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // record the users finishing the test
        try {
            FileWriter writer = new FileWriter(Users_Finished_Test,true);
            if (!Users_Finished_Test.toString().contains(" ** " + UserName + " ** ")) {
                writer.append(" ** ");
                writer.append(UserName + " ** ");
                writer.flush();
                writer.close();
//                Toast.makeText(FitnessTestActivity.this, Users_Finished_Test.toString(), Toast.LENGTH_SHORT).show();
//                Log.d("WTF 2", Users_Finished_Test.toString());
                Toast.makeText(FitnessTestActivity.this, "User Fitness Test Status Recorded", Toast.LENGTH_SHORT).show();

            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // log the test result
        if (js != null) {
            try {
                FileWriter writer2 = new FileWriter(User_Test_Result,true);
                writer2.append(UserName + " $$ " + "\n");
                writer2.append(js.toString() + " ** " + "\n\n");
                writer2.flush();
                writer2.close();
                Toast.makeText(FitnessTestActivity.this, "User Fitness Test Result Recorded", Toast.LENGTH_SHORT).show();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static int[] getRecommendations() {
        Random rand = new Random();
        int[] recommendations = new int[12];

        ArrayList<Integer> XML_list = new ArrayList<>();
        for (int i: SELECT_WORKOUT_XMLS) {
            XML_list.add(i);
        }
        Collections.shuffle(XML_list);
        for (int i = 0; i < 12; i++) {
            recommendations[i] = XML_list.get(i);
        }

        for (int i = 0; i < 3; i++) {
            user.RECOMMENDED_WORKOUT_3.add(recommendations[i]);
        }
        for (int i = 3; i < 12; i++) {
            user.RECOMMENDED_WORKOUT_9.add(recommendations[i]);
        }


        return recommendations;
    }
}


package com.example.skynet;
import com.lf.skynet_service.SkynetServiceCallbacks;
import com.lf.skynet_service.WorkoutStreamData;
import com.lf.skynet_service.SkynetServiceWrapper;
import com.lf.skynet_service.WorkoutState;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.example.skynet.LoginActivity.User_Info;
import static com.example.skynet.LoginActivity.user;

public class UserInfoActivity extends AppCompatActivity {

//    private class SendUserDetails extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String data = "";
//
//            HttpURLConnection httpURLConnection = null;
//            try {
//
//                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
//                httpURLConnection.setRequestMethod("POST");
//
//                httpURLConnection.setDoOutput(true);
//
//                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes("PostData=" + params[1]);
//                wr.flush();
//                wr.close();
//
//                InputStream in = httpURLConnection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(in);
//
//                int inputStreamData = inputStreamReader.read();
//                while (inputStreamData != -1) {
//                    char current = (char) inputStreamData;
//                    inputStreamData = inputStreamReader.read();
//                    data += current;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (httpURLConnection != null) {
//                    httpURLConnection.disconnect();
//                }
//            }
//
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
//        }
//    }

    private String[] items, items2, items3;
    static EditText height;
    static EditText weight;
    static EditText age;

    static Spinner gender, fit_level, goal;

    private boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Button confirm_button = (Button) findViewById(R.id.btn_confirm);
        Button cancel = findViewById(R.id.userinfo_cancel);

        // Toast.makeText(UserInfoActivity.this, Arrays.toString(LoginActivity.dummy_existing_users), Toast.LENGTH_LONG).show();
        if (user == null) {
            User user = new User();
        }

        // setup the spinners
        items = new String[]{"Male", "Female"};
        gender = (Spinner) findViewById(R.id.user_gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);

        //temporarily name goals to goal 1, 2, 3
        items2 = new String[] { "0", "1", "2", "3", "4", "5", "6", "7"};
        fit_level = (Spinner) findViewById(R.id.user_current);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        fit_level.setAdapter(adapter2);

        items3 = new String[] { "1", "2", "3", "4", "5", "6", "7"};
        goal = (Spinner) findViewById(R.id.user_desire);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items3);
        goal.setAdapter(adapter3);

        height = (EditText) findViewById(R.id.editText_height);
        weight = (EditText) findViewById(R.id.editText_weight);
        age = (EditText) findViewById(R.id.editText_age);


        confirm_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String age_text = age.getText().toString();
                String height_text = height.getText().toString();
                String weight_text = weight.getText().toString();

                if (age_text.equals("") || height_text.equals("") || weight_text.equals("")) {
                    Toast.makeText(UserInfoActivity.this, "Please fill in your information", Toast.LENGTH_SHORT).show();
                } else {
                    user.USER_AGE = Integer.parseInt(age_text);
                    FitnessTestActivity.ROCKPORT_USER_AGE = user.USER_AGE;

                    user.USER_HEIGHT = Integer.parseInt(height_text);

                    user.USER_WEIGHT = Double.parseDouble(weight_text);
                    FitnessTestActivity.ROCKPORT_USER_WEIGHT = user.USER_WEIGHT;

                    user.USER_GENDER = gender.getSelectedItem().toString();
                    switch (user.USER_GENDER) {
                        case "Male":
                            FitnessTestActivity.ROCKPORT_USER_GENDER = 1;
                        case "Female":
                            FitnessTestActivity.ROCKPORT_USER_GENDER = 0;
                    }

                    user.current_workout_level = fit_level.getSelectedItem().toString();
                    user.desired_workout_level = goal.getSelectedItem().toString();
                    String res= "";
                    //JSONObject random2= new JSONObject();
                    // try{random2 = buildJsonObject();}
                    //catch (Exception e){}
                    //String test = random2.toString();

                    JSONObject obj= new JSONObject();
                    try {
                        obj = buildJsonObject();
                    } catch (Exception e){ }
                    try {
                        res = httpPost(obj, "http://ijetlab.com/api/v1/lifefitness/login/");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("response: ", res);

                    // record user info in local database
                    store_user_info(user.USER_NAME, user.USER_AGE, user.USER_WEIGHT, user.USER_HEIGHT, user.USER_GENDER, user.current_workout_level, user.desired_workout_level);

                    // go to fitness test activity
                    Intent MainIntent = new Intent(UserInfoActivity.this, FitnessTestActivity.class);
                    startActivity(MainIntent);
                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gg = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(gg);
            }
        });

    }

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


    //private static class HTTPAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            // params comes from the execute() call: params[0] is the url.
//            try {
//                try {
//                    return httpPost(urls[0]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return "Error!";
//                }
//            } catch (Exception e) {
//                return "Unable to retrieve web page. URL may be invalid.";
//            }
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Log.d("result: ", result);
//        }
//    }


//    public void send(View view) {
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
//        // perform HTTP POST request
//        new HTTPAsyncTask().execute("https://ijetlab.com/api/v1/lifefitness/login/");
//    }

    private static JSONObject buildJsonObject() throws JSONException {
          JSONObject postData = new JSONObject();
          JSONObject m = new JSONObject();
          String number = age.getText().toString();

        m.put("age", Integer.parseInt(number));
        m.put("height", height.getText().toString());
        m.put("weight", weight.getText().toString());
        if(gender.getSelectedItem().toString() == "Male")
        m.put("gender","m");
        else
            m.put("gender","f");

        m.put("currentFitnessLevel", fit_level.getSelectedItem().toString());
        m.put("desiredFitnessLevel", goal.getSelectedItem().toString());

        postData.put("action", "register");
        postData.put("param", m);

        Log.i("JSON: ", postData.toString());

        return postData;
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

    private void store_user_info(String UserName, int age, double weight, double height, String gender, String current_lvl, String desired_lvl) {
        try {
            FileWriter writer = new FileWriter(User_Info,true);
            writer.append("UserName: " + UserName + " ** " + "\n");
            writer.append("Age: " + age + " ** " + "\n");
            writer.append("Weight: " + weight + " ** " + "\n");
            writer.append("Height: " + height + " ** " + "\n");
            writer.append("Gender: " + gender + " ** " + "\n");
            writer.append("Current times of workout per week: " + current_lvl + " ** " + "\n");
            writer.append("Desired times of workout per week: " + desired_lvl + " ** " + "\n\n");
            writer.flush();
            writer.close();
            Toast.makeText(UserInfoActivity.this, "Your information have been recorded. Please proceed to take a fitness test ", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}



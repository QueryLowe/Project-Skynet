package com.example.skynet;

import android.content.Intent;
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

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserInfoActivity extends AppCompatActivity {

//    private static class SendDeviceDetails extends AsyncTask<String, Void, String> {
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

    public static int USER_AGE = 999;
    private String[] items, items2;
    EditText height, weight, age;
    Spinner gender, goal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Button confirm_button = (Button) findViewById(R.id.btn_confirm);



        // setup the spinners
        items = new String[]{"male", "female"};
        gender = (Spinner) findViewById(R.id.user_gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gender.setAdapter(adapter);

        //temporarily name goals to goal 1, 2, 3
        items2 = new String[] {"Goal 1", "Goal 2", "Goal 3"};
        goal = (Spinner) findViewById(R.id.user_goal);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        goal.setAdapter(adapter2);

        height = (EditText) findViewById(R.id.editText_height);
        weight = (EditText) findViewById(R.id.editText_weight);
        age = (EditText) findViewById(R.id.editText_age);

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age_text = age.getText().toString();
                USER_AGE = Integer.parseInt(age_text);
            }
        });

        confirm_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                JSONObject postData = new JSONObject();
//                try {
//                    postData.put("height", height.getText().toString());
//                    postData.put("weight", weight.getText().toString());
//                    postData.put("age", age.getText().toString());
//                    postData.put("gender", gender.toString());
//                    postData.put("goal", goal.toString());
//
//                    new SendDeviceDetails().execute("http://142.93.254.242:8000/", postData.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }



                // go to fitness test activity
                Intent MainIntent = new Intent(UserInfoActivity.this, FitnessTestActivity.class);
                startActivity(MainIntent);
            }
        });

    }

    private  String formatDataAsJSON() {
        final JSONObject postData = new JSONObject();
        try {
            postData.put("height", height.getText().toString());
            postData.put("weight", weight.getText().toString());
            postData.put("age", age.getText().toString());
            postData.put("gender", gender.toString());
            postData.put("goal", goal.toString());

            return postData.toString();
        } catch (JSONException e1) {
            Log.d("GG", "can't format JSON");
        }
        return null;
    }

    private void sendDataToServer() {
        final String json = formatDataAsJSON();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return (String) getServerResponse(json);
            }

            @Override
            protected  void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }.execute();
    }

    private String getServerResponse(String json) {
        HttpPost post;

        return null;
    }

}

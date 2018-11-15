package com.example.collection_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class FeedbackActivity extends AppCompatActivity {

    public String getfeedbackfrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Intent intent = getIntent();
        String message = intent.getStringExtra(DisplayMessageActivity.EXTRA_MESSAGE);
        this.getfeedbackfrom = message;

    }

    public void pushMessage(View view) {
        String urladd = "http://35.238.183.60:8000/pushfeedback";
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner);
//        EditText editText = (EditText) findViewById(R.id.editText3);
        String feedback = String.valueOf(spinner1.getSelectedItem());
        TextView textView = findViewById(R.id.textView3);
        String output = getfeedbackfrom + ": " + feedback;


        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("getfeedbackfrom", this.getfeedbackfrom);
            jsonParam.put("feedback", feedback);
            String get= new NetConnect().execute(urladd,jsonParam.toString()).get();
            if (get != null){
                textView.setText("Thank you for your feedback");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        System.exit(1);
//                    }
//                },
//                2000);
    }
}

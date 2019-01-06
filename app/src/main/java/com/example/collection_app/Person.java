package com.example.collection_app;

import android.app.Activity;
import android.util.JsonToken;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class Person {
    private int user_id;
    private Activity activity;
    private ArrayList<String> user_recommend;
    private ArrayList<String> user_feedback;
    private Boolean isValid = false;

    public Person(int user_id, Activity activity){
        this.user_id = user_id;
        this.activity = activity;
        readRecommend("http://35.238.183.60:8000/getrecommend");
    }

    public ArrayList<String> getUserRecommend(){
        return this.user_recommend;
    }

    public void pushUserFeedback(ArrayList<String> feedback){
        this.user_feedback = feedback;
//        TODO: give feedback to Sid
        return;
    }

    public Boolean getValid() {
        return isValid;
    }

//    public void readRecommend(String filename){
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(this.activity.getAssets().open(filename)));
//
//            // do reading, usually loop until end of file reading
//            String mLine = reader.readLine();
//            while ((mLine = reader.readLine()) != null) {
//                String[] temp = mLine.split(",");
//                if (Integer.parseInt(temp[1]) == user_id){
//                    this.user_recommend = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(temp, 2, temp.length)));
//                    this.isValid = true;
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            Log.d("read", "error");
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    Log.d("close", "error");
//                }
//            }
//        }
//    }

    public void readRecommend(String urladd) {
        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("userid", this.user_id);
            String get = new NetConnect().execute(urladd,jsonParam.toString()).get();
            if (get != null){
                Type listType = new TypeToken<String []>() {}.getType();
                String[] temp = new Gson().fromJson(get, listType);
                this.user_recommend = new ArrayList<>(Arrays.asList(temp));
                this.isValid = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

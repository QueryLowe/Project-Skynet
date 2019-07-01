package com.example.skynet;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class User {
    public String USER_NAME;
    public String USER_PASSWORD;
    public boolean is_first_time_user = true;
    public String USER_GENDER;

    public int USER_AGE;
    public double USER_WEIGHT;
    public double USER_HEIGHT;
    public double USER_AVG_HEART_RATE;

    public String current_workout_level;
    public String desired_workout_level;

    public ArrayList<Integer> RECOMMENDED_WORKOUT_3 = new ArrayList<>(3);
    public ArrayList<Integer> RECOMMENDED_WORKOUT_9 = new ArrayList<>(9);

    User(){
        USER_NAME = null;
        USER_PASSWORD = null;
        is_first_time_user = true;
    }

    User(String UserName, String Password) {
        USER_NAME = UserName;
        USER_PASSWORD = Password;
        is_first_time_user = true;
    }

    User(String UserName, String Password, boolean is_first_time) {
        USER_NAME = UserName;
        USER_PASSWORD = Password;
        is_first_time_user = is_first_time;
    }

    public void setRecommendations(ArrayList<Integer> workout_3, ArrayList<Integer> workout_9) {
        if (!is_first_time_user) {
            RECOMMENDED_WORKOUT_3 = workout_3;
            RECOMMENDED_WORKOUT_9 = workout_9;
        } else {
            Log.d("User Class Error", "User needs to take fitness test before recommendations");
        }
    }

    public void setUserInfo(int age, int weight, int height) {
        USER_AGE = age;
        USER_WEIGHT = weight;
        USER_HEIGHT = height;
    }

    public void setUserRequirements(String current, String desired) {
        current_workout_level = current;
        desired_workout_level = desired;
    }

}

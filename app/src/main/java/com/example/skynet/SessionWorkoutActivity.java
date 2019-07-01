package com.example.skynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.skynet.LoginActivity.user;

public class SessionWorkoutActivity extends AppCompatActivity {

    public static String[] rec_str_9 = new String[9];
    public static int[] rec_int = FitnessTestActivity.getRecommendations();

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_workout);

        String[] rec_str_3 = new String[3];

        int[] rec = rec_int;
//        Toast.makeText(SessionWorkoutActivity.this, "recommender: " + Arrays.toString(rec), Toast.LENGTH_SHORT).show();
//        Log.d("rec", Arrays.toString(rec));
//        Log.d("Walk Jog Run", Integer.toString(R.raw.walk_jog_run));


//        R.raw.belt_it_out,
//                R.raw.burn_30,
//                R.raw.distance_workout,
//                R.raw.five_k_trainer,
//                R.raw.in_it_for_the_long_run,
//                R.raw.reverse_treadmill_workout,
//                R.raw.runners_high,
//                R.raw.simple_hill_sprints,
//                R.raw.time_interval,
//                R.raw.torch,
//                R.raw.treadmill_turmoil,
//                R.raw.walk_jog_run

        for (int i = 0; i < 3; i++) {
            rec_str_3[i] = "null";
//            Log.d("wtf_rec", Integer.toString(rec[i]) + "*" + i);
            switch (rec[i]) {
                case 2131623936:
                   // Log.d("wtf_1", Integer.toString(rec[i]));
                    rec_str_3[i] = "Belt it Out";
                    break;
                case 2131623937:
                   // Log.d("wtf_2", Integer.toString(rec[i]));
                    rec_str_3[i] = "Burn 30";
                    break;
                case 2131623940:
                   // Log.d("wtf_3", Integer.toString(rec[i]));
                    rec_str_3[i] = "Distance Workout";
                    break;
                case 2131623941:
                  //  Log.d("wtf_4", Integer.toString(rec[i]));
                    rec_str_3[i] = "5K Trainer";
                    break;
                case 2131623942:
                    //Log.d("wtf_5", Integer.toString(rec[i]));
                    rec_str_3[i] = "In in for the Long Run";
                    break;
                case 2131623943:
                    //Log.d("wtf_6", Integer.toString(rec[i]));
                    rec_str_3[i] = "Reverse Treadmill Workout";
                case 2131623945:
                  //  Log.d("wtf_7", Integer.toString(rec[i]));
                    rec_str_3[i] = "Runners High";
                    break;
                case 2131623946:
                  //  Log.d("wtf_8", Integer.toString(rec[i]));
                    rec_str_3[i] = "Simple Hill Sprints";
                    break;
                case 2131623947:
                   // Log.d("wtf_9", Integer.toString(rec[i]));
                    rec_str_3[i] = "Time Interval";
                    break;
                case 2131623948:
                   // Log.d("wtf_10", Integer.toString(rec[i]));
                    rec_str_3[i] = "Torch";
                    break;
                case 2131623949:
                  //  Log.d("wtf_11", Integer.toString(rec[i]));
                    rec_str_3[i] = "Treadmill Turmoil";
                    break;
                case 2131623950:
                  //  Log.d("wtf_12", Integer.toString(rec[i]));
                    rec_str_3[i] = "Walk Jog Run";
                    break;
            }
        }
//        Toast.makeText(SessionWorkoutActivity.this, "rec3: " + Arrays.toString(rec_str_3), Toast.LENGTH_SHORT).show();
//        Log.d("rec3", Arrays.toString(rec_str_3));

        for (int i = 3; i < 12; i++) {
            rec_str_9[i - 3] = "null";
            switch (rec[i]) {
                case R.raw.belt_it_out:
                    rec_str_9[i - 3] = "Belt it Out";
                    break;
                case R.raw.burn_30:
                    rec_str_9[i - 3] = "Burn 30";
                    break;
                case R.raw.distance_workout:
                    rec_str_9[i - 3] = "Distance Workout";
                    break;
                case R.raw.five_k_trainer:
                    rec_str_9[i - 3] = "5K Trainer";
                    break;
                case R.raw.in_it_for_the_long_run:
                    rec_str_9[i - 3] = "In in for the Long Run";
                    break;
                case R.raw.reverse_treadmill_workout:
                    rec_str_9[i - 3] = "Reverse Treadmill Workout";
                    break;
                case R.raw.runners_high:
                    rec_str_9[i - 3] = "Runners High";
                    break;
                case R.raw.simple_hill_sprints:
                    rec_str_9[i - 3] = "Simple Hill Sprints";
                    break;
                case R.raw.time_interval:
                    rec_str_9[i - 3] = "Time Interval";
                    break;
                case R.raw.torch:
                    rec_str_9[i - 3] = "Torch";
                    break;
                case R.raw.treadmill_turmoil:
                    rec_str_9[i - 3] = "Treadmill Turmoil";
                    break;
                case R.raw.walk_jog_run:
                    rec_str_9[i - 3] = "Walk Jog Run";
                    break;
            }
        }

//        Toast.makeText(SessionWorkoutActivity.this, "rec9: " + Arrays.toString(rec_str_9), Toast.LENGTH_SHORT).show();
//        Log.d("rec9", Arrays.toString(rec_str_9));

        listview = (ListView) findViewById(R.id.session_workout_list_view);
        String[] recommendations = new String[] {"Workout 1", "Workout 2", "Workout 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, rec_str_3);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listview.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),rec_str_3[itemPosition] + " selected.", Toast.LENGTH_LONG).show();
                WorkoutScreenActivity.rawAssetId = rec[position];
            }
        });

        Button start, explore, cancel;
        start = (Button) findViewById(R.id.session_workout_start);
        explore = (Button) findViewById(R.id.session_workout_explore);
        cancel = (Button) findViewById(R.id.session_workout_cancel);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WorkoutScreenActivity.rawAssetId == 0 || WorkoutScreenActivity.rawAssetId == 6666) {
                    Toast.makeText(SessionWorkoutActivity.this, "Please select a workout to proceed, or click cancel to log out", Toast.LENGTH_SHORT).show();
                } else {
                    Intent start_workout = new Intent(SessionWorkoutActivity.this, WorkoutScreenActivity.class);
                    startActivity(start_workout);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gg = new Intent(SessionWorkoutActivity.this, LoginActivity.class);
                startActivity(gg);
            }
        });




        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_explore = new Intent(SessionWorkoutActivity.this, ExploreActivity.class);
                startActivity(to_explore);
            }
        });




//        final ListView lv = (ListView)findViewById(R.id.session_workout_list_view);
//        final ArrayList<String> list;
//        list = new ArrayList<String>();
//
//        Collections.addAll(list, recommendations);
//
//        ArrayAdapter workout_adapter = new ArrayAdapter<String>(this,R.layout.activity_session_workout,list);
//        lv.setAdapter(workout_adapter);
    }
}

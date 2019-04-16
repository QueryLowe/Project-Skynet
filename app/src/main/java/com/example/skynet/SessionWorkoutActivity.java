package com.example.skynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class SessionWorkoutActivity extends AppCompatActivity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_workout);

        listview = (ListView) findViewById(R.id.session_workout_list_view);
        String[] recommendations = new String[] {"Workout 1", "Workout 2", "Workout 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, recommendations);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listview.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });

        Button start, explore, cancel;
        start = (Button) findViewById(R.id.session_workout_start);
        explore = (Button) findViewById(R.id.session_workout_explore);
        cancel = (Button) findViewById(R.id.session_workout_cancel);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start_workout = new Intent(SessionWorkoutActivity.this, WorkoutScreenActivity.class);
                startActivity(start_workout);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

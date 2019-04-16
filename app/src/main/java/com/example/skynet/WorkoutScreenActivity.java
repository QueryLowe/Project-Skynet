package com.example.skynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WorkoutScreenActivity extends AppCompatActivity {

    Button workout_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_screen);

        workout_proceed = (Button) findViewById(R.id.workoutscreen_proceed);
        workout_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_explore = new Intent(WorkoutScreenActivity.this, ExploreActivity.class);
                startActivity(to_explore);
            }
        });
    }
}

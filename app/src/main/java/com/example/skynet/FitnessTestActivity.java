package com.example.skynet;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FitnessTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_test);

        Button btn_proceed = findViewById(R.id.proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent req = new Intent(FitnessTestActivity.this, UserRequirementActivity.class);
                startActivity(req);
            }
        });

        if (UserInfoActivity.USER_AGE <= 50) {
            Toast.makeText(getApplicationContext(),"Cooper Test",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Rockport Test",Toast.LENGTH_SHORT).show();
        }
    }
}

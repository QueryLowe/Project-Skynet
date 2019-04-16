package com.example.skynet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserRequirementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_requirement);

        Button btn_conf = findViewById(R.id.btn_user_req_confirm);
        btn_conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSessionWorkout = new Intent(UserRequirementActivity.this, SessionWorkoutActivity.class);
                startActivity(toSessionWorkout);
            }
        });
    }
}

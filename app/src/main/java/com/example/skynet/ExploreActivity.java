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

import com.lf.skynet_service.ResultCode;
import com.lf.skynet_service.ShutdownReason;
import com.lf.skynet_service.SkynetServiceCallbacks;
import com.lf.skynet_service.SkynetServiceError;
import com.lf.skynet_service.SkynetServiceWrapper;
import com.lf.skynet_service.Units;
import com.lf.skynet_service.WorkoutState;
import com.lf.skynet_service.WorkoutStreamData;

import static com.example.skynet.SessionWorkoutActivity.rec_int;
import static com.example.skynet.SessionWorkoutActivity.rec_str_9;


//private SkynetServiceWrapper mSkynetService;

public class ExploreActivity extends AppCompatActivity {

    ListView listview_exp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        WorkoutScreenActivity.rawAssetId = 6666;

        Button start_exp, cancel_exp;

        start_exp = (Button) findViewById(R.id.explore_start);
        cancel_exp = (Button) findViewById(R.id.explore_cancel);

        listview_exp = (ListView) findViewById(R.id.explore_list_view);
        //String[] recommendations_exp = new String[] {"Workout 1", "Workout 2", "Workout 3", "Workout 5", "Workout 6", "Workout 7", "Workout 8", "Workout 9" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, rec_str_9);
        listview_exp.setAdapter(adapter);
        listview_exp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listview_exp.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),rec_str_9[position] + " selected.", Toast.LENGTH_LONG).show();
                WorkoutScreenActivity.rawAssetId = rec_int[position + 3];
            }
        });

        start_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WorkoutScreenActivity.rawAssetId == 6666 || WorkoutScreenActivity.rawAssetId == 0) {
                    Toast.makeText(getApplicationContext(),"Please select a workout to proceed, or click cancel to log out.", Toast.LENGTH_LONG).show();
                } else {
                    Intent start_workout = new Intent(ExploreActivity.this, WorkoutScreenActivity.class);
                    startActivity(start_workout);
                }
            }
        });

        cancel_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutScreenActivity.rawAssetId = 0;
                Intent gg = new Intent(ExploreActivity.this, LoginActivity.class);
                startActivity(gg);
            }
        });

    }


}

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

public class ExploreActivity extends AppCompatActivity {

    ListView listview_exp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Button start_exp, cancel_exp;

        start_exp = (Button) findViewById(R.id.explore_start);
        cancel_exp = (Button) findViewById(R.id.explore_cancel);

        listview_exp = (ListView) findViewById(R.id.explore_list_view);
        String[] recommendations_exp = new String[] {"Workout 1", "Workout 2", "Workout 3", "Workout 5", "Workout 6", "Workout 7", "Workout 8", "Workout 9" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1, recommendations_exp);
        listview_exp.setAdapter(adapter);
        listview_exp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listview_exp.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :"+position+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });

        start_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start_workout = new Intent(ExploreActivity.this, WorkoutScreenActivity.class);
                startActivity(start_workout);
            }
        });

        cancel_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }


}

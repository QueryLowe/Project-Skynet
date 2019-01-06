package com.example.collection_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "dummy2";
    public int user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        this.user = Integer.parseInt(message);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText("Hello, userid: " + message + "!");

        Person person = new Person(Integer.parseInt(message), this);
        ArrayList<String> recommend = person.getUserRecommend();
//        TextView textView3 = findViewById(R.id.textView3);
//        textView3.setText(recommend);

        ListView lv = (ListView) findViewById(R.id.listView);
        MyCustomAdapter mcadapter = new MyCustomAdapter(this,recommend);
        lv.setAdapter(mcadapter);

}
}

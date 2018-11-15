package com.example.collection_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "dummy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText5);
        String message = editText.getText().toString();
        Person person = new Person(Integer.parseInt(message), this);
//        System.out.println("haha");
        if (person.getValid()){
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
        else{
            TextView textView = findViewById(R.id.textView4);
            textView.setText("User not found error");
        }

    }

}

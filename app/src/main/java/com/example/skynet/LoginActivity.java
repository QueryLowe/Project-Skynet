package com.example.skynet;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skynet.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Spliterator;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    private EditText mPasswordView, mUserName;
    private View mProgressView;
    private View mLoginFormView;

    public static boolean user_not_complete_test_flag = true;

    public static User user = new User();
    public static List<String> existing_users = new ArrayList<String>();

    public static File root = new File(Environment.getExternalStorageDirectory() + File.separator + "User_Info", "Credentials");

    public static File User_Credentials = new File(root, "User_Credentials.txt");
    public static File User_Info = new File(root, "User_Info.txt");
    public static File Users_Finished_Test = new File(root, "Users_Finished_Test.txt");
    public static File User_Test_Result = new File(root, "User_Test_Result.txt");
    public static File User_Workout_Result = new File(root, "User_Workout_Result.txt");
    public static File User_Recommendations = new File(root, "User_Recommendations.txt");


    public static String[] dummy_existing_users = new String[]{
//            "eric",
//            "max",
//            "andrew",
//            "prateek",
//            "shashank",
//            "troy"
    };

    private void cleanup() {
        FileWriter g1 = null, g2 = null, g3 = null, g4 = null, g5 = null, g6 = null;
        if (User_Credentials.exists()) {

            User_Credentials.delete();
            User_Credentials = new File(root, "User_Credentials.txt");
            assert g1 != null;
            try { g1 = new FileWriter(User_Credentials, false);} catch (IOException e) { }
            try { g1.write("");} catch (Exception e) { }
            try { g1.flush();} catch (IOException e) { }
            try { g1.close();} catch (IOException e) { }

        }
        if (User_Info.exists()) {
            User_Info.delete();
            User_Info = new File(root, "User_Info.txt");
            assert g2 != null;
            try { g2 = new FileWriter(User_Info, false);} catch (IOException e) { }
            try { g2.write("");} catch (Exception e) { }
            try { g2.flush();} catch (IOException e) { }
            try { g2.close();} catch (IOException e) { }
        }
        if (Users_Finished_Test.exists()) {
            User_Info.delete();
            Users_Finished_Test = new File(root, "Users_Finished_Test.txt");
            assert g3 != null;
            try { g3 = new FileWriter(Users_Finished_Test, false);} catch (IOException e) { }
            try { g3.write("");} catch (Exception e) { }
            try { g3.flush();} catch (IOException e) { }
            try { g3.close();} catch (IOException e) { }
        }
        if (User_Test_Result.exists()) {
            User_Test_Result.delete();
            User_Test_Result = new File(root, "User_Test_Result.txt");
            assert g4 != null;
            try { g4 = new FileWriter(User_Test_Result, false);} catch (IOException e) { }
            try { g4.write("");} catch (Exception e) { }
            try { g4.flush();} catch (IOException e) { }
            try { g4.close();} catch (IOException e) { }
        }
        if (User_Workout_Result.exists()) {
            User_Workout_Result.delete();
            User_Workout_Result = new File(root, "User_Workout_Result.txt");
            assert g5 != null;
            try { g5 = new FileWriter(User_Workout_Result, false);} catch (IOException e) { }
            try { g5.write("");} catch (Exception e) { }
            try { g5.flush();} catch (IOException e) { }
            try { g5.close();} catch (IOException e) { }
        }
        if (User_Recommendations.exists()) {
            User_Recommendations.delete();
            User_Recommendations = new File(root, "User_Recommendations.txt");
            assert g6 != null;
            try { g6 = new FileWriter(User_Recommendations, false);} catch (IOException e) { }
            try { g6.write("");} catch (Exception e) { }
            try { g6.flush();} catch (IOException e) { }
            try { g6.close();} catch (IOException e) { }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // cleanup();

        WorkoutScreenActivity.rawAssetId = 0;

        if (!root.exists()) {
            root.mkdirs();
        }
        if (!User_Credentials.exists()) {
            User_Credentials = new File(root, "User_Credentials.txt");
        }
        if (!User_Info.exists()) {
            User_Info = new File(root, "User_Info.txt");
        }
        if (!Users_Finished_Test.exists()) {
            Users_Finished_Test = new File(root, "Users_Finished_Test.txt");
        }
        if (!User_Test_Result.exists()) {
            User_Test_Result = new File(root, "User_Test_Result.txt");
        }
        if (!User_Workout_Result.exists()) {
            User_Workout_Result = new File(root, "User_Workout_Result.txt");
        }
        if (!User_Recommendations.exists()) {
            User_Recommendations = new File(root, "User_Recommendations.txt");
        }

        // declaring obejct of EditText control
        mUserName = (EditText) findViewById(R.id.txtUserName);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button cancel = (Button) findViewById((R.id.btnCancel));


        Collections.addAll(existing_users, dummy_existing_users);

//        Toast.makeText(LoginActivity.this,"file path: " + root.toString(), Toast.LENGTH_LONG).show();
//        Log.d("file path", root.toString());


        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserName = mUserName.getText().toString();
                String Pwd = mPasswordView.getText().toString();


                if (UserName.equals("") || Pwd.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill in your user name and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (UserExists(UserName)) {
                    if (VerifyUsers(UserName, Pwd)) {
                        user.USER_NAME = UserName;
                        user.USER_PASSWORD = Pwd;
                       // Toast.makeText(LoginActivity.this,"WTF: " + UserName + " " + User_is_new(UserName), Toast.LENGTH_LONG).show();
                        if (User_is_new(UserName)) {
                            StartFitnessTest();
                            Toast.makeText(LoginActivity.this,"Welcome back! Please finish the fitness Test to proceed", Toast.LENGTH_LONG).show();
                        } else {
                            StartSessionWorkout();
                            Toast.makeText(LoginActivity.this,"Welcome back! Retrieving your previous workouts", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this,"Wrong credentials. If you are new to Skynet, Please register a new account", Toast.LENGTH_LONG).show();
                    }
                } else {
                    AddUser(UserName, Pwd);
                    user.USER_NAME = UserName;
                    user.USER_PASSWORD = Pwd;
                   // Toast.makeText(LoginActivity.this, existing_users.toString(), Toast.LENGTH_LONG).show();
                    user.is_first_time_user = true;
                    StartUserInfo();
                    Toast.makeText(LoginActivity.this,"Welcome to Skynet! Please tell us some basic information of yourself", Toast.LENGTH_LONG).show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gg = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(gg);
            }
        });
    }

    private boolean User_is_new(String UserName) {
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/storage/emulated/0/User_Info/Credentials/Users_Finished_Test.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (Exception ei) {
            ei.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }

//        Log.d("users finished: ", text.toString());
//        Toast.makeText(LoginActivity.this, "users finished: " + text.toString(), Toast.LENGTH_LONG).show();

        String[] users_finished = text.toString().split(" \\*\\* ", 0);
     //   Toast.makeText(LoginActivity.this, "Users Finished:  " + Arrays.toString(users_finished), Toast.LENGTH_LONG).show();
        Log.d("WTF 1", Arrays.toString(users_finished));

        user.is_first_time_user = true;
        for (String i : users_finished) {
            if (i.equals(UserName)) {
                user.is_first_time_user = false;
                break;
            }
        }

        return user.is_first_time_user;
    }

    public void AddUser(String UserName, String Password) {
        try {
            FileWriter writer = new FileWriter(User_Credentials,true);
            writer.append(UserName + " " + Password + "\n");
            writer.flush();
            writer.close();
            existing_users.add(UserName + " ");
            Toast.makeText(LoginActivity.this, "User Record Created", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean VerifyUsers(String UserName, String Password) {
        List<String> all_user_list = ReadUserCredentials(UserName);
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/storage/emulated/0/User_Info/Credentials/User_Credentials.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (Exception ei) {
            ei.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
//
//        Log.d("users in txt: ", text.toString());
//        Toast.makeText(LoginActivity.this, "users in txt: " + text.toString(), Toast.LENGTH_LONG).show();
//

//        Toast.makeText(LoginActivity.this, "WTF: all_user_list: " + all_user_list.toString(), Toast.LENGTH_LONG).show();
//        Log.d("WTF: all_user_list: ", all_user_list.toString());


        String[] s = all_user_list.toString().split(", ");
//        Toast.makeText(LoginActivity.this, "Verify User: s: " + Arrays.toString(s), Toast.LENGTH_LONG).show();

//        Log.d("WTF: String[] s: ", Arrays.toString(s));
//        Toast.makeText(LoginActivity.this, "WTF: String[] s: " + Arrays.toString(s), Toast.LENGTH_LONG).show();

        for (int i = 0; i < s.length; i++) {
//            Log.d("WTF s[" + Integer.toString(i) + "]: ", s[i]);
 //           Log.d("substring", s[i].substring(0, s[i].indexOf(" ")));
//            Toast.makeText(LoginActivity.this, "WTF s[" + Integer.toString(i) + "]: " +  s[i], Toast.LENGTH_LONG).show();

            if (s[i].substring(0, s[i].indexOf(" ")).equals(UserName) || s[i].substring(0, s[i].indexOf(" ")).equals("[" + UserName)) {

                String[] temp = s[i].split(" ", 0);
//                Toast.makeText(LoginActivity.this, "WTF: temp: " + Arrays.toString(temp), Toast.LENGTH_LONG).show();
//                Log.d("WTF: temp: ", Arrays.toString(temp));

                if (temp[1].contains("]")){
                    String temp2 = temp[1].substring(0, temp[1].indexOf("]"));
//                    Toast.makeText(LoginActivity.this, "WTF: temp2: " + temp2, Toast.LENGTH_LONG).show();
//                    Log.d("WTF: temp2: ", temp2);
                    return temp2.equals(Password);
                }
                return temp[1].equals(Password);
            }
        }
        return false;

//        if (Arrays.toString(s).contains(UserName)) {
//            String sb = s
//            return all_user_list.get(all_user_list.indexOf(UserName) + 1).equals(Password);
//        }
//        return false;
    }

    private void StartUserInfo() {
        Intent MainIntent2 = new Intent(LoginActivity.this, UserInfoActivity.class);
        startActivity(MainIntent2);
    }

    private void StartSessionWorkout() {
        Intent MainIntent = new Intent(LoginActivity.this, SessionWorkoutActivity.class);
        startActivity(MainIntent);
    }

    private void StartFitnessTest() {
        Intent MainIntent3 = new Intent(LoginActivity.this, FitnessTestActivity.class);
        startActivity(MainIntent3);
    }

    public boolean UserExists(String UserName) {
        if (existing_users.contains(UserName)) {
            return true;
        }
        if (ReadUserCredentials(UserName).toString().contains(UserName)) {
            for (String i : ReadUserCredentials(UserName)) {
                Log.d("all user", i.substring(0, i.indexOf(" ")));
                if (i.substring(0, i.indexOf(" ")).equals(UserName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> ReadUserCredentials(String UserName) {
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/storage/emulated/0/User_Info/Credentials/User_Credentials.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (Exception ei) {
            ei.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }

        //Log.d("users in txt: ", text.toString());
        //Toast.makeText(LoginActivity.this, "users in txt: " + text.toString(), Toast.LENGTH_LONG).show();



        String[] all_users_str = text.toString().split("\n", 0);
        List<String> all_user_list = new ArrayList<>(Arrays.asList(all_users_str));

       // Toast.makeText(LoginActivity.this, "Users in list by ReadUser:  " + all_user_list.toString(), Toast.LENGTH_LONG).show();
        return all_user_list;
    }
}

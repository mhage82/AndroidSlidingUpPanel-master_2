package com.courtside.demo.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.courtside.demo.async.AsyncLogin;
import com.courtside.demo.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class LoginActivity extends AppCompatActivity {

    public static final int MIN_LENGTH = 8;
    private EditText username;
    private EditText password;

    public void loginClick(View view){

        // Get text from email and passord field
        final String user = username.getText().toString();
        final String pass = password.getText().toString();

        if(DemoActivity.OFFLINE_MODE == true){
            Intent intent = new Intent(this, DemoActivity.class);
            startActivity(intent);

        }else{
            if(pass.isEmpty() || pass.length()<MIN_LENGTH){
                password.setError("You must have "+ MIN_LENGTH+" characters in your password");
            }else {
                Log.i("loginClick", "user = " + user);
                Log.i("loginClick", "pass = " + pass);
                // Initialize  AsyncLogin() class with email and password
                new AsyncLogin(this).execute(user, pass);
            }
        }
    }

    public void signupClick(View view){
        Log.i("signupClick", "started");
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.emailET);
        password = (EditText)findViewById(R.id.passwordET);

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }
}

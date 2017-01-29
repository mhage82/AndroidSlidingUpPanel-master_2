package com.courtside.demo.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.courtside.demo.async.AsyncSignup;
import com.courtside.demo.R;

public class SignupActivity extends AppCompatActivity {

    final int PASS_MIN_LENGTH= 8;
    private EditText emailET;
    private EditText passwordET;
    private EditText passwordRetypeET;
    private EditText displaynameET;
    private EditText phoneET;

    public void signupRegClick(View view){

        final String  email= emailET.getText().toString();
        final String  password= passwordET.getText().toString();
        final String  retypepassword= passwordRetypeET.getText().toString();
        final String  displayname= displaynameET.getText().toString();
        final String  phone= phoneET.getText().toString();

        if(!password.equals(retypepassword)){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show();
        }else if(password.isEmpty() || password.length()<PASS_MIN_LENGTH){
            passwordET.setError("Password should contain a minimum of "+ PASS_MIN_LENGTH+" characters");
            //           Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show();
        }else{
            new AsyncSignup(this).execute(email, password, displayname, phone);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailET=(EditText)findViewById(R.id.emailSignupET);
        passwordET=(EditText)findViewById(R.id.passwordSignupET);
        passwordRetypeET=(EditText)findViewById(R.id.passwordRetypeET);
        displaynameET=(EditText)findViewById(R.id.displayNameET);
        phoneET=(EditText)findViewById(R.id.phoneET);
    }
}

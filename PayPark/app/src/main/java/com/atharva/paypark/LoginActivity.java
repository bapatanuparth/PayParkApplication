package com.atharva.paypark;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.atharva.paypark.model.UserModel;
import com.atharva.paypark.util.Communication;


public class LoginActivity extends AppCompatActivity {

    EditText idField;
    EditText passwordField;
    int backCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            idField = findViewById(R.id.idField_login);
            passwordField = findViewById(R.id.passwordField_login);
    }

    @Override
    public void onBackPressed(){
        backCount++;
        if(backCount==1)
            Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        else
            this.finishAffinity();
    }

    public void registerActivity(View view) {
        UserModel.getInstance().clearFields();
        startActivity(new Intent(this,Register1Activity.class));
    }

    public void loginActivity(View view) {
        idField = findViewById(R.id.idField_login);
        passwordField = findViewById(R.id.passwordField_login);
        String fid = idField.getText().toString();
        String pass = passwordField.getText().toString();
        Communication.verifyLogin(fid,pass);
        if(UserModel.getInstance().isLoginState()){
            //show main page
            Log.d(null, "loginActivity: Login Verified");
            Log.d(null,UserModel.getInstance().toString());
            startActivity(new Intent(this,ParkingLogActivity.class));
        } else {
            Log.d(null, "loginActivity: Login INVALID");
            Toast.makeText(this,"Invalid Family ID or Password",Toast.LENGTH_LONG).show();
        }
    }
}
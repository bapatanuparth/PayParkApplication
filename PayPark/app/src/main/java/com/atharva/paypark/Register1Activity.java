package com.atharva.paypark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.atharva.paypark.model.UserModel;
import com.atharva.paypark.util.Communication;

public class Register1Activity extends AppCompatActivity {

    //view 1
    EditText idField;
    EditText passwordField;
    EditText mobileField;
    EditText noVehicleField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        idField = findViewById(R.id.idField_register1);
        passwordField = findViewById(R.id.passwordField_register1);
        mobileField = findViewById(R.id.mobileField_register1);
        noVehicleField = findViewById(R.id.no_vehicle_register1);
        setFields();
    }

    @SuppressLint("SetTextI18n")
    private void setFields() {
        String fid = UserModel.getInstance().getFid();
        String mobile = UserModel.getInstance().getMobile();
        if(!fid.equals("")) idField.setText(fid);
        if(!mobile.equals("")) mobileField.setText(mobile);
        if(!UserModel.getInstance().getVehicleNos().isEmpty()) noVehicleField.setText(UserModel.getInstance().getVehicleNos().size()+"");
        //if(vehicles.size()!=0) noVehicleField.setText(vehicles.size());
//        RelativeLayout rl = new RelativeLayout(this);
//        RelativeLayout.LayoutParams rlparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        rlparams.addRule(RelativeLayout.BELOW,1);
    }

    public void nextActivity(View view) {
        UserModel.getInstance().setFid(idField.getText().toString());
        UserModel.getInstance().setMobile(mobileField.getText().toString());
        String pass = passwordField.getText().toString();
        int noVehicles = Integer.parseInt(noVehicleField.getText().toString());
        Intent intent = new Intent(this,Register2Activity.class);
        intent.putExtra("pass",pass);
        intent.putExtra("noVehicles",noVehicles);
        startActivity(intent);
    }

    public void registerActivity(View view) {
        int error = Communication.registerUser(passwordField.getText().toString());
        switch (error){
            case 0:
                startActivity(new Intent(this,ParkingLogActivity.class));
                break;
            case 1:
                System.out.println("DUPLICATE ID");
                Toast.makeText(this,"Family ID must be unique",Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this,"Vehicle can be registered only on one account",Toast.LENGTH_LONG).show();
                System.out.println("DUPLICATE VEHICLE/ES");
                break;
        }
    }
}

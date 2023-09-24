package com.atharva.paypark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atharva.paypark.model.ParkingLogModel;
import com.atharva.paypark.model.UserModel;
import com.atharva.paypark.util.Communication;

import java.util.ArrayList;
import java.util.List;

public class Register2Activity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        linearLayout = findViewById(R.id.linearLayout_register2);
        List<String> vehicles = UserModel.getInstance().getVehicleNos();
        if(!vehicles.isEmpty()) {
            for (String v: vehicles ) {
                EditText editText = new EditText(this);
                editText.setText(v);
                editText.setId(vehicles.indexOf(v)+1);
                linearLayout.addView(editText);
            }
        }
        else {
            for(int i=0 ; i<getIntent().getIntExtra("noVehicles",0) ; i++){
                EditText editText = new EditText(this);
                editText.setHint("Enter Number of vehicle " + (i+1));
                editText.setId(i+1);
                linearLayout.addView(editText);
            }
        }


    }

    public void registerActivity(View view) {
        EditText editText;
        List<String> vehicle = new ArrayList<>();
        for (int i=0 ; i<getIntent().getIntExtra("noVehicles",0) ; i++) {
            editText = findViewById(i+1);
            vehicle.add(editText.getText().toString());
        }
        UserModel.getInstance().setVehicleNos(vehicle);
        int error = Communication.registerUser(getIntent().getStringExtra("pass"));
        switch (error){
            case 0:
                startActivity(new Intent(this,ParkingLogActivity.class));
                break;
            case 1:
                Toast.makeText(this,"Please enter different Family ID",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,Register1Activity.class));
                break;
            case 2:
                Toast.makeText(this,"A vehicle can be registered on only one account",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,Register2Activity.class));
                break;
        }
    }
}
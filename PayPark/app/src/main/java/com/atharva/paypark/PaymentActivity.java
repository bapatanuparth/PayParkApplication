package com.atharva.paypark;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.atharva.paypark.payment.GooglePay;
import com.atharva.paypark.payment.Paytm;

public class PaymentActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }

    public void payUsingGooglePay(View view) {
        EditText editText = findViewById(R.id.amount);
        Intent intent = new Intent(this,GooglePay.class);
        intent.putExtra("amount",editText.getText().toString());
        startActivity(intent);
    }

    public void payUsingPaytm(View view) {
        EditText editText = findViewById(R.id.amount);
        Intent intent = new Intent(this,Paytm.class);
        intent.putExtra("amount",editText.getText().toString());
        startActivity(intent);
    }
}

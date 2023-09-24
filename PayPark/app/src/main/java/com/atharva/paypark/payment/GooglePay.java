package com.atharva.paypark.payment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.atharva.paypark.R;

public class GooglePay extends AppCompatActivity {
    private static final int GOOGLE_PAY_REQUEST_CODE = 123;
    private static final String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", "test@axisbank")
                .appendQueryParameter("pn", "Test Merchant")
                .appendQueryParameter("mc", "1234")
                .appendQueryParameter("tr", "123456789")
                .appendQueryParameter("tn", "test transaction note")
                .appendQueryParameter("am", this.getIntent().getStringExtra("amount"))
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("url", "https://test.merchant.website")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            switch (resultCode){
                case 1:
                    Toast.makeText(this,"Payment Successful",Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this,"Payment Canceled",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(this,"Payment Unsuccessful",Toast.LENGTH_SHORT).show();
                    break;
            }
            Log.d("GooglePay", data.getStringExtra("Status"));
            super.onBackPressed();
        }
    }

}

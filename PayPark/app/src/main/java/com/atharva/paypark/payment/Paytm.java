package com.atharva.paypark.payment;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.Toast;

import com.atharva.paypark.model.UserModel;
import com.atharva.paypark.util.Communication;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Paytm extends AppCompatActivity {

    String MID = "DQPHBx70776843915469";
    String ORDER_ID = UserModel.getInstance().getFid();
    String CUST_ID = ORDER_ID;
    String INDUSTRY_TYPE_ID = "Retail";
    String CHANNEL_ID = "WAP";
    String TXN_AMOUNT ;
    String WEBSITE = "WEBSTAGING";
    String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    String CHECKSUMHASH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        ORDER_ID = ORDER_ID + random.nextInt(10000);
        TXN_AMOUNT = this.getIntent().getStringExtra("amount");
        CALLBACK_URL = CALLBACK_URL + ORDER_ID;

        List<Pair<String, String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>("MID", MID));
        pairList.add(new Pair<>("ORDER_ID", ORDER_ID));
        pairList.add(new Pair<>("CUST_ID", CUST_ID));
        pairList.add(new Pair<>("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID));
        pairList.add(new Pair<>("CHANNEL_ID", CHANNEL_ID));
        pairList.add(new Pair<>("TXN_AMOUNT", TXN_AMOUNT));
        pairList.add(new Pair<>("WEBSITE", WEBSITE));
        pairList.add(new Pair<>("CALLBACK_URL", CALLBACK_URL));
        CHECKSUMHASH = Communication.getChecksum(pairList);

        pairList.add(new Pair<>("CHECHSUMHASH", CHECKSUMHASH));
//        if(Communication.verifyChecksum(pairList)){
            PaytmPGService Service = PaytmPGService.getStagingService();

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put( "MID" , MID);
            paramMap.put( "ORDER_ID" , ORDER_ID);
            paramMap.put( "CUST_ID" , CUST_ID);
            paramMap.put( "CHANNEL_ID" , CHANNEL_ID);
            paramMap.put( "TXN_AMOUNT" , TXN_AMOUNT);
            paramMap.put( "WEBSITE" , WEBSITE);
            paramMap.put( "INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
            paramMap.put( "CALLBACK_URL", CALLBACK_URL);
            paramMap.put( "CHECKSUMHASH" , CHECKSUMHASH);
            PaytmOrder Order = new PaytmOrder(paramMap);

            Service.initialize(Order, null);

            Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

                @Override
                public void someUIErrorOccurred(String inErrorMessage) {
                    Log.d("PaytmError",inErrorMessage);
                    Log.d("MID",MID);
                    Log.d("ORDER_ID",ORDER_ID);
                    Log.d("CUST_ID",CUST_ID);
                    Log.d("INDUSTRY_TYPE_ID",INDUSTRY_TYPE_ID);
                    Log.d("CHANNEL_ID",CHANNEL_ID);
                    Log.d("TXN_AMOUNT",TXN_AMOUNT);
                    Log.d("WEBSITE",WEBSITE);
                    Log.d("CALLBACK_URL",CALLBACK_URL);
                    Log.d("CHECKSUMHASH",CHECKSUMHASH);
                    Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTransactionResponse(Bundle inResponse) {
                    Log.d("PaytmSucess",inResponse.toString());
                    Log.d("MID",MID);
                    Log.d("ORDER_ID",ORDER_ID);
                    Log.d("CUST_ID",CUST_ID);
                    Log.d("INDUSTRY_TYPE_ID",INDUSTRY_TYPE_ID);
                    Log.d("CHANNEL_ID",CHANNEL_ID);
                    Log.d("TXN_AMOUNT",TXN_AMOUNT);
                    Log.d("WEBSITE",WEBSITE);
                    Log.d("CALLBACK_URL",CALLBACK_URL);
                    Log.d("CHECKSUMHASH",CHECKSUMHASH);
                    Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void networkNotAvailable() {
                    Log.d("PaytmError","Network not available");
                    Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                }

                @Override
                public void clientAuthenticationFailed(String inErrorMessage) {
                    Log.d("PaytmError",inErrorMessage);
                    Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                    Log.d("PaytmError",inErrorMessage);
                    Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Log.d("PaytmError","Back button pressed");
                    Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                    Log.d("PaytmError",inErrorMessage);
                    Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}

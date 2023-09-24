package com.atharva.paypark;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.atharva.paypark.model.ParkingLogModel;
import com.atharva.paypark.model.UserModel;
import com.atharva.paypark.util.Communication;
import com.atharva.paypark.util.Preferences;

import java.util.List;

public class ParkingLogActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int backCount =0;

    TableLayout tableLayout;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!UserModel.getInstance().isLoginState())
            startActivity(new Intent(this,LoginActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tableLayout = findViewById(R.id.table_parking_log);
        spinner = findViewById(R.id.vehicle_spinner);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(UserModel.getInstance().isLoginState()) {
            refresh();
            Preferences.sharedPreferences = getSharedPreferences("PayParkPref",MODE_PRIVATE);
            Preferences.notification = "true".equals(Preferences.get(Preferences.Low_Battery_Notification_KEY));

//            Intent resultIntent = new Intent(this, ResultActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addParentStack(ResultActivity.class);
//
//// Adds the Intent that starts the Activity to the top of the stack
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationCompat.Builder nbuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            nbuilder = new NotificationCompat.Builder(this,NotificationChannel.DEFAULT_CHANNEL_ID);
        else
            nbuilder = new NotificationCompat.Builder(this);
        nbuilder.setSmallIcon(R.mipmap.ic_launcher);
        nbuilder.setContentTitle("Low Balance");
        nbuilder.setContentText("Add money to experience hustle free parking");
        nbuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(111111,nbuilder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(UserModel.getInstance().isLoginState())
            refresh();
    }

    @Override
    public void onBackPressed() {
        backCount++;
        if(backCount==1)
            Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        else
            this.finishAffinity();
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,PreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_parking_log) {
            //Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_account_settings) {
            startActivity(new Intent(this,AccountSettingsActivity.class));
            //Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_preferences) {
            startActivity(new Intent(this,PreferencesActivity.class));
            //Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_logout) {
//            Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
            UserModel.getInstance().clearFields();
            startActivity(new Intent(this,LoginActivity.class));
        }

        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void refresh(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,UserModel.getInstance().getVehicleNos());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLog(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
//        setLog(0);
    }

    private void setLog(int i){
        tableLayout.removeAllViews();
        TableRow tableRow1 = new TableRow(this);
        tableRow1.setPadding(5,10,5,10);

        TextView ent1 = new TextView(this);
        ent1.setText(getString(R.string.entry_time));
        ent1.setTextSize(20);
        ent1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ent1.setWidth(160);

        TextView ext1 = new TextView(this);
        ext1.setText(getString(R.string.exit_time));
        ext1.setTextSize(20);
        ext1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        tableRow1.addView(ent1);
        tableRow1.addView(ext1);
        tableLayout.addView(tableRow1);

        List<ParkingLogModel> parkingLog = Communication.getParkingLog(UserModel.getInstance().getVehicleNos().get(i));
        for (ParkingLogModel parkingLogModel : parkingLog){
            String exitTime = parkingLogModel.getExit();
            TableRow tableRow = new TableRow(this);

            TextView ent = new TextView(this);
            ent.setText(parkingLogModel.getEntry());
            ent.setTextSize(16);
            ent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView ext = new TextView(this);
            if("nul".equals(exitTime))
                ext.setText("-");
            else
                ext.setText(exitTime);
            ext.setTextSize(16);
            ext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            tableRow.addView(ent);
            tableRow.addView(ext);
            tableLayout.addView(tableRow);
        }
    }

}

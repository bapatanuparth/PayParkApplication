package com.atharva.paypark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atharva.paypark.model.UserModel;

public class AccountSettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int backCount=0;
    ListView listView;
    TextView fidView;
    TextView mobileView;
    TextView balanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView = findViewById(R.id.vehicle_list);
        fidView = findViewById(R.id.fid_view);
        mobileView = findViewById(R.id.mobile_view);
        balanceView = findViewById(R.id.balance_view);
        refresh();
    }

    @Override
    public void onResume(){
        super.onResume();
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
        getMenuInflater().inflate(R.menu.account_settings, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_parking_log) {
//            this.setContentView(R.layout.content_parking_log);
            startActivity(new Intent(this,ParkingLogActivity.class));
//            Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_account_settings) {
//            setContentView(R.layout.content_account_settings);
//            Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_preferences) {
//            setContentView(R.layout.content_preferences);
            startActivity(new Intent(this,PreferencesActivity.class));
//            Toast.makeText(this,"LOGOUT",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_logout) {
            UserModel.getInstance().clearFields();
            startActivity(new Intent(this,LoginActivity.class));
        }

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void refresh(){
        fidView.setText(UserModel.getInstance().getFid());
        mobileView.setText(UserModel.getInstance().getMobile());
        balanceView.setText(UserModel.getInstance().getBalance() + "");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,UserModel.getInstance().getVehicleNos());
        listView.setAdapter(arrayAdapter);
    }

    public void makePayment(View view) {
        startActivity(new Intent(this,PaymentActivity.class));
        Toast.makeText(this,"PAYMENT",Toast.LENGTH_SHORT).show();
    }
}

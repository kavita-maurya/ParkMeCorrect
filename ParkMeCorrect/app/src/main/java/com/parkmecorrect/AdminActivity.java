package com.parkmecorrect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class AdminActivity extends AppCompatActivity {

    ImageButton mParked;
    ImageButton mHistory;
    ImageButton mRegister;
    ImageButton mHistoryParking;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new Logout().logout(this);
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mHistory = (ImageButton) findViewById(R.id.history);
        mParked = (ImageButton) findViewById(R.id.parkedV) ;
        mRegister = (ImageButton) findViewById(R.id.registration);
        mHistoryParking = (ImageButton) findViewById(R.id.history_parking) ;

        mHistoryParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ParkingHistoryActivity.class);
                startActivity(intent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, UserRegistrationActivity.class);
                startActivity(intent);
            }
        });

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AlertListActivity.class);
                startActivity(intent);
            }
        });

        mParked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, CarListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

}

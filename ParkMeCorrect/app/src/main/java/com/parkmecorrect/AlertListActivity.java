package com.parkmecorrect;

import android.app.Fragment;

import java.util.ArrayList;


public class AlertListActivity extends SingleFragmentActivity {

    private ArrayList<Alert> alerts;

    @Override
    protected Fragment createFragment() {
        AlertList.setInstance();
        return new AlertListFragment();
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

}

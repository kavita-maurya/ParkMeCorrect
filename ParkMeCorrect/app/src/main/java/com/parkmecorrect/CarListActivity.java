package com.parkmecorrect;

import android.app.Fragment;


public class CarListActivity extends SingleFragmentActivityForCar {


    @Override
    protected Fragment createFragment() {
        CarList.setInstance();
        return new CarListFragment();
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }


}

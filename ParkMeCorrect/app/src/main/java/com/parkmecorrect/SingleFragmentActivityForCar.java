package com.parkmecorrect;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;


public abstract class SingleFragmentActivityForCar extends AppCompatActivity {

    protected abstract Fragment createFragment();

    public Fragment fragment = null;

    public int type = 0;
    public int alertType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_car);


        fragment = createFragment();
        Bundle args = new Bundle();
        args.putInt("type",type);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        Spinner mFilterType = (Spinner) findViewById(R.id.spinnerFilterType);

        mFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type=i;

                fragment = createFragment();
                Bundle args = new Bundle();
                args.putInt("type",type);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}

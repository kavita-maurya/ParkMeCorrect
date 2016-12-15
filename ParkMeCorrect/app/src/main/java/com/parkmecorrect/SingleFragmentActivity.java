package com.parkmecorrect;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    public Fragment fragment = null;

    public int type = 0;
    public int alertType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        fragment = createFragment();
        Bundle args = new Bundle();
        args.putInt("type",type);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        Spinner mFilterType = (Spinner) findViewById(R.id.spinnerFilterType);
        final LinearLayout malertLayout = (LinearLayout)findViewById(R.id.AlertFilter);
        final Spinner mAlertType = (Spinner) findViewById(R.id.spinnerAlertType);
        final LinearLayout mCarLayout = (LinearLayout)findViewById(R.id.CarFilter);
        final EditText mCarNumberText = (EditText)findViewById(R.id.carNumber);
        final ImageButton mSearchBtn = (ImageButton)findViewById(R.id.search);

        mFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCarNumberText.setText("");
                if(i==0) {
                    type = 0;
                    malertLayout.setVisibility(View.GONE);
                    mCarLayout.setVisibility(View.GONE);
                }
                else if(i==1){
                    type = 1;
                    mCarLayout.setVisibility(View.VISIBLE);
                    malertLayout.setVisibility(View.GONE);
                }
                else{
                    type = 2;
                    mCarLayout.setVisibility(View.GONE);
                    malertLayout.setVisibility(View.VISIBLE);
                    alertType = mAlertType.getSelectedItemPosition();
                }


                fragment = createFragment();
                Bundle args = new Bundle();
                args.putInt("type",type);
                args.putInt("Atype",alertType);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNumber = null;
                carNumber = mCarNumberText.getText().toString();
                if(carNumber!=null || !carNumber.isEmpty()) {
                    fragment = createFragment();
                    Bundle args = new Bundle();
                    args.putInt("type",type);
                    args.putString("Cnum",carNumber);
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

                }
            }
        });


        mAlertType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("i",i+"");
                alertType = i;

                fragment = createFragment();
                Bundle args = new Bundle();
                args.putInt("type",type);
                args.putInt("Atype",alertType);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}

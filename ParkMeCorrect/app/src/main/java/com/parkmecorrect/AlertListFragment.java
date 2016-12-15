package com.parkmecorrect;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AlertListFragment extends Fragment {

    private RecyclerView mAlertRecyclerView;
    private AlertAdapter mAdapter;
    private int alertType;
    private int filterType;
    private String carNum;
    private Alert mAlert;


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        filterType = args.getInt("type");
        if(filterType == 1) {
            carNum = args.getString("Cnum");
        }
        else if(filterType == 2){
            alertType = args.getInt("Atype");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_alert_list, container, false);

        mAlertRecyclerView = (RecyclerView) view
                .findViewById(R.id.alert_recycler_view);
        mAlertRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())   {
            case R.id.menu_refresh:
                updateUI();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }



    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        AlertList alertList = AlertList.getInstance(getActivity());

        List<Alert> alerts = null;
        if(alertList != null) {
            alerts = new ArrayList<Alert>();
            alerts.addAll(alertList.getAlerts());
        }

        if(filterType==1) {
            List<Alert> filter = new ArrayList<Alert>();
            for(int i = 0; i < alerts.size(); i++){
                Alert al = alerts.get(i);
                if(al.getCar().carNo.equalsIgnoreCase(carNum)){
                    filter.add(al);
                }
            }
            alerts.clear();
            alerts.addAll(filter);
        }
        else if(filterType==2){
            List<Alert> filter = new ArrayList<Alert>();
            if(alertType == 0){
                for(int i = 0; i < alerts.size(); i++){
                    Alert al = alerts.get(i);
                    if(al.alertType.equalsIgnoreCase("wrong parking")){
                        filter.add(al);
                    }
                }
                alerts.clear();
                alerts.addAll(filter);
            }
            else if(alertType == 1){
                for(int i = 0; i < alerts.size(); i++){
                    Alert al = alerts.get(i);
                    if(al.alertType.equalsIgnoreCase("night parking")){
                        filter.add(al);
                    }
                }
                alerts.clear();
                alerts.addAll(filter);
            }
            else{
                for(int i = 0; i < alerts.size(); i++){
                    Alert al = alerts.get(i);
                    if(al.alertType.equalsIgnoreCase("locked")){
                        filter.add(al);
                    }
                }
                alerts.clear();
                alerts.addAll(filter);
            }
        }




            mAdapter = new AlertAdapter(alerts);
            mAlertRecyclerView.setAdapter(mAdapter);


    }

    private class AlertHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mSolvedCheckBox;

        private Alert mAlert;

        public AlertHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_alert_carno_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_alert_date_text_view);
            mSolvedCheckBox = (TextView) itemView.findViewById(R.id.list_item_alert_type_text_view);
        }

        public void bindAlert(Alert alert) {
            mAlert = alert;
           mTitleTextView.setText("Car No: "+mAlert.getCar().carNo);
            mDateTextView.setText("Alert Date: "+mAlert.getAlertDate());
            mSolvedCheckBox.setText("Alert Type: "+mAlert.getAlertType());
        }

        @Override
        public void onClick(View v) {
            Log.i("INFO","Iamclicked");
            String title = "Car: "+mAlert.getCar().carNo;
            StringBuffer buffer=new StringBuffer();

            buffer.append("StickerId: "+mAlert.getCar().sticker+"\n");
            buffer.append("Email Id: "+mAlert.getCar().email+"\n");

            showMessage(title, buffer.toString());

            /*Toast.makeText(getActivity(),
                    mAlert.getCar().carNo + " clicked!", Toast.LENGTH_SHORT)
                    .show();*/

            //Intent intent = new Intent(getActivity(), CrimeActivity.class);

            /*Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);*/
        }

        public void showMessage(String title,String message)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }
    }

    private class AlertAdapter extends RecyclerView.Adapter<AlertHolder> {

        private List<Alert> mAlerts;

        public AlertAdapter(List<Alert> alerts) {


            if(alerts != null) {
                mAlerts = alerts;
                //Toast.makeText(getActivity(),"Total alerts : "+getItemCount(),Toast.LENGTH_SHORT).show();
            } else {
                mAlerts = new ArrayList<Alert>();
                Toast.makeText(getActivity(),R.string.errorMessage,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public AlertHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_alert, parent, false);
            return new AlertHolder(view);
        }

        @Override
        public void onBindViewHolder(AlertHolder holder, int position) {
            Alert alert = mAlerts.get(position);
            holder.bindAlert(alert);
        }

        @Override
        public int getItemCount() {
            return mAlerts.size();
        }
    }
}

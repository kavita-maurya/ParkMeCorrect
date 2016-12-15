package com.parkmecorrect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


public class Alerts extends Fragment {

    private EditText mCarNo;
    private TextView mDisplay;
    private View mProgressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_alerts, container, false);
        mCarNo = (EditText)view.findViewById(R.id.carNumEditText) ;
        Button msendNAlert = (Button)view.findViewById(R.id.sendNightAlert);
        Button msendWAlert = (Button)view.findViewById(R.id.sendWrongParkingAlert);
        Button msendLAlert = (Button)view.findViewById(R.id.sendCarLockedAlert);
        Button mSearch = (Button)view.findViewById(R.id.search);
        mDisplay = (TextView) view.findViewById(R.id.output);
        mProgressView = view.findViewById(R.id.alert_progress);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNo = mCarNo.getText().toString();
                if(!carNo.isEmpty()) {
                    ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new SearchServerActivity(getActivity()).execute(carNo);
                    } else {
                        Toast.makeText(getActivity(), R.string.NetworkErr, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    View focusView = null;
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    focusView.requestFocus();
                }
            }
        });

        msendLAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNo = mCarNo.getText().toString();
                if(!carNo.isEmpty()) {
                    ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        showProgress(true);
                        new SendAlertActivity(getActivity()).execute(carNo,"Locked");
                    } else {
                        Toast.makeText(getActivity(), R.string.NetworkErr, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    View focusView = null;
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    focusView.requestFocus();
                }
            }
        });

        msendWAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNo = mCarNo.getText().toString();
                if(!carNo.isEmpty()) {
                    ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        showProgress(true);
                        new SendAlertActivity(getActivity()).execute(carNo,"wrong parking");
                    } else {
                        Toast.makeText(getActivity(), R.string.NetworkErr, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    View focusView = null;
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    focusView.requestFocus();
                }
            }
        });

        msendNAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNo = mCarNo.getText().toString();
                if(!carNo.isEmpty()) {
                    ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        showProgress(true);
                        new SendAlertActivity(getActivity()).execute(carNo,"night parking");
                    } else {
                        Toast.makeText(getActivity(), R.string.NetworkErr, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    View focusView = null;
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    focusView.requestFocus();
                }
            }
        });


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        //Log.d("in studentcheckin!!!","on setuserVisbileHint");
        if(!isVisibleToUser)
        {
            if(getView()!=null) {
                mDisplay.setText("");
                mCarNo.setText("");
            }
        }
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);



            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    private class SendAlertActivity extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public SendAlertActivity(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                String carNo = (String)arg0[0];
                String type = (String)arg0[1];


                String link = "http://192.168.57.4:8080/CarParkingServer/sendalert";

                InputStream stream = null;
                StringBuffer output = new StringBuffer("");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.setConnectTimeout(5000);
                httpConnection.connect();

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("carNo", carNo);
                postDataParams.put("alertType", type);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    String result = sb.toString();

                    Map<String,String> map = new Gson().fromJson(result, Map.class);
                    if(map.get("flag").equals("success")) {
                        return true;
                    }
                    return false;
                }
                Log.d("ServerActivity","Http response not ok");
                return false;
            } catch(Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
                return false;
            }
        }
        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(Boolean status){
            showProgress(false);
            if (status) {
                Toast.makeText(context, R.string.alertSend,Toast.LENGTH_SHORT).show();
                mCarNo.setText("");
            }
            else {
                Toast.makeText(context,R.string.errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class SearchServerActivity extends AsyncTask<String, Void, Map<String,String >> {

        private Context context;

        public SearchServerActivity(Context context) {
            this.context = context;
        }

        @Override
        protected Map<String ,String> doInBackground(String... arg0) {
            try {
                String carNo = (String)arg0[0];
                String link = "http://192.168.57.4:8080/CarParkingServer/carsearch";

                InputStream stream = null;
                StringBuffer output = new StringBuffer("");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("carNo", carNo);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    String result = sb.toString();

                    Map<String,String> map = new Gson().fromJson(result, Map.class);
                    return map;
                    //return finalOutput;
                }
                Log.d("ServerActivity","Http response not ok");
                return null;
            } catch(Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
            }
            return null;
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(Map<String, String> map){
            String car_no;
            String sticker_id;
            String checkin_time;

            if(map == null)
            {
                Toast.makeText(this.context,context.getString(R.string.errorMessage),Toast.LENGTH_SHORT).show();
            }
            else if(map.get("flag").equalsIgnoreCase("1")) {

                car_no = map.get("car_no");
                sticker_id = map.get("sticker_id");
                if(sticker_id.equals(""))
                {
                    sticker_id = "Guest";
                }
                checkin_time = map.get("checkin_time");
                String log = "Car No.: " + car_no + " \nSticker No: " + sticker_id + " \nCheckin Time : " + checkin_time ;
                mDisplay.setText(log);

            }
            else if(map.get("flag").equalsIgnoreCase("0"))
            {
                car_no = map.get("car_no");
                sticker_id = map.get("sticker_id");
                String log = "Car No.: " + car_no + " \nSticker No: " + sticker_id ;
                mDisplay.setText(log);

            }
            else
            {
                Toast.makeText(this.context, R.string.carNotRegistered,Toast.LENGTH_SHORT).show();
            }
        }
    }

}

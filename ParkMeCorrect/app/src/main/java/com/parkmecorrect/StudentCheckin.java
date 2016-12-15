package com.parkmecorrect;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by srishtichandok on 10/18/16.
 */
public class StudentCheckin extends Fragment {

    public TextView mDisplay;
    public Button mSearchButton;
    public Button mCheckinButton;
    public Button mCheckoutButton;
    EditText mCarNo;
    public String checkin_time;
    public String checkout_time;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("in studentcheckin!!!","on setuserVisbileHint");
        if(!isVisibleToUser)
        {
            if(getView()!=null) {
                mCheckinButton.setVisibility(View.GONE);
                mDisplay.setText("");
                mCarNo.setText("");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("studentcheckin!!!!","in guest checkin onCreate");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("studentcheckin!!!!","in guest checkin onAttach");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.getActivity().getFragmentManager().beginTransaction().addToBackStack(null);
        Log.d("studentcheckin!!!!"," onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("studentcheckin!!!!","on start");
    }


// Fragment is active

    @Override
    public void onPause() {
        super.onPause();
        Log.d("studentcheckin!!!!","onPAuse");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("studentcheckin!!!!","onStop");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("studentcheckin!!!!","onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("studentcheckin!!!!","onDestroyView");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("studentcheckin!!!!","on Resume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("studentcheckin!!!!","oncreateview");


        final RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_checkin_registered_user, container, false);
        mSearchButton = (Button)rl.findViewById(R.id.search);
        mCheckinButton = (Button) rl.findViewById(R.id.checkinTime);
        mCheckoutButton = (Button) rl.findViewById(R.id.checkoutTime);
        mCarNo = (EditText) rl.findViewById(R.id.editText1);
        mDisplay = (TextView) rl.findViewById(R.id.output);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View focusView = null;
                boolean cancel = false;
                if(TextUtils.isEmpty(mCarNo.getText().toString())){
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    cancel = true;
                }
                if(cancel) {
                    focusView.requestFocus();
                } else {
                    final String carNo;
                    carNo=mCarNo.getText().toString();
                    Log.d("inside retrieve",carNo);
                    mDisplay.setText("");
                    mCheckinButton.setVisibility(View.GONE);
                    mCheckoutButton.setVisibility(View.GONE);

                    ConnectivityManager  connMgr =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        new SearchServerActivity(getActivity()).execute(carNo);
                    }
                    else
                        Toast.makeText(getActivity(), R.string.NetworkErr,Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }

            }
        });

        mCheckinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String carNo;
                carNo = mCarNo.getText().toString();
                ConnectivityManager connMgr =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    new ParkingServerActivity(getActivity()).execute(carNo, "checkin");
                    mCarNo.setText("");
                    mCheckinButton.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getActivity(), R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                }

            }
        });

        mCheckoutButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   String carNo;
                                                   carNo = mCarNo.getText().toString();
                                                   String duration;
                                                   Time now = new Time();
                                                   now.setToNow();
                                                   int checkoutHour = now.hour;
                                                   int checkoutMin = now.minute;
                                                   int checkoutSecond = now.second;
                                                   checkout_time = String.valueOf(now.hour) + ":" + String.valueOf(now.minute) + ":" + String.valueOf(now.second);
                                                   String[] items = checkin_time.split(":");
                                                   int checkinHour = Integer.parseInt(items[0]);
                                                   int checkinMin = Integer.parseInt(items[1]);
                                                   int checkinSecond = Integer.parseInt(items[2]);
                                                   int checkin_in_seconds = checkinHour*60*60 + checkinMin*60 + checkinSecond;
                                                   int checkout_in_seconds = checkoutHour*60*60 + checkoutMin*60 + checkoutSecond;
                                                   int duration_in_seconds = checkout_in_seconds - checkin_in_seconds;
                                                   int durationHour  = (int) duration_in_seconds / 3600;
                                                   int remainder = (int) duration_in_seconds - durationHour * 3600;
                                                   int durationMin = remainder / 60;
                                                   remainder = remainder - durationMin * 60;
                                                   int durationSecond = remainder;
                                                   duration = durationHour+":"+durationMin+":"+durationSecond;
                                                   //Toast.makeText(getActivity(), "Duration of parking:" + durationHour + " hrs" + durationMin + " mins" + durationSecond + " secs", Toast.LENGTH_SHORT).show();
                                                   ConnectivityManager connMgr =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                   NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                                   if(networkInfo != null && networkInfo.isConnected()) {
                                                       new ParkingServerActivity(getActivity()).execute(carNo,"checkout",duration);
                                                       mCarNo.setText("");
                                                       mCheckoutButton.setVisibility(View.GONE);
                                                   }
                                                   else {
                                                       Toast.makeText(getActivity(), R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                                                   }
                                               }

                                           }
        );
        Log.d("in studentcheckin","oncreateview");
        return rl;
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
                mCheckoutButton.setVisibility(View.VISIBLE);
            }
            else if(map.get("flag").equalsIgnoreCase("0"))
            {
                car_no = map.get("car_no");
                sticker_id = map.get("sticker_id");
                String log = "Car No.: " + car_no + " \nSticker No: " + sticker_id ;
                checkin_time = "";
                checkout_time = "";
                mDisplay.setText(log);
                mCheckinButton.setVisibility(View.VISIBLE);
            }
            else
            {
                Toast.makeText(this.context, R.string.carNotRegistered,Toast.LENGTH_SHORT).show();
            }
        }
    }


    class ParkingServerActivity extends AsyncTask<String, Void, String> {

        private Context context;

        public ParkingServerActivity(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String carNo = (String)arg0[0];
                String status = (String)arg0[1];
                String dur=null;
                if(arg0.length == 3)
                    dur = (String)arg0[2];

                String link = "http://192.168.57.4:8080/CarParkingServer/insertparking";
                Time now = new Time();
                now.setToNow();

                String time = String.valueOf(now.hour) + ":" + String.valueOf(now.minute) + ":" + String.valueOf(now.second);
                if(status.equals("checkin"))
                    checkin_time = time;
                else
                    checkout_time = time;

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
                postDataParams.put("status",status);
                postDataParams.put("time",time);
                postDataParams.put("carType","Registered");


                if(dur!=null)
                    postDataParams.put("duration",dur);


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
                        if(status.equals("checkin"))
                            return "Checkin Time: "+checkin_time;
                        else
                            return "Checkout Time: "+checkout_time;
                    }
                    return null;
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
        protected void onPostExecute(String msg){

            if(msg!=null) {
                mDisplay.setText(mDisplay.getText().toString()+"\n"+msg);
                //
                //Toast.makeText(this.context, R.string.timeUpdated, Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(this.context, R.string.errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }


}

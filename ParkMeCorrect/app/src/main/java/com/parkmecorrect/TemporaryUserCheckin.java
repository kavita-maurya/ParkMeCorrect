package com.parkmecorrect;


import android.app.Activity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemporaryUserCheckin extends Fragment {
    EditText mCarNo;
    EditText mMobileNo;
    Button mCheckinButton;
    TextView mDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tempcheckin!!!!","in guest checkin onCreate");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("tempcheckin!!!!","in guest checkin onAttach");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.getActivity().getFragmentManager().beginTransaction().addToBackStack(null);
        Log.d("guest!!!!"," onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("in guest act","on start");
    }


// Fragment is active

    @Override
    public void onPause() {
        super.onPause();
        Log.d("in guest","onPAuse");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("in guest","onStop");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("in guest","onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("in guest","onDestroyView");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("in guest act^^^","on Resume");
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("in guest act^^^","on setuserVisbileHint");
        if(!isVisibleToUser)
        {
            if(getView()!=null) {
                mCheckinButton.setVisibility(View.VISIBLE);
                mDisplay.setText("");
            }
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_checkin_temporary_user, container, false);
        mCheckinButton = (Button) rl.findViewById(R.id.gcheckinTime);
        mCarNo = (EditText) rl.findViewById(R.id.carNo);
        mMobileNo = (EditText) rl.findViewById(R.id.mobNo);
        mDisplay = (TextView) rl.findViewById(R.id.goutput) ;
        mCheckinButton.setVisibility(View.VISIBLE);
        mDisplay.setText("");
        Log.d("tempcheckin!!!!","in guest checkin oncreateview");


        mCheckinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String car = mCarNo.getText().toString();
                String mob = mMobileNo.getText().toString();
                Pattern pattern = Pattern.compile("^[a-zA-Z]{2}[0-9a-zA-Z]{2}[a-zA-Z]{2}[0-9]{4}$");
                Matcher result = pattern.matcher(car);
                View focusView = null;
                boolean cancel = false;
                if(TextUtils.isEmpty(car)){
                    mCarNo.setError(getString(R.string.error_field_required));
                    focusView = mCarNo;
                    cancel = true;
                }
                else if(!result.find()){
                    mCarNo.setError("Invalid Car Number");
                    focusView = mCarNo;
                    cancel = true;
                }
                else if(TextUtils.isEmpty(mob)){
                    mMobileNo.setError(getString(R.string.error_field_required));
                    focusView = mMobileNo;
                    cancel = true;
                }

                else if(mob.startsWith("0")||mob.startsWith("1")||mob.startsWith("2")||mob.startsWith("3")||mob.startsWith("4")||mob.startsWith("5")||mob.startsWith("6")){
                    mMobileNo.setError(getString(R.string.error_invalid_mob));
                    focusView = mMobileNo;
                    cancel = true;
                }

                else if(mob.length()!=10 || !TextUtils.isDigitsOnly(mob)){
                    mMobileNo.setError(getString(R.string.error_invalid_mob));
                    focusView = mMobileNo;
                    cancel = true;
                }

                if(cancel) {
                    focusView.requestFocus();
                } else {
                    ConnectivityManager connMgr =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        new ParkingServerActivity(getActivity()).execute(car, mob, "checkin");
                        mCarNo.setText("");
                        mMobileNo.setText("");
                    }
                    else {
                        Toast.makeText(getActivity(), R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                    }
                }



            }

        });

        return rl;
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
                String mobNo = (String)arg0[1];
                String status = (String)arg0[2];


                String link = "http://192.168.57.4:8080/CarParkingServer/insertparking";
                Time now = new Time();
                now.setToNow();

                String time = String.valueOf(now.hour) + ":" + String.valueOf(now.minute) + ":" + String.valueOf(now.second);

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
                postDataParams.put("carType","Guest");
                postDataParams.put("mobileNo",mobNo);



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
                    String log = "Car No.: " + carNo + " \nSticker No: " + "Guest" ;
                    Map<String,String> map = new Gson().fromJson(result, Map.class);
                    if(map.get("flag").equals("success")) {
                        return log+ "\nCheckin Time: "+time;
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
                //Toast.makeText(this.context, R.string.timeUpdated, Toast.LENGTH_SHORT).show();
                mCheckinButton.setVisibility(View.GONE);
            }
            else {

                Toast.makeText(this.context, R.string.errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

}

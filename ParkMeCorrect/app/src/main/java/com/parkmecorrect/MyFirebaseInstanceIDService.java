package com.parkmecorrect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
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


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference), Context.MODE_PRIVATE);
        String car = sharedPrefProfile.getString("carno",null);
        if(car==null) {
            SharedPreferences sharedPrefToken = getSharedPreferences(getString(R.string.token_shared_pref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefToken.edit();
            editor.putString("token", token);
            editor.commit();
        }
        else{
            ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                new SaveTokenToServer(token,car).execute((Void)null);
            }
            else{
                Toast.makeText(this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveTokenToServer extends AsyncTask<Void, Void, Void> {


        String token;
        String carNum;
        SaveTokenToServer(String t,String car) {
            token = t;
            carNum = car;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String link = "http://192.168.57.4:8080/CarParkingServer/updatetokenserv";
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
                postDataParams.put("token", token);
                postDataParams.put("carNum", carNum);

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
                        Log.d("ServerActivity","Http response ok");
                    }
                    else
                        Log.d("ServerActivity","Http response failure");

                }
                Log.d("ServerActivity","Http response not ok");

            }
            catch (Exception e) {
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
        protected void onPostExecute(Void params) {

        }


    }
}
package com.parkmecorrect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private int REQUEST_CODE_SETTINGS=1;
    SharedPreferences sharedPrefLogin;
    SharedPreferences sharedPrefProfile;
    private EditText mCarNo;
    private View mProgressView;
    private String userType = "";
    private String langSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        sharedPrefLogin = getSharedPreferences(getString(R.string.login_shared_preference), Context.MODE_PRIVATE);
        mCarNo = (EditText)findViewById(R.id.carNumEditText) ;
        Button msendAlert = (Button)findViewById(R.id.sendAlert);
        mProgressView = findViewById(R.id.alert_progress);
        Intent intentFromLogin = getIntent();
        userType = intentFromLogin.getStringExtra("userType");
        sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference), Context.MODE_PRIVATE);

        msendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carNo = mCarNo.getText().toString();
                if(!carNo.isEmpty()) {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        showProgress(true);
                        new SendAlertActivity(UserActivity.this).execute(carNo);
                    } else {
                        Toast.makeText(UserActivity.this, R.string.NetworkErr, Toast.LENGTH_SHORT).show();
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startSettingsActivity.putExtra("userType",userType);
                startActivityForResult(startSettingsActivity,REQUEST_CODE_SETTINGS);
                return true;
            // User chose the "Settings" item, show the app settings UI...
            case R.id.action_logout:
                new Logout().logout(this);
                Locale locale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = locale;
                res.updateConfiguration(conf, dm);
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_language:
                String lang = sharedPrefLogin.getString("language",null);
                Log.d("@@@@@in checkAct",lang);
                if(lang.equals("en")) {
                    langSelected = "hi";
                    //Toast.makeText(getApplicationContext(), R.string.selected_hindi, Toast.LENGTH_SHORT).show();

                }
                else if(lang.equals("hi")) {
                    langSelected = "en";
                    //Toast.makeText(getApplicationContext(), R.string.selected_English, Toast.LENGTH_SHORT).show();
                }

                String regid = sharedPrefProfile.getString("regid", "");
                try {
                    ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        new UserActivity.UpdateServerActivity(UserActivity.this).execute("updateLang", langSelected, regid).get();
                    }
                    else {
                        Toast.makeText(UserActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                    }

                } catch(Exception e) {
                    Log.d("UserActivity","Exception");
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    public void setLocale(String lang) {
        Locale locale;
        locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        SharedPreferences.Editor editor1 = sharedPrefLogin.edit();
        SharedPreferences.Editor editor2 = sharedPrefProfile.edit();
        editor1.putString("language", lang);
        editor2.putString("language", lang);
        editor1.commit();
        editor2.commit();
        Intent refresh = new Intent(this, UserActivity.class);
        finish();
        startActivity(refresh);

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
                postDataParams.put("alertType", "wrong parking");


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
                    else if(map.get("flag").equals("notfound")){
                        return false;
                    }
                    return null;
                }
                Log.d("ServerActivity","Http response not ok");
                return null;
            } catch(Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
                return null;
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
            if(status==null)
                Toast.makeText(context,R.string.errorMessage,Toast.LENGTH_SHORT).show();
            else if (status) {
                Toast.makeText(context, R.string.alertSend,Toast.LENGTH_SHORT).show();
                mCarNo.setText("");
            }
            else {
                Toast.makeText(context,"Could Not Identify the user",Toast.LENGTH_SHORT).show();
            }
        }
    }



    public class UpdateServerActivity extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public UpdateServerActivity(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                String fieldToBeUpdated = (String) arg0[0];
                String newValue = (String) arg0[1];
                String regId = (String) arg0[2];
                String link = "http://192.168.57.4:8080/CarParkingServer/updatelangserv";

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
                if (fieldToBeUpdated.equals("updateLang")) {
                    postDataParams.put("updatedvalue", newValue);
                    postDataParams.put("regid", regId);
                }

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    String result = sb.toString();

                    Map<String, String> map = new Gson().fromJson(result, Map.class);

                    if (map.get("flag").equals("success")) {
                        if (newValue.equalsIgnoreCase("hi")) {
                            setLocale("hi");
                        } else {
                            setLocale("en");
                        }
                        return true;
                    }

                    return false;
                    //return finalOutput;
                }
                Log.d("ServerActivity", "Http response not ok");
                return false;
            } catch (Exception e) {
                Log.d("ServerActivity", "Exception");
                e.printStackTrace();
            }
            return false;
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
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
        protected void onPostExecute(Boolean status) {
            if (status) {
                //Toast.makeText(this.context, "Updated", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this.context, "Could not update.", Toast.LENGTH_SHORT).show();
            }
        }
    }



}

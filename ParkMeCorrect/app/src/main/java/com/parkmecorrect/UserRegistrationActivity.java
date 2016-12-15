package com.parkmecorrect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationActivity extends AppCompatActivity {

    private Button mSave;
    private EditText mName;
    private EditText mCarNo;
    private EditText mSticker;
    private EditText mMob;
    private EditText mEmail;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mSave = (Button) findViewById(R.id.save);
        mName = (EditText) findViewById(R.id.eName);
        mCarNo = (EditText) findViewById(R.id.eCarNum);
        mSticker = (EditText) findViewById(R.id.eSticker);
        mMob = (EditText) findViewById(R.id.eMobNum);
        mEmail = (EditText) findViewById(R.id.eEmail);
        mProgressView = findViewById(R.id.register_progress);


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSave();
            }
        });
    }

    private void attemptSave(){
        mEmail.setError(null);
        mName.setError(null);
        mCarNo.setError(null);
        mSticker.setError(null);
        mMob.setError(null);

        String name = mName.getText().toString();
        String mob = mMob.getText().toString();
        String car = mCarNo.getText().toString();
        String email = mEmail.getText().toString();
        String stick = mSticker.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile("^[a-zA-Z]{2}[0-9a-zA-Z]{2}[a-zA-Z]{2}[0-9]{4}$");
        Matcher result = pattern.matcher(car);

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name)){
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        }

        else if(TextUtils.isEmpty(car)){
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
            mMob.setError(getString(R.string.error_field_required));
            focusView = mMob;
            cancel = true;
        }

        else if(mob.length()!=10 || !TextUtils.isDigitsOnly(mob)){
            mMob.setError(getString(R.string.error_invalid_mob));
            focusView = mMob;
            cancel = true;
        }

        else if(mob.startsWith("0")||mob.startsWith("1")||mob.startsWith("2")||mob.startsWith("3")||mob.startsWith("4")||mob.startsWith("5")||mob.startsWith("6")){
            mMob.setError(getString(R.string.error_invalid_mob));
            focusView = mMob;
            cancel = true;
        }

        else if(TextUtils.isEmpty(email)){
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }

        else if (!email.matches(emailPattern))
        {
            mEmail.setError(getString(R.string.incorrect_email));
            focusView = mEmail;
            cancel = true;
        }

        else if(!email.contains("@iiitd.ac.in")){
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        else if(TextUtils.isEmpty(stick)){
            mSticker.setError(getString(R.string.error_field_required));
            focusView = mSticker;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else{
            ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                showProgress(true);
                new RegisterServerActivity(UserRegistrationActivity.this).execute(car,stick,mob,email,name);
            }
            else {
                Toast.makeText(this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed(){
        this.finish();
    }

    public class RegisterServerActivity extends AsyncTask<String, Void, Integer> {

        private Context context;

        public RegisterServerActivity(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            try {
                String carNo = (String)arg0[0];
                String stickNo = (String)arg0[1];
                String mobNo = (String)arg0[2];
                String emailId = (String)arg0[3];
                String name = (String)arg0[4];

                String link = "http://192.168.57.4:8080/CarParkingServer/studreg";

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
                postDataParams.put("stickNo", stickNo);
                postDataParams.put("mobNo", mobNo);
                postDataParams.put("email", emailId);
                postDataParams.put("name", name);


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
                        return 0;
                    }
                    else if(map.get("flag").equals("DuplicateC"))
                    {
                        return 3;
                    }
                    return 2;
                }
                Log.d("ServerActivity","Http response not ok");
                return 2;
            } catch(Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
                return 2;
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
        protected void onPostExecute(Integer status){
            showProgress(false);
            if (status==0) {
                Toast.makeText(context, R.string.regSuccess,Toast.LENGTH_SHORT).show();
                mEmail.setText("");
                mName.setText("");
                mMob.setText("");
                mSticker.setText("");
                mCarNo.setText("");
            }
            else if(status==3)
            {
                View focusView = null;
                focusView = mCarNo;
                mCarNo.setError(getString(R.string.carNumAvailabilty));
                focusView.requestFocus();
            }
            else {
                Toast.makeText(context,R.string.errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}

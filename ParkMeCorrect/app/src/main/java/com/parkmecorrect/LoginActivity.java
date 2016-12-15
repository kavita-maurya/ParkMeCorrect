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
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private boolean loginSuccess = false;
    private String userType = "";
    private String userLang="";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mNewUser;
    SharedPreferences sharedPrefLogin;
    SharedPreferences sharedPrefProfile;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LoginActivity","In onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPrefLogin = getSharedPreferences(getString(R.string.login_shared_preference),Context.MODE_PRIVATE);
        sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference), Context.MODE_PRIVATE);

        String user = sharedPrefLogin.getString("userId",null);
        String pass = sharedPrefLogin.getString("pwd",null);
        userType = sharedPrefLogin.getString("type",null);
        String lang = sharedPrefLogin.getString("language",null);

        if(user!=null && pass!=null && lang!=null){

            locale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
            SharedPreferences sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference),Context.MODE_PRIVATE);
            String carNum = sharedPrefProfile.getString("carno",null);
            if(carNum!=null)
                sendTokentoServer(carNum);
            Intent i = getReqiredIntent(userType);
            startActivity(i);
            finish();
        }
        else {
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
        mNewUser = (TextView) findViewById(R.id.newUser);
        mNewUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, NewUserRegistration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
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
        editor1.putString("language", lang);
        editor1.commit();
        Intent refresh = new Intent(this, LoginActivity.class);
        finish();
        startActivity(refresh);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_language:
                String lang = sharedPrefLogin.getString("language",null);
                Log.d("login",String.valueOf(Resources.getSystem().getConfiguration().locale));

                if(lang!=null && lang.contains("hi")) {
                    setLocale("en");
                    //Toast.makeText(getApplicationContext(), "You have selected English", Toast.LENGTH_SHORT).show();
                    Log.d("login", "in if");
                }
                else if(lang!=null && lang.contains("en")) {
                    setLocale("hi");
                    //Toast.makeText(getApplicationContext(),R.string.selected_hindi , Toast.LENGTH_SHORT).show();
                    Log.d("login", "in el if 1");
                }
                else if(String.valueOf(Resources.getSystem().getConfiguration().locale).contains("en")) {
                    setLocale("hi");
                    //Toast.makeText(getApplicationContext(), R.string.selected_hindi, Toast.LENGTH_SHORT).show();
                    Log.d("login", "in elif 2");
                }
                else if(String.valueOf(Resources.getSystem().getConfiguration().locale).contains("hi")) {
                    setLocale("en");
                    //Toast.makeText(getApplicationContext(), "You have selected English", Toast.LENGTH_SHORT).show();
                    Log.d("login", "in el if 3");
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void sendTokentoServer(String carNum){
        SharedPreferences sharedPrefToken = getSharedPreferences(getString(R.string.token_shared_pref), Context.MODE_PRIVATE);
        String token = sharedPrefToken.getString("token",null);
        if(token!=null){
            if(carNum!=null) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new SaveTokenToServer(token, carNum).execute((Void) null);
                } else {
                    Toast.makeText(this, R.string.NetworkErr, Toast.LENGTH_SHORT).show();
                }
            }
            /*SharedPreferences.Editor editor = sharedPrefToken.edit();
            editor.remove("token");
            editor.commit();*/
        }
    }

    private Intent getReqiredIntent(String user){
        if(user.equalsIgnoreCase("admin")){
            return new Intent(LoginActivity.this,AdminActivity.class);
        }
        else if(user.equalsIgnoreCase("guard")){
            return new Intent(LoginActivity.this,GuardActivity.class);
        }
        else {
            Intent i = new Intent(LoginActivity.this, UserActivity.class);
            i.putExtra("userType",userType);
            return i;

        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }


        mEmailView.setError(null);
        mPasswordView.setError(null);


        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        else if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }




        if (cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            ConnectivityManager  connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()) {
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

            }
            else {
                showProgress(false);
                Toast.makeText(LoginActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LoginActivity","In onResume");
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                String link = "http://192.168.57.4:8080/CarParkingServer/pserv";
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
                postDataParams.put("userid", mEmail);
                postDataParams.put("password",mPassword);

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
                    if(map.get("Login").equals("success")) {
                        userType = map.get("user_type");
                        userLang = map.get("language");
                        Log.d("user lang at login ^^^^",userLang);
                        SharedPreferences sharedPrefLogin = getSharedPreferences(getString(R.string.login_shared_preference),Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = sharedPrefLogin.edit();
                        editor3.putString("language",map.get("language"));
                        editor3.commit();
                        setLocale(userLang);
                        Log.d("@@@in login",userType);
                        if(userType.equalsIgnoreCase("Student")) {
                            SharedPreferences sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference),Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPrefProfile.edit();

                            editor2.putString("regid",map.get("regid"));
                            editor2.putString("carno",map.get("carno"));
                            editor2.putString("sticker",map.get("sticker"));
                            editor2.putString("name",map.get("name"));
                            editor2.putString("mobile",map.get("mobile"));
                            editor2.putString("email",map.get("email"));
                            editor2.putString("language",map.get("language"));
                            editor2.commit();


                        }
                        else if(userType.equalsIgnoreCase("outsider")){
                            SharedPreferences sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference),Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPrefProfile.edit();

                            editor2.putString("regid",map.get("regid"));
                            editor2.putString("carno",map.get("carno"));
                            editor2.putString("name",map.get("name"));
                            editor2.putString("mobile",map.get("mobile"));
                            editor2.putString("email",map.get("email"));
                            editor2.putString("language",map.get("language"));
                            editor2.commit();
                        }
                        else if(userType.equalsIgnoreCase("guard"))
                        {
                            Log.d("guest lang@@@@",map.get("language"));
                            SharedPreferences.Editor editor2 = sharedPrefLogin.edit();
                            editor2.putString("language",map.get("language"));
                            editor2.commit();
                            Log.d("@@@sharedLogin updte gu",sharedPrefLogin.getString("language",null));
                        }
                        else
                            sendTokentoServer(null);

                        return true;
                    }
                    return false;
                }
                Log.d("ServerActivity","Http response not ok");
                return null;
            }
            catch (Exception e) {
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
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if(success==null){
                Toast.makeText(LoginActivity.this,R.string.errorMessage,Toast.LENGTH_SHORT).show();
            }
            else if (success) {
                loginSuccess = true;
                //Toast.makeText(LoginActivity.this,"Login success as "+userType,Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPrefLogin = getSharedPreferences(getString(R.string.login_shared_preference),Context.MODE_PRIVATE);

                SharedPreferences.Editor editor1 = sharedPrefLogin.edit();


                editor1.putString("userId", mEmail);
                editor1.putString("pwd", mPassword);
                editor1.putString("type",userType);
                editor1.putString("language", userLang);

                editor1.commit();

                SharedPreferences sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference),Context.MODE_PRIVATE);
                String carNum = sharedPrefProfile.getString("carno",null);
                if(carNum!=null)
                    sendTokentoServer(carNum);


                Intent i = getReqiredIntent(userType);
                startActivity(i);
                finish();
            } else {
                loginSuccess = false;
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}


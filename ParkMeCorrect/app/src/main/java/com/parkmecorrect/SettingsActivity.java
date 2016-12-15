package com.parkmecorrect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class SettingsActivity extends AppCompatActivity {
    public String MyPREFERENCES = null;
    public String PasswordPREFERENCES = null;
    public static final String Name = "name";
    public static final String StickerNo = "sticker";
    public static final String Email = "email";
    public static final String CarNo = "carno";
    public static final String MobileNo = "mobile";
    public static final String Password = "pwd";
    public static final String RegId = "regid";
    TextView mName;
    TextView mEmail;
    TextView mStickerNo;
    TextView mCarNo;
    TextView mMobileNo;
    TextView mRegId;
    ImageButton mChangePasswordButton;
    ImageButton mChangeMobileNo;
    private String userType = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        MyPREFERENCES = getString(R.string.profile_shared_preference);
        PasswordPREFERENCES = getString(R.string.login_shared_preference);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mStickerNo = (TextView) findViewById(R.id.stickerNo);
        mCarNo = (TextView) findViewById(R.id.car);
        mMobileNo = (TextView) findViewById(R.id.mobile);
        mChangePasswordButton = (ImageButton) findViewById(R.id.changePass);
        mChangeMobileNo = (ImageButton) findViewById(R.id.changeMob);
        mRegId = (TextView) findViewById(R.id.regId);

        Intent intentFromLogin = getIntent();
        userType = intentFromLogin.getStringExtra("userType");


        if (sharedpreferences.contains(Name)) {
            mName.setText(sharedpreferences.getString(Name, ""));
        }
        if (sharedpreferences.contains(Email)) {
            mEmail.setText(mEmail.getText().toString()+" "+sharedpreferences.getString(Email, ""));

        }
        if (sharedpreferences.contains(CarNo)) {
            mCarNo.setText(mCarNo.getText().toString()+" "+sharedpreferences.getString(CarNo, ""));

        }
        if (sharedpreferences.contains(StickerNo)) {
            mStickerNo.setText(mStickerNo.getText().toString()+" "+sharedpreferences.getString(StickerNo, ""));

        }
        else
        {
            mStickerNo.setVisibility(View.GONE);
        }
        if (sharedpreferences.contains(MobileNo)) {
            mMobileNo.setText(mMobileNo.getText().toString()+" "+sharedpreferences.getString(MobileNo, ""));

        }
        if (sharedpreferences.contains(RegId)) {
            mRegId.setText(mRegId.getText().toString()+" "+sharedpreferences.getString(RegId, ""));

        }


        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
                View promptsView = li.inflate(R.layout.change_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setTitle(R.string.update_password);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText newPassword = (EditText) promptsView
                        .findViewById(R.id.newPass);
                final EditText oldPassword = (EditText) promptsView
                        .findViewById(R.id.oldPass);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.update,null)
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String newPasswordString = newPassword.getText().toString();
                                String oldPasswordString = oldPassword.getText().toString();
                                newPassword.setError(null);
                                oldPassword.setError(null);
                                SharedPreferences sharedpreferencesPassword = getSharedPreferences(PasswordPREFERENCES, Context.MODE_PRIVATE);getSharedPreferences(PasswordPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                                if(oldPasswordString.equals(""))
                                {
                                    oldPassword.setError(getString(R.string.error_old_password));
                                }
                                else if(newPasswordString.equals(""))
                                {
                                    newPassword.setError(getString(R.string.error_new_password));
                                }
                                else if(!(oldPasswordString.equals(sharedpreferencesPassword.getString(Password, ""))))
                                {
                                    oldPassword.setError(getString(R.string.wrong_password));
                                }
                                else {
                                    String regid = sharedPreferences.getString(RegId, "");
                                    try {
                                        ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                        if(networkInfo != null && networkInfo.isConnected()) {
                                            new UpdateServerActivity(SettingsActivity.this).execute("updatePassword", newPasswordString, regid).get();
                                        }
                                        else {
                                            Toast.makeText(SettingsActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                                        }

                                    } catch(Exception e) {
                                        Log.d("SettingActivity","Exception");
                                    }

                                    alertDialog.dismiss();
                                }


                            }
                        });
                    }
                });
                // show it
                alertDialog.show();

            }
        });


        mChangeMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
                View promptsView = li.inflate(R.layout.change_mobile, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setTitle(R.string.update_mobile);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText newMobileNo = (EditText) promptsView.findViewById(R.id.newMobile);


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(R.string.update,null)
                        .setNegativeButton(R.string.cancel,

                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();


                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                final String newMobileString = newMobileNo.getText().toString();
                                newMobileNo.setError(null);
                                if(newMobileString.equals(""))
                                {
                                    newMobileNo.setError("Mobile Number can't be empty");
                                }
                                else if(newMobileString.length()!=10 && TextUtils.isDigitsOnly(newMobileString)) {
                                    newMobileNo.setError(getString(R.string.error_invalid_mobile));
                                    //Toast.makeText(getApplicationContext(), "Invalid mobile", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mMobileNo.setText("Mobile Number: " + newMobileString);
                                    SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                    String regid = sharedpreferences.getString(RegId, "");
                                    try {
                                        ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                        if(networkInfo != null && networkInfo.isConnected()) {
                                            new UpdateServerActivity(SettingsActivity.this).execute("updateMobile", newMobileString, regid).get();
                                        }
                                        else {
                                            Toast.makeText(SettingsActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                                        }

                                    } catch(Exception e) {
                                        Log.d("SettingActivity","Exception");
                                    }
                                    alertDialog.dismiss();
                                }

                            }
                        });
                    }
                });

                // show it
                alertDialog.show();
            }


            /*public void show(AlertDialog.Builder alertDialogBuilder) {
                //canceled = false;
                alertDialogBuilder.show();
            }*/


        });
    }


    @Override
    public void onBackPressed(){
        this.finish();
    }

    public class UpdateServerActivity extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public UpdateServerActivity(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                String fieldToBeUpdated = (String)arg0[0];
                String newValue = (String)arg0[1];
                String regId = (String)arg0[2];
                String link = "http://192.168.57.4:8080/CarParkingServer/updateserv";

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
                if(fieldToBeUpdated.equals("updateMobile")) {
                    postDataParams.put("updatedvalue", newValue);
                    postDataParams.put("flag", "mobileNo");
                    postDataParams.put("regid", regId);
                    postDataParams.put("flag2",userType);
                }
                if(fieldToBeUpdated.equals("updatePassword")) {
                    postDataParams.put("updatedvalue", newValue);
                    postDataParams.put("flag", "password");
                    postDataParams.put("regid", regId);
                }
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
                        if(fieldToBeUpdated.equalsIgnoreCase("password")) {
                            SharedPreferences sharedpreferencesPassword = getSharedPreferences(PasswordPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferencesPassword.edit();
                            editor.putString(Password, newValue);
                            editor.commit();
                        }
                        else{
                            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(MobileNo, newValue);
                            editor.commit();
                        }
                        return true;
                    }

                    return false;
                    //return finalOutput;
                }
                Log.d("ServerActivity","Http response not ok");
                return false;
            } catch(Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
            }
            return false;
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
        protected void onPostExecute(Boolean status) {
            if(status) {
                //Toast.makeText(this.context,"Updated", Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(this.context, "Could not update.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

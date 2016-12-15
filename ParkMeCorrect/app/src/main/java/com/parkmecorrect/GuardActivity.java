package com.parkmecorrect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

public class GuardActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    SharedPreferences sharedPrefLogin;
    SharedPreferences sharedPrefProfile;
    private String langSelected="";


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefLogin = getSharedPreferences(getString(R.string.login_shared_preference), Context.MODE_PRIVATE);
        sharedPrefProfile = getSharedPreferences(getString(R.string.profile_shared_preference), Context.MODE_PRIVATE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.destroyDrawingCache();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new Logout().logout(this);
                Locale locale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = locale;
                res.updateConfiguration(conf, dm);
                Intent intent = new Intent(GuardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                return true;
            case R.id.action_language:
                String lang = sharedPrefLogin.getString("language",null);
                Log.d("@@@@@in guardAct",lang);
                if(lang.equals("en")) {
                    langSelected = "hi";
                    Log.d("@@@in guardAct",langSelected);
                    //Toast.makeText
                    // (getApplicationContext(), R.string.selected_hindi, Toast.LENGTH_SHORT).show();

                }
                else if(lang.equals("hi")) {
                    langSelected = "en";
                    //Toast.makeText(getApplicationContext(), R.string.selected_English, Toast.LENGTH_SHORT).show();
                }

                String regid = sharedPrefLogin.getString("userId",null);
                Log.d("in guard@@@",regid);
                try {
                    ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        new GuardActivity.UpdateServerActivity(GuardActivity.this).execute("updateLang", langSelected, regid).get();
                    }
                    else {
                        Toast.makeText(GuardActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
                    }

                } catch(Exception e) {
                    Log.d("GuardActivity","Exception");
                }

            default:
                return super.onOptionsItemSelected(item);
        }
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
        Intent refresh = new Intent(this, GuardActivity.class);
        finish();
        startActivity(refresh);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1) {


                View rootView = inflater.inflate(R.layout.fragment_checkin_registered_user, container, false);
                return rootView;
            }
            else  if(getArguments().getInt(ARG_SECTION_NUMBER)==2) {
                View rootView = inflater.inflate(R.layout.fragment_checkin_temporary_user, container, false);
                Button mCheckinButton = (Button) rootView.findViewById(R.id.gcheckinTime);
                TextView mDisplay = (TextView) rootView.findViewById(R.id.goutput) ;
                mCheckinButton.setVisibility(View.VISIBLE);
                mDisplay.setText("");
                return rootView;
            }
            else {


                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position)
            {
                case 0: return new StudentCheckin();
                case 1: return new TemporaryUserCheckin();
                case 2: return new Alerts();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.checkin_checkout);
                case 1:
                    return getString(R.string.guest_checkin);
                case 2:
                    return getString(R.string.alerts);
            }
            return null;
        }
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
                    Log.d("@@in guard's updateserv",newValue);
                    if (map.get("flag").equals("success")) {
                        Log.d("@@in g's updateserv'if",newValue);
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

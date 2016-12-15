package com.parkmecorrect;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParkingHistoryActivity extends AppCompatActivity {

    private LinearLayout mMainLayout;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_history);
        mMainLayout = (LinearLayout) findViewById(R.id.activity_parking_history);
        scrollView = (ScrollView) findViewById(R.id.activity_parking_history_sv);

        ImageButton mDateButton = (ImageButton) findViewById(R.id.date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    public void display(ArrayList<HashMap<String,String>> parkingList) {
        String[] column = {"CarNumber","CheckIn","CheckOut"};
        int cl = column.length;
        TableLayout TblLayout = (TableLayout) findViewById(R.id.TblLayout);
        scrollView.removeView(TblLayout);

        TableLayout tableLayout = createTableLayout(column, cl,parkingList);
        scrollView.addView(tableLayout);
    }

    public void setTextAppearance(TextView v, Context context, int resId) {

        if (Build.VERSION.SDK_INT < 23) {
            v.setTextAppearance(context, resId);
        } else {
            v.setTextAppearance(resId);
        }
        v.setTextColor(Color.parseColor("#000000"));
    }

    public void setTextAppearanceHeader(TextView v, Context context, int resId) {

        if (Build.VERSION.SDK_INT < 23) {
            v.setTextAppearance(context, resId);
        } else {
            v.setTextAppearance(resId);
        }
        v.setTextColor(Color.parseColor("#000000"));
        v.setTypeface(Typeface.DEFAULT_BOLD);
    }


    private TableLayout createTableLayout(String[] cv, int columnCount,ArrayList<HashMap<String,String>> parkingList) {


        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        tableLayoutParams.setMargins(0,50,0,0);

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setId(R.id.TblLayout);

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(0,0,30,0);
        tableRowParams.weight = 1;




        TableRow tableRow = new TableRow(this);
        tableRow.setMinimumHeight(100);

        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(cv[0]);
        textView.setLayoutParams(tableRowParams);
        setTextAppearanceHeader(textView,this,android.R.style.TextAppearance_Medium);
        tableRow.addView(textView, tableRowParams);

        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(cv[1]);
        textView.setLayoutParams(tableRowParams);
        setTextAppearanceHeader(textView,this,android.R.style.TextAppearance_Medium);
        tableRow.addView(textView, tableRowParams);

        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(cv[2]);
        textView.setLayoutParams(tableRowParams);
        setTextAppearanceHeader(textView,this,android.R.style.TextAppearance_Medium);
        tableRow.addView(textView, tableRowParams);

        tableRow.setBackgroundResource(R.drawable.header_row_border);
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setMinimumHeight(150);
        tableLayout.addView(tableRow, tableLayoutParams);


        for(int i=0;i<parkingList.size();i++) {
            Map<String,String> row = parkingList.get(i);

            tableRow = new TableRow(this);
            tableRow.setBackgroundResource(R.drawable.row_border);

            textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            String data = row.get("carno")+ "\n" + row.get("type");;
            textView.setText(data);
            textView.setLayoutParams(tableRowParams);
            setTextAppearance(textView,this,android.R.style.TextAppearance_Medium);
            tableRow.addView(textView, tableRowParams);

            textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            data = row.get("checkin") + "\n" + row.get("checkindate");
            textView.setText(data);
            textView.setLayoutParams(tableRowParams);
            setTextAppearance(textView,this,android.R.style.TextAppearance_Medium);
            tableRow.addView(textView, tableRowParams);


            textView = new TextView(this);
            textView.setGravity(Gravity.CENTER);
            if(row.get("checkout")!=null)
                data = row.get("checkout") + "\n" + row.get("checkoutdate");
            else
                data="";
            textView.setText(data);
            textView.setLayoutParams(tableRowParams);
            setTextAppearance(textView,this,android.R.style.TextAppearance_Medium);
            tableRow.addView(textView, tableRowParams);


            tableLayout.addView(tableRow, tableLayoutParams);

        }
        return tableLayout;
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getFragmentManager(),"Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDate(year,monthOfYear,dayOfMonth);
        }
    };


    private void setDate(int yr,int mnth,int day){
        ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            new GetParkingDataTask(day + "-" + (mnth + 1) + "-" + yr).execute((Void) null);
        }
        else
            Toast.makeText(ParkingHistoryActivity.this, R.string.NetworkErr,Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onBackPressed(){
        this.finish();
    }

    private class GetParkingDataTask extends AsyncTask<Void, Void, ArrayList<HashMap<String,String>>> {

        private String date;


        GetParkingDataTask(String date) {
            this.date = date;
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
            try {

                String link = "http://192.168.57.4:8080/CarParkingServer/parkhistoryserv";
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
                postDataParams.put("date", date);

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

                    Map<String,Object> map = new Gson().fromJson(result, Map.class);
                    ArrayList<HashMap<String,String>> parkingList = (ArrayList<HashMap<String,String>>)map.get("parkinglist");
                    if(((String)map.get("flag")).equals("success")) {
                            return parkingList;
                    }
                    return null;
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
        protected void onPostExecute(ArrayList<HashMap<String,String>> parkingList) {
            if (parkingList != null) {
                if (parkingList.size() == 0) {
                    Toast.makeText(ParkingHistoryActivity.this, "No Data Availabe for this date", Toast.LENGTH_SHORT).show();
                    TableLayout TblLayout = (TableLayout) findViewById(R.id.TblLayout);
                    scrollView.removeView(TblLayout);
                }
                else
                    display(parkingList);
            }
            else
                Toast.makeText(ParkingHistoryActivity.this, R.string.errorMessage, Toast.LENGTH_SHORT).show();
        }

    }

}

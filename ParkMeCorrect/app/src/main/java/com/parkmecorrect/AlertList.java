package com.parkmecorrect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlertList {
    private static AlertList instance;

    private static ArrayList<Alert> alerts;

    public static AlertList getInstance(Context context) {
        if(alerts!=null && alerts.size()==0)
            setInstance();
        if(instance == null) {
            instance = new AlertList(context);
        }
        return instance;
    }

    public static void setInstance() {
        AlertList.instance = null;
    }

    private AlertList(Context context) {
        alerts = new ArrayList<Alert>();
        try{
            ConnectivityManager connMgr =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                new AlertTask(context).execute().get();
            }
            else{
                Toast.makeText(context, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
            }
            Log.d("*****","*******");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getAlertsFromServer(Context context){
        try{
            ConnectivityManager connMgr =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                new AlertTask(context).execute().get();
            }
            else{
                Toast.makeText(context, R.string.NetworkErr,Toast.LENGTH_SHORT).show();
            }
            Log.d("*****","*******");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public Alert getAlert(String carNo) {
        return null;
    }



    public class AlertTask extends AsyncTask<Void,Void,Boolean>{


        private Context context;

        public AlertTask(Context context) {
            this.context = context;
        }



        protected Boolean doInBackground(Void... params) {
            alerts = new ArrayList<Alert>();
            try {
                //Thread.sleep(2000);
                Log.d("AlertList","Setting the server");
                String link = "http://192.168.57.4:8080/CarParkingServer/historyserv";
                InputStream stream = null;
                StringBuffer output = new StringBuffer("");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                Log.d("AlertList","Connecting to server");
                httpConnection.setConnectTimeout(5000);
                httpConnection.connect();
                Log.d("AlertList","Connected to the server");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("flag", "success");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                Log.d("AlertList","Checking response code");
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("AlertList","Response code ok");
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

                    Map<String,ArrayList<HashMap<String,String>>> map = new Gson().fromJson(result, Map.class);
                    ArrayList<HashMap<String,String>> alertList = map.get("alertlist");
                    Log.d("Before for","size"+alerts.size());
                    for(int i=0;i<alertList.size();i++) {
                        //Alert alert = new Alert(alertList.get(i).getCar(),alertList.get(i).getAlertType(),alertList.get(i).getAlertDate());
                        //Log.d("Inside for","size"+alertList.get(i).getAlertDate());
                        Map<String,String> hs = alertList.get(i);

                        Car car = new Car(hs.get("carno"),hs.get("stickerid"),hs.get("email"));
                        Alert alert = new Alert(car,hs.get("alerttype"),hs.get("alertdate"));
                        alerts.add(alert);
                    }
                    Log.d("After for","size"+alerts.size());
                    return true;
                }
                Log.d("ServerActivity","Http response not ok");
                return false;
            }
            catch (Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
                //Toast.makeText(context, R.string.errorMessage, Toast.LENGTH_SHORT).show();
                alerts = null;
                return false;
            }


            /*if(userId.equals(mEmail) && pswd.equals(mPassword))
                return true;*/

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


        protected void onPostExecute(final Boolean success) {
           // AlertList alertList = AlertList.getInstance(context, alerts);
            //Log.d("AlertActi","size"+alerts.size());
            Log.d("AlertListActivity","postExecute");
        }
    }

}

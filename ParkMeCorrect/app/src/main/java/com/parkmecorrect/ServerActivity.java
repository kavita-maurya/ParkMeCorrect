package com.parkmecorrect;

import android.content.Context;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Anurag on 10/4/2016.
 */
public class ServerActivity extends AsyncTask<String, Void, String> {

    private Context context;

    public ServerActivity(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String userid = (String)arg0[0];
            String password = (String)arg0[1];
            String link = "http://192.168.57.4:8080/CarParkingServer/pserv";

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
            postDataParams.put("userid", userid);
            postDataParams.put("password", password);

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
                String finalOutput = "";

                Map<String,String> map = new Gson().fromJson(result, Map.class);
                if(map.get("Login").equalsIgnoreCase("Success")) {
                    finalOutput = "Login successful as "+map.get("user_type");
                }
                return finalOutput;
            }
            Log.d("ServerActivity","Http response not ok");
            return "";
        } catch(Exception e) {
            Log.d("ServerActivity","Exception");
            e.printStackTrace();
        }
        return "";
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
    protected void onPostExecute(String result){
        if(result=="")
            Toast.makeText(this.context, "Login fails......",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this.context, result,Toast.LENGTH_LONG).show();
    }
}

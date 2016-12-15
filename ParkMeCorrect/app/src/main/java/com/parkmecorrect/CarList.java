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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CarList {
    private static CarList instance;

    private static  ArrayList<Car> parkedCars;

    public static CarList getInstance(Context context) {
        if(parkedCars!=null && parkedCars.size()==0)
            setInstance();
        if(instance == null) {
            instance = new CarList(context);
        }
        return instance;
    }

    public static void setInstance() {
        CarList.instance = null;
    }


    private CarList(Context context) {
        parkedCars = new ArrayList<Car>();
        try{
            ConnectivityManager connMgr =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                Boolean success = new CarTask(context).execute().get();
            }

            else
                Toast.makeText(context, R.string.NetworkErr,Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<Car> getCars() {
        return parkedCars;
    }

    public class CarTask extends AsyncTask<Void,Void,Boolean>{


        private Context context;

        public CarTask(Context context) {
            this.context = context;
        }



        protected Boolean doInBackground(Void... params) {

            try {
                //Thread.sleep(2000);
                String link = "http://192.168.57.4:8080/CarParkingServer/findparkcarsserv";
                InputStream stream = null;
                StringBuffer output = new StringBuffer("");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                httpConnection.setRequestMethod("GET");
                httpConnection.setDoInput(true);
                httpConnection.setConnectTimeout(5000);
                httpConnection.connect();

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

                    Map<String,ArrayList<HashMap<String,String>>> map = new Gson().fromJson(result, Map.class);
                    ArrayList<HashMap<String,String>> parkingList = map.get("parkinglist");
                    Log.d("Before for","size"+parkedCars.size());
                    for(int i=0;i<parkingList.size();i++) {
                        Map<String,String> hs = parkingList.get(i);
                        Car car = new Car(hs.get("carno"),hs.get("type"),hs.get("mob"),hs.get("checkin"));
                        parkedCars.add(car);
                    }
                    Log.d("After for","size"+parkedCars.size());
                    return true;
                }
                Log.d("ServerActivity","Http response not ok");
                return false;
            }
            catch (Exception e) {
                Log.d("ServerActivity","Exception");
                e.printStackTrace();
                parkedCars = null;
                return false;
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


        protected void onPostExecute(final Boolean success) {
            /*if(!success)
                Toast.makeText(context,"",Toast.LENGTH_SHORT).show();*/
            //Log.d("CarActi","size"+parkedCars.size());
            Log.d("CarListActivity","postExecute");
        }
    }

}

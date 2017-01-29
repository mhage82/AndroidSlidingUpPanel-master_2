package com.courtside.demo.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.courtside.demo.screens.DemoActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MELHAGEH on 10/26/2016.
 */

public class AsyncPost extends AsyncTask<String,String,String> {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    Activity activity;
    ProgressDialog pdLoading;
    HttpURLConnection conn;
    URL url = null;


    public AsyncPost(Activity activity){
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdLoading = new ProgressDialog(activity);
        pdLoading.setMessage("\t Posting game, Please Wait ...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            url = new URL(DemoActivity.SERVER_URL+"/postgame");
            Log.i("doInBackGround::", "connecting ...");
        }catch(MalformedURLException e){
            e.printStackTrace();
            return "exception";
        }

        try{
            Log.i("doInBackGround::", "sending data 1 ...");
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("title", params[0])
                    .appendQueryParameter("playerscount", params[1])
                    .appendQueryParameter("longitude", params[2])
                    .appendQueryParameter("latitude", params[3])
                    .appendQueryParameter("startdate", params[4])
                    .appendQueryParameter("enddate", params[5])
                    .appendQueryParameter("gametype", params[6])
                    .appendQueryParameter("duration", params[7])
                    .appendQueryParameter("currenttime", params[8]);

            String query = builder.build().getEncodedQuery();

            conn.setRequestProperty("User-Agent","Court Side Mobile");
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            Log.i("doInBackGround::", "sending data 2 ...");

        }catch(IOException e){
            e.printStackTrace();
            return "exception";
        }

        try{
            int reposnseCode = conn.getResponseCode();

            Log.i("4 ",conn.getHeaderField(4));
            Log.i("5 ",conn.getHeaderField(5));

            if(reposnseCode == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result  = new StringBuilder();
                String line;

                while((line=reader.readLine())!=null){
                    result.append(line);
                }
                Log.i("doInBackground", "connecting 3... ");
                return(result.toString());
            }else{
                return ("unsuccessful");
            }
        }catch(IOException e){
            e.printStackTrace();
            return "exception";
        }finally{
            conn.disconnect();
            //  return "something";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        pdLoading.dismiss();

        Log.i("signup::onPostExecute", "return value is :"+s);

        if(s.equalsIgnoreCase("POST_SUCCESS")){
            Log.i("signup::onPostExecute", "strating activity ... ");
            activity.finish();
        }else if(s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")){
            Toast.makeText(activity, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
        }
    }

}

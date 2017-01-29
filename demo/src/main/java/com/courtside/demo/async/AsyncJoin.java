package com.courtside.demo.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.courtside.demo.screens.DemoActivity;
import com.courtside.demo.utility.UserInfo;

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

public class AsyncJoin extends AsyncTask<String,String,String> {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    Activity activity;
    ProgressDialog pdLoading;
    HttpURLConnection conn;
    URL url = null;



    public AsyncJoin(Activity activity){
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdLoading = new ProgressDialog(activity);
        pdLoading.setMessage("\t Joining game...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            url = new URL(DemoActivity.SERVER_URL+"/joingame");
            Log.i("doInBackGround::", "connecting ...");
        }catch(MalformedURLException e){
            e.printStackTrace();
            return "exception";
        }

        try{
            Log.i("doInBackGround::", "Joining Game ... user is "+ new UserInfo().get_userId());
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("gameid", params[0])
                    .appendQueryParameter("username", new UserInfo().get_userId());

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

        if(s.equals("JOIN_SUCCESS")){
            Log.i("join::onPostExecute", "Join in successful with return value is :"+s);
        }else if(s.equals("USER_ALREADY_EXISTS")){
            Toast.makeText(activity.getApplicationContext(), "User already joined", Toast.LENGTH_LONG).show();
        }
        else{
            Log.i("join::onPostExecute", "Join in failed with return value is :"+s);
        }

        activity.finish();
    }

}

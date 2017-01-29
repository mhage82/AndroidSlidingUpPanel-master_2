package com.courtside.demo.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import static com.courtside.demo.utility.JSONUtility.setUserInfo;

/**
 * Created by melhageh on 10/18/2016.
 */
public class AsyncLogin extends AsyncTask<String, String, String> {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    HttpURLConnection conn;
    URL url = null;
    private Activity activity;
    ProgressDialog pdLoading;

    public AsyncLogin(Activity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdLoading = new ProgressDialog(activity);
        //this method will be running on UI thread
        pdLoading.setMessage("\tSigning in...");
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            url = new URL(DemoActivity.SERVER_URL + "/login");
            Log.i("doInBackground", "connecting ... ");

        }catch(MalformedURLException e){
            e.printStackTrace();

            return "exception";
        }

        try{
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder  = new Uri.Builder()
                    .appendQueryParameter("username", params[0])
                    .appendQueryParameter("password", params[1]);

            String query = builder.build().getEncodedQuery();
            Log.i("doInBackground", "connecting 2... the query is :"+ query);

            conn.setRequestProperty("User-Agent","Court Side Mobile");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            Log.i("doInBackground", "connecting 2... ");
        }catch (IOException e1){
            e1.printStackTrace();
            return "exception";
        }

        try{
            int response_code = conn.getResponseCode();
            if(response_code == HttpURLConnection.HTTP_OK){
                InputStream inpstr = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inpstr));
                StringBuilder result = new StringBuilder();
                String line;

                while((line=reader.readLine()) != null){
                    result.append(line);
                }

                Log.i("doInBackground", "connecting 3... ");
                return(result.toString());
            }else{
                return ("unsuccessful");
            }
        }catch(IOException e3){
            e3.printStackTrace();
            return "Exception";
        }finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String s) {

        Log.i("onPostExecute", "Going In ... result is : " + s);
        pdLoading.dismiss();

        if(s.contains("LOGIN_SUCCESS")){
            String[] result = s.split("&&&");
            setUserInfo(result[1]);
            Intent intent = new Intent(activity, DemoActivity.class);
            activity.startActivity(intent);
            Log.i("onPostExecute", "strating activity ... " + result[1]);
            Log.i("username", " "+new UserInfo().get_userId());
            Log.i("username", " "+new UserInfo().get_id());
            Log.i("username", " "+new UserInfo().get_displayname());
            Log.i("username", " "+new UserInfo().get_phonenumber());
        }else if (s.equalsIgnoreCase("LOGIN_FAILED")){
            Toast.makeText(activity, "Invalid email or password", Toast.LENGTH_LONG).show();
        } else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {
            Toast.makeText(activity, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
        }else{
            Log.i("onPostExecute", "unknown result "+s);
        }
    }
}
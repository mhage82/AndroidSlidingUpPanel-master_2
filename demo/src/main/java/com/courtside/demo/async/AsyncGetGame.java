package com.courtside.demo.async;//package com.sothree.slidinguppanel.demo.async;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.sothree.slidinguppanel.demo.screens.DemoActivity;
//import com.sothree.slidinguppanel.demo.utility.JSONUtility;
//import com.sothree.slidinguppanel.demo.utility.JsonElement;
//import com.sothree.slidinguppanel.demo.utility.UserInfo;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * Created by mohammadhage82 on 12/29/2016.
// */
//public class AsyncGetGame extends AsyncTask<String,String,String> {
//    public static final int CONNECTION_TIMEOUT=10000;
//    public static final int READ_TIMEOUT=15000;
//
//    Activity activity;
//    ProgressDialog pdLoading;
//    HttpURLConnection conn;
//    URL url = null;
//
//    public AsyncGetGame(Activity activity){
//        this.activity = activity;
//    }
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        pdLoading = new ProgressDialog(activity);
//        pdLoading.setMessage("\t Getting Game Info...");
//        pdLoading.setCancelable(false);
//        pdLoading.show();
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        try{
//            url = new URL(DemoActivity.SERVER_URL+"/getgame");
//            Log.i("doInBackGround::", "connecting ...");
//        }catch(MalformedURLException e){
//            e.printStackTrace();
//            return "exception";
//        }
//
//        try{
//            Log.i("doInBackGround::", "Joining Game ... user is "+ new UserInfo().get_userId());
//            conn = (HttpURLConnection)url.openConnection();
//            conn.setReadTimeout(READ_TIMEOUT);
//            conn.setConnectTimeout(CONNECTION_TIMEOUT);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            Uri.Builder builder = new Uri.Builder()
//                    .appendQueryParameter("gameid", params[0])
//                    .appendQueryParameter("username", new UserInfo().get_userId());
//
//            String query = builder.build().getEncodedQuery();
//
//            conn.setRequestProperty("User-Agent","Court Side Mobile");
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(query);
//            writer.flush();
//            writer.close();
//            os.close();
//            conn.connect();
//            Log.i("doInBackGround::", "sending data 2 ...");
//        }catch(IOException e){
//            e.printStackTrace();
//            return "exception";
//        }
//
//        try{
//            int reposnseCode = conn.getResponseCode();
//            if(reposnseCode == HttpURLConnection.HTTP_OK){
//                InputStream is = conn.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                StringBuilder result  = new StringBuilder();
//                String line;
//
//                while((line=reader.readLine())!=null){
//                    result.append(line);
//                }
//                Log.i("doInBackground", "connecting 3... ");
//                return(result.toString());
//            }else{
//                return ("unsuccessful");
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//            return "exception";
//        }finally{
//            conn.disconnect();
//            //  return "something";
//        }
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//
//            Log.i("onPostExecute", "main AsyncGetGames");
//        if(s.equals("GET_GAME_FAILED")){
//            Toast.makeText(activity.getApplicationContext(), "Connection Failure !!!", Toast.LENGTH_LONG).show();
//        }else{
//            Log.i("get::onPostExecute", "get game success with return value is :"+s);
//            //mGameJsonStr = s;
//            JsonElement jsonElement = JSONUtility.getGameFromJsonString(s);
//            if(jsonElement != null){
////                mGameTitleTV.setText(jsonElement.getGameTitle());
////                mLongitude = String.valueOf(jsonElement.getLongitude());
////                mLatitude = String.valueOf(jsonElement.getLatitude());
//            }else {
//                Log.i("JoinActivity", "json element is null");
//            }
//
//        }
//
//        pdLoading.dismiss();
//
//    }
//
//}
//

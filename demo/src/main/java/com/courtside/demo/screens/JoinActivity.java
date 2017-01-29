package com.courtside.demo.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.courtside.demo.async.AsyncJoin;
import com.courtside.demo.utility.JSONUtility;
import com.courtside.demo.utility.JoinListAdapter;
import com.courtside.demo.utility.JoinListElement;
import com.courtside.demo.utility.JsonElement;
import com.courtside.demo.utility.UserInfo;
import com.courtside.demo.R;

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
import java.util.ArrayList;
import java.util.Locale;


public class JoinActivity extends AppCompatActivity {

    private String mGameId = null;
    private String mUserName = null;
    private String mLatitude = null;
    private String mLongitude = null;

//    private String mGameJsonStr = null;
    private TextView mGameTitleTV = null;
    private ImageView mImageView = null;
    private ListView mListView = null;

    protected static final String STATIC_MAP_API_MAIN = "http://maps.google.com/maps/api/staticmap";
    protected static final String STATIC_MAP_API_CENTER ="?center=";//37.4223662,-122.0839445
    protected static final String STATIC_MAP_API_ZOOM = "&zoom=15";
    protected static final String STATIC_MAP_API_SIZE = "&size=400x400";
    protected static final String STATIC_MAP_API_SENSOR = "&sensor=false";
    protected static String STATIC_MAP_API_MARKER = "&markers=color:blue%7Clabel:S%7C";
    protected static String STATIC_MAP_API_LAT_LNG = null;

    public void joinGameClick(View v){
        new AsyncJoin(this).execute(mGameId, mUserName);
    }

    public void directionsClick(View v){

        Log.i("directionsClick", "longitude is "+ mLongitude);
        Log.i("directionsClick", "latitude  is "+ mLatitude);
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?f=d&daddr=%f,%f", Float.parseFloat(mLatitude), Float.parseFloat(mLongitude));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setComponent(new ComponentName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mGameTitleTV = (TextView)findViewById(R.id.gameTitleJoinTV);
        mImageView = (ImageView)findViewById(R.id.mapimage);
        mListView = (ListView)findViewById(R.id.joinList);
        // get info from invoking activity
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            mGameId = bundle.getString("GameID");
            mUserName = bundle.getString("username");
            mLatitude = bundle.getString("latitude");
            mLongitude = bundle.getString("longitude");
            mGameTitleTV.setText(bundle.getString("title"));
        }

        // get a static image of the selected game location
        STATIC_MAP_API_LAT_LNG = mLatitude +"," + mLongitude;
        String query = STATIC_MAP_API_MAIN
                +   STATIC_MAP_API_CENTER
                +   STATIC_MAP_API_LAT_LNG
                +   STATIC_MAP_API_MARKER
                +   STATIC_MAP_API_LAT_LNG
                +   STATIC_MAP_API_ZOOM
                +   STATIC_MAP_API_SIZE
                +   STATIC_MAP_API_SENSOR;
        new AsyncGetMapImage().execute(query);

        // get selected game list fo joined players
        new AsyncGetGame(this).execute(mGameId);

//        Toast.makeText(getApplicationContext(), "game id: " + mGameId, Toast.LENGTH_LONG).show();
    }


    public class AsyncGetMapImage extends AsyncTask<String, Void, Bitmap>{

        Bitmap bmp = null;
        URL url = null;
        HttpURLConnection conn = null;
        @Override
        protected Bitmap doInBackground(String... urlString) {

            try {
                url = new URL(urlString[0]);
            }catch(MalformedURLException e){
                e.printStackTrace();
            }

            try{
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                bmp = BitmapFactory.decodeStream(is, null, options);
            }catch(IOException e){
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
            super.onPostExecute(result);
        }
    }

    public class AsyncGetGame extends AsyncTask<String,String,String> {
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        Activity activity;
        ProgressDialog pdLoading;
        HttpURLConnection conn;
        URL url = null;

        public AsyncGetGame(Activity activity){
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading = new ProgressDialog(activity);
            pdLoading.setMessage("\t Getting Game Info...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                url = new URL(DemoActivity.SERVER_URL+"/getgame");
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

            if(s.equals("GET_GAME_FAILED")){
                Toast.makeText(activity.getApplicationContext(), "Connection Failure !!!", Toast.LENGTH_LONG).show();
            }else{
                Log.i("get::onPostExecute", "get game success with return value is :"+s);

//                //mGameJsonStr = s;
                JsonElement jsonElement = JSONUtility.getGameFromJsonString(s);
                ArrayList<JoinListElement> joinedPlayers = JSONUtility.getPlayerFromGame(jsonElement.getPlayers());

                mListView.setAdapter(new JoinListAdapter(JoinActivity.this, R.layout.join_row, joinedPlayers));

            }

            pdLoading.dismiss();

        }

    }

}

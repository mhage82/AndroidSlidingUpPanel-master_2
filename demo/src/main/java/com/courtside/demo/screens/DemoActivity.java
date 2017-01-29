package com.courtside.demo.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.courtside.demo.utility.JSONUtility;
import com.courtside.demo.utility.JsonElement;
import com.courtside.demo.utility.PlacesListAdapter;
import com.courtside.demo.utility.UserInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DemoActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener{
    private static final String TAG = "DemoActivity";
    public final static String SERVER_URL= "https://backend1-mohammadhage82.c9users.io";
 //   public final static String SERVER_URL= "https://dry-lake-51618.herokuapp.com";


    private SlidingUpPanelLayout mLayout;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private GoogleApiClient client;
    private ListView mlv;
    public static ArrayList<JsonElement> placesList = null;
    public final String RADIUS="10";
    public static final boolean OFFLINE_MODE = false;
    String[] drawerData;

    public static String getDate(){
        String dateString;
        Date now = new Date();
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        dateString = simpleDateFormat.format(now);
        return dateString;
    }

    public void settingsMapClick(View view){
        PopupMenu popupMenu = new PopupMenu(DemoActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        //registering popup with OnMenuItemClickListener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(DemoActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popupMenu.show();

//        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void postMapClick(View view){
        Location location = getCurrentUserLocation(mLocationManager);

        Intent intent = new Intent(this, PostActivity.class);

        if(location!=null){
            intent.putExtra("latitude", String.valueOf(location.getLatitude()));
            intent.putExtra("longitude", String.valueOf(location.getLongitude()));
        }else{
            Log.i("postMapClick", "location not found");
        }
        startActivity(intent);
    }
    private Location getCurrentUserLocation(LocationManager lm) {
        Location location = null;

        List<String> providers = lm.getProviders(true);
        if(lm != null){
            for (String provider : providers) {
                Location l = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    l = lm.getLastKnownLocation(provider);
                }
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l;
                    Log.i("getCurrentUserLocation", "choosen provider : "+ provider );

                }
            }
        }
        return location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mlv = (ListView) findViewById(R.id.list);
        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(DemoActivity.this, JoinActivity.class);
                intent.putExtra("GameID", placesList.get(position).getGameId());
                intent.putExtra("username", new UserInfo().get_userId());
                intent.putExtra("latitude", String.valueOf(placesList.get(position).getLatitude()));
                intent.putExtra("longitude", String.valueOf(placesList.get(position).getLongitude()));
                intent.putExtra("title", placesList.get(position).getGameTitle());
                startActivity(intent);
            }
        });

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    400,
                    1000,
                    (LocationListener) this);
        }

        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setHint("Search");

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                new AsyncGetGames(DemoActivity.this).execute(getDate(), "POST", String.valueOf(place.getLatLng().longitude),
                                                                        String.valueOf(place.getLatLng().latitude),
                                                                        RADIUS);

            }

            @Override
            public void onError(Status status) {
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("onMapReady", "called");

        mMap = googleMap;

        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }

        GoogleMapOptions options = new GoogleMapOptions();

        options.getZoomControlsEnabled();
        options.getZoomGesturesEnabled();

        // Getting Current Location
        Location location = getCurrentUserLocation(mLocationManager);

        if (location != null) {
            onLocationChanged(location);
        }

//        Log.i("getISO8601S", getDate());
        if(location!=null) {
            Log.i("onMapReady", "location is not null");

            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            new AsyncGetGames(this).execute(getDate(), "POST", longitude, latitude, RADIUS);
        }else{
            Log.i("onMapReady", "location is null");
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location location = getCurrentUserLocation(mLocationManager);

                if(location!=null){
                    onLocationChanged(location);

                    new AsyncGetGames(DemoActivity.this).execute(getDate(), "POST", String.valueOf(location.getLongitude()),
                            String.valueOf(location.getLatitude()),
                            RADIUS);

                }
                return false;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = mMap.getCameraPosition().target;
                Log.i("camera Idle", "latitdue : "+ String.valueOf(latLng.latitude)+" longitude : "+ String.valueOf(latLng.longitude));
                new AsyncGetGames(DemoActivity.this).execute(getDate(), "POST", String.valueOf(latLng.longitude),
                        String.valueOf(latLng.latitude),
                        RADIUS);

            }
        });

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged", "----------------------------------------------called");
        if(location!=null){
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 10);
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
                mLocationManager.removeUpdates((LocationListener) this);
            }

            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }

        }else{
            Toast.makeText(getApplicationContext(), "Looking for location ...", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.i("onMarkerClick", "marker clicked");
        marker.showInfoWindow();
        return false;
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Demo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class AsyncGetGames extends AsyncTask<String, String, String> {
        //ProgressDialog progressDialog;
        private Activity activity;
        URL url=null;
        HttpURLConnection conn;
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;
        private  String query;

        public AsyncGetGames(Activity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                if(params[1].equals("POST")){
                    url = new URL(DemoActivity.SERVER_URL+"/futuregames");
                }else{
                    url = new URL(DemoActivity.SERVER_URL+"/getgames");
                }
            }catch (MalformedURLException e){
                e.printStackTrace();

                return "exception";
            }

            try{
                conn = (HttpURLConnection)url.openConnection();
                if(params[1].equals("POST")) {
                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setConnectTimeout(CONNECTION_TIMEOUT);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("querydate", params[0])
                            .appendQueryParameter("longitude", params[2])
                            .appendQueryParameter("latitude", params[3])
                            .appendQueryParameter("radius", params[4]);

                    query = builder.build().getEncodedQuery();
                }
                conn.setRequestProperty("User-Agent","Court Side Mobile");

                if(params[1].equals("POST")){
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                }
            }catch (IOException e){
                e.printStackTrace();
                return "exception";
            }

            try{
                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        result.append(line);
                    }
                    return result.toString();
                }else{
                    return "unsuccessful";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
                 Log.i("onPostExecute:: result", s);
            if(!s.equalsIgnoreCase("unsuccessful")){
                DemoActivity.placesList = JSONUtility.getListFromJsonString(s);
                if(placesList.size()>0){
                    List list = new ArrayList();

                    for(int i=0; i<placesList.size(); i++){

                        list.add(new JsonElement(
                                placesList.get(i).getLatitude(),
                                placesList.get(i).getLongitude(),
                                placesList.get(i).getUsername(),
                                placesList.get(i).getGameTitle(),
                                placesList.get(i).getGameType(),
                                placesList.get(i).getGameTime(),
                                placesList.get(i).getGameId(),
                                placesList.get(i).getPlayers(),
                                placesList.get(i).getOpenSeatsNum(),
                                placesList.get(i).isGamePublic()  ));

                        LatLng latLng = new LatLng(placesList.get(i).getLatitude(), placesList.get(i).getLongitude());
                        if(mMap !=null){
                            mMap.addMarker(new MarkerOptions().position(latLng).title(placesList.get(i).getGameTitle())).showInfoWindow();
                        }
                    }
                    Log.i("onPostExecute", "setting list adapter");
                    mlv.setAdapter(new PlacesListAdapter(activity, R.layout.row, list));

                }else{
                    if(mMap !=null) {
                        Log.i("onPostExecute", "clearing map ........................");
                        mMap.clear();
                    }

                    Log.i("onPostExecute", "places list is null");
                }

            }
            super.onPostExecute(s);
        }
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.action_toggle: {
//                if (mLayout != null) {
//                    if (mLayout.getPanelState() != PanelState.HIDDEN) {
//                        mLayout.setPanelState(PanelState.HIDDEN);
//                        item.setTitle(R.string.action_show);
//                    } else {
//                        mLayout.setPanelState(PanelState.COLLAPSED);
//                        item.setTitle(R.string.action_hide);
//                    }
//                }
//                return true;
//            }
//            case R.id.action_anchor: {
//                if (mLayout != null) {
//                    if (mLayout.getAnchorPoint() == 1.0f) {
//                        mLayout.setAnchorPoint(0.7f);
//                        mLayout.setPanelState(PanelState.ANCHORED);
//                        item.setTitle(R.string.action_anchor_disable);
//                    } else {
//                        mLayout.setAnchorPoint(1.0f);
//                        mLayout.setPanelState(PanelState.COLLAPSED);
//                        item.setTitle(R.string.action_anchor_enable);
//                    }
//                }
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.demo, menu);
//        MenuItem item = menu.findItem(R.id.action_toggle);
//        if (mLayout != null) {
//            if (mLayout.getPanelState() == PanelState.HIDDEN) {
//                item.setTitle(R.string.action_show);
//            } else {
//                item.setTitle(R.string.action_hide);
//            }
//        }
//        return true;
//    }

}


package com.courtside.demo.screens;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.courtside.demo.async.AsyncPost;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.courtside.demo.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {
    private EditText gameTitleET;
    String mCurrentLongitude;
    String mCurrentLatitude;
    String mPlaceLongitude;
    String mPlaceLatitude;

    //    private EditText playersCountET;
    private EditText cntSpn;
    public static TextView mSelectedEndTimeView;
    public static TextView mSelectedStartTimeView;
    public static TextView mSelectedDateView;
    private Spinner gameType;
    private EditText gameTypeET;
    private boolean mGameTypeIsCustom = false;

//    public TextView mDuration;

    public static String getDate(){
        String dateString;
        Date now = new Date();
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        dateString = simpleDateFormat.format(now);
        Log.i("getDate() ...", dateString);
        return dateString;
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            // Create a new instance of DatePickerDialog and return it
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mSelectedDateView.setText( (month + 1)+ " " +  day+ " " +year);
        }
    }
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        String mType = null;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Log.i("onCreateDialog", "debug1");
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE)/5;
            if(minute>11){
                minute = 0;
            }

            mType = getArguments().getString("timer");
            Log.i("onCreateDialog", "debug2 minute is " + minute);
            // Create a new instance of TimePickerDialog and return it
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, hour, minute,false);

            timePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Log.i("onCreateDialog", "debug3");
                    int tpLayoutId = getResources().getIdentifier("timePickerLayout", "id", "android");

                    ViewGroup tpLayout = (ViewGroup) timePickerDialog.findViewById(tpLayoutId);
                    ViewGroup layout = (ViewGroup) tpLayout.getChildAt(0);
                    Log.i("onCreateDialog", "debug4");

                    // Customize minute NumberPicker
                    NumberPicker minutePicker = (NumberPicker) layout.getChildAt(2);

                    Log.i("onCreateDialog", "debug5" + minutePicker.getMaxValue() + " " +minutePicker.getMinValue());
                    minutePicker.setMinValue(0);
                    Log.i("onCreateDialog", "debug7");
                    minutePicker.setMaxValue(11);
                    minutePicker.setDisplayedValues(new String[]{"00", "05","10","15","20","25", "30","35","40", "45","50","55"});
                    Log.i("onCreateDialog", "debug6");

                }
            });
            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int realMinute = minute*5;
            if(mType.equals("start")){
                if(realMinute<10){
                    mSelectedStartTimeView.setText(hourOfDay+":0"+realMinute);
                }else{
                    mSelectedStartTimeView.setText(hourOfDay+":"+realMinute);
                }
            }else if(mType.equals("end")){
                if(realMinute<10){
                    mSelectedEndTimeView.setText(hourOfDay+":0"+realMinute);
                }else{
                    mSelectedEndTimeView.setText(hourOfDay+":"+realMinute);
                }
            }else{

            }

        }
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("timer", "start");
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("timer", "end");
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void postClick(View view){
        final String  title = gameTitleET.getText().toString();
        final String count = cntSpn.getText().toString();
        final String startTime = mSelectedStartTimeView.getText().toString();
        final String endTime = mSelectedEndTimeView.getText().toString();
        final String date = mSelectedDateView.getText().toString();

        Calendar calendar = Calendar.getInstance();
        String currentTime2 =  getDate();
//        String currentTime2 = currentTime.substring(0,19);

        final String startDateTime = date+" "+startTime+":00"; // the last "00" is for seconds. We don't need
                                                    // to be that accurate
        final String gameTypeStr;

        final String endDateTime = date+" "+endTime+":00"; // the last "00" is for seconds. We don't need
                                                                // to be that accurate



        if(mGameTypeIsCustom){
            gameTypeStr = gameTypeET.getText().toString();
        }else{
            gameTypeStr = gameType.getSelectedItem().toString();
        }
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);


        Log.i("current date", currentTime2);
//        String.valueOf(calendar.get(Calendar.MONTH) + 1)+ " " + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+ " " + String.valueOf(calendar.get(Calendar.YEAR))+ " " +
//                        String.valueOf(hour)+ ":" +String.valueOf(min)+":00";

        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int startMinute = Integer.parseInt(startTime.split(":")[1]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int endMinute = Integer.parseInt(endTime.split(":")[1]);

        int duration = (endHour*60 + endMinute) - (startHour*60 + startMinute);

        if(startHour>12){
            startHour = startHour -12;
        }
        if(endHour>12){
            endHour = endHour -12;
        }

        Boolean pastTime = false;
        if((startHour<hour || (startHour==hour && startMinute <= min)) ||
                (endHour<hour || (endHour==hour && endMinute <= min))  ){
            pastTime = true;
        }

        boolean invalidDuration = false;
        if(endHour == startHour && endMinute<=startMinute){
            invalidDuration = true;
        }

        CheckBox checkBox = (CheckBox)findViewById(R.id.postCHK);

        if(pastTime) {
                Toast.makeText(getApplicationContext(), "Please enter a future Time", Toast.LENGTH_LONG).show();
        }else if(invalidDuration){
            Toast.makeText(getApplicationContext(), "Please enter a valid game duration", Toast.LENGTH_LONG).show();
        }else{
            try {
                int countInt = Integer.parseInt(count);
                if(checkBox.isChecked()) {
                    new AsyncPost(this).execute(title, count, mCurrentLongitude, mCurrentLatitude, startDateTime, endDateTime, gameTypeStr, String.valueOf(duration), currentTime2);
                }else{
                    new AsyncPost(this).execute(title, count, mPlaceLongitude, mPlaceLatitude, startDateTime, endDateTime, gameTypeStr, String.valueOf(duration), currentTime2);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter an integer between 1 and 5", Toast.LENGTH_LONG).show();
            }
         }
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mSelectedDateView = (TextView)findViewById(R.id.pickDateTV);
        mSelectedStartTimeView = (TextView)findViewById(R.id.pickTimeStartTV);
        mSelectedEndTimeView = (TextView)findViewById(R.id.pickTimeEndTV);

        gameType = (Spinner)findViewById(R.id.spinner);

        List<String> gameList = Arrays.asList(getResources().getStringArray(R.array.games_types_array_list));
        gameType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gameList));

        gameTypeET = (EditText)findViewById(R.id.gameTypeET);
        gameTypeET.setVisibility(View.INVISIBLE);

        gameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem =adapterView.getSelectedItem().toString();

                if(selectedItem.equals("Custom, Type game type")){
                    gameTypeET.setVisibility(View.VISIBLE);
                    mGameTypeIsCustom = true;
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gameTitleET = (EditText)findViewById(R.id.titleET);

        cntSpn = (EditText)findViewById(R.id.countET);

        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            mCurrentLatitude = extras.getString("latitude");
            mCurrentLongitude = extras.getString("longitude");
        }

        final PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_post);
        placeAutocompleteFragment.setHint("Find place or address");
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //Toast.makeText(getApplicationContext(), place.getAddress() + "lat: "+ String.valueOf(place.getLatLng().latitude)+"lng: "+String.valueOf(place.getLatLng().longitude), Toast.LENGTH_LONG).show();
                mPlaceLatitude = String.valueOf(place.getLatLng().latitude);
                mPlaceLongitude = String.valueOf(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {

            }
        });

        CheckBox checkBox = (CheckBox)findViewById(R.id.postCHK);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                RelativeLayout rl = (RelativeLayout)findViewById(R.id.search_layout);
                if(isChecked){
                    rl.setVisibility(View.INVISIBLE);
                }else{
                    rl.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}

package com.courtside.demo.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.courtside.demo.R;

import java.util.List;

/**
 * Created by MELHAGEH on 11/11/2016.
 */

public class PlacesListAdapter extends ArrayAdapter {
    private LayoutInflater layoutInflater;
    private int resource;
    private Context context;

    public PlacesListAdapter(Context context, int resourceId, List listObjects) {
        super(context, resourceId, listObjects);
        resource = resourceId;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        view = (RelativeLayout)layoutInflater.inflate(resource, null);
        JsonElement jsonElement = (JsonElement) getItem(position);

        TextView textView = (TextView)view.findViewById(R.id.element_title);
        textView.setText(jsonElement.getGameTitle());
        Log.i("PlacesListAdapter", "debug 1");

        TextView gameType = (TextView)view.findViewById(R.id.gameTypeTV );
        gameType.setText(jsonElement.getGameType());
        Log.i("PlacesListAdapter", "debug 2");

        TextView gameTime = (TextView)view.findViewById(R.id.gameTimeTV);
        gameTime.setText(jsonElement.getGameTime());
        Log.i("PlacesListAdapter", "debug 3");

        TextView seatsNum = (TextView)view.findViewById(R.id.remainSeatsTV);
        seatsNum.setText(String.valueOf(jsonElement.getOpenSeatsNum())+ " seats remaining");
        Log.i("PlacesListAdapter", "debug 4");

        return view;
    }


}

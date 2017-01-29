package com.courtside.demo.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.courtside.demo.R;

import java.util.List;

/**
 * Created by mohammadhage82 on 12/30/2016.
 */

public class JoinListAdapter extends ArrayAdapter<JoinListElement> {

    private LayoutInflater layoutInflater;
    private int resource;
    private Context context;

    public JoinListAdapter(Context context, int res, List<JoinListElement> objects) {
        super(context, res, objects);
        resource = res;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = (RelativeLayout)layoutInflater.inflate(resource, null);
        JoinListElement joinListElement = (JoinListElement)getItem(position);

        TextView textview = (TextView)convertView.findViewById(R.id.displayNameTV);
        textview.setText(joinListElement.getDisplayName());

        return convertView;
    }
}

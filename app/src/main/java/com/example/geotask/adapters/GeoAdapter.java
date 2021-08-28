package com.example.geotask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.geotask.GeoObject;
import com.example.geotask.R;

import java.util.ArrayList;

public class GeoAdapter extends ArrayAdapter {
    private final LayoutInflater mInflater;
    private final ArrayList<GeoObject> mGeoObjects = new ArrayList<>();


    static class ViewHolder{
        TextView geoName;
        TextView geoDescription;
    }

    public GeoAdapter(Context context, int resource, ArrayList<GeoObject> geoObjects) {
        super(context, resource);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (geoObjects != null)
            mGeoObjects.addAll(geoObjects);
    }

    public ArrayList<GeoObject> getData() {
        return mGeoObjects;
    }

    public void setData(ArrayList<GeoObject> geoObjects) {
        mGeoObjects.clear();
        if (geoObjects != null)
            mGeoObjects.addAll(geoObjects);
    }

    @Override
    public int getCount() {
        return mGeoObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mGeoObjects.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_geo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.geoName = convertView.findViewById(R.id.geoName);
            viewHolder.geoDescription = convertView.findViewById(R.id.geoDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.geoName.setText(mGeoObjects.get(position).getName());
        viewHolder.geoDescription.setText(mGeoObjects.get(position).getDescription());

        return convertView;
    }
}

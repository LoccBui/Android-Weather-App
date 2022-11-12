package com.dlp.weatherapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dlp.weatherapp.ListFvLocation;
import com.dlp.weatherapp.MainActivity;
import com.dlp.weatherapp.R;
import com.dlp.weatherapp.sql.CityDAO;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<String> {

    @NonNull
    Activity context;
    int resource;
    @NonNull ArrayList<String> objects;

    public CityAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View row, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        row = layoutInflater.inflate(this.resource, null);

        TextView txtName = row.findViewById(R.id.txtName);
        ImageButton ibtnUnlike = row.findViewById(R.id.ibtnUnlike);

        txtName.setText(objects.get(position).toString());

        ibtnUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new CityDAO(context).Delete(txtName.getText().toString());
                    ListFvLocation.Remove(position);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                MainActivity.listCity = new CityDAO(context).GetAll();
            }
        });

        return row;
    }
}

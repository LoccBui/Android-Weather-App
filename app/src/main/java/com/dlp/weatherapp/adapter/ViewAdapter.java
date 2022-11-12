package com.dlp.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dlp.weatherapp.MainActivity;
import com.dlp.weatherapp.R;
import com.dlp.weatherapp.models.City;
import com.dlp.weatherapp.sql.CityDAO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<City> listModelCity;
    String DB_NAME = "Weather.db";
    private String DB_PATH = "/databases/";

    @Override
    public int getCount() {
        return listModelCity.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    public ViewAdapter(Context context, ArrayList<City> listModelCity){
        this.context = context;
        this.listModelCity = listModelCity;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);

        ImageView imgIcon = view.findViewById(R.id.imgIcon);
        TextView txtCityName = view.findViewById(R.id.txtCityName);
        TextView txtCountryName = view.findViewById(R.id.txtCountryName);
        TextView txtTemperature = view.findViewById(R.id.txtTemperature);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        TextView txtHumidity = view.findViewById(R.id.txtHumidity);
        TextView txtCloud = view.findViewById(R.id.txtCloud);
        TextView txtWind = view.findViewById(R.id.txtWind);
        TextView txtDay = view.findViewById(R.id.txtDay);
        ImageButton ibtnLike = view.findViewById(R.id.ibtnLike);

        City city = listModelCity.get(position);

        txtCityName.setText(city.getCityName().toString());
        txtCountryName.setText(city.getCountryName().toString());
        txtStatus.setText(city.getStatus().toString());
        txtTemperature.setText(city.getTemperature().toString());
        txtHumidity.setText(city.getHumidity().toString());
        txtCloud.setText(city.getCloud().toString());
        txtWind.setText(city.getWind().toString());
        Picasso.get().load("http://openweathermap.org/img/w/"+city.getImg()+".png").into(imgIcon);
        txtDay.setText(city.getDay().toString());

        if (!city.isFavourite()){
            ibtnLike.setImageResource(R.drawable.ic_unfavourite);
        }
        else {
            ibtnLike.setImageResource(R.drawable.ic_favourite);
        }

        ibtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city.isFavourite()){
                    try {
                        new CityDAO(context).Delete(city.getCityName().toString());
                        city.setFavourite(false);
                        ibtnLike.setImageResource(R.drawable.ic_unfavourite);

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        new CityDAO(context).Add(city.getCityName().toString());
                        ibtnLike.setImageResource(R.drawable.ic_favourite);
                        city.setFavourite(true);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                MainActivity.listCity.clear();
                MainActivity.listCity = new CityDAO(context).GetAll();
            }
        });

        container.addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}

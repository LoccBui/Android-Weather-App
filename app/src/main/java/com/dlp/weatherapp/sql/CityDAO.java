package com.dlp.weatherapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityDAO extends DBHelper{
    public CityDAO(@Nullable Context context) {
        super(context);
    }

    public ArrayList<String> GetAll(){
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM City", null);
        cs.moveToFirst();
        while (!cs.isAfterLast()){
            list.add(cs.getString(0));
            cs.moveToNext();
        }

        cs.close();
        db.close();
        return list;
    }

    public boolean Add(String cityName){
        ContentValues values = new ContentValues();
        values.put("name", cityName);

        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.insert("City", null, values) > 0;
        db.close();
        return  isSuccessfull;
    }

    public boolean Delete(String cityName){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessfull = db.delete("City", "name=?", new String[]{cityName}) > 0;
        db.close();
        return isSuccessfull;
    }
}
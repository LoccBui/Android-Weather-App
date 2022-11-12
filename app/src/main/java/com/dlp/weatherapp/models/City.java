package com.dlp.weatherapp.models;

public class City {
    private String cityName, countryName, humidity, temperature, wind, date, cloud, status, img, day;
    private boolean isCurrent, isFavourite;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public City(String cityName, String countryName, String humidity, String temperature, String wind, String date, String cloud, String status, String img, String day, boolean isCurrent) {
        this.cityName = cityName;
        this.countryName = countryName;
        this.humidity = humidity;
        this.temperature = temperature;
        this.wind = wind;
        this.date = date;
        this.cloud = cloud;
        this.status = status;
        this.img = img;
        this.day = day;
        this.isCurrent = false;
        this.isFavourite = false;
    }

    public City() {
        this.cityName = "";
        this.countryName = "";
        this.humidity = "";
        this.temperature = "";
        this.wind = "";
        this.date = "";
        this.cloud = "";
        this.status = "";
        this.img = "";
        this.day = "";
        this.isCurrent = false;
        this.isFavourite = false;
    }
}

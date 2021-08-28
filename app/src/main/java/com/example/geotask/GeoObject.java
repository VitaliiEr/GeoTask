package com.example.geotask;

import org.json.JSONException;
import org.json.JSONObject;

public class GeoObject {
    private String name;
    private String description;
    private double latitude;
    private double longitude;

    public GeoObject(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoObject(JSONObject jObject) throws JSONException {
        JSONObject jGeoObject = jObject.getJSONObject("GeoObject");
        if (jGeoObject.has("name"))
            this.name = jGeoObject.getString("name");
        if (jGeoObject.has("description"))
            this.description = jGeoObject.getString("description");

        JSONObject jPoint = jGeoObject.getJSONObject("Point");
        String pos = jPoint.getString("pos");
        this.longitude = Double.parseDouble(pos.split(" ")[0]);
        this.latitude = Double.parseDouble(pos.split(" ")[1]);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

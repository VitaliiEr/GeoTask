package com.example.geotask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.example.geotask.adapters.GeoAdapter;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private final String MAPKIT_API_KEY = "";
    private final String GEOCODER_API_KEY = "";

    private final Point TARGET_LOCATION = new Point(55.755811, 37.617617);

    private MainActivity mainActivity;
    private MapView mapView;
    private AutoCompleteTextView addressFrom;

    private Bitmap marker;
    private ArrayList<GeoObject> geoObjects;
    private GeoAdapter geoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_GeoTask_Launcher);
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        mapView = findViewById(R.id.mapView);
        addressFrom = findViewById(R.id.addressFrom);

        marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
        geoObjects = new ArrayList<>();
        geoAdapter = new GeoAdapter(this, R.layout.item_geo, geoObjects);
        addressFrom.setAdapter(geoAdapter);

        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        // Слушатель ввода текста используется для отправки введенной строки в Геокодер
        addressFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 3)
                    return;

                // Запрос в Геокодер
                new GetUrlContentTask(mainActivity).execute(s.toString());
            }
        });

        addressFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                double longitude = geoObjects.get(position).getLongitude();
                double latitude = geoObjects.get(position).getLatitude();

                Point currentPoint = new Point(latitude, longitude);
                mapView.getMap().move(
                        new CameraPosition(currentPoint, 11.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 0),
                        null);

                mapView.getMap().getMapObjects().clear();
                mapView.getMap().getMapObjects().addPlacemark(currentPoint, ImageProvider.fromBitmap(marker));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    private class GetUrlContentTask extends AsyncTask<String, Integer,  ArrayList<GeoObject>> {
        private final MainActivity m_mainActivity;

        GetUrlContentTask(MainActivity mainActivity) {
            m_mainActivity = mainActivity;
        }

        @Override
        protected ArrayList<GeoObject> doInBackground(String... str) {
            // Устанавливаем соединение для запроса в Геокодер
            String content = "";
            String strUrl = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=" + GEOCODER_API_KEY + "&geocode=" + str[0];
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                int status = connection.getResponseCode();
                if (status == 200) {
                    BufferedReader data = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = data.readLine()) != null) {
                        content += line + "\n";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            geoObjects.clear();
            // Парсим JSON
            try {
                JSONObject jObject = new JSONObject(content);
                JSONObject jResponse = jObject.getJSONObject("response");
                JSONObject jGeoObjectCollection = jResponse.getJSONObject("GeoObjectCollection");
                JSONArray jFeatureMembers = jGeoObjectCollection.getJSONArray("featureMember");
                for (int i = 0; i < jFeatureMembers.length(); ++i) {
                    GeoObject geoObject = new GeoObject(jFeatureMembers.getJSONObject(i));
                    geoObjects.add(geoObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return geoObjects;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<GeoObject> result) {
            super.onPostExecute(result);

            if (m_mainActivity == null || m_mainActivity.isFinishing())
                return;

            geoAdapter = new GeoAdapter(m_mainActivity, R.layout.item_geo, geoObjects);
            addressFrom.setAdapter(geoAdapter);

            geoAdapter.notifyDataSetChanged();
        }

    }
}
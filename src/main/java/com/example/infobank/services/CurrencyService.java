package com.example.infobank.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

@Service
public class CurrencyService implements Converter{
    private final String request = "https://www.nbrb.by/api/exrates/currencies";
    HttpURLConnection connection;

    public HashSet<String> setConnection() throws IOException, JSONException {
        URL url = new URL(request);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            InputStream inputStream = connection.getInputStream();
            String temp = convertStreamToString(inputStream);
            return convertFromJsonToObject(temp);
        }
    }

    public int setConnectionByDate(int start, int end, String name) throws IOException, JSONException {
        URL url = new URL(request);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            InputStream inputStream = connection.getInputStream();
            String temp = convertStreamToString(inputStream);
            return findByDate(temp, start, end, name);
        }
    }

    private HashSet<String> convertFromJsonToObject(String json) throws JSONException {
        JSONArray fullInfo = new JSONArray(json);
        HashSet<String> result = new HashSet<>();
        for (int i = 0; i < fullInfo.length(); i++) {
            result.add(fullInfo.getJSONObject(i).getString("Cur_Name"));
        }
        return result;
    }

    private int findByDate(String json, int start, int end, String name) throws JSONException {
        JSONArray fullInfo = new JSONArray(json);
        int result = -1;
        for (int i = 0; i < fullInfo.length(); i++) {
            if ((fullInfo.getJSONObject(i).getString("Cur_Name").equals(name))
                    && (Integer.parseInt(fullInfo.getJSONObject(i).getString("Cur_DateStart").substring(0, 4)) <= start)
                    && (Integer.parseInt(fullInfo.getJSONObject(i).getString("Cur_DateEnd").substring(0, 4)) >= end)) {
                result = fullInfo.getJSONObject(i).getInt("Cur_ID");
                return result;
            }
        }
        return result;
    }

}

package com.example.infobank.services;

import com.example.infobank.data.BankData;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class ChartService implements Converter{
    private final String request = "https://www.nbrb.by/API/ExRates/Rates/Dynamics/";

    HttpURLConnection connection;

    public ArrayList<Double> rate = new ArrayList<>();
    public ArrayList<String> date = new ArrayList<>();

    public boolean setConnection(BankData bankData) throws IOException, JSONException {
        int id = findId(bankData.getStartDate().get(Calendar.YEAR), bankData.getEndDate().get(Calendar.YEAR), bankData.getCurrency());
        if (id == -1) return false;
        StringBuilder stringBuilder = new StringBuilder(request);
        Calendar start = bankData.getStartDate();
        Calendar end = bankData.getEndDate();
        stringBuilder.append(id).append("/?startDate=")
                .append(start.get(Calendar.YEAR)).append("-")
                .append(start.get(Calendar.MONTH)).append("-")
                .append(start.get(Calendar.DAY_OF_MONTH)).append("&endDate=")
                .append(end.get(Calendar.YEAR)).append("-")
                .append(start.get(Calendar.MONTH)).append("-")
                .append(start.get(Calendar.DAY_OF_MONTH));

        System.out.println(stringBuilder.toString());

        URL url = new URL(stringBuilder.toString());

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            InputStream inputStream = connection.getInputStream();
            String temp = convertStreamToString(inputStream);
            rate = getRates(temp);
            date = getDates(temp);
        }
        return true;
    }

    private ArrayList<Double> getRates(String json) throws JSONException {
        JSONArray fullInfo = new JSONArray(json);
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < fullInfo.length(); i++) {
            result.add(fullInfo.getJSONObject(i).getDouble("Cur_OfficialRate"));
        }
        return result;
    }

    private ArrayList<String> getDates(String json) throws JSONException {
        JSONArray fullInfo = new JSONArray(json);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < fullInfo.length(); i++) {
            result.add(fullInfo.getJSONObject(i).getString("Date").substring(0, 4));
        }
        return result;
    }

    private int findId(int start, int end, String name) throws JSONException, IOException {
        CurrencyService currencyService = new CurrencyService();
        int res = currencyService.setConnectionByDate(start, end, name);
        return res;
    }

}

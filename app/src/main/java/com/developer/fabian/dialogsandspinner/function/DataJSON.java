package com.developer.fabian.dialogsandspinner.function;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataJSON {

    public static JSONObject getJSONFromURL() {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.androidbegin.com/tutorial/jsonparsetutorial.txt");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        try {
            BufferedReader reader = null;

            if (is != null)
                reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");

            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {

            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }
}

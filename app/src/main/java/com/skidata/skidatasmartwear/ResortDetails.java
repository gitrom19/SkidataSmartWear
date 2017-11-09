package com.skidata.skidatasmartwear;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ResortDetails extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resort_details);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }

    public void onClickCandellightDinnerImage(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueSingleton.getInstance(view.getContext()).getRequestQueue();
        //String url ="http://echo.jsontest.com/insert-key-here/insert-value-here/key/value";
        String url = RequestQueueSingleton.URL_PREFIX + "bookItem?id=1&item=1&price=22";
        final Context context = view.getContext();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTextView.setText("Response is: "+ response.toString());
                Toast.makeText(context, "Romantic nerd...", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!"+error.getMessage());
                Toast.makeText(context, "did not work...", Toast.LENGTH_SHORT).show();
            }
        });
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,500));
                Toast.makeText(context, "Romantic nerd...", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
                Toast.makeText(context, "did not work...", Toast.LENGTH_SHORT).show();
            }
        });
// Add the request to the RequestQueue.
        RequestQueueSingleton.getInstance(this).addToRequestQueue(
                jsonObjectRequest);
    }

}

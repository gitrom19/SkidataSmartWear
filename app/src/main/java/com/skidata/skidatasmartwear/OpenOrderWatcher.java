package com.skidata.skidatasmartwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by roman on 11/9/17.
 */

public class OpenOrderWatcher implements Runnable {
    private final View view;
    private final Activity parentActivity;

    public OpenOrderWatcher(Activity activity, View view) {
        this.view = view;
        this.parentActivity = activity;
    }

    @Override
    public void run() {
        while (true) {
            // Instantiate the RequestQueue.
            final Context context = view.getContext();
            RequestQueue queue = RequestQueueSingleton.getInstance(context).getRequestQueue();
            //String url ="http://echo.jsontest.com/insert-key-here/insert-value-here/key/value";
            String url = RequestQueueSingleton.URL_PREFIX + "getTransactions2accept?id=08A40130";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // final List transactions = response.
                    double priceTransaction = 0.0D;
                    try {
                        JSONArray jsonArray = response.getJSONArray("Items");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                priceTransaction += jsonArray.getJSONObject(i).getDouble("price");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final double submitPrice = priceTransaction;
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(view.getContext(), OrderPaymentConfirmationActivity.class);
                            intent.putExtra(OrderPaymentConfirmationActivity.TOTAL_PRICE_INFO, submitPrice);
                            parentActivity.startActivity(intent);
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "did not work...", Toast.LENGTH_SHORT).show();
                }
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

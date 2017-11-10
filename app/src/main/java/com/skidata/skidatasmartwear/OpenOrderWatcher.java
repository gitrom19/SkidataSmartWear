package com.skidata.skidatasmartwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by roman on 11/9/17.
 */

public class OpenOrderWatcher extends Thread {
    public static OpenOrderWatcher THREAD;

    private final Object LOCK = new Object();


    private final View view;
    private final Activity parentActivity;
    private boolean flag = false;

    public OpenOrderWatcher(Activity activity, View view) {
        this.view = view;
        this.parentActivity = activity;
    }

    @Override
    public void run() {
         while (!this.isInterrupted()) {
             synchronized (LOCK){
                 if(!flag)
                     try {
                         LOCK.wait();
                     }catch(InterruptedException e){
                        return;
                     }
             }

            // Instantiate the RequestQueue.
            final Context context = view.getContext();
            RequestQueue queue = RequestQueueSingleton.getInstance(context).getRequestQueue();
            //String url ="http://echo.jsontest.com/insert-key-here/insert-value-here/key/value";
            String url = RequestQueueSingleton.URL_PREFIX + "getTransactions2accept?id=" + RequestQueueSingleton.NFC_ID;
            Log.i("OpenOrderWatcher", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // final List transactions = response.
                    int transactionId=0;
                    double priceTransaction = 0.0D;
                    try {
                        Log.i("OpenOrderWatcher", "JsonObject: " + response);
                        transactionId = response.getInt("transactionID");
//                        Toast.makeText(context, "Got Transaction ID: " + transactionId, Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = response.getJSONArray("items");
                        Log.i("OpenOrderWatcher", "JsonArray: " + jsonArray);
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                priceTransaction += jsonArray.getJSONObject(i).getDouble("price");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final double submitPrice = priceTransaction;
                    final int finalTransactionId = transactionId;
                    if (submitPrice > 1.0) {
                        setFlag(false);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(view.getContext(), OrderPaymentConfirmationActivity.class);
                                intent.putExtra(OrderPaymentConfirmationActivity.TOTAL_PRICE_INFO, submitPrice);
                                intent.putExtra(OrderPaymentConfirmationActivity.TRANSACTION_ID, finalTransactionId);
                                parentActivity.startActivity(intent);
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("transaction list error" +error.toString());
                    //Toast.makeText(context, "transaction list error" +error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFlag(boolean flag) {
        synchronized (LOCK) {
            this.flag = flag;

            if(flag)
                LOCK.notify();
        }
    }


}

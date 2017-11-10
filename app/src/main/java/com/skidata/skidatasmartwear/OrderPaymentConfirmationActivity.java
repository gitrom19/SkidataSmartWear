package com.skidata.skidatasmartwear;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class OrderPaymentConfirmationActivity extends WearableActivity {

    private TextView mTextView;
    private double currentOrderPrice = 0.0D;
    private int transactionId = 0;

    public static final String TOTAL_PRICE_INFO = "TotalPriceInfo";
    public static final String TRANSACTION_ID = "TransactionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_confirmation);

        mTextView = (TextView) findViewById(R.id.priceInfoId);
        currentOrderPrice = this.getIntent().getDoubleExtra(TOTAL_PRICE_INFO, 0.0D);
        transactionId = this.getIntent().getIntExtra(TRANSACTION_ID, 0);
        mTextView.setText(new Double(currentOrderPrice).toString());

        // Enables Always-on
        setAmbientEnabled();
    }

    public void onClickOkBtn(View view) {
        final Context context = view.getContext();
        RequestQueue queue = RequestQueueSingleton.getInstance(context).getRequestQueue();
        //String url ="http://echo.jsontest.com/insert-key-here/insert-value-here/key/value";
        String url = RequestQueueSingleton.URL_PREFIX + "/acceptTransaction?id="+ RequestQueueSingleton.NFC_ID + "&transactionId=" + transactionId +"&accepted=true";
        Log.i(this.getClass().getName(), "url: "+ url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Accepted order for transaction id " + transactionId);
                OpenOrderWatcher.THREAD.setFlag(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                OpenOrderWatcher.THREAD.setFlag(true);
            }
        });

        RequestQueueSingleton.getInstance(context).addToRequestQueue(stringRequest);

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                getString(R.string.payment_confirmed_text));
        startActivity(intent);
        finish();
    }
}

package com.skidata.skidatasmartwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends WearableActivity {
    private  Thread t;

    private TextView mTextView;
    private WearableRecyclerView mWearableRecyclerView;
    private WearableRecyclerView.Adapter mAdapter;
    private WearableRecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.header_text_resorts);

        // Enables Always-on
        setAmbientEnabled();

        OpenOrderWatcher.THREAD = new OpenOrderWatcher(this, mTextView);
        OpenOrderWatcher.THREAD.start();
        OpenOrderWatcher.THREAD.setFlag(true);
//        startOrderWatcher(this, mTextView);
    }

    public void onResortButtonClicked(View view) {
 //       Toast.makeText(view.getContext(), "Show me details", Toast.LENGTH_SHORT).show();
        Intent resortsDetailIntent = new Intent(this, ResortDetails.class);
        this.startActivity(resortsDetailIntent);
    }

//    public static void startOrderWatcher(Activity activity, View view ) {
//        new Thread(new OpenOrderWatcher(activity, view)).start();
//    }
}

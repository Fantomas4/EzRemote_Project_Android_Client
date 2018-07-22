package com.example.android.ezremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScanNetworkActivity extends AppCompatActivity {

    private List<DetectedDevice> deviceList = new ArrayList<>();
    private RecyclerView devicesRecyclerView;
    private DevicesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_network);

        devicesRecyclerView = (RecyclerView) findViewById(R.id.detected_device_list);

        adapter = new DevicesRecyclerViewAdapter(deviceList);
    }
}

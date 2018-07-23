package com.example.android.ezremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        devicesRecyclerView.setLayoutManager(layoutManager);
        devicesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        devicesRecyclerView.setAdapter(adapter);

        prepareDevicesData();
    }

    private void prepareDevicesData() {
        deviceList.add(new DetectedDevice("Stefanos", "102.168.1.2", "Ready"));
        deviceList.add(new DetectedDevice("Panos", "102.168.1.3", "Not Ready"));

        adapter.notifyDataSetChanged();
    }
}

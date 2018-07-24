package com.example.android.ezremote;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScanNetworkActivity extends AppCompatActivity {

    private List<DetectedDevice> deviceList = new ArrayList<>();
    private RecyclerView devicesRecyclerView;
    private DevicesRecyclerViewAdapter adapter;
    private TextView refreshCountdownTextView;
    private RefreshCountdownHandler refreshCountdownHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_network);

        devicesRecyclerView = (RecyclerView) findViewById(R.id.detected_device_list);

        refreshCountdownTextView = (TextView) findViewById(R.id.refresh_countdown_text_view);

        adapter = new DevicesRecyclerViewAdapter(deviceList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        devicesRecyclerView.setLayoutManager(layoutManager);
        devicesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        devicesRecyclerView.setAdapter(adapter);

        refreshCountdownHandler = new RefreshCountdownHandler();

        prepareDevicesData();
        RefreshCountdownTask();
    }

    private class RefreshCountdownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            refreshCountdownTextView.setText("done!");
        }

    }

    // temporary method used for creating dummy data to test DevicesRecyclerView
    private void prepareDevicesData() {
        deviceList.add(new DetectedDevice("Stefanos", "102.168.1.2", "Ready"));
        deviceList.add(new DetectedDevice("Panos", "102.168.1.3", "Not Ready"));

        adapter.notifyDataSetChanged();
    }

    private class RefreshCountdownTimer extends CountDownTimer {

        public RefreshCountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
//            long ms = millisUntilFinished;
//            String text = String.format("%02d\' %02d\"",
//                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
//                    TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
            refreshCountdownHandler.sendEmptyMessage(0);
        }

        @Override
        public void onFinish() {

        }
    }

    private void RefreshCountdownTask() {

        RefreshCountdownTimer timer = new RefreshCountdownTimer(3000,1000);
        timer.start();

    }
}


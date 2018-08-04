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
            Bundle bundle = msg.getData();
            long secondsRemaining = bundle.getLong("time_remaining");
            String baseText = "Refreshing in ";
            String secondsText = String.valueOf(secondsRemaining);

            refreshCountdownTextView.setText(baseText.concat(secondsText));
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

        private void sendTimeToHandler(long millisUntilFinished) {
            Bundle bundle = new Bundle();
            Message msg;

            msg = refreshCountdownHandler.obtainMessage();
            bundle.putLong("time_remaining", millisUntilFinished);
            msg.setData(bundle);
            refreshCountdownHandler.sendMessage(msg);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendTimeToHandler(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
//            String text = String.format("%02d\' %02d\"",
//                    TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
//                    TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
//            String text = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(ms));

        }

        @Override
        public void onFinish() {
            sendTimeToHandler(0);
        }
    }

    private void RefreshCountdownTask() {

        RefreshCountdownTimer timer = new RefreshCountdownTimer(10000,100);
        timer.start();

    }
}


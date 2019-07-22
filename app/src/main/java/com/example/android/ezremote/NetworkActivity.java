package com.example.android.ezremote;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class NetworkActivity extends AppCompatActivity {

    protected ClientService clientService;
    protected boolean isBound = false;

    BroadcastReceiver clientStateReceiver;
    IntentFilter statusIntentFilter;


    public final class Constants {

        // Defines a custom Intent action
        public static final String BROADCAST_ACTION =
                "com.example.android.ezremote.BROADCAST";

        // Defines the key for the status "extra" in an Intent
        public static final String EXTENDED_DATA_STATUS =
                "com.example.android.ezremote.STATUS";
    }

    protected abstract int getLayoutResourceId();

    protected abstract void switchActivity(Bundle bundle);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // The filter's action is BROADCAST_ACTION
        statusIntentFilter = new IntentFilter(
                RemoteMenuActivity.Constants.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
        // (com.example.android.ezremote)
        //        statusIntentFilter.addDataScheme("http");

        // Broadcast receiver for receiving status updates from the ClientService
        // *** WARNING *** There are 2 types of broadcast, static and dynamic.
        // We implement a dynamic broadcast (no need to edit manifest)
        clientStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BroadcastReceiver", "MPIKA");

                String status;
                Bundle extras = intent.getExtras();

                if (extras == null) {
                    status = null;
                } else {
                    status = extras.getString(RemoteMenuActivity.Constants.EXTENDED_DATA_STATUS);
                }

                Log.d("BroadcastReceiver", "received broadcast!");

                // The Server has abruptly ended the connection.
                // Switch to the new activity as required
                Bundle bundle = new Bundle();
                bundle.putString("notificationMessage", status);
                switchActivity(bundle);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, ClientService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    protected void onResume() {

        super.onResume();

        // Registers the ClientStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                clientStateReceiver,
                statusIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clientStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        isBound = false;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    protected ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to ClientService, cast the IBinder and get ClientService instance
            ClientService.LocalBinder binder = (ClientService.LocalBinder) service;
            clientService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}

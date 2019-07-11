package com.example.android.ezremote;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteMenuActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private ClientService clientService;
    private boolean isBound = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_menu);

        // The filter's action is BROADCAST_ACTION
        statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

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
                // Switch to the ManualConnection activity and print an error message
                // inside the notification message element
                Intent manualConnectionActivityIntent = new Intent(RemoteMenuActivity.this, ManualConnectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("notificationMessage", status);
                manualConnectionActivityIntent.putExtras(bundle);
                startActivity(manualConnectionActivityIntent);
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

    @Override
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
    private ServiceConnection connection = new ServiceConnection() {

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

    /*
     * A method used to determine the Android UI's "Back Button" behavior.
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            // This is the second time the user has pressed the "Back" button,
            // so we prepare to terminate the connection to the Server and switch
            // to the previous activity

            // Execute TerminateConnectionTask using AsyncTask
            new TerminateConnectionTask().execute();

            // Call the original onBackPressed method that calls finish() for the current
            // activity and switches to the previous activity
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onClickShutdownComputerButton(View v) {
        Intent shutdownCommandActivityIntent = new Intent(this, ShutdownCommandActivity.class);
        startActivity(shutdownCommandActivityIntent);
    }

    class TerminateConnectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String serverResponseData = null;

            // Create the INITIALIZE_NEW_CONNECTION request's json message
            Map<String, String> msgData = new HashMap<>();
            JSONObject jsonData = MessageGenerator.generateJsonObject("TERMINATE_CONNECTION", msgData);

            JSONObject jsonResponse = null;
            String status = null;
            JSONObject data = null;

            try {
                jsonResponse = new JSONObject(clientService.sendMsgAndRecvReply(jsonData));
                status = jsonResponse.getString("status");;
                data = jsonResponse.getJSONObject("data");

                if (status.equals("SUCCESS")) {
                    // The Server has responded with a SUCCESS status, so we know that the
                    // TERMINATE_CONNECTION request has been serviced successfully

                    // Terminate the ClientService's currently established connection to the Server
                    clientService.terminateConnection();

                } else {
                    serverResponseData = jsonResponse.getString("data");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                serverResponseData = "Error!";
            }

            return serverResponseData;
        }

        @Override
        protected void onPostExecute(String serverResponseData) {
            if (serverResponseData != null)
                // The Server has responded with a FAIL or ERROR status or an exception was caught,
                // so we notify the user and exit
                Toast.makeText(getApplicationContext(), serverResponseData, Toast.LENGTH_LONG).show();

        }
    }
}

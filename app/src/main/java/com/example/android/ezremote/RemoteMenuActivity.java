package com.example.android.ezremote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RemoteMenuActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private ClientService clientService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_menu);
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

            try {
                jsonResponse = new JSONObject(clientService.sendMsgAndRecvReply(jsonData));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                if (jsonResponse.get("status").equals("SUCCESS")) {
                    // The Server has responded with a SUCCESS status, so we know that the
                    // TERMINATE_CONNECTION request has been serviced successfully

                    // Terminate the ClientService's currently established connection to the Server
                    clientService.terminateConnection();

                } else {
                    serverResponseData = jsonResponse.getString("data");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return serverResponseData;
        }

        @Override
        protected void onPostExecute(String serverResponseData) {
            if (serverResponseData != null)
            // The Server has responded with a FAIL or ERROR status, so we notify the user and exit
            Toast.makeText(getApplicationContext(), serverResponseData, Toast.LENGTH_LONG).show();

        }
    }
}

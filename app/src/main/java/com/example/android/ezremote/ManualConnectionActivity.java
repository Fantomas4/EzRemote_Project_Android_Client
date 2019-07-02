package com.example.android.ezremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    Button connectButton;
    final static int CLIENT_JOB_ID = 1000;;
    static ConnectionStatusReceiver connectionStatusReceiver;

    // Broadcast receiver for receiving status updates from the IntentService.
    private class ConnectionStatusReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Handle Intents here.
             */

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);
        ipInput = findViewById(R.id.ipEditText);
        portInput = findViewById(R.id.portEditText);
        notificationMsg = findViewById(R.id.notificationMsgTextView);
        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        // Initialize the ConnectionStatusReceiver which is the BroadcastReceiver used to
        // receive status updates from the IntentService
        connectionStatusReceiver = new ConnectionStatusReceiver();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionStatusReceiver,
                new IntentFilter("connection_status"));
    }

    private void connect() {

        String ipString = ipInput.getText().toString();
        String port = portInput.getText().toString();

        Log.d("ip input: ", ipInput.getText().toString());
        Log.d("port input: ", portInput.getText().toString());

        String msg = "";

        boolean validIpFormat = ClientService.isIpFormatCorrect(ipString);

        // initialize validPortFormat to true
        boolean validPortFormat = true;

        // Check if the input can be converted to integer, otherwise catch
        // the thrown exception and notify the user
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            validPortFormat = false;
        }

        if (validPortFormat) {
            validPortFormat = ClientService.isPortFormatCorrect(Integer.parseInt(port));
        }


        if (validIpFormat && validPortFormat) {
            notificationMsg.setText("Connecting...");

            // Initialize Client Service
            /*
             * Creates a new Intent to start the ClientService
             * JobIntentService. Passes a URI in the
             * Intent's "data" field.
             */
            Intent serviceIntent = new Intent();
            serviceIntent.putExtra("activity_request", "START_CLIENT");
            serviceIntent.putExtra("remote_ip", ipString);
            serviceIntent.putExtra("remote_port", port);

            // Starts the JobIntentService
            ClientService.enqueueWork(getApplicationContext(), ClientService.class, CLIENT_JOB_ID, serviceIntent);
        } else {

            if (!validIpFormat) {
                msg += "Wrong ip format!";
            }

            if (!validPortFormat) {
                msg += "\nWrong port format!";
            }

            notificationMsg.setText(msg);
        }
    }
}

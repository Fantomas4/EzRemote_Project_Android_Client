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
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    final static int CLIENT_JOB_ID = 1000;
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

            // Get extra data included in the Intent
            String connectionStatus = intent.getStringExtra("status");
            String connectionData = intent.getStringExtra("data");
            Log.d("receiver", "Got message: " + connectionStatus + " " + connectionData);

            switch (connectionStatus) {
                case "CONNECTION_INITIALIZATION_SUCCESS":
                    // Switch to the RemoteMenuActivity screen.
                    // note: Instead of using (getApplicationContext) use YourActivity.this
                    Intent newActivityIntent = new Intent(ManualConnectionActivity.this, RemoteMenuActivity.class);
                    ManualConnectionActivity.this.startActivity(newActivityIntent);

                    // Create a JSON message containing the request type and request data that the Client Service
                    // will be sending to the target Server
                    Map<String, String> msg_data = new HashMap<>();
                    msg_data.put("client_ip", ClientService.getClientIpAddress());
                    JSONObject jsonData = MessageGenerator.generateJsonObject("INITIALIZE_NEW_CONNECTION", msg_data);

                    // Request the Client Service to send the request to the server (connection initialization request)
                    Intent serviceIntent = new Intent();
                    serviceIntent.putExtra("activity_request","SEND_REQUEST_TO_SERVER");
                    // Convert JSON to String in order to use it with putExtra
                    serviceIntent.putExtra("json_data", jsonData.toString());
                    ClientService.enqueueWork(getApplicationContext(), ClientService.class, CLIENT_JOB_ID, serviceIntent);
                    break;

                case "CONNECTION_INITIALIZATION_ERROR":
                    notificationMsg.setText(connectionData);
                    break;

                case "CONNECTION_INITIALIZATION_FAIL":
                    notificationMsg.setText(connectionData);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);
        ipInput = findViewById(R.id.ipEditText);
        portInput = findViewById(R.id.portEditText);
        notificationMsg = findViewById(R.id.notificationMsgTextView);

        // Initialize the ConnectionStatusReceiver which is the BroadcastReceiver used to
        // receive status updates from the IntentService
        connectionStatusReceiver = new ConnectionStatusReceiver();

        // Register to receive messages.
        // We are registering an observer (connectionStatusReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(connectionStatusReceiver,
                new IntentFilter("connection_status"));
    }

    public void onClickConnectButton(View v) {

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
             * JobIntentService.
             */

            // Ask the Client Service to initialize the connection to the specified target Server
            Intent serviceIntent = new Intent();
            serviceIntent.putExtra("activity_request", "START_CLIENT");
            serviceIntent.putExtra("remote_ip", ipString);
            serviceIntent.putExtra("remote_port", port);

            // Starts the Client JobIntentService with the connection initialization request
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

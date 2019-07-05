package com.example.android.ezremote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;


public class ManualConnectionActivity extends AppCompatActivity {


    private EditText ipInput;
    private EditText portInput;
    private TextView notificationMsg;

    private ClientService clientService;
    private boolean isBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);
        ipInput = findViewById(R.id.ipEditText);
        portInput = findViewById(R.id.portEditText);
        notificationMsg = findViewById(R.id.notificationMsgTextView);

    }

    @Override
    protected void onStart() {

        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, ClientService.class);
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

    public void onConnectButtonClick(View v) {

        String ipString = ipInput.getText().toString();
        String portString = portInput.getText().toString();
        int portNumber = 0;

        Log.d("ip input: ", ipInput.getText().toString());
        Log.d("port input: ", portInput.getText().toString());

        String msg = "";

        // Check if the given ip string has a correct format
        boolean validIpFormat = ClientService.isIpFormatCorrect(ipString);
        // Initialize validPortFormat to true
        boolean validPortFormat = true;

        // Check if the given port string can be converted to integer, otherwise catch
        // the thrown exception and notify the user
        try {
            portNumber = Integer.parseInt(portString);
            // Check if the given port integer has a correct format
            validPortFormat = ClientService.isPortFormatCorrect(portNumber);
        } catch (NumberFormatException e) {
            validPortFormat = false;
        }


        if (validIpFormat && validPortFormat) {

            notificationMsg.setText("Connecting...");

            // Class used for the parallel execution of the new connection task
            class NewConnectionTask extends Thread {

                private String ipString;
                private int portNumber;

                public NewConnectionTask(String ipString, int portNumber) {
                    this.ipString = ipString;
                    this.portNumber = portNumber;
                }

                @Override
                public void run() {
                    try {
                        // Ask the Client Service to establish a socket connection to the specified target Server
                        clientService.createNewConnection(ipString, portNumber);

                        // If no exception occurs, the connection to the target Server has been
                        // established, so we now bind our application to the Server by sending an INITIALIZE_NEW_CONNECTION
                        // request (described in the application's communication protocol)

                        // Create the INITIALIZE_NEW_CONNECTION request's json message
                        Map<String, String> msg_data = new HashMap<>();
                        msg_data.put("client_ip", clientService.getClientIpAddress());
                        JSONObject jsonData = MessageGenerator.generateJsonObject("INITIALIZE_NEW_CONNECTION", msg_data);

                        JSONObject jsonResponse = new JSONObject(clientService.sendMsgAndRecvReply(jsonData));

                        if (jsonResponse.getString("status").equals("SUCCESS")) {
                            // The Server has responded with a SUCCESS status, so we know that we have successfully bind our
                            // Client Application to the Server and the Server has granted us access permission

                            // Now we are ready to switch to the next activity
                            Intent newActivityIntent = new Intent(ManualConnectionActivity.this, RemoteMenuActivity.class);
                            ManualConnectionActivity.this.startActivity(newActivityIntent);
                        } else {
                            // The Server has responded with a FAIL or ERROR status, so we notify the user and exit
                            notificationMsg.setText(jsonResponse.getString("data"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        if (e instanceof ConnectException) {
                            // Server was unreachable
                            Log.d("timeout", "ConnectException!!!!!");
                            notificationMsg.setText("The specified server is unreachable!");

                        } else if (e instanceof SocketTimeoutException) {
                            notificationMsg.setText("Connection timed out!");

                        } else {
                            notificationMsg.setText("An unhandled exception occurred!");

                        }
                    }
                }
            }

            new NewConnectionTask(ipString,portNumber).start();

            } else{

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

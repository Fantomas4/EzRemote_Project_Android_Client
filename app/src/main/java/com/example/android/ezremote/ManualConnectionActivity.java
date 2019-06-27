package com.example.android.ezremote;

import android.content.Intent;
import android.os.AsyncTask;
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

public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    Button connectButton;

    Client clientInstance;


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

    }


    private class ConnectionTask extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... connectionData) {

            // create a new connection to the server

            // NOTE: Network activities should NEVER be put in the main thread (causes unhandled exception)

            // *** FOR NORMAL APPLICATION USE ***

            clientInstance = Client.getInstance();

            HashMap<String,String> executionResult = new HashMap<>();

            try {
                Log.d("in", "innnnn");
                clientInstance.createNewConnection(connectionData[0], Integer.parseInt(connectionData[1]));
                Log.d("out", "outtttt");

                // create make_connection request json message
                Map<String, String> msg_data = new HashMap<>();
                msg_data.put("client_ip", clientInstance.getClientIpAddress());
                JSONObject jsonObject = MessageGenerator.generateJsonObject("INITIALIZE_NEW_CONNECTION", msg_data);

                executionResult.put("connection_status", "SUCCESS");
                executionResult.put("connection_data", clientInstance.sendMsgAndRecvReply(jsonObject));

            } catch (Exception e) {
                e.printStackTrace();

                if (e instanceof ConnectException) {
                    // Server was unreachable
                    Log.d("timeout", "ConnectException!!!!!");

                    executionResult.put("connection_status", "ERROR");
                    executionResult.put("connection_data", "The specified server is unreachable!");

                } else if (e instanceof SocketTimeoutException) {
                    executionResult.put("connection_status", "ERROR");
                    executionResult.put("connection_data", "Connection timed out!");
                } else {
                    executionResult.put("connection_status", "ERROR");
                    executionResult.put("connection_data", "An unhandled exception occurred!");
                }
            }

            return executionResult;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> executionResult) {

            if (executionResult.get("connection_status").equals("SUCCESS")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(executionResult.get("connection_data"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "========================================================================== unexpected JSON exception", e);
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.getString("status").equals("SUCCESS")) {
                        // received json message has a "success" status
                        // Switch to the RemoteMenuActivity screen.
                        // note: Instead of using (getApplicationContext) use YourActivity.this
                        Intent intent = new Intent(ManualConnectionActivity.this, RemoteMenuActivity.class);
                        ManualConnectionActivity.this.startActivity(intent);
                    } else if (jsonObject.getString("status").equals("ERROR")){
                        // received json message
                        notificationMsg.setText(jsonObject.getJSONObject("data").getString("error_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (executionResult.get("status").equals("ERROR")) {
                // an error occurred while connecting to the client, so we notify the user
                notificationMsg.setText(executionResult.get("data"));
            }
        }
    }


    private void connect() {

        String ipString = ipInput.getText().toString();
        String port = portInput.getText().toString();

        Log.d("ip input: ", ipInput.getText().toString());
        Log.d("port input: ", portInput.getText().toString());

        String msg = "";

        boolean validIpFormat = Client.isIpFormatCorrect(ipString);

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
            validPortFormat = Client.isPortFormatCorrect(Integer.parseInt(port));
        }


        if (validIpFormat && validPortFormat) {
            notificationMsg.setText("Connecting...");
            new ConnectionTask().execute(ipString, port);
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

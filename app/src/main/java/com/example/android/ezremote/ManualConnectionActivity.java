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
                msg_data.put("ip", clientInstance.getClientIpAddress());
                JSONObject jsonObject = MessageGenerator.generateJsonObject("make_connection", msg_data);

                executionResult.put("STATUS", "SUCCESS");
                executionResult.put("DATA", "clientInstance.sendMsgAndRecvReply(jsonObject);\n");

            } catch (Exception e) {
                e.printStackTrace();

                if (e instanceof ConnectException) {
                    // Server was unreachable
                    Log.d("timeout", "SocketTimeoutException!!!!!!");

                    executionResult.put("STATUS", "FAILED");
                    executionResult.put("DATA","The specified server is unreachable!" );

                } else {
                    executionResult.put("STATUS", "FAILED");
                    executionResult.put("DATA", "An unhandled exception occured!");
                }
            }

            return executionResult;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> executionResult) {
//            Log.d("Receive debug prefinal", reply);
            // xreiazetai?
//            super.onPostExecute(reply);
//            Log.d("Receive debug final", reply);

            if (executionResult.get("STATUS").equals("SUCCESS")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(executionResult.get("DATA"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "========================================================================== unexpected JSON exception", e);
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.getString("status").equals("success")) {
                        // received json message has a "success" status
                        // Switch to the RemoteMenuActivity screen.
                        // note: Instead of using (getApplicationContext) use YourActivity.this
                        Intent intent = new Intent(ManualConnectionActivity.this, RemoteMenuActivity.class);
                        ManualConnectionActivity.this.startActivity(intent);
                    } else {
                        // received json message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (executionResult.get("STATUS").equals("FAILED")) {
                // an error occurred while connecting to the client, so we notify the user
                notificationMsg.setText(executionResult.get("DATA"));
            }

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);
        ipInput = (EditText)findViewById(R.id.ipEditText);
        portInput = (EditText)findViewById(R.id.portEditText);
        notificationMsg = (TextView)findViewById(R.id.notificationMsgTextView);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

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

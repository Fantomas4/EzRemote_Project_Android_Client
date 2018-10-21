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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    Button connectButton;

    Client clientInstance;

    private class ConnectionTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... connectionData) {

            // create a new connection to the server

            // *** USED ONLY DURING APP TESTING ***
//            Client.instance = new Client("192.168.1.103", 7890);
//            clientInstance = Client.instance;

            // *** FOR NORMAL APPLICATION USE ***
            Client.instance = new Client(connectionData[0], Integer.parseInt(connectionData[1]));
            clientInstance = Client.instance;

            // create make_connection request json message
            Map<String, String> msg_data = new HashMap<>();
            msg_data.put("ip", clientInstance.getClientIpAddress());
            JSONObject jsonObject = MessageGenerator.generateJsonObject("request", "make_connection", msg_data);

            return clientInstance.sendMsgAndRecvReply(jsonObject);

        }

        @Override
        protected void onPostExecute(String reply) {
            Log.d("Receive debug prefinal", reply);
            // xreiazetai?
            super.onPostExecute(reply);
            Log.d("Receive debug final", reply);
            String analysisResult = MessageAnalysis.analyzeMessage(reply);

            if (analysisResult.equals("connection_request_accepted")) {
                // Switch to the RemoteMenuActivity screen.
                // note: Instead of using (getApplicationContext) use YourActivity.this
                Intent intent = new Intent(ManualConnectionActivity.this, RemoteMenuActivity.class);
                ManualConnectionActivity.this.startActivity(intent);
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

        new ConnectionTask().execute(ipString, port);

    }
}

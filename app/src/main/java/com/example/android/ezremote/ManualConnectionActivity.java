package com.example.android.ezremote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    Button connectButton;

    Client clientInstance;

    private class ConnectionTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void...arg0) {

            // create a new connection to the server
            clientInstance = new Client("192.168.1.102", 3456);

            // create make_connection request json message
            Map<String, String> msg_data = new HashMap<>();
            msg_data.put("ip", clientInstance.getClientIpAddress());
//            msg_data.put("ip", "192.168.1.102");
            JSONObject jsonObject = MessageGenerator.generateJsonObject("request", "make_connection", msg_data);

            return clientInstance.sendMsgAndRecvReply(jsonObject);

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Receive debug prefinal", result);
            // xreiazetai?
            super.onPostExecute(result);
            Log.d("Receive debug final", result);
            MessageAnalysis.analyzeMessage(result);


        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);

        ipInput = (EditText)findViewById(R.id.ip_input);
        portInput = (EditText)findViewById(R.id.port_input);
        notificationMsg = (TextView)findViewById(R.id.notification_msg_textView);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

    }

    private void connect() {

        new ConnectionTask().execute();

    }
}

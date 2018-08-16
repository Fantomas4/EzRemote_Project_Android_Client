package com.example.android.ezremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ManualConnectionActivity extends AppCompatActivity {


    EditText ipInput;
    EditText portInput;
    TextView notificationMsg;
    Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_connection);

        ipInput = (EditText)findViewById(R.id.ip_input);
        portInput = (EditText)findViewById(R.id.port_input);
        notificationMsg = (TextView)findViewById(R.id.notification_msg_textView);
        connectButton = (Button)findViewById(R.id.connect_button);

    }
}

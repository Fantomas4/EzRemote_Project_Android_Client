package com.example.android.ezremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button scanNetworkBtn;
    private Button manualConnectionBtn;
    private Button settingsBtn;

//    public static
//    MainActivity::client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanNetworkBtn = findViewById(R.id.scan_network_btn);
        scanNetworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanNetworkActivity();
            }
        });

        manualConnectionBtn = findViewById(R.id.manual_connection_btn);
        manualConnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManualConnectionActivity();
            }
        });


        settingsBtn = findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });


    }

    public void openScanNetworkActivity() {
        Intent intent = new Intent(this, ScanNetworkActivity.class);
        startActivity(intent);
    }

    public void openManualConnectionActivity() {
        Intent intent = new Intent(this, ManualConnectionActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

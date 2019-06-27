package com.example.android.ezremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RemoteMenuActivity extends AppCompatActivity {

    Button shutdownCommandButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_menu);

        shutdownCommandButton = findViewById(R.id.shutdown_command_button);
        shutdownCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToShutdownCommandActivity();
            }
        });
    }

    private void switchToShutdownCommandActivity() {
        Intent intent = new Intent(this, ShutdownCommandActivity.class);
        startActivity(intent);
    }
}

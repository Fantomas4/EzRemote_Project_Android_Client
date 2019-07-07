package com.example.android.ezremote;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RemoteMenuActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_menu);
    }

    /*
     * A method used to determine the Android UI's "Back Button" behavior.
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            // This is the second time the user has pressed the "Back" button,
            // so we prepare to terminate the connection to the Server and switch
            // to the previous activity

            // Terminate our connection to the Server through the ClientService


            // Call the original onBackPressed method that calls finish() for the current
            // activity and switches to the previous activity
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onClickShutdownComputerButton(View v) {
        Intent shutdownCommandActivityIntent = new Intent(this, ShutdownCommandActivity.class);
        startActivity(shutdownCommandActivityIntent);
    }
}

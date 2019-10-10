package com.example.android.ezremote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RemoteMenuActivity extends NetworkActivity {

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void switchActivity(Bundle bundle) {
        // Switch to the MainActivity activity and print an error message
        // inside the notification message element
        Intent mainActivityIntent = new Intent(RemoteMenuActivity.this, MainActivity.class);
        mainActivityIntent.putExtras(bundle);
        startActivity(mainActivityIntent);
        // Kill this activity
        finishActivity();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_remote_menu;
    }

    public void finishActivity() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

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

            // Execute TerminateConnectionTask using AsyncTask
            new TerminateConnectionTask().execute();

//            // Call the original onBackPressed method that calls finish() for the current
//            // activity and switches to the previous activity
//            super.onBackPressed();
            Bundle  bundle = new Bundle();
            bundle.putString("notificationMessage", "");
            switchActivity(bundle);

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

    class TerminateConnectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            return clientService.terminateConnection();
        }

        @Override
        protected void onPostExecute(String serverResponseData) {
            if (serverResponseData != null)
                // The Server has responded with a FAIL or ERROR status or an exception was caught,
                // so we notify the user and exit
                Toast.makeText(getApplicationContext(), serverResponseData, Toast.LENGTH_LONG).show();

        }
    }
}

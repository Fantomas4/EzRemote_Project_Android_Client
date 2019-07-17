package com.example.android.ezremote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ShutdownCommandActivity extends NetworkActivity implements View.OnClickListener {

    private Button incHoursButton;
    private Button incMinsButton;
    private Button incSecsButton;
    private Button incMsecsButton;

    private TextView hoursTextView;
    private TextView minsTextView;
    private TextView secsTextView;
    private TextView msecsTextView;

    private Button decHoursButton;
    private Button decMinsButton;
    private Button decSecsButton;
    private Button decMsecsButton;

    private Button setTimerButton;
    private Button shutdownNowButton;

    private TextView notificationMsgTextView;
    private Button cancelTimerButton;

    CountDownTimer shutdownCountDownTimer;


    public void finishActivity() {
        finish();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_shutdown_command;
    }

    @Override
    protected void switchActivity(Bundle bundle) {
        // Switch to the MainActivity activity and print an error message
        // inside the notification message element
        Intent mainActivityIntent = new Intent(ShutdownCommandActivity.this, MainActivity.class);
        mainActivityIntent.putExtras(bundle);
        startActivity(mainActivityIntent);
        // Kill this activity
        finishActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        incHoursButton = findViewById(R.id.incHoursButton);
        incHoursButton.setOnClickListener(this);
        incMinsButton = findViewById(R.id.incMinsButton);
        incMinsButton.setOnClickListener(this);
        incSecsButton = findViewById(R.id.incSecsButton);
        incSecsButton.setOnClickListener(this);
        incMsecsButton = findViewById(R.id.incMsecsButton);
        incMsecsButton.setOnClickListener(this);

        hoursTextView = findViewById(R.id.hoursTextView);
        minsTextView = findViewById(R.id.minsTextView);
        secsTextView = findViewById(R.id.secsTextView);
        msecsTextView = findViewById(R.id.msecsTextView);

        decHoursButton = findViewById(R.id.decHoursButton);
        decHoursButton.setOnClickListener(this);
        decMinsButton = findViewById(R.id.decMinsButton);
        decMinsButton.setOnClickListener(this);
        decSecsButton = findViewById(R.id.decSecsButton);
        decSecsButton.setOnClickListener(this);
        decMsecsButton = findViewById(R.id.decMsecsButton);
        decMsecsButton.setOnClickListener(this);

        setTimerButton = findViewById(R.id.setTimerButton);
        setTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick listener", "mpika1");
                new ShutdownCommandTask().execute(hoursTextView.getText().toString(), minsTextView.getText().toString(),
                        secsTextView.getText().toString(), msecsTextView.getText().toString());
            }
        });


        shutdownNowButton = findViewById(R.id.shutdownNowButton);
        shutdownNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick listener", "mpika2");
                new ShutdownCommandTask().execute("0", "0", "0", "0");
            }
        });

        notificationMsgTextView = findViewById(R.id.notificationMsgTextView);

        cancelTimerButton = findViewById(R.id.cancelTimerButton);
        cancelTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelShutdownCommandTask().execute();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    // it is necessary to implement implements View.OnClickListener for the activity class,
    // otherwise "Method does not override method from its superclass error" is shown.
    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..

        switch (v.getId()) {

            case R.id.incHoursButton:
                changeTextViewValue("increment", hoursTextView);
                break;

            case R.id.incMinsButton:
                changeTextViewValue("increment", minsTextView);
                break;

            case R.id.incSecsButton:
                changeTextViewValue("increment", secsTextView);
                break;

            case R.id.incMsecsButton:
                changeTextViewValue("increment", msecsTextView);
                break;

            case R.id.decHoursButton:
                changeTextViewValue("decrement", hoursTextView);
                break;

            case R.id.decMinsButton:
                changeTextViewValue("decrement", minsTextView);
                break;

            case R.id.decSecsButton:
                changeTextViewValue("decrement", secsTextView);
                break;

            case R.id.decMsecsButton:
                changeTextViewValue("decrement", msecsTextView);
                break;

            default:
                break;
        }
    }

    private void changeTextViewValue(String action, TextView t) {

        if (action.equals("increment")) {
            if (t.getText().toString().equals("9999")) {
                t.setText("0");
            } else {
                int value = Integer.parseInt(t.getText().toString());
                value += 1;
                t.setText(Integer.toString(value));
            }
        } else if (action.equals("decrement")) {
            if (t.getText().toString().equals("0")) {
                t.setText("9999");
            } else {
                int value = Integer.parseInt(t.getText().toString());
                value -= 1;
                t.setText(Integer.toString(value));
            }
        }

    }

    // Class used for the parallel execution of the shutdown command task
    private class ShutdownCommandTask extends AsyncTask<String, Void, Object> {

        String hours;
        String mins;
        String secs;
        String msecs;

        @Override
        protected Object doInBackground(String... params) {
            hours = params[0];
            mins = params[1];
            secs = params[2];
            msecs = params[3];


            // Create a json object that holds the request data that will be send to the server.
            HashMap<String, String> msgData = new HashMap<>();
            msgData.put("hours", hours);
            msgData.put("mins", mins);
            msgData.put("secs", secs);
            msgData.put("msecs", msecs);

            JSONObject jsonData = MessageGenerator.generateJsonObject("EXECUTE_SHUTDOWN_COMMAND", msgData);

            Object executionResult = null;

            try {
                executionResult = new JSONObject(clientService.sendMsgAndRecvReply(jsonData));
            } catch (JSONException e) {
                e.printStackTrace();

                executionResult = e;
            } catch (IOException e) {
                e.printStackTrace();

                executionResult = e;
            }

            return executionResult;
        }

        @Override
        protected void onPostExecute(Object executionResult) {

            if (executionResult instanceof JSONObject) {

                JSONObject jsonResponse = (JSONObject) executionResult;
                String status = null;
                JSONObject data = null;

                try {
                    status = jsonResponse.getString("status");
                    data = jsonResponse.getJSONObject("data");

                    if (status.equals("SUCCESS")) {
                        // The Server has responded with a SUCCESS status, so we know that the shutdown command
                        // request has been serviced successfully

                        // Display the "Cancel Timer" button, disable the "Set timer" and "Shutdown now" buttons.
                        setTimerButton.setEnabled(false);
                        shutdownNowButton.setEnabled(false);
                        cancelTimerButton.setVisibility(View.VISIBLE);

                        // Calculate the total time of the shutdown timer by converting user's input in milliseconds
                        final int totalTime = Integer.parseInt(hours) * 3600000 + Integer.parseInt(mins) * 60000 +
                                Integer.parseInt(secs) * 1000 + Integer.parseInt(msecs);

                        // Start the countdown timer using totalTime
                        shutdownCountDownTimer = new CountDownTimer(totalTime, 1000) {

                            @Override
                            public void onTick(final long millisUntilFinished) {
                                // Print the countdown to notificationMsgTextView
                                notificationMsgTextView.setText(String.format(Locale.getDefault(), "Remote computer will shutdown in: \n %02d : %02d : %02d",
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                            }

                            @Override
                            public void onFinish() {
                                // update the notificationMsgTextView to inform the user that the shutdown command
                                // has been executed, enable the "Set timer" and "Shutdown now" buttons and hide the cancelTimerButton button.
                                notificationMsgTextView.setText("Shutdown executed!");
                                cancelTimerButton.setVisibility(View.GONE);
                                setTimerButton.setEnabled(true);
                                shutdownNowButton.setEnabled(true);
                            }
                        }.start();

                    } else if (status.equals("FAIL")) {
                        // The Server has responded with a FAIL status, so we notify the user and exit
                        notificationMsgTextView.setText(data.getString("fail_message"));

                    } else if (status.equals("ERROR")) {
                        // The Server has responded with an ERROR status, so we notify the user and exit
                        notificationMsgTextView.setText(data.getString("error_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (executionResult instanceof IOException) {
                // The Server has abruptly ended the connection.
                // Switch to the ManualConnection activity and print an error message
                // inside the notification message element
                Intent manualConnectionActivityIntent = new Intent(ShutdownCommandActivity.this, ManualConnectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("notificationMsg", "Error: Server unresponsive!");

                switchActivity(bundle);

            } else if (executionResult instanceof Exception) {
                // An unexpected exception has occured
                ((Exception) executionResult).printStackTrace();
            }


        }
    }

    // Class used for the parallel execution of the cancel shutdown command task
    private class CancelShutdownCommandTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... voids) {

            // Create a json object that holds the request data that will be send to the server.
            HashMap<String, String> msgData = new HashMap<>();
            JSONObject jsonData = MessageGenerator.generateJsonObject("CANCEL_SHUTDOWN_COMMAND", msgData);

            Object executionResult = null;

            try {
                executionResult = new JSONObject(clientService.sendMsgAndRecvReply(jsonData));
            } catch (JSONException e) {
                e.printStackTrace();

                executionResult = e;
            } catch (IOException e) {
                e.printStackTrace();

                executionResult = e;
            }

            return executionResult;
        }

        @Override
        protected void onPostExecute(Object executionResult) {

            if (executionResult instanceof JSONObject) {

                JSONObject jsonResponse = (JSONObject) executionResult;
                String status = null;
                JSONObject data = null;

                try {
                    status = jsonResponse.getString("status");
                    data = jsonResponse.getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (status.equals("SUCCESS")) {
                        // The Server has responded with a SUCCESS status, so we know that the cancel shutdown
                        // command request has been serviced successfully

                        // Cancel the shutdownCountDownTimer that prints the remaining time in the notificationMsgTextView
                        shutdownCountDownTimer.cancel();

                        // Notify the user by printing a message in notificationMsgTextView
                        notificationMsgTextView.setText("Shutdown command has been canceled!");

                        // Re-enable the "Set timer" and "Shutdown now" buttons
                        setTimerButton.setEnabled(true);
                        shutdownNowButton.setEnabled(true);
                        cancelTimerButton.setVisibility(View.GONE);

                    } else if (status.equals("FAIL")) {
                        // Modify the user by printing a message in notificationMsgTextView
                        notificationMsgTextView.setText(data.getString("fail_message"));

                        // Re-enable the "Set timer" and "Shutdown now" buttons
                        setTimerButton.setEnabled(true);
                        shutdownNowButton.setEnabled(true);
                        cancelTimerButton.setVisibility(View.GONE);

                    } else if (status.equals("ERROR")) {
                        // Notify the user by printing a message in notificationMsgTextView
                        notificationMsgTextView.setText(data.getString("error_message"));

                        // Re-enable the "Set timer" and "Shutdown now" buttons
                        setTimerButton.setEnabled(true);
                        shutdownNowButton.setEnabled(true);
                        cancelTimerButton.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (executionResult instanceof IOException) {
                // The Server has abruptly ended the connection.
                // Switch to the ManualConnection activity and print an error message
                // inside the notification message element
                Intent manualConnectionActivityIntent = new Intent(ShutdownCommandActivity.this, ManualConnectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("notificationMsg", "Error: Server unresponsive!");

                switchActivity(bundle);

            } else if (executionResult instanceof Exception) {
                // An unexpected exception has occured
                ((Exception) executionResult).printStackTrace();
            }
        }
    }
}

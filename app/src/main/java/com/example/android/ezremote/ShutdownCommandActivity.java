package com.example.android.ezremote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShutdownCommandActivity extends AppCompatActivity implements View.OnClickListener{

    Client clientInstance;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown_command);

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

    private class ShutdownCommandTask extends AsyncTask<String, String, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(String... commandType) {

            // json object that holds the data that will be send to the server.
            JSONObject jsonObject = null;

            // create a client instance that has static fields containing the info for the connection
            // that has already been created.
            clientInstance = Client.instance;
//            clientInstance = new Client("192.168.1.108", 7890);

            // create make_connection request json message for instant shutdown
            Map<String, String> msg_data = new HashMap<>();
            msg_data.put("hours", commandType[0]);
            msg_data.put("mins", commandType[1]);
            msg_data.put("secs", commandType[2]);
            msg_data.put("msecs", commandType[3]);

            jsonObject = MessageGenerator.generateJsonObject("execute_shutdown_system_command", msg_data);


            Map<String, String> replyAndTimerData = new HashMap<>();
            replyAndTimerData.put("reply", clientInstance.sendMsgAndRecvReply(jsonObject));
            replyAndTimerData.put("hours", commandType[0]);
            replyAndTimerData.put("mins", commandType[1]);
            replyAndTimerData.put("secs", commandType[2]);
            replyAndTimerData.put("msecs", commandType[3]);

            return replyAndTimerData;


        }

        @Override
        protected void onPostExecute(Map<String, String> replyAndTimerData) {
            Log.d("Receive debug prefinal", replyAndTimerData.get("reply"));
            // xreiazetai?
            super.onPostExecute(replyAndTimerData);
            Log.d("Receive debug final", replyAndTimerData.get("reply"));
            // MessageAnalysis.analyzeMessage(getApplicationContext(), result);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(replyAndTimerData.get("reply"));
            } catch (JSONException e) {
                Log.e("MYAPP", "========================================================================== unexpected JSON exception", e);
                e.printStackTrace();
            }

            try {
                if (jsonObject.getString("status").equals("success")) {
                    // received json message has a "success" status
                    // Start the countdown timer according to the time data in "replyAndTimerData"
                    // Display the "Cancel Timer" button

                    // the total time of the shutdown timer the user set in milliseconds
                    int totalTime = (Integer.parseInt(replyAndTimerData.get("hours")) * 3600000) + (Integer.parseInt(replyAndTimerData.get("mins")) * 60000) +
                            (Integer.parseInt(replyAndTimerData.get("secs")) * 1000) + Integer.parseInt(replyAndTimerData.get("msecs"));

                    // delay represents the time lost while the server prepared and sent its reply to our client
                    int delay = 1000;

                    CountDownTimer shutdownCountDownTimer = new CountDownTimer(totalTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            notificationMsgTextView.setText("Remote computer will shutdown in: \n" + millisUntilFinished);

                        }

                        @Override
                        public void onFinish() {
                            notificationMsgTextView.setText("done!");
                        }
                    }.start();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }





        }

    }


}
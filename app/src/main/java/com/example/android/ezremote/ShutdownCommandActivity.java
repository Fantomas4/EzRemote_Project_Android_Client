package com.example.android.ezremote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                new ShutdownCommandTask().execute("timer_shutdown");
            }
        });


        shutdownNowButton = findViewById(R.id.shutdownNowButton);
        shutdownNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick listener", "mpika2");
                new ShutdownCommandTask().execute("instant_shutdown");
            }
        });


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

    private class ShutdownCommandTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... commandType) {

            // json object that holds the data that will be send to the server.
            JSONObject jsonObject = null;

            // create a client instance that has static fields containing the info for the connection
            // that has already been created.
//            clientInstance = Client.instance;
            clientInstance = new Client("155.207.229.119", 3456);

            // takes the string argument located in the 0 position of the String[] array commandType.
            if (commandType[0].equals("instant_shutdown")) {
                // create make_connection request json message for instant shutdown
                Map<String, String> msg_data = new HashMap<>();
                msg_data.put("type", "shutdown_system");
                msg_data.put("hours", "0");
                msg_data.put("mins", "0");
                msg_data.put("secs", "0");
                msg_data.put("msecs", "0");

                jsonObject = MessageGenerator.generateJsonObject("request", "execute_command", msg_data);

                return clientInstance.sendMsgAndRecvReply(jsonObject);

            } else if (commandType[0].equals("timer_shutdown")) {
                // create make_connection request json message for instant shutdown
                Map<String, String> msg_data = new HashMap<>();
                msg_data.put("type", "shutdown_system");
                msg_data.put("hours", hoursTextView.getText().toString());
                msg_data.put("mins", minsTextView.getText().toString());
                msg_data.put("secs", secsTextView.getText().toString());
                msg_data.put("msecs", msecsTextView.getText().toString());

                jsonObject = MessageGenerator.generateJsonObject("request", "execute_command", msg_data);


            }

            return clientInstance.sendMsgAndRecvReply(jsonObject);

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Receive debug prefinal", result);
            // xreiazetai?
            super.onPostExecute(result);
            Log.d("Receive debug final", result);
            // MessageAnalysis.analyzeMessage(getApplicationContext(), result);


        }

    }


}

package com.example.android.ezremote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.content.ContextCompat.startActivity;

public class MessageAnalysis {

    private void switchToRemoteMenuActivity() {

    }

    static public void analyzeMessage(Context context, String msg) {

        Log.d("analyzeMessage", "MSG RECEIVED IS ::::::::::::::::::: " + msg);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);
        } catch (JSONException e) {
            Log.e("MYAPP", "========================================================================== unexpected JSON exception", e);
            e.printStackTrace();
        }

        try {
            if (jsonObject.getString("msg_type").equals("response")) {

                if (jsonObject.getString("msg_content").equals("connection_request_status")) {

                    JSONObject msgData = jsonObject.getJSONObject("msg_data");

                    String status = msgData.getString("connection_request_status");

                    if(status.equals("accepted")) {
                        if (msgData.getString("connection_request_status").equals("accepted")) {
                            // The server has accepted the client's request for bonding in a new connection
                            // Switch to the RemoteMenuActivity screen.
                            Log.d("intent", "eftasa sto intent))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
                            Intent intent = new Intent(context, RemoteMenuActivity.class);
                            context.startActivity(intent);
                            Log.d("intent", "perasa to intent((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((new in");
                        }
                    }


                } else if (jsonObject.getString("msg_content").equals("connection_request_status")) {
                    JSONObject msgData = jsonObject.getJSONObject("msg_data");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

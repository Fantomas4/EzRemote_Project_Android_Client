package com.example.android.ezremote;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageAnalysis {

    static public void analyzeMessage(String msg) throws JSONException {

        JSONObject jsonObject = new JSONObject(msg);

        if (jsonObject.getString("msg_type").equals("response")) {

            if (jsonObject.getString("msg_content").equals("connection_request_status")) {

                JSONObject msgData = jsonObject.getJSONObject("msg_data");

                String status = msgData.getString("connection_request_status");

                if(status.equals("accepted")) {

                }
            }
        }
    }
}

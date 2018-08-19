package com.example.android.ezremote;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageAnalysis {

    static public void analyzeMessage(String msg) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);
        } catch (JSONException e) {
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

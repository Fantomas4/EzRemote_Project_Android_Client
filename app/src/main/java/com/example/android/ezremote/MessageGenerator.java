package com.example.android.ezremote;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessageGenerator {

    public static JSONObject generateJsonObject(String msgType, String msgContent, Map<String, String> msgData) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg_type", msgType);
            jsonObject.put("msg_content", msgContent);
            JSONObject msgDataJsonObj = new JSONObject(msgData);
            jsonObject.put("msg_data", msgDataJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }
}

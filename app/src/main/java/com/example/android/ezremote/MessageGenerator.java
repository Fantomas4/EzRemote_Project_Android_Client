package com.example.android.ezremote;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessageGenerator {

    public static JSONObject generateJsonObject(String request, Map<String, String> msgData) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("request", request);
            JSONObject msgDataJsonObj = new JSONObject(msgData);
            jsonObject.put("data", msgDataJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }
}

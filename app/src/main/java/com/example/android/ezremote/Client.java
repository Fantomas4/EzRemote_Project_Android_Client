package com.example.android.ezremote;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class Client extends AsyncTask<Void, Void, String> {

    private String dstAddress;
    private int dstPort;
    private String response = "";

    private Socket socket;

    Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
    }


    private void createSocket() {
        socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(JSONObject jsonObject) {

        String message = jsonObject.toString();

        // Use encoding of your choice
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), "UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // append and flush in logical chunks
        try {
            out.append(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String receiveMessage() {

        //Socket socket = null;


        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            /*
             * notice: inputStream.read() will block if no data return
             */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }

        return response;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        return receiveMessage();
    }

    @Override
    protected void onPostExecute(String result) {

        // xreiazetai?
        super.onPostExecute(result);
    }

}
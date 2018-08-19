package com.example.android.ezremote;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static String dstAddress;
    private static int dstPort;
    private String response = "";

    private static Socket socket;

    private static boolean inConnection = false;

    private class ClientNotInConnection extends Exception {

        private ClientNotInConnection(String message) {
            super(message);
        }

    }



    Client(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
        inConnection = true;
        Log.d("constructor1", "before createSocket() reached");
        createSocket();
        Log.d("constructor2", "after createSocket() reached");
    }

    Client() {

        try {
            if (!inConnection) {
                throw new ClientNotInConnection("Error: The client has not been connected to a server yet!");
            }
        } catch (ClientNotInConnection e) {
            e.printStackTrace();
        }
    }


    private void createSocket() {
        socket = null;
        try {
            Log.d("bug", "mpika");
            socket = new Socket(dstAddress, dstPort);
            if (socket == null) {
                Log.d("socket12", "it is null");
            } else {
                Log.d("socket12", "it is NOT null");
            }

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

    public String getClientIpAddress() {
        InetAddress addr = socket.getInetAddress();
        String ip = (addr != null) ? addr.getHostAddress() : "*";

        return ip;
    }

    public String sendMsgAndRecvReply(JSONObject jsonObject) {
        sendMessage(jsonObject);
        return receiveMessage();
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

}
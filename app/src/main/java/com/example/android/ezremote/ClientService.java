package com.example.android.ezremote;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ClientService extends JobIntentService {

    private String dstAddress;
    private int dstPort;
    private OutputStreamWriter outputStreamWriter;
    private Writer bufWriter;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufReader;
    private Socket socket;
    private boolean inConnection;

    private static final Pattern REGEX_IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");


    public ClientService() {
        this.inConnection = false;
    }

    @Override
    protected void onHandleWork(Intent workIntent) {
        // Gets data from the incoming Intent
        Bundle intentExtras = workIntent.getExtras();

        // Do work here, based on the contents of intentExtras
        if (intentExtras != null) {
            Log.d("sender", "Broadcasting message");
            Intent replyIntent = new Intent("connection_status");
            String request = intentExtras.getString("activity_request");

            switch (request) {
                case "START_CLIENT":
                    try {
                        createNewConnection(intentExtras.getString("remote_ip"), Integer.parseInt(intentExtras.getString("remote_port")));
                    } catch (Exception e) {
                        e.printStackTrace();

                        if (e instanceof ConnectException) {
                            // Server was unreachable
                            Log.d("timeout", "ConnectException!!!!!");

                            replyIntent.putExtra("status", "ERROR");
                            replyIntent.putExtra("data", "The specified server is unreachable!");

                        } else if (e instanceof SocketTimeoutException) {
                            replyIntent.putExtra("status", "ERROR");
                            replyIntent.putExtra("data", "Connection timed out!");
                        } else {
                            replyIntent.putExtra("status", "ERROR");
                            replyIntent.putExtra("data", "An unhandled exception occurred!");
                        }
                    }
                    // Send intent to activity's BroadcastReceiver
                    LocalBroadcastManager.getInstance(this).sendBroadcast(replyIntent);

                    break;
            }
        }
    }

    private class ClientNotInConnection extends Exception {

        private ClientNotInConnection(String message) {
            super(message);
        }

    }

    public void createNewConnection(String ip, int port) throws Exception {
        this.dstAddress = ip;
        this.dstPort = port;

//        try {
//            createSocket();
//            this.inConnection = true;
//        } catch (Exception e) {
//            throw e;
//        }


        createSocket();
        this.inConnection = true;

        try {
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use encoding of your choice
        Log.d("writer 1-1", "writer 1-1");
        bufWriter = new BufferedWriter(outputStreamWriter);
        Log.d("writer 1-2", "writer 1-2");


        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("reader 1-1", "reader 1-1");
        bufReader = new BufferedReader(inputStreamReader);

        Log.d("reader 1-2", "reader 1-2");


        Log.d("eftasa", "5");
    }

    private void createSocket() throws Exception {
//        Log.d("eftasa", "3");
//        socket = null;
//        socket = new Socket(dstAddress, dstPort);
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(dstAddress, dstPort), 5000);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    public static boolean isIpFormatCorrect(String ip) {
        return ip.matches(String.valueOf(REGEX_IP_ADDRESS));
    }

    public static boolean isPortFormatCorrect(int port) {

        return port >= 0 && port <= 65535;
    }

    public String getClientIpAddress() {
        InetAddress addr = socket.getInetAddress();
        String ip = (addr != null) ? addr.getHostAddress() : "*";

        return ip;
    }


    public String sendMsgAndRecvReply(JSONObject jsonObject) {
        sendMessage(jsonObject);

        String receivedMessage = receiveMessage();

        return receivedMessage;
    }

    public void sendMessage(JSONObject jsonObject) {

        String message = jsonObject.toString();
//        Log.d("writer", "The message1 is: " + message);
        // **** IMPORTANT ****
        // Adding the \0 delimiter at the end of the message that will be send to the server
        // is important, as this delimiter is used by the server to determine the end of the message.
        message += "\0";


        // append and flush in logical chunks
        try {
            Log.d("writer 1-3", "writer 1-3");
            Log.d("writer", "The message2 is: " + message);
            bufWriter.append(message);
            bufWriter.flush();
            Log.d("writer 1-4", "writer 1-4");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String receiveMessage() {

        Log.d("Receive debug", "in receive_msg client func BEGINNING");


        final int DEFAULT_BUFFER_SIZE = 5000;
        char[] cbuf = new char[DEFAULT_BUFFER_SIZE];
        String finalMsg = "";
        int offset = 0;


        int recvSize;
        boolean endReception = false;

        try {
            while ((recvSize = bufReader.read(cbuf)) != -1) {
                Log.d("in.read loop", "diavasa " + recvSize + "chars");
                //offset = recvSize;
                Log.d("in.read loop", "thesi 2");


                // for debugging only
                int counter = 0;

                // Read each character from the received message in the cbuf buffer until the delimiter
                // "\0" is detected. This delimiter marks the end of the message.
                for (char c : cbuf) {
                    if (Character.toString(c).equals("\0")) {
                        Log.d("in for each loop", "vrika delimiter");

                        // set the endReception flag to true to stop the InputStreamReader from
                        // blocking the thread and waiting for another message.
                        endReception = true;
                        break;
                    } else {
                        finalMsg += String.valueOf(c);
//                        counter ++;
//                        Log.d("in for each loop", "vrika char");
//                        Log.d("in for each loop", "char counter: " + counter);
                    }
                }

                if (endReception) {
                    // exit the while loop to prevent the InputStreamReader from blocking.
                    break;
                }

                Log.d("in.read loop", "finalMsg is: " + finalMsg);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("Receive debug", "in receive_msg client func END");

        Log.d("reader 1-3", "reader 1-3");

        return finalMsg;
    }
}

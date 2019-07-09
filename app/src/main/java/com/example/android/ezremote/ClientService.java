package com.example.android.ezremote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ClientService extends Service {

    private String dstAddress;
    private int dstPort;
    private OutputStreamWriter outputStreamWriter = null;
    private Writer bufWriter = null;
    private InputStreamReader inputStreamReader = null;
    private BufferedReader bufReader = null;
    private static Socket socket = null;
    private boolean inConnection;
    // Binder given to clients
    private final IBinder binder;
    // Used to send HEARTBEAT_CHECK requests at the determined time intervals to the Server
    private HeartbeatThread heartbeatThread;


    private static final Pattern REGEX_IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");


    public ClientService() {
        this.inConnection = false;
        binder = new LocalBinder();
    }

    /*
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ClientService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class ClientNotInConnection extends Exception {

        private ClientNotInConnection(String message) {
            super(message);
        }

    }

    public boolean isInConnection() {
        return  inConnection;
    }

    public void createNewConnection(String ip, int port) throws Exception {
        this.dstAddress = ip;
        this.dstPort = port;

        createSocket();

        try {
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Use encoding of your choice
        bufWriter = new BufferedWriter(outputStreamWriter);

        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufReader = new BufferedReader(inputStreamReader);

        // Start the Heartbeat request thread
        heartbeatThread = new HeartbeatThread();
        heartbeatThread.start();
    }

    private class HeartbeatThread extends Thread {

        private boolean stopHeartbeat = false;

        public void run(){

            while (!stopHeartbeat) {
                // Heartbeat request interval
                SystemClock.sleep(1000);

                // Create the HEARTBEAT_CHECK request's json message
                HashMap<String, String> msgData = new HashMap<>();
                JSONObject jsonData = MessageGenerator.generateJsonObject("HEARTBEAT_CHECK", msgData);

                try {
                    JSONObject jsonResponse = new JSONObject(sendMsgAndRecvReply(jsonData));

                    if (jsonResponse.getString("status").equals("SUCCESS")) {
                        // The Server has responded with a SUCCESS status, so we know that our heartbeat request
                        // was acknowledged and the Server is still there!
                        Log.d("heartbeat", "HEARTBEAT_CHECK OK!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public void terminate() {
            stopHeartbeat = true;
        }
    }

    // Method used to terminate our currently established connection to a Server
    // and return the status of the termination action
    public boolean terminateConnection() throws JSONException {
        // Temporarily terminate the heartbeat request thread so
        // that its requests are not send to a closed socket if
        // the Server successfully satisfies the TERMINATE_CONNECTION
        // request
        heartbeatThread.terminate();

        // First, we send a TERMINATE_CONNECTION request to the Server

        // Create the TERMINATE_CONNECTION request's json message
        HashMap<String, String> msgData = new HashMap<>();
        JSONObject jsonData = MessageGenerator.generateJsonObject("INITIALIZE_NEW_CONNECTION", msgData);

        JSONObject jsonResponse = new JSONObject(sendMsgAndRecvReply(jsonData));

        if (jsonResponse.getString("status").equals("SUCCESS")) {
            // The Server has responded with a SUCCESS status, so we know that our request to
            // terminate our connection has been acknowledged

            // Release the Client's resources before closing the socket
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (bufReader != null) {
                    bufReader.close();
                }

                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }

                if (bufWriter != null) {
                    bufWriter.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Call closeSocket() method to close our connection's socket
            closeSocket();

            return true;

        } else {
            // The Server has responded with a FAIL or ERROR status, so we notify the user by
            // returning false

            // Restart the Heartbeat request thread since the last TERMINATE_CONNECTION
            // request was unsuccessful
            heartbeatThread.start();

            return false;
        }
    }

    private void createSocket() throws Exception {

        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(dstAddress, dstPort), 5000);
            this.inConnection = true;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }



    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
                this.inConnection = false;
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

    public static String getClientIpAddress() {
        InetAddress addr = socket.getInetAddress();

        return (addr != null) ? addr.getHostAddress() : "*";

    }


    public String sendMsgAndRecvReply(JSONObject jsonObject) {
        sendMessage(jsonObject);

        String receivedMessage = receiveMessage();

        return receivedMessage;
    }

    private void sendMessage(JSONObject jsonObject) {

        String message = jsonObject.toString();
//        Log.d("writer", "The message1 is: " + message);
        // **** IMPORTANT ****
        // Adding the \0 delimiter at the end of the message that will be send to the server
        // is important, as this delimiter is used by the server to determine the end of the message.
        message += "\0";


        // append and flush in logical chunks
        try {
            Log.d("writer", "The message2 is: " + message);
            bufWriter.append(message);
            bufWriter.flush();
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

        return finalMsg;
    }
}

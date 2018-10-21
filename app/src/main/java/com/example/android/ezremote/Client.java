package com.example.android.ezremote;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static Client instance;

    private static String dstAddress;
    private static int dstPort;
    private static OutputStreamWriter outputStreamWriter;
    private static Writer bufWriter;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufReader;
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

        createSocket();

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
        Log.d("eftasa", "3");
        socket = null;
        try {
            socket = new Socket(dstAddress, dstPort);

//            Socket socket = new Socket();
//            socket.connect(new InetSocketAddress(dstAddress, dstPort), 5000);

            Log.d("eftasa", "4");

//            if (socket.isBound() == true) {
//                Log.d("socket state", "socket is bound");
//            } else {
//                Log.d("socket state", "socket is not bound");
//            }

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
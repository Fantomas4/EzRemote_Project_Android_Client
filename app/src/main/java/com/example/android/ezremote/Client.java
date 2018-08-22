package com.example.android.ezremote;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;

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
        Log.d("eftasa", "1");
        dstAddress = addr;
        dstPort = port;
        inConnection = true;
        Log.d("eftasa", "2");
        createSocket();
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
        return receiveMessage();
    }

    public void sendMessage(JSONObject jsonObject) {

        String message = jsonObject.toString();

        // **** IMPORTANT ****
        // Adding the \0 delimiter at the end of the message that will be send to the server
        // is important, as this delimiter is used by the server to determine the end of the message.
        message += "\0";

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

        Log.d("Receive debug", "in receive_msg client func BEGINNING");

        BufferedReader in = null;
        final int DEFAULT_BUFFER_SIZE = 5000;
        char[] cbuf = new char[DEFAULT_BUFFER_SIZE];
        int offset = 0;


        try {
//            in = new BufferedReader(new InputStreamReader(
//                    socket.getInputStream(), "UTF8"));
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), "UTF8"));

            int recvSize;

            while ((recvSize = in.read(cbuf, offset, DEFAULT_BUFFER_SIZE)) != -1) {
                Log.d("in.read loop", "diavasa " + recvSize + "chars");
                offset = recvSize;
            }

            Log.d("cbuf", String.valueOf(cbuf));


        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
//                    1024);
//            byte[] buffer = new byte[1024];
//
//            int bytesRead;
//            InputStream inputStream = socket.getInputStream();
//
//            Log.d("Receive debug", "reached point 1!");
//
//            /*
//             * notice: inputStream.read() will block if no data return
//             */
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//                Log.d("Receive debug", "reached point 2!");
//            }
//
//            Log.d("Receive debug", "reached point 3!");
//
//        } catch (UnknownHostException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            response = "UnknownHostException: " + e.toString();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            response = "IOException: " + e.toString();
//        }

        Log.d("Receive debug", "in receive_msg client func END");

        return response;
    }

}
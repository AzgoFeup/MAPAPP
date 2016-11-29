package com.azgo.mapapp;

import android.util.Log;

/**
 * Created by Jose Valverde on 17/11/2016.
 */

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient implements Runnable {

    private String serverMessage;
    public static final String SERVERIP = "192.168.50.138"; //your computer IP address
    public static final int SERVERPORT = 20502;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    public boolean done = false;
    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(final String message){
        if (out != null && !out.checkError()) {
            Log.d("TCP Client", "S: Sending" + message);

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    out.println(message);
                    out.flush();
                }
            }.start();

        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {

            //here you must put your computer's IP address.
            Log.e("TCP Client", "C: Connecting to "+ SERVERIP);
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.e("TCP Client", "C: Sent.");



                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.e("TCP Client", "C: Done.");

                done = true;
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();
                    Log.d("TCP Client", "R: Received" + serverMessage);

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.e("TCP", "CLOSING");
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error " + e, e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
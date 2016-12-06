package com.azgo.mapapp;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Jose Valverde on 17/11/2016.
 */

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

class TCPClient implements Runnable {

    private String serverMessage;
    private static final String SERVERIP = "192.168.50.138"; //TODO: pinguim.fe.up.pt dosen't work
    private static final int SERVERPORT = 20502;
    private boolean mRun = false;
    boolean messageAdded = false;
    private PrintWriter out;
    static Queue<String> array;

    //TODO : FAZER ISTO MAIS FIAVEL

    private static TCPClient instance= null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    private TCPClient() {
    }


    /**
     * Creates a new instance of this class.
     * @return The new instance.
     */
    static TCPClient getInstance() {
        if (instance == null) {
            instance = new TCPClient();
            array = new LinkedList<>();
            instance.run();
        }
        return instance;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    void sendMessage(final String message){
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
            Log.e("TCPClient", "run(): Connecting to "+ SERVERIP);
            //InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCPClient", "run(): Connecting...");
            //create a socket to make the connection with the server
            Socket socket = new Socket(SERVERIP, SERVERPORT);
            try {
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.e("TCPClient", "run(): out created");

                //receive the message which the server sends back
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.e("TCPClient", "run(): in created");

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();
                    //Log.e("TCPClient", "run(): Received: " + serverMessage);

                    if (serverMessage != null) {
                        //call the method messageReceived from MyActivity class
                        messageReceived(serverMessage);
                        messageAdded = true;
                    }
                    serverMessage = null;
                }
            } catch (Exception e) {
                Log.e("TCPClient", "run(): Error_1", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                Log.e("TCPClient", "CLOSING");

            }

        } catch (Exception e) {

            Log.e("TCPClient", "run(): Error_2", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    private void messageReceived(String message) {
        Log.e("TCPClient", "messageReceived(): "+ message);
        array.add(message);
    }

}
package com.azgo.mapapp;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Jose Valverde on 17/11/2016.
 */

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    public boolean loginReceived = false;
    public boolean comunicationReceived = false;
    private PrintWriter out;
    private BufferedReader in;
    public Socket socket;
    static Queue<String> loginArray;
    static Queue<String> comunicationArray;
    public boolean socketTimeout = false;
    static public boolean connected = false;
    static public Thread t;

    private Object lockArray1 = new Object();
    private Object lockArray2 = new Object();

    //TODO : FAZER ISTO MAIS FIAVEL

    private static TCPClient instance= null;

    /**
     *  Constructor of the class.
     */
    private TCPClient() {
    }


    /**
     * Creates a new instance of this class.
     * @return The new instance.
     */
    static TCPClient getInstance() {

        if (instance == null) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d("TCPClient", "Creating Instance");
            instance = new TCPClient();
            loginArray = new LinkedList<>();
            comunicationArray = new LinkedList<>();
            t = new Thread(instance);
            t.start();

            Log.d("TCPClient", "run");

        }
        Log.d("TCPClient", "Returning Instance");
        return instance;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    void sendMessage(final String message){
        if (out == null || connected == false)
        { //TODO: Check errors
            Log.d("TCP Client", "Reconcting" );
            t = new Thread(instance);
            t.start();
        }
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

    /**
     *
     * @throws Exception
     */
    public void startSocket() throws Exception {
        Log.d("TCPClient", "run(): Connecting to "+ SERVERIP);
        //InetAddress serverAddr = InetAddress.getByName(SERVERIP);

        Log.d("TCPClient", "run(): Connecting...");
        //create a socket to make the connection with the server
        SocketAddress sockaddr = new InetSocketAddress(SERVERIP, SERVERPORT);

        socket = new Socket();
        socket.connect(sockaddr,10000); //TODO: Change value

        //create output streamer
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);
        Log.d("TCPClient", "run(): out created");

        //receive the message which the server sends back
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Log.d("TCPClient", "run(): in created");
        TCPClient.connected = true;


    }


    public void stopClient(){
        try {
            Log.e("TCPClient", "CLOSING");
            if(socket != null)socket.close();
            if(in != null) in.close();
            if(out != null)out.close();
            connected = false;
            t.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
            //TODO: CENAS
        }


        mRun = false;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("TCP_RUN");

        mRun = true;
        int nullmessage = 0;

        try {
            try {
                startSocket();
            } catch (SocketTimeoutException | SocketException e ) {
                Log.e("TCPClient", "run(): SocketTimeoutException", e);
                socketTimeout = true;

                return;
            }
            Log.d("TCPClient", "run(): Started!");

            //in this while the client listens for the messages sent by the server
            while (mRun) {
                Log.d("TCPClient", "run(): Receiving....");
                serverMessage = in.readLine();
                Log.d("TCPClient", "run(): Received: " + serverMessage);

                if (serverMessage != null) {
                    nullmessage = 0;
                    //call the method messageReceived from MyActivity class
                    messageReceived(serverMessage);
                    //messageAdded = true;
                }
                else if(nullmessage == 1000) { //Great number???
                    throw new Exception("No Connection");
                }
                else {
                    nullmessage++;
                }
                serverMessage = null;
            }

        }  catch (Exception e) {
            stopClient();
            Log.e("TCPClient", "run(): Generic Error", e);
        }
        finally {
            //the socket must be closed. It is not possible to reconnect to this socket
            // after it is closed, which means a new socket instance has to be created.
            try {
                socket.close();
            } catch (IOException e) {
                //TODO
                e.printStackTrace();
            }
            Log.e("TCPClient", "Finally: CLOSING");

        }


    }


    private void messageReceived(String message) {
        Log.e("TCPClient", "messageReceived(): "+ message);

        String[] items = message.split("\\$");
        for (String item : items)
        {
            System.out.println("item = " + item);
        }

        Log.e("MENSAGEM" , items[0]);
        if(items[0].equals("Login")) {
            Log.e("TCPClient", "messageReceived: is login");
            synchronized (lockArray1){
                loginArray.add(message);
                loginReceived = true;
            }
        }
        else if(items[0].equals("Coordinates")) {
            Log.e("TCPClient", "messageReceived: is coordinates");
            synchronized (lockArray2) {
                comunicationArray.add(message);
                comunicationReceived = true;
            }
        }
    }



}
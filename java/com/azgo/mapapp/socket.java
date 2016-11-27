package com.azgo.mapapp;


import android.app.ProgressDialog;
import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by Jose Valverde on 12/10/2016.
 */

public class socket {

    private static final int    servicePORT = 20502;
    private static final String HOST      = "pinguim.fe.up.pt";
    private static final int connectionTimeout = 1000;


    private final Socket sock;
    private final BufferedReader in;
    private final PrintWriter out;

    /**
     *
     * @throws Exception
     */
    public socket() throws Exception{
        Log.i("Debug" , "[SOCKET] Trying to connect to Host: " + HOST + " ,Port: " + servicePORT );
        sock = new Socket();
        sock.connect(new InetSocketAddress(HOST, servicePORT), connectionTimeout);
        sock.setSoTimeout(10000);
        in  = new BufferedReader(new InputStreamReader( sock.getInputStream()));
        out = new PrintWriter( sock.getOutputStream(), true );
        Log.i("Debug" , "[SOCKET] Connection Established!" );
    }




    /**
     *
     * @param line
     */
    public void send(String line){
        Log.d("[SOCKET]", "Sent: "+line);
        out.println(line);
    }

    /**
     *
     * @return
     */
    public String receive(){
        String dout;
        try {
            dout = in.readLine();
        } catch (IOException ex) {
            System.err.println(ex);
            //TODO: timeout receptions
            System.err.println("Problems with Reception");
            return null;
        }
        System.out.println("Received: "+dout);
        return dout;
    }

    /**
     *
     */
    public void close(){
        try {
            out.close();
            in.close();
            sock.close();
        } catch (IOException ex) {
            System.err.println("Error closing socket.");
        }
    }
}


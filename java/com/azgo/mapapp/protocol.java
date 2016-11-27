package com.azgo.mapapp;

import android.util.Log;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by José Valverde on 12/10/2016.
 */

public class protocol {

    private static socket sc;
    private static boolean connected = false;



    public static boolean connect() {
        try {
            if (!connected)
                sc = new socket();
        } catch (Exception ex) {
            //TODO : VER SE SOCKET EXISTE
            return false;
        }
        connected = true;
        return true;
    }


    public static boolean isConnected() {
        return connected;
    }

    public static void unConnect() {
        if (connected)
            sc.close();
        connected = false;
    }

    public static void sendString(final String type, final String data) {
        if (!connect())
            connect();

        String toSend = type+"@"+data+"@0";
        sc.send(toSend);



    }

    public static String hear(){
        if (!connect()) {
            Log.e("Debug" , "[Protocol] Not Connected" );
            if (!connect())
            return null;
        }
        //TODO: This shall not be this way

        String sound = sc.receive();
        if(sound == null) {
            Log.e("Debug", "[Protocol] Reception problems");
            return null;
        }
        String[] tokens = sound.split("@");

        return tokens[0];
        //TODO: handlers
        /*
        switch (tokens[0]){
            case "Login"    : return handlerLogin(tokens);
            case "Logout"   : return handlerLogout(tokens);
            case "Player"   : return handlerPlayer(tokens);
            case "Register" : return handlerRegister(tokens);
            case "Move"     : return handlerMove(tokens);
            case "NewGame"  : return handlerNewGame(tokens);
            default: return null;
        }
        */
    }
}

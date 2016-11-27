package com.azgo.mapapp;

import android.util.Log;

/**
 * Created by Jose Valverde on 17/11/2016.
 */

public class serverCommunication{


    public serverCommunication(final String uid) {

        new Thread() {
            @Override
            public void run() {
                login(uid);
                while (true) Log.d("SERVER", listener());
            }
        }.start();

    }

    private boolean login(String uid){
        protocol.sendString("LOGIN", uid);
        return true;
    }

    private static String listener() {
        while (true) {

            String tokens = protocol.hear();
            if (tokens == null)
                continue;

            return tokens;
        }
    }
}

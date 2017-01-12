package com.azgo.mapapp;

/**
 * Created by José Valverde on 12/01/2017.
 */

public class FriendsData <N,E,Nr> {


    private N name;

    private E email;

    private Nr number;



    public FriendsData(N name, E email, Nr number){

        this.name = name;

        this.email = email;

        this.number = number;

    }



    public N getName(){ return name; }

    public E getEmail(){ return email; }

    public Nr getNumber() { return number; }



    public void setName(N name){ this.name = name; }

    public void setEmail(E email){ this.email = email; }

    public void setNumber(Nr number){ this.number = number; }
}

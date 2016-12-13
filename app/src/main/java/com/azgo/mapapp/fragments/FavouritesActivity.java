package com.azgo.mapapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.azgo.mapapp.DrawerMain;
import com.azgo.mapapp.R;
import com.azgo.mapapp.mainLogin;

import org.w3c.dom.Text;

/**
 * Created by Francisco on 01-12-2016.
 */

public class FavouritesActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.favourites_places,container, false);
    }



}

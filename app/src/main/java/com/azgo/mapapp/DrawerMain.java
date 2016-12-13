package com.azgo.mapapp;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.azgo.mapapp.fragments.AboutActivity;
import com.azgo.mapapp.fragments.FavouritesActivity;
import com.azgo.mapapp.fragments.HistoryActivity;
import com.azgo.mapapp.fragments.SettingsActivity;

/**
 * Created by Utilizador on 12-12-2016.
 */

public class DrawerMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

        NavigationView navigationView = null;
        Toolbar toolbar = null;
        DrawerLayout drawer = null;
        ActionBarDrawerToggle toggle = null;


        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_activity);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
       toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener( this);


    }

        @Override
        public void onBackPressed () {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
                drawer.closeDrawer(GravityCompat.END);
            } else {
                super.onBackPressed();
                System.exit(0);
            }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            if (toggle.onOptionsItemSelected(item)) {
                return true;
            }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
            if (id == R.id.action_openRight) {
                drawer.openDrawer(GravityCompat.END); /*Opens the Right Drawer*/
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            HistoryActivity historyActivityFragment = new HistoryActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    historyActivityFragment,
                    historyActivityFragment.getTag()).commit();
        } else if (id == R.id.favourites) {

            FavouritesActivity FavouritesActivityFragment = new FavouritesActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    FavouritesActivityFragment,
                    FavouritesActivityFragment.getTag()).commit();


        } else if (id == R.id.about) {
            AboutActivity AboutActivityFragment = new AboutActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    AboutActivityFragment,
                    AboutActivityFragment.getTag()).commit();

        } else if (id == R.id.settings) {
            SettingsActivity SettingsActivityFragment = new SettingsActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    SettingsActivityFragment,
                    SettingsActivityFragment.getTag()).commit();

        } else if (id == R.id.logout) {

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    }


package com.azgo.mapapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.LinkedList;
import java.util.List;
import static com.azgo.mapapp.MainActivity.adj;
import static com.azgo.mapapp.MainActivity.grafo;
import static com.azgo.mapapp.MainActivity.nodes;

public class Navigation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    PolylineOptions linePath = new PolylineOptions();
    Marker marker;
    Polyline mPolyLine;
    //String locationNav;
    SupportMapFragment mapFragNav;
    GoogleApiClient mGoogleApiClientNav;
    GoogleMap mGoogleMapNav;
    LocationRequest mLocationRequestNav;
    Location mLastLocationNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //Bundle mapData = getIntent().getExtras();
        //get location to navigate to, from main Activity

        //Intent thisapp = getIntent();


        if (googleServicesAvailable()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            Toast.makeText(this, "Connected!!", Toast.LENGTH_LONG).show();

            mapFragNav = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_nav);
            mapFragNav.getMapAsync(this);

        } else {
            //No Google Maps Layout
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClientNav != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientNav, this);

        }
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to Google play services", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMapNav = googleMap;
        if (mGoogleMapNav != null) {
            mGoogleMapNav.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Navigation.this.setMarker("Local", latLng.latitude, latLng.longitude);
                }
            });

            //set markers on all the rooms
            for(Graph.Node no : nodes){
                if(no.getIndex()<=35)
                    this.setMarker(no.getLabel(), no.getLatitude(), no.getLongitude());
                else
                    break;
            }

        }


        mGoogleMapNav.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMapNav.setIndoorEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMapNav.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mGoogleMapNav.setMyLocationEnabled(true);
        }
        Log.d("location", MainActivity.location);
        navigation(MainActivity.location);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientNav = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClientNav.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequestNav = new LocationRequest();
        mLocationRequestNav.setInterval(1000);
        mLocationRequestNav.setFastestInterval(1000);
        mLocationRequestNav.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientNav, mLocationRequestNav, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocationNav = location;
        //Get map on navigation mode
        LatLng latLng = new LatLng(mLastLocationNav.getLatitude(), mLastLocationNav.getLongitude());
        // Construct a CameraPosition focusing on current position and animate the camera to that position.
        //change camera view on current user's location to start navigation
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(21)                   // Sets the zoom
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMapNav.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        //stop location updates
        if (mGoogleApiClientNav != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientNav, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClientNav == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMapNav.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }







    private void removeEverything() {
        marker.remove();
        marker = null;
    }




    private void setMarker(final String locality, double lat, double lng) {

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                //.draggable(true)
                //to specify a custom marker, use .icon(BitmapDescriptorFactory.fromResource(id_Resource))...
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(lat, lng))
                .snippet("I am here"); //something added to add more info


        marker = mGoogleMapNav.addMarker(options);

    }


    public void navigation (String location){
        Graph.Node searchNode = null;
        for(Graph.Node no : nodes){
            if(no.getLabel().equals(location)) {
                searchNode = no;
                break;
            }
        }
        startNavigationTo(searchNode, MainActivity.sendLastLocation);

    }

    public void startNavigationTo(Graph.Node searchNode, Location mLastLocation){

        //calculate closest Node to mLastLocation
        //mGoogleMap.UiSettings.setMapToolbarEnabled(false);
        LinkedList<Graph.Node> caminho = new LinkedList<>();
        List<Graph.Node> nos = grafo.getListNodes();
        Graph.Node closestNode;
        closestNode = findClosestNode(mLastLocation.getLatitude(), mLastLocation.getLongitude(), nos);
        Graph.Node indexSource = grafo.getNode(closestNode.getIndex());


        //calculate shortest path from firstNode to searchNode
        Graph.Node indexDest= grafo.getNode(searchNode.getIndex());
        double result = MatrixGraphAlgorithms.shortestPath(adj, grafo, indexSource, indexDest, caminho);
        //shortest path is on caminho
        //draw path on Google Maps
        // Instantiates a new Polyline object and adds points to define the navigation path

        linePath.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        /*listIter = myList.listIterator(myList.size());
        while (listIter.hasPrevious()) {
            String prev = listIter.previous();
            // Do something with prev here
        }*/

        for(Graph.Node no : caminho){
            linePath.add(new LatLng(no.getLatitude(), no.getLongitude()));
        }

        //add extra options
        linePath.width(25)
                .geodesic(false)
                .color(Color.GREEN);

        // Get back the mutable Polyline
        Polyline mPolyLine = mGoogleMapNav.addPolyline(linePath);

    }

    public static Graph.Node findClosestNode(double latitude, double longitude, List<Graph.Node> nodes){
        Graph.Node closestNode = null;
        double[][] points = new double[69][2];
        double shortestDistance=0;
        double distance=0;

        //enter x,y coords into the 69x2 table points[][]
        for(Graph.Node no : nodes){
            points[no.getIndex()][0] = no.getLatitude();
            points[no.getIndex()][1] = no.getLongitude();
        }

        //get the distance between the point in the ith row and the (m+1)th row
        //and check if it's shorter than the distance between 0th and 1st
        for(Graph.Node no : nodes)
        {
            //use m=i rather than 0 to avoid duplicate computations
            for (int m=no.getIndex(); m<69-1;m++)
            {
                double dx = points[no.getIndex()][0] - latitude;
                double dy = points[no.getIndex()][1] - longitude;
                distance = Math.sqrt(dx*dx + dy*dy);

                //set shortestDistance and closestPoints to the first iteration
                if (m == 0 && no.getIndex() == 0)
                {
                    shortestDistance = distance;
                    closestNode = no;
                }
                //then check if any further iterations have shorter distances
                else if (distance < shortestDistance)
                {
                    shortestDistance = distance;
                    closestNode = no;
                }
            }
        }
        //search the closest Node on shortest path

        return closestNode;
    }



    public void exit(View view){
        //when navigation is over
        //mPolyLine.setVisible(false);
        Intent j = new Intent(this, MainActivity.class);
        startActivity(j);
        //this.finish();
    }
}
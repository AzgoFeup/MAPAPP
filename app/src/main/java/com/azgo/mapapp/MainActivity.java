package com.azgo.mapapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrentLocationMarker;
    Button myButton;
    String localizacao = null;
    int navegacao = 0;



    //prepare graph
    Graph grafo = new Graph();
    List<Graph.Node> nodes = grafo.insertNodes();
    boolean[][] adj = grafo.fillMatrix();
    List<Graph.Node> nos = grafo.getListNodes();


    //Communicação
    public TCPClient mTcpClient;
    private static boolean messageReceived;
    private static String Message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Eliminate this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new backgroundReception().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                new backgroundSending().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                new backgroundReception().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                new backgroundSending().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        }
        //till were


        if (googleServicesAvailable()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            Toast.makeText(this, "Connected!!", Toast.LENGTH_LONG).show();

            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);

        } else {
            //No Google Maps Layout
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

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
        mGoogleMap = googleMap;

        if (mGoogleMap != null) {


            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    MainActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);
                }
            });
            //set markers on all the rooms
            for(Graph.Node no : nodes){
                if(no.getIndex()<=35)
                    MainActivity.this.setMarker(no.getLabel(), no.getLatitude(), no.getLongitude());
                else
                    break;
            }

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(MainActivity.this);
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                    //localizacao = marker.getTitle();




                }
            });
        }


        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                LatLng ll = marker.getPosition();
                tvLocality.setText(marker.getTitle());
                tvLat.setText("Latitude: " + ll.latitude);
                tvLng.setText("Longitude: " + ll.longitude);
                tvSnippet.setText(marker.getSnippet());
                //localizacao = marker.getTitle();

                return v;
            }


        });

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setIndoorEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);


    }

    Marker marker;

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();
        Graph.Node searchNode = null;
        //converts string to lat and lng
        //devolve a localização
        //String locality = address.getLocality();
        //set markers on all the rooms
        for(Graph.Node no : nodes){
            if(no.getLabel().equals(location)) {
                searchNode = no;
                break;
            }
        }


        Toast.makeText(this, location, Toast.LENGTH_LONG).show();

        double lat = searchNode.getLatitude();
        double lng = searchNode.getLongitude();
        goToLocationZoom(lat, lng, (float) 21);
        //setMarker(locality, lat, lng);

    }

    public void navigation (View view) throws IOException{
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();
        if(location!=null && !location.isEmpty()){
            Graph.Node searchNode = null;
            for(Graph.Node no : nodes){
                if(no.getLabel().equals(location)) {
                    searchNode = no;
                    break;
                }
            }
            double lat = searchNode.getLatitude();
            double lng = searchNode.getLongitude();
            Toast.makeText(this, "Starting navigation to "+location, Toast.LENGTH_LONG).show();
            startNavigationTo(searchNode, mLastLocation);
        }

    }

    Circle circle;

    private void setMarker(final String locality, double lat, double lng) {

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                //.draggable(true)
                //to specify a custom marker, use .icon(BitmapDescriptorFactory.fromResource(id_Resource))...
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(lat, lng))
                .snippet("I am here"); //something added to add more info


        marker = mGoogleMap.addMarker(options);

    }

    private void removeEverything() {
        marker.remove();
        marker = null;
        circle.remove();
        circle = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.logout:
                signOut();
                break;
            case R.id.info:
                goUserInfoPage();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /*protected void createLocationRequest(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) 19.08);
        //move map camera
        mGoogleMap.animateCamera(cameraUpdate);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void startNavigationTo(Graph.Node searchNode, Location mLastLocation){
        navegacao = 1;
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
        PolylineOptions linePath = new PolylineOptions();
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
        Polyline mPolyLine = mGoogleMap.addPolyline(linePath);

        //Get map on navigation mode
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // Construct a CameraPosition focusing on current position and animate the camera to that position.
        //change camera view on current user's location to start navigation
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(21)                   // Sets the zoom
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        /*while(mLastLocation.getLongitude()!=indexDest.getLongitude() && mLastLocation.getLatitude()!=indexDest.getLatitude()){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            //move map camera
            mGoogleMap.animateCamera(cameraUpdate);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, (float)19.06);
            mGoogleMap.moveCamera(update);
        }*/



        //when navigation is over mPolyline.setVisible(false)
        //navegacao = 0;
        //return?
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

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
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

    public void signOut() {
        FirebaseAuth.getInstance().signOut(); //for gmail
        LoginManager.getInstance().logOut(); //for facebook

        goLoginScreen();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, mainLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goUserInfoPage() {
        Intent intent = new Intent(this, infoPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


///COMUNICAÇÃO

    public class backgroundReception extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    Log.e("DEBUGME", message);
                    publishProgress(message);

                }
            });

            mTcpClient.run();


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


            Log.d("onProgress", values[0]);
            Message = values[0];
            messageReceived = true;
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list


        }
    }

    public class backgroundSending extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... message) {

            while (mTcpClient == null) ;



                try {
                    //mTcpClient.sendMessage();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return "";

        }
    }
}
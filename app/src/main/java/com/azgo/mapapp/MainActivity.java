package com.azgo.mapapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    PolylineOptions linePath = new PolylineOptions();
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Marker mCurrentLocationMarker;
    String location;
    Location sendLastLocation;
    Polyline mPolyLine;
    private LocationManager locationManager;
    Location location_nav;



    //prepare graph
    static Graph grafo = new Graph();
    static List<Graph.Node> nodes = grafo.insertNodes();
    static boolean[][] adj = grafo.fillMatrix();


    //Communicação
    public TCPClient mTcpClient;
    private static boolean messageReceived;
    private static String Message;
    boolean logoutPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)        // 1 second, in milliseconds
                .setFastestInterval(1000) // 1 second, in milliseconds
                .setSmallestDisplacement(1); //1 meter
        /*createBuilder();
        createLocationRequest();*/
        mGoogleMap = null;

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
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //mGoogleMap.setIndoorEnabled(true);

        } else {
            //No Google Maps Layout
        }

        //Location location = locationManager.getLastKnownLocation(provider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resuming", "Resuming");

        //locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //mGoogleMap=null;

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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setIndoorEnabled(true);

        if (mGoogleMap != null) {


            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    MainActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);
                }
            });
            //set markers on all the rooms
            for (Graph.Node no : nodes) {
                if (no.getIndex() <= 35)
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
        final Button testButton = (Button) findViewById(R.id.startActivityButton);
        testButton.setTag(1);
        testButton.setText("Navigate Here");
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    navigation(v);
                    testButton.setText("Stop Navigation");

                    v.setTag(0);
                } else {
                    testButton.setText("Navigate Here");
                    stopNavigation(mLastLocation);
                    v.setTag(1);
                }
            }
        });
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
        for (Graph.Node no : nodes) {
            if (no.getLabel().equals(location)) {
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

    public void navigation(View view) {
        final EditText et = (EditText) findViewById(R.id.editText);
        location = et.getText().toString();
        PolylineOptions linePath = new PolylineOptions();
        //mLastLocation = location;
        //Get map on navigation mode
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // Construct a CameraPosition focusing on current position and animate the camera to that position.
        //change camera view on current user's location to start navigation
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(21)                   // Sets the zoom
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Graph.Node searchNode = null;
        for (Graph.Node no : nodes) {
            if (no.getLabel().equals(location)) {
                searchNode = no;
                break;
            }
        }
        startNavigationTo(searchNode, mLastLocation);


        //Intent i = new Intent(this, Navigation.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //i.putExtra("location", locationMessage);
        //send String to navigate to room number
       /* if(location!=null && !location.isEmpty()){
            Toast.makeText(this, "Starting navigation to "+location, Toast.LENGTH_LONG).show();
        }*/
        //startActivity(i);
        //finish();
        //location.equals(null);

    }

    public void startNavigationTo(Graph.Node searchNode, Location mLastLocation) {

        //calculate closest Node to mLastLocation
        //mGoogleMap.UiSettings.setMapToolbarEnabled(false);
        LinkedList<Graph.Node> caminho = new LinkedList<>();
        List<Graph.Node> nos = grafo.getListNodes();
        Graph.Node closestNode;
        closestNode = findClosestNode(mLastLocation.getLatitude(), mLastLocation.getLongitude(), nos);
        Graph.Node indexSource = grafo.getNode(closestNode.getIndex());


        //calculate shortest path from firstNode to searchNode
        Graph.Node indexDest = grafo.getNode(searchNode.getIndex());
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

        for (Graph.Node no : caminho) {
            linePath.add(new LatLng(no.getLatitude(), no.getLongitude()));
        }

        //add extra options
        linePath.width(25)
                .geodesic(false)
                .color(Color.GREEN);

        // Get back the mutable Polyline
        mPolyLine = mGoogleMap.addPolyline(linePath);

    }

    public static Graph.Node findClosestNode(double latitude, double longitude, List<Graph.Node> nodes) {
        Graph.Node closestNode = null;
        double[][] points = new double[69][2];
        double shortestDistance = 0;
        double distance = 0;

        //enter x,y coords into the 69x2 table points[][]
        for (Graph.Node no : nodes) {
            points[no.getIndex()][0] = no.getLatitude();
            points[no.getIndex()][1] = no.getLongitude();
        }

        //get the distance between the point in the ith row and the (m+1)th row
        //and check if it's shorter than the distance between 0th and 1st
        for (Graph.Node no : nodes) {
            //use m=i rather than 0 to avoid duplicate computations
            for (int m = no.getIndex(); m < 69 - 1; m++) {
                double dx = points[no.getIndex()][0] - latitude;
                double dy = points[no.getIndex()][1] - longitude;
                distance = Math.sqrt(dx * dx + dy * dy);

                //set shortestDistance and closestPoints to the first iteration
                if (m == 0 && no.getIndex() == 0) {
                    shortestDistance = distance;
                    closestNode = no;
                }
                //then check if any further iterations have shorter distances
                else if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestNode = no;
                }
            }
        }
        //search the closest Node on shortest path

        return closestNode;
    }

    public void stopNavigation(Location mLastLocation) {
        String name = "I am here";
        //Place current location marker
        mPolyLine.remove();
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        setMarker(name, mLastLocation.getLatitude(), mLastLocation.getLongitude());


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom((float) 19.08)                   // Sets the zoom
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) 19.08);
        //move map camera
        //mGoogleMap.moveCamera(cameraUpdate);
    }

    Circle circle;

    private void setMarker(final String locality, double lat, double lng) {
        BitmapDescriptor icon;
        switch (locality) {
            case "B001":
            case "B002":
            case "B003":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.auditorio);
                break;
            default:
                if (locality.charAt(0) == 'B') // Temporário até adicionar novos edificios ao mapa
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.sala);
                else
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.posicao);
        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                //.draggable(true)
                //to specify a custom marker, use .icon(BitmapDescriptorFactory.fromResource(id_Resource))...
                .icon(icon)
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
                logoutPressed = true;
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (location_nav == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(mLastLocation);
        };
        //mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
        //mGoogleApiClient);
    }
    private void handleNewLocation(Location location) {
        Log.d("localização", location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mGoogleMap.addMarker(options);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) 19.08);
        //move map camera
        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
        /*locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 1, locListener);
        mobileLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/

        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        /*mLastLocation = location;
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        String name = "I am here";
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setMarker(name, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) 19.08);
        //move map camera
        mGoogleMap.moveCamera(cameraUpdate);
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
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
            mTcpClient = TCPClient.getInstance();

            while(mTcpClient != null) {

                while (mTcpClient.messageAdded == false) ;

                if (!mTcpClient.array.isEmpty()) {
                    Log.e("MainActivity", "AsyncTask Reception: " + mTcpClient.array.peek());

                    publishProgress(mTcpClient.array.peek());
                    mTcpClient.array.remove();
                    mTcpClient.messageAdded = false;
                } else {
                    Log.e("MainActivity", "AsyncTask Reception: Mensagem recebida não chegou aqui");
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //in the arrayList we add the messaged received from server
            Message = values[0];
            messageReceived = true;
            Log.e("MainActivity", "onProgressUpdate: " + Message);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
        }
    }

    public class backgroundSending extends AsyncTask<String, String, String> {
        String coordenadas;
        @Override
        protected String doInBackground(String... message) {

            while(mTcpClient == null);

            //long startTime = System.currentTimeMillis();
            //while((System.currentTimeMillis()-startTime)<20000) {
            while(!logoutPressed) {
                //Envia coordenadas durante 20 seg. O ideal é enviar até ser feito o logout.

                try {
                    if(mLastLocation!=null) {
                        Double latitude_enviar = mLastLocation.getLatitude();//41.7777777; //mLastLocation.getLatitude();
                        Double longitude_enviar = mLastLocation.getLongitude();//50.9999999; //mLastLocation.getLongitude();
                        coordenadas = Double.toString(latitude_enviar) + "$" + Double.toString(longitude_enviar);
                    }
                    //if(mLastLocation != null) {
                    if (coordenadas != "$") {
                        mTcpClient.sendMessage(coordenadas);
                        //Log.e("ASYNC", Double.toString(mLastLocation.getLatitude()));
                    }
                    else {
                        Log.e("MainActivity", "AsyncTask Sending: Coordenadas não enviadas");
                    }
                    Thread.sleep(2000);

                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logoutPressed = false;
            return null;
        }
    }
}
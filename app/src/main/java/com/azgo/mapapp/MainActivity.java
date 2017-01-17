package com.azgo.mapapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.azgo.mapapp.fragments.AboutActivity;
import com.azgo.mapapp.fragments.FavouritesActivity;
import com.azgo.mapapp.fragments.HistoryActivity;
import com.azgo.mapapp.fragments.MapActivity;
import com.azgo.mapapp.fragments.SettingsActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {

    PolylineOptions linePath = new PolylineOptions();
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;

    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    Marker mCurrentLocationMarker;
    String location;
    Location sendLastLocation;
    Polyline mPolyLine;
    private LocationManager locationManager;
    Location location_nav;
    private CameraPosition mCameraPosition;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private int navigation_on = 0;
    float mDeclination;
    SensorManager mSensorManager;
    private Sensor mRotVectorSensor;
    private final float[] mRotationMatrix = new float[16];
    double angle;

    //prepare graph
    static Graph grafo = new Graph();
    static List<Graph.Node> nodes = grafo.insertNodes();
    static boolean[][] adj = grafo.fillMatrix();

    //design
    NavigationView navigationView = null;
    NavigationView navigationViewRight = null;
    final Menu menu = navigationViewRight.getMenu();
    Toolbar toolbar = null;
    DrawerLayout drawer = null;
    ActionBarDrawerToggle toggle = null;


    //Communicação
    public TCPClient mTcpClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static boolean messageReceived;
    private static String Message;
    private static String[][] FriendsMessage;
    boolean logoutPressed = false;
    private AsyncTask senAsync;
    private AsyncTask recAsync;
    private AsyncTask friAsync;
    private AsyncTask logAsync;
    private AsyncTask waitConnection;
    private AsyncTask meetTask;
    //static Queue<String> numbersArray = new LinkedList<>();
    static String friends = "Friends";

    static List<FriendsData<String, String, String>> FriendsDataList = new ArrayList<>();


    private Object lockmessa = new Object();
    private Object lockfriends = new Object();
    private Object lockReception = new Object();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);




        // Create the LocationRequest object
        /*mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)        // 1 second, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
                //.setSmallestDisplacement(1); //1 meter
        /*createBuilder();
        createLocationRequest();*/
        //mGoogleMap = null;


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewRight = (NavigationView) findViewById(R.id.nvView_right);



        //Communication Stuff

        mAuth = FirebaseAuth.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            recAsync = new backgroundReception().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            senAsync = new backgroundSending().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            friAsync = new backgroundSendFriends().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            recAsync = new backgroundReception().execute();
            senAsync = new backgroundSending().execute();
            friAsync = new backgroundSendFriends().execute();
        }
        //till were

        /*
        if (googleServicesAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        //Location location = locationManager.getLastKnownLocation(provider);*/
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        //Location location = locationManager.getLastKnownLocation(provider);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //getnumbers
        Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String separator = "$";
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("\\s+", ""); //tirar espaços;
            phoneNumber = phoneNumber.substring(phoneNumber.length() - 9); //buscar ultimos 9 numeros
            String oldfriends = friends + separator + phoneNumber;
            friends = oldfriends;
        }
        //Log.e("AsyncFriends", "Sending Friends: " +friends);
        phones.close();// close cursor
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        //low to avoid flickering
        mSensorManager.registerListener(this, mRotVectorSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        updateMarkers();
        //TODO: be tested
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            recAsync = new backgroundReception().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            senAsync = new backgroundSending().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            recAsync = new backgroundReception().execute();
            senAsync = new backgroundSending().execute();
        }
*/
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister listener
        mSensorManager.unregisterListener(this);
        //mGoogleMap=null;

        //Stop asyncTask
        //TODO: be tested
        /*
        senAsync.cancel(true);
        recAsync.cancel(true);
        */

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
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
        updateLocationUI();
        // Add markers for nearby places.
        updateMarkers();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setIndoorEnabled(true);
        //set markers on all the rooms
        for (Graph.Node no : nodes) {
            if (no.getIndex() <= 35)
                MainActivity.this.setMarker(no.getLabel(), no.getLatitude(), no.getLongitude());
            else
                break;
        }
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView tvLocality = (TextView) infoWindow.findViewById(R.id.tv_locality);
                TextView tvLat = (TextView) infoWindow.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) infoWindow.findViewById(R.id.tv_lng);
                TextView tvSnippet = (TextView) infoWindow.findViewById(R.id.tv_snippet);

                LatLng ll = marker.getPosition();
                tvLocality.setText(marker.getTitle());
                tvLat.setText("Latitude: " + ll.latitude);
                tvLng.setText("Longitude: " + ll.longitude);
                tvSnippet.setText(marker.getSnippet());

                //TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                //snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (mCameraPosition != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), (float) 19.08));
        } else {

        }


        final Button testButton = (Button) findViewById(R.id.startActivityButton);
        testButton.setTag(1);
        testButton.setBackgroundResource(R.drawable.navigate);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    navigation(v);
                    testButton.setBackgroundResource(R.drawable.cancelnavigation);
                    navigation_on = 1;
                    v.setTag(0);
                } else {
                    testButton.setBackgroundResource(R.drawable.navigate);
                    stopNavigation(mCurrentLocation);
                    v.setTag(1);
                    navigation_on = 0;
                }
            }
        });
        //está repetido do código acima...
        /*if (mCameraPosition != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), (float)19.08));
        } else {
        }*/
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();


    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
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
        //setMarker(location, lat, lng);

    }

    public void navigation(View view) {
        final EditText et = (EditText) findViewById(R.id.editText);
        location = et.getText().toString();
        PolylineOptions linePath = new PolylineOptions();
        //mLastLocation = location;
        //Get map on navigation mode
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
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
        startNavigationTo(searchNode, mCurrentLocation);


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

    public void startNavigationTo(Graph.Node searchNode, Location mCurrentLocation) {

        //calculate closest Node to mLastLocation
        //mGoogleMap.UiSettings.setMapToolbarEnabled(false);
        LinkedList<Graph.Node> caminho = new LinkedList<>();
        List<Graph.Node> nos = grafo.getListNodes();
        Graph.Node closestNode;
        closestNode = findClosestNode(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), nos);
        Graph.Node indexSource = grafo.getNode(closestNode.getIndex());


        //calculate shortest path from firstNode to searchNode
        Graph.Node indexDest = grafo.getNode(searchNode.getIndex());
        double result = MatrixGraphAlgorithms.shortestPath(adj, grafo, indexSource, indexDest, caminho);
        //shortest path is on caminho
        //draw path on Google Maps
        // Instantiates a new Polyline object and adds points to define the navigation path

        linePath.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
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

    /*
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
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

    @Override
    public void onConnected(Bundle bundle) {
        getDeviceLocation();
        mSensorManager.registerListener(this, mRotVectorSensor, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            location_nav = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (location_nav == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location_nav);
        };*/
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

        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        /*Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());*/
    }

    @Override
    public void onLocationChanged(Location location) {
        //handleNewLocation(location);
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
        mCurrentLocation = location;
        updateMarkers();
        GeomagneticField field = new GeomagneticField(
                (float) mCurrentLocation.getLatitude(),
                (float) mCurrentLocation.getLongitude(),
                (float) mCurrentLocation.getAltitude(),
                System.currentTimeMillis()
        );
        //getDeclination returns degrees
        mDeclination = field.getDeclination();

        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        if (navigation_on == 1) {
            marker.remove();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .bearing(mCurrentLocation.getBearing())// Sets the center of the map to Mountain View
                    .zoom(21)                   // Sets the zoom
                    .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            setMarker("local", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else {
            marker.remove();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .bearing(mCurrentLocation.getBearing())// Sets the center of the map to Mountain View
                    .zoom((float) 19.08)                   // Sets the zoom
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            setMarker("local", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateMarkers() {
        if (mGoogleMap == null) {
            return;
        }

       /* if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(placeLikelihood.getPlace().getLatLng())
                                .title((String) placeLikelihood.getPlace().getName())
                                .snippet(snippet));
                    }
                    // Release the place likelihood buffer.
                    likelyPlaces.release();
                }
            });
        } else {
        }*/
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }


    public void signOut() {

        logoutPressed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.e("SignOut", "Logout - if");
            logAsync = new logout().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            Log.e("SignOut", "Logout - if");
            logAsync = new logout().execute();
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, mainLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goUserInfoPage() {
        Intent intent = new Intent(this, infoPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            if (Math.abs(Math.toDegrees(orientation[0] - angle)) > 0.8) {
                float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
                updateCamera(bearing);
            }
            angle = Math.toDegrees(orientation[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mGoogleMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            System.exit(0);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            final TextView textViewToChange = (TextView) findViewById(R.id.toolbar_title);
            textViewToChange.setText("HISTORY");
            HistoryActivity historyActivityFragment = new HistoryActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    historyActivityFragment,
                    historyActivityFragment.getTag()).commit();

        } else if (id == R.id.favourites) {
            final TextView textViewToChange = (TextView) findViewById(R.id.toolbar_title);
            textViewToChange.setText("FAVOURITES");
            FavouritesActivity FavouritesActivityFragment = new FavouritesActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    FavouritesActivityFragment,
                    FavouritesActivityFragment.getTag()).commit();


        } else if (id == R.id.about) {
            final TextView textViewToChange = (TextView) findViewById(R.id.toolbar_title);
            textViewToChange.setText("ABOUT");
            AboutActivity AboutActivityFragment = new AboutActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    AboutActivityFragment,
                    AboutActivityFragment.getTag()).commit();

        } else if (id == R.id.settings) {
            final TextView textViewToChange = (TextView) findViewById(R.id.toolbar_title);
            textViewToChange.setText("SETTINGS");
            SettingsActivity SettingsActivityFragment = new SettingsActivity();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_frame,
                    SettingsActivityFragment,
                    SettingsActivityFragment.getTag()).commit();

        } else if (id == R.id.map) {
            final TextView textViewToChange = (TextView) findViewById(R.id.toolbar_title);
            textViewToChange.setText("MapApp");
            FragmentManager manager = getSupportFragmentManager();
            MapActivity MapActivityFragment = new MapActivity();
            manager.beginTransaction().replace(R.id.content_frame,
                    MapActivityFragment,
                    MapActivityFragment.getTag()).commit();


        } else if (id == R.id.logout) {
            signOut();
            Intent intent = new Intent(this, mainLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * Get the number from the contacts list
     */
    public void meetSend() {

        String num = "123456789";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.e("meetSend", "Meet - if");
            meetTask = new sendMeetRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, num);
        } else {
            Log.e("meetSend", "Meet - else");
            meetTask = new sendMeetRequest().execute(num);
        }

        meetReply();  //to be removed
    }

    public void meetReply() {
        String[] items = mTcpClient.meetRArray.peek().split("\\$");
        items[1] = "email_1@fe.up.pt"; //to be removed

        String replyStatus = "OK"; // TODO: Take the reply from the user (OK/FAIL)
        String reply = items[1] + "$" + replyStatus;
        mTcpClient.meetRArray.remove();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.e("meetReply", "Meet - if");
            meetTask = new sendMeetReply().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reply);
        } else {
            Log.e("meetReply", "Meet - else");
            meetTask = new sendMeetReply().execute(reply);
        }
    }


///COMUNICAÇÃO

    public class backgroundReception extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = TCPClient.getInstance();

            while (mTcpClient != null) {

                // Loop while doesn't receive
                while ((!mTcpClient.comunicationReceived) && (!mTcpClient.meetRStatus)) ;

                synchronized (lockReception) {
                    if (!TCPClient.comunicationArray.isEmpty()) {
                        Log.e("MainActivity", "AsyncTask Reception: " + TCPClient.comunicationArray.peek());

                        publishProgress(TCPClient.comunicationArray.peek());
                        TCPClient.comunicationArray.remove();
                        mTcpClient.comunicationReceived = false;
                    } else if (!TCPClient.meetRArray.isEmpty()) {
                        Log.e("MainActivity", "AsyncTask Reception: " + TCPClient.meetRArray.peek());
                        publishProgress(TCPClient.meetRArray.peek());
                        //TCPClient.meetRArray.remove();  //will be needed to the reply
                        mTcpClient.meetRStatus = false;
                    } else {
                        Log.e("MainActivity", "AsyncTask Reception: Mensagem recebida não chegou aqui");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //in the arrayList we add the messaged received from server
            synchronized (lockmessa) {
                Message = values[0];
                messageReceived = true;
            }
            //TODO: check message type and handle it
            // MeetRequest$num -> Present the request PopUp
            // Coordinates$OK -> What should we do here??

            Log.e("MainActivity", "onProgressUpdate: " + Message);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
        }
    }

    public class backgroundSending extends AsyncTask<String, String, String> {

        private boolean connection = false;

        @Override
        protected String doInBackground(String... message) {
            String coordenadas = "";
            while (mTcpClient == null) ;
            connection = true;

            while (!logoutPressed) {

                try {
                    Log.e("ASYNC", "Sending Coordinates$: " + mCurrentLocation);
                    if (mCurrentLocation != null) {
                        Double latitude_enviar = mCurrentLocation.getLatitude();

                        Double longitude_enviar = mCurrentLocation.getLongitude();
                        coordenadas = Double.toString(latitude_enviar) + "$" + Double.toString(longitude_enviar);
                    }

                    if (coordenadas != "$" && !coordenadas.equals("")) {
                        if (!TCPClient.connected && connection) {
                            publishProgress("");
                            connection = false;
                        } else if (TCPClient.connected) {
                            connection = true;
                            mTcpClient.sendMessage("Coordinates$" + mAuth.getCurrentUser().getEmail() + "$" + coordenadas);
                            Log.e("ASYNC", "Sending Coordinates$: " + coordenadas);
                        } else {
                            Log.e("ASYNC", "Waiting for connection ");
                        }

                    } else {
                        Log.e("MainActivity", "AsyncTask Sending: Wrong Coordinates");
                    }
                    Thread.sleep(2000);

                    if (logoutPressed) cancel(true);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logoutPressed = false;
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Log.e("backgroundSending", "waitConnection - if");
                waitConnection = new waitConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                Log.e("backgroundSending", "waitConnection - if");
                waitConnection = new waitConnection().execute();
            }


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            connection = false;
            if (mTcpClient != null) mTcpClient.stopClient();

            Log.e("ASYNC", "CANCELED: ");
            return;
        }
    }

    public class logout extends AsyncTask<String, String, String> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... values) {


            FirebaseAuth.getInstance().signOut(); //for gmail
            LoginManager.getInstance().logOut(); //for facebook

            Log.e("SignOut", "Firebase");

            recAsync.cancel(true);
            senAsync.cancel(true);
            friAsync.cancel(true);

            Log.e("SignOut", "Async Cancel " + logoutPressed);

            if (mTcpClient != null) {
                mTcpClient.stopClient();
                mTcpClient = null;
            }

            //while(mTcpClient != null)


            Log.e("SignOut", "GoingTo login" + logoutPressed);
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Thread.currentThread().setName("Logout-async");
            //this.dialog.setMessage("Login out...");
            //this.dialog.show();
            Log.e("AsyncTask", "Processing created");
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            Log.e("AsyncTask", "onPostExecute");

            //this.dialog.dismiss();
            goLoginScreen();
            if (this.isCancelled()) cancel(true);

        }
    }

    public class waitConnection extends AsyncTask<String, String, String> {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(String... values) {

            while (!TCPClient.connected) {
                try {
                    Log.e("AsyncTask", "Sending Login");
                    Thread.sleep(1000);
                    mTcpClient.sendMessage("Login$" + mAuth.getCurrentUser().getDisplayName() + "$" + mAuth.getCurrentUser().getEmail());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Thread.currentThread().setName("Logout-async");
            //this.dialog.setMessage("Reconnecting...");
            //this.dialog.show();
            Log.e("AsyncTask", "Processing created");
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            Log.e("AsyncTask", "onPostExecute");
            //this.dialog.dismiss();

        }
    }

    /*
     * To call the async task do it like this
     * String num = "xxxxxxxxx";
     * meet = new sendMeetRequest().execute(num);
     */
    public class sendMeetRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... num) {
            while (mTcpClient == null) ;

            Log.e("ASYNC", "Sending MeetRequest to: " + num[0]);
            mTcpClient.meetStatus = true;
            mTcpClient.sendMessage("Meet$" + num[0]);

            //TODO: waiting message for the user (onProgressUpdate)
            while (mTcpClient.meetStatus) publishProgress("Waiting");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            Log.e("ASYNC", "CANCELED: ");
            return;
        }
    }

    public class sendMeetReply extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... reply) {
            while (mTcpClient == null) ;

            Log.e("ASYNC", "Sending MeetReply to: " + reply[0]);
            mTcpClient.meetStatus = true;
            mTcpClient.sendMessage("MeetRequest$" + reply[0]);

            //TODO: waiting message for the user (onProgressUpdate)
            while (mTcpClient.meetStatus) publishProgress("Waiting");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            Log.e("ASYNC", "CANCELED: ");
            return;
        }
    }


    public class backgroundSendFriends extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... message) {
            while (mTcpClient == null) ;
            while (friends == "Friends") ;

            if (friends != "Friends") {
                mTcpClient.sendMessage(friends);
                Log.e("AsyncFriends", "Sending Friends: " + friends);
            } else {
                Log.e("AsyncFriends", "Error sending Friends");
            }

            while (!mTcpClient.friendsReceived) ;

            if (!TCPClient.friendsArray.isEmpty()) {

                publishProgress(TCPClient.friendsArray.peek());
                TCPClient.friendsArray.remove();
                mTcpClient.friendsReceived = false;
            } else {
                Log.e("AsyncFriends", "Reception Friends: Error");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            synchronized (lockfriends) {
                String[] items = values[0].split("\\$"); //values[0] está a mensagem toda do server
                for (String item : items) {
                    int i = 0;
                    if (i != 0) {
                        String[] data = item.split("\\#");
                        FriendsData<String, String, String> trio = new FriendsData<>(data[0], data[1], data[2]);
                        FriendsDataList.add(trio);
                        menu.add("ASD");
                    }
                    i++;
                }
                //Message = FriendsDataList; //required: java.lang.string <-> found: java.util.list
                messageReceived = true;
            }


            for (FriendsData friendsdata : FriendsDataList) {
                Log.e("AsyncFriends", "onProgressUpdate: " + friendsdata.getName() + friendsdata.getEmail() + friendsdata.getNumber());
            }


        }
    }

}
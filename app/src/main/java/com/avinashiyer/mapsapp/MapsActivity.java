package com.avinashiyer.mapsapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap.OnInfoWindowClickListener mInfoWindowClickListener;
    GoogleMap.OnMapClickListener mMapClickListener;
    GoogleMap.OnMapLongClickListener mMapLongClickListener;
    GoogleMap.OnMarkerClickListener mMarkerClickListener;
    private static final String TAG = "MAPACTIVITY";
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private ProgressDialog pDialog;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final String url = "https://api.catapult.inetwork.com/v1/users/u-7yzqqs547oitbxp43nzfthy/messages";
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called!");
        mGoogleApiClient.connect();
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    systemSpeech("You have started your journey!");


                }
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop called!");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected called!");
        //initCamera(mCurrentLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private GoogleMap mMap;


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called!");
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(mInfoWindowClickListener);
        mMap.setOnMapClickListener(mMapClickListener);
        mMap.setOnMarkerClickListener(mMarkerClickListener);
        mMap.setOnMapLongClickListener(mMapLongClickListener);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Log.d(TAG, mCurrentLocation.describeContents() + " - " + mCurrentLocation.getLatitude());
        initCamera(mCurrentLocation);
    }

    private void initCamera(Location location) {
        LatLng l = getCurrentLocation(MapsActivity.this);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(l)
                .bearing(0.0f)
                .tilt(0.0f)
                .zoom(16f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        MarkerOptions options = new MarkerOptions().position( l );
        options.icon(BitmapDescriptorFactory.defaultMarker());
        final Marker marker = mMap.addMarker(options);
        final LatLng destination = new LatLng(35.906868, -79.045742);
        mMap.addPolyline(new PolylineOptions()
                .add(l,destination)
                .width(10)
                .color(Color.BLUE)
        );

//        new java.util.Timer().schedule(
//                new java.util.TimerTask() {
//                    @Override
//                    public void run() {
//                        // your code here
//                        Log.d(TAG,"HI");

//                    }
//                },
//                5000
//        );
        //try{Thread.sleep(10000);}catch(Exception e){}
        final CameraPosition newPosition = new CameraPosition.Builder()
                .target(destination)
                .bearing(0.0f)
                .tilt(0.0f)
                .zoom(14f)
                .build();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                MarkerAnimation.animateMarkerToGB(marker, destination, new LatLngInterpolator.Spherical());

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition));
                pDialog = new ProgressDialog(MapsActivity.this);
                pDialog.setMessage("Sending SMS...");
                pDialog.setIndeterminate(false);
                pDialog.show();
                initSpeech();
                sendSMS(Utils.url);
                drawCircle(destination);
                addFriendMarkers();
            }
        }, 10000);

    }
    private void initSpeech(){
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    systemSpeech("You have reached your destination.");
                    systemSpeech("An SMS has been sent to your nearby friends.");


                }
            }
        });
    }
    private void addFriendMarkers(){
        LatLng l = new LatLng(35.905154, -79.051382);
        MarkerOptions options = new MarkerOptions().position( l );
        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.drawable.face1 ) ));
        mMap.addMarker(options);

        l = new LatLng(35.912019, -79.045052);
        options = new MarkerOptions().position(l);
        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.drawable.face2 ) ));
        mMap.addMarker(options);

        l = new LatLng(35.902432, -79.046435);
        options = new MarkerOptions().position(l);
        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.drawable.face3 ) ));
        mMap.addMarker(options);

        l = new LatLng(35.908336, -79.039329);
        options = new MarkerOptions().position(l);
        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.drawable.face4 ) ));
        mMap.addMarker(options);


    }
    private void drawCircle( LatLng location ) {
        CircleOptions options = new CircleOptions();
        options.center( location );
        //Radius in meters
        options.radius( 1300 );
        options.fillColor( getResources()
                .getColor( R.color.fillColor ) );
        options.strokeColor( getResources()
                .getColor( R.color.strokeColor ) );
        options.strokeWidth( 10 );
        mMap.addCircle(options);
    }
    private void sendSMS(String url){

        String smsText = "";
        String json = "{\"from\": \"+19047120118\", \"to\": \"+13522830967\", \"text\": \"User has reached her destination!\"}";
        json= json.replaceAll("\n", "\\n");
        json = json.substring(json.indexOf("{"));
        JSONObject jsonBody=null;
        try{
            jsonBody = new JSONObject(json);
        }catch(Exception e){

        }
        Log.d(TAG, "Json object: "+jsonBody);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"IN onRepsonse");
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"IN onErrorResponse");
                Log.d(TAG,"Error is: "+error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.d("NetworkResponseError",res.toString());
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                // hide the progress dialog
                pDialog.hide();
            }


        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                String credentials = Utils.username+":"+Utils.password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String auth1 =Utils.auth;
                params.put("Authorization",auth1);
                //params.put("Content-Type","application/json");

                return params;
            }};

// Adding request to request queue
        MapApplication.getInstance().addToRequestQueue(jsonObjReq);

    }

    public LatLng getCurrentLocation(Context context) {
        try {
            LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String locProvider = locMgr.getBestProvider(criteria, false);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return new LatLng(0, 0);
            }
            Location location = locMgr.getLastKnownLocation(locProvider);

            // getting GPS status
            boolean isGPSEnabled = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNWEnabled = locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNWEnabled)
            {
                // no network provider is enabled
                return null;
            }
            else
            {
                // First get location from Network Provider
                if (isNWEnabled)
                    if (locMgr != null)
                        location = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                    if (location == null)
                        if (locMgr != null)
                            location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (NullPointerException ne)
        {
            Log.e("Current Location", "Current Lat Lng is Null");
            return new LatLng(0, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new LatLng(0, 0);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text){
        String utteranceId=this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
    }
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text){
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_ADD, map);
    }

    private void systemSpeech(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ttsGreater21(text);
//            text="Tap your phone to enter your destination.";
//            ttsGreater21(text);
        } else {
            ttsUnder20(text);
//            text="Tap your phone to enter your destination.";
//            ttsUnder20(text);
        }
    }

}

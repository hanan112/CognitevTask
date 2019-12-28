package com.hanan_ali.cognitevtask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    final String CLIENT_ID = "IXTUWI32KGVX5Q4EXOBSGB22MBYBUZ3TUMUJTQPYM41HSWEZ";
    final String CLIENT_SECRET = "Z3ZICB44SQXXX12KSPFFIJ33LUQEJ45AEAZKI13TLSNO2OZ5";
    //private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int proximityRadius = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void  onClick(View v)
    {
        String hospital="hospital",school="school",restaurant="restaurant";
        Object transfErData[]=new Object[2];
        GetNearByPlaces getNearByPlaces=new GetNearByPlaces();


        switch (v.getId())
        {
            case R.id.search_address :
                EditText addressField=(EditText)findViewById(R.id.location_search);
                String address=addressField.getText().toString();

                List<Address> addressList=null;
                MarkerOptions userMarkerOptions=new MarkerOptions();
                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try {
                        addressList=geocoder.getFromLocationName(address,6);
                        if (addressList!=null)
                        {
                            for (int i=0;i<addressList.size();i++)
                            {
                                Address userAddress=addressList.get(i);
                                LatLng latLng=new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                            }
                        }
                        else
                        {
                            Toast.makeText(this, "Location not found ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }


                }
                else
                {
                    Toast.makeText(this, "please write any location name !!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.hospital_nearby:
                mMap.clear();
                String url= getUrl(latitude,longitude,hospital);
                transfErData[0]=mMap;
                transfErData[1]=url;
                getNearByPlaces.execute(transfErData);
                Toast.makeText(this, "Searching for nearby hospital ", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing the nearby hospital ", Toast.LENGTH_SHORT).show();





                break;

            case R.id.school_nearby:
                mMap.clear();
                url= getUrl(latitude,longitude,school);
                transfErData[0]=mMap;
                transfErData[1]=url;
                getNearByPlaces.execute(transfErData);
                Toast.makeText(this, "Searching for nearby schools ", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing the nearby schools ", Toast.LENGTH_SHORT).show();
                break;

            case R.id.restaurant_nearby:
                mMap.clear();
                url= getUrl(latitude,longitude,restaurant);
                transfErData[0]=mMap;
                transfErData[1]=url;
                getNearByPlaces.execute(transfErData);
                Toast.makeText(this, "Searching for nearby restaurant ", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing the nearby restaurant ", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&raduis= " + proximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "12:25:73:E4:F5:C5:74:D6:A2:25:E5:3E:8E:D0:15:E2:B6:A7:39:FD");

        Log.d("GoogleMapsActivity", "url= " + googleURL.toString());

        return googleURL.toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }


    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);

            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    Toast.makeText(this, "permission denied ", Toast.LENGTH_SHORT).show();
                }
                return;


        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));


        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(11000);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

  /*  public class fourquare extends AsyncTask<View, Void, String> {

        String temp;

        public fourquare(double latitude, double longitude, String restaurant) {
        }

        @Override
        protected String doInBackground(View... urls) {
            // make Call to the url
            temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id="
                    + CLIENT_ID
                    + "&client_secret=IXTUWI32KGVX5Q4EXOBSGB22MBYBUZ3TUMUJTQPYM41HSWEZ"
                    + CLIENT_SECRET
                    + "&v=20130815&ll=Z3ZICB44SQXXX12KSPFFIJ33LUQEJ45AEAZKI13TLSNO2OZ5"
                    + String.valueOf(latitude)
                    + ","
                    + String.valueOf(longitude));
            Log.e("Link ---- > ", temp);
            return "";
        }
    }
    public static String makeCall(String url) {

        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();
        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // trim the whitespaces
        return replyString.trim();
    }*/
}

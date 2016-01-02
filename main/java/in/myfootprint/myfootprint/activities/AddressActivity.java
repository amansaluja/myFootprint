package in.myfootprint.myfootprint.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.utils.NetworkUtils;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Aman on 15-11-2015.
 */

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Toolbar mToolbar;

    TextView title, title2;

    private GoogleMap googleMap;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    MarkerOptions markerOp;
    int choice = 1;
    double longitude = 0.0;
    double latitude = 0.0;

    int loop;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    LinearLayout thisAddress, gotoMap, fillLater;
    String hasFilledAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title2 = (TextView) findViewById(R.id.title2);
        title.setText("SET YOUR HOME LOCATION");
        title2.setText("TO ALLOW CHECK-INS");

        thisAddress = (LinearLayout) findViewById(R.id.thisPlace);
        gotoMap = (LinearLayout) findViewById(R.id.gotoMap);
        fillLater = (LinearLayout) findViewById(R.id.later);

        hasFilledAddress = "";
        hasFilledAddress = PrefUtils.getAddress();

        if(!hasFilledAddress.equals(""))
        {
            Intent intent = new Intent();
            intent.setClass(AddressActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            AddressActivity.this.finish();
        }

        if (ContextCompat.checkSelfPermission(AddressActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddressActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(AddressActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if(!NetworkUtils.checkGPS(AddressActivity.this)){
            NetworkUtils.buildAlertMessageNoGps(AddressActivity.this);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();

        loop = 1;
        try {

            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        PrefUtilsNew.setSetAddressPhoto("");

        thisAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] latlong = PrefUtils.getLatLong().split(",");
                //if(latlong[0] != "" || latlong[1] != ""){
                try{
                    latitude = Double.parseDouble(latlong[0]);
                    longitude = Double.parseDouble(latlong[1]);
                }catch(NumberFormatException ex){ // handle your exception
                    ex.printStackTrace();
                }

                if(latitude == 0.0 || longitude == 0.0){
                    Toast.makeText(getApplicationContext(), "Please wait for the marker above to point to your current location.", Toast.LENGTH_LONG).show();

                }else{

                    Intent i = new Intent(AddressActivity.this, BlueActivity.class);
                    i.putExtra("FragmentNumber", "1");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    AddressActivity.this.finish();
                }
            }
        });

        gotoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(AddressActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                AddressActivity.this.finish();
            }
        });

        fillLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setAddress("");
                Intent intent = new Intent();
                intent.setClass(AddressActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                AddressActivity.this.finish();
            }
        });
    }

    private void initilizeMap() {

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        markerOp = new MarkerOptions();
        markerOp.icon(BitmapDescriptorFactory.fromResource(R.drawable.favicon_marker));
        markerOp.draggable(false);

        if(latitude == 0.0 || longitude == 0.0){
            googleMap.setOnMyLocationChangeListener(myLocationChangeListener());
        }else{
            showMap(latitude, longitude);
        }

    }

    public void showMap(double latitude, double longitude){
        if(choice == 1) {

            googleMap.clear();
            LatLng sydney = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));
            googleMap.addMarker(markerOp.position(sydney));
            PrefUtils.setLatLong(String.valueOf(latitude) + "," + String.valueOf(longitude));

        }else{
            Toast.makeText(getApplicationContext(), "Can't display map without permission. " +
                    "Please grant the permission", Toast.LENGTH_SHORT).show();
        }
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if(choice == 1) {

                    if(loop == 1){

                        googleMap.clear();
                        final LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        googleMap.addMarker(markerOp.position(loc));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 13.0f));
                        PrefUtils.setLatLong(String.valueOf(latitude) + "," + String.valueOf(longitude));
                        loop++;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Can't get your location without permission. " +
                            "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onConnected(Bundle bundle) {
        initilizeMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        hasFilledAddress = PrefUtils.getAddress();

        if(!hasFilledAddress.equals(""))
        {
            Intent intent = new Intent();
            intent.setClass(AddressActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            AddressActivity.this.finish();
        }

        checkPlayServices();
        MyFootprintApplication.getInstance().trackScreenView("Address Screen");
    }

    @Override
    public void onBackPressed() {

        AddressActivity.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    choice = 1;

                } else {

                    choice = 0;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

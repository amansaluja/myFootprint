package in.myfootprint.myfootprint.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.PlaceAutocompleteAdapter;
import in.myfootprint.myfootprint.utils.PrefUtils;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Aman on 27-11-2015.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private Toolbar mToolbar;
    private LinearLayout doneButton;

    LatLng latestLoc;

    private GoogleMap googleMap;
    AutoCompleteTextView etLocality;
    private Subscription subscription;

    MarkerOptions markerOp;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    int choice = 1;


    double latitude = 28.61;
    double longitude = 77.20;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAutoCompleteAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(23.0, 72.0),new LatLng(32.733333, 74.9));
    String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        markerOp = new MarkerOptions();
        markerOp.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_address));
        markerOp.draggable(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID , this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        etLocality = (AutoCompleteTextView) findViewById(R.id.locality);
        etLocality.setThreshold(3);
        etLocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setAutocompleteStatus(false);
            }
        });
        etLocality.setOnItemClickListener(mAutocompleteClickListener);

        mAutoCompleteAdapter = new PlaceAutocompleteAdapter(this, R.layout.autocomplete_list_item,
                BOUNDS_GREATER_SYDNEY, null);
        etLocality.setAdapter(mAutoCompleteAdapter);

        doneButton = (LinearLayout) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, BlueActivity.class);
                i.putExtra("FragmentNumber", "1");
                MyFootprintApplication.getInstance().trackEvent("Located Address", "To comfirmAddress", "MapScreen");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MapActivity.this.finish();
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if(choice == 1) {

                LatLng sydney = new LatLng(latitude, longitude);
                googleMap.setMyLocationEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));
                googleMap.addMarker(markerOp.position(sydney));
                PrefUtils.setLatLong(String.valueOf(latitude) + "," + String.valueOf(longitude));

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        latestLoc = marker.getPosition();
                        PrefUtils.setLatLong(String.valueOf(latestLoc.latitude) + "," + String.valueOf(latestLoc.longitude));
                    }
                });

        }else{
            Toast.makeText(getApplicationContext(), "Can't display map without permission. " +
                    "Please grant the permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                PrefUtils.setAddress("");
                Intent i = new Intent(MapActivity.this, AddressActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PrefUtils.setLatLong(String.valueOf(0.0) + "," + String.valueOf(0.0));
                startActivity(i);
                MapActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        MyFootprintApplication.getInstance().trackScreenView("Map Screen");
    }

    @Override
    public void onBackPressed() {

        PrefUtils.setAddress("");
        Intent i = new Intent(MapActivity.this, AddressActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PrefUtils.setLatLong(String.valueOf(0.0) + "," + String.valueOf(0.0));
        startActivity(i);
        MapActivity.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    choice = 1;

                } else {

                    choice = 0;
                }
                return;
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            PrefUtils.setAutocompleteStatus(true);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {

                PrefUtils.setAutocompleteStatus(false);
                return;
            }
            PrefUtils.setAutocompleteStatus(true);
            final Place place = places.get(0);
            LatLng latlng = place.getLatLng();
            latitude = latlng.latitude;
            longitude = latlng.longitude;
            place.getName();
            String placeName = (String) place.getName();

            showMap(latitude, longitude);
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        mAutoCompleteAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public void showMap(double latitude, double longitude){
        if(choice == 1) {

            googleMap.clear();
            LatLng sydney = new LatLng(latitude, longitude);
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));
            googleMap.addMarker(markerOp.position(sydney));
            PrefUtils.setLatLong(String.valueOf(latitude) + "," + String.valueOf(longitude));

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    latestLoc = marker.getPosition();
                    PrefUtils.setLatLong(String.valueOf(latestLoc.latitude) + "," + String.valueOf(latestLoc.longitude));
                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "Can't display map without permission. " +
                    "Please grant the permission", Toast.LENGTH_SHORT).show();
        }
    }
}

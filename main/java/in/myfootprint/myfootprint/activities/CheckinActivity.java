package in.myfootprint.myfootprint.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.network.FetchFBPhoto;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.NameAutoCompleteAdapter;
import in.myfootprint.myfootprint.navigation.FragmentDrawerFragment;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;
import in.myfootprint.myfootprint.views.RoundedImageView;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

public class CheckinActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Toolbar mToolbar;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    Uri selectedImageUri;
    private Uri outputFileUri;
    RelativeLayout rlImage;
    ImageView image;
    private String imagePath = "";
    ImageView cam_icon;
    TextView cam_text;
    EditText etDescription;
    AutoCompleteTextView etPerson;
    CheckBox cbFacebook;
    CheckBox cbPrivate;
    LinearLayout checkInButton;
    String nameId;
    public RoundedImageView profileImage;
    String fbUserID;

    final String[] description = new String[1];
    final String[] person = new String[1];
    final String[] share = new String[1];

    private GoogleMap googleMap;

    String hasSelectedName = "";

    double latitude = 0.0;
    double longitude = 0.0;
    MarkerOptions markerOp;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
    int choice = 1;

    int loop;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    NameAutoCompleteAdapter mNameAutoCompleteAdapter;
    JSONArray suggestionNames;

    String hostName;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("CHECK-IN");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        OpenPopUp();

        profileImage = (RoundedImageView) findViewById(R.id.profileImage);
        image = (ImageView)  findViewById(R.id.image);
        cam_icon = (ImageView)  findViewById(R.id.cam_icon);
        cam_text = (TextView)  findViewById(R.id.cam_text);
        checkInButton = (LinearLayout)  findViewById(R.id.checkinButton);

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitCheckin();
            }
        });

        if (ContextCompat.checkSelfPermission(CheckinActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(CheckinActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(CheckinActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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

        //Controller.getAutocompleteActivity();
        imageLoader = MyFootprintApplication.getInstance().getImageLoader();
        fbUserID = PrefUtils.getFBUserID();

        String path = PrefUtils.getProfileImagePath();
        if(!path.equals("")){
            Bitmap image = Controller.decodeBase64(path);
            profileImage.setImageBitmap(image);
        }else {
            FetchFBPhoto asyncTask = (FetchFBPhoto) new FetchFBPhoto(getApplicationContext(), fbUserID, new FetchFBPhoto.AsyncResponse() {

                @Override
                public void processFinish(Bitmap output) {

                    profileImage.setImageBitmap(output);
                }
            }).execute();
        }
    }

    public void OpenPopUp() {

        rlImage = (RelativeLayout) findViewById(R.id.imageCapture);
        etDescription = (EditText) findViewById(R.id.description);
        etPerson = (AutoCompleteTextView) findViewById(R.id.person);
        etPerson.requestFocus();
        etPerson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean gotfocus) {
                // TODO Auto-generated method stub
                if (gotfocus) {
                    etPerson.setCompoundDrawables(null, null, null, null);
                } else if (!gotfocus) {
                    if (etPerson.getText().length() == 0)
                        etPerson.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                }
            }
        });

        if(etPerson.isFocused()){
            etPerson.setCompoundDrawables(null, null, null, null);
        }else{
            etPerson.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
        }

        cbFacebook = (CheckBox)  findViewById(R.id.checkbox1);
        cbPrivate = (CheckBox)  findViewById(R.id.checkbox2);

        share[0] = "";

        cbFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbFacebook.isChecked()){
                    cbPrivate.setChecked(false);
                }
            }
        });
        cbPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPrivate.isChecked()) {
                    cbFacebook.setChecked(false);
                }
            }
        });

        etPerson.setThreshold(3);
        etPerson.setOnItemClickListener(mAutocompleteClickListener);

        try {
            suggestionNames = new JSONArray(PrefUtils.getNameList());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mNameAutoCompleteAdapter = new NameAutoCompleteAdapter(CheckinActivity.this, android.R.layout.simple_gallery_item);
        etPerson.setAdapter(mNameAutoCompleteAdapter);

        rlImage.setOnClickListener(new RelativeLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                new captureImage().execute();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", outputFileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        outputFileUri = savedInstanceState.getParcelable("file_uri");
    }

    private class captureImage extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);

            root.mkdirs();
            final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
            final File sdImageMainDirectory = new File(root, fname);

            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for(ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                cameraIntents.add(intent);
            }

            // Filesystem.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // Chooser of filesystem options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
            //chooserIntent.putExtra(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(chooserIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            imagePath = "";
            if (resultCode == RESULT_OK) {

                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {
                    selectedImageUri = outputFileUri;

                    try {
                        imagePath = selectedImageUri.getPath().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                } else {
                    /*Uri img = data == null ? null : data.getData();
                    imagePath = getRealPathFromURI(img);*/
                    try {
                        Uri img = data == null ? null : data.getData();
                        imagePath = getRealPathFromURI(img);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                if (imagePath != ""){
                    previewMedia();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        if(Build.VERSION.SDK_INT >19)
        {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(contentUri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{ id }, null);
        }
        else
        {
            cursor = getContentResolver().query(contentUri, projection, null, null, null);
        }
        String path = null;
        try
        {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        }
        catch(NullPointerException e) {

        }
        return path;
    }

    private void previewMedia() {

        cam_icon.setVisibility(View.GONE);
        cam_text.setVisibility(View.GONE);

        BitmapFactory.Options options = new BitmapFactory.Options();
        // down sizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 8;

        final Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        image.setImageBitmap(bitmap);
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
            PrefUtils.setLatlongCheckin(String.valueOf(latitude) + "," + String.valueOf(longitude));

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
                        PrefUtils.setLatlongCheckin(String.valueOf(latitude) + "," + String.valueOf(longitude));
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
        checkPlayServices();
        MyFootprintApplication.getInstance().trackScreenView("CheckIn Screen");
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final NameAutoCompleteAdapter.PlaceAutocomplete item = mNameAutoCompleteAdapter.getItem(position);
            nameId = String.valueOf(item.id);
            hostName = String.valueOf(item.name);
            PrefUtilsNew.setHostName(hostName);
            if(nameId.equals("inviteonwhatsapp") || nameId.equals("inviteonwhatsapp1")){
                Toast.makeText(getApplicationContext(), "Invite your friend on Whatsapp here", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(CheckinActivity.this, MainActivity.class);
                intent.putExtra("InviteTab", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                CheckinActivity.this.finish();
            }
            hasSelectedName = "Y";
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_tick:
                SubmitCheckin();

                break;
            case android.R.id.home:
                Intent i = new Intent(CheckinActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                CheckinActivity.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(CheckinActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        CheckinActivity.this.finish();
    }

    public void SubmitCheckin() {

        share[0] = "private";
        if (cbFacebook.isChecked()) {
            share[0] = "friends";
        } else if (cbPrivate.isChecked()) {
            share[0] = "private";
        }

        description[0] = etDescription.getText().toString();
        person[0] = nameId;

        String[] latlong = PrefUtils.getLatlongCheckin().split(",");
        //if(latlong[0] != "" || latlong[1] != ""){
        try{
            latitude = Double.parseDouble(latlong[0]);
            longitude = Double.parseDouble(latlong[1]);
        }catch(NumberFormatException ex){ // handle your exception
            ex.printStackTrace();
        }
        //}

        if(etPerson.getText().toString().equals("") || etPerson.getText().toString() == null ||etPerson.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter your host name", Toast.LENGTH_LONG).show();

        }else{
            if(nameId != null) {

                if (nameId.equals("inviteonwhatsapp") || nameId.equals("inviteonwhatsapp1")) {
                    Toast.makeText(getApplicationContext(), "Invite your friend here", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(CheckinActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("InviteTab", 2);
                    startActivity(intent);
                    CheckinActivity.this.finish();
                } else {
                    if (hasSelectedName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please select one of the host from the list. " +
                                "Or ask your host to install myFootprint and try again!", Toast.LENGTH_SHORT).show();
                    } else if (description[0].equals("")) {
                        description[0] = "I just checked in ";
                    } else {
                        if(latitude == 0.0 || longitude == 0.0){
                            Toast.makeText(getApplicationContext(), "Could not get your location. Please wait for the " +
                                    "marker below to capture your location and try again", Toast.LENGTH_SHORT).show();
                        }else{
                            Controller.sendGuestCheckin(description[0], ""/* activity[0]*/, person[0], share[0], latitude,
                                    longitude, imagePath, CheckinActivity.this);
                        }
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(), "Sorry your host is not registered with myFootprint. Invite " +
                        "and ask your host to install myFootprint!", Toast.LENGTH_SHORT).show();
            }
        }
        MyFootprintApplication.getInstance().trackEvent("CheckedIn", "To approval", "CheckinScreen");
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

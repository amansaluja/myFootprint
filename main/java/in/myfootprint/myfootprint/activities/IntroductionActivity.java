package in.myfootprint.myfootprint.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.ViewPagerAdapter;
import in.myfootprint.myfootprint.utils.NetworkUtils;
import in.myfootprint.myfootprint.utils.PrefUtils;

/**
 * Created by Aman on 15-11-2015.
 */

public class IntroductionActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
    private int[] mImageResources = {
            R.drawable.fa_content_p0,
            R.drawable.fa_content_p1,
            R.drawable.fa_content_p2,
            R.drawable.fa_content_p3};

    boolean hasLoggedIn;

    LoginButton loginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private String fbUserID;
    private String fbAuthToken;
    private Date fbExpires;
    private AccessToken accessToken;
    static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_introduction);

        intro_images = (ViewPager) findViewById(R.id.pager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        mAdapter = new ViewPagerAdapter(this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();

        hasLoggedIn = PrefUtils.getHasLoggedIn();

        if(hasLoggedIn)
        {
            Intent intent = new Intent();
            intent.setClass(IntroductionActivity.this, AddressActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            IntroductionActivity.this.finish();

        }

        //Facebook login
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        loginButton.setReadPermissions(Arrays.asList("public_profile ", "user_friends", "email"));

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
                if(permissions.contains("email")) {
                    // Continue your stuff.
                } else {
                    GraphRequest request = new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/permissions", null, HttpMethod.DELETE,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse graphResponse) {
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(getApplicationContext(), "Email permission is needed. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                    request.executeAsync();
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.

                try {
                    fbAuthToken = currentAccessToken.getToken();
                    fbUserID = currentAccessToken.getUserId();
                    PrefUtils.setFBUserID(fbUserID);
                    fbExpires = currentAccessToken.getExpires();
                }catch (NullPointerException e){

                }
                getFacebookAccess(fbUserID, fbAuthToken, String.valueOf(fbExpires), IntroductionActivity.this);

            }
        };

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        if(!NetworkUtils.checkGPS(IntroductionActivity.this)){
            //NetworkUtils.buildAlertMessageNoGps(IntroductionActivity.this);
            Toast.makeText(getApplicationContext(), "Please switch your GPS on for faster performance.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(!NetworkUtils.isNetworkConnected(this)){
            Toast.makeText(getApplicationContext(), "Please turn on Internet", Toast.LENGTH_SHORT).show();
        }
    }

    //The whole view Pager
    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.pointer_page_unactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.pointer_page_active));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.pointer_page_unactive));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.pointer_page_active));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {

        boolean hasLoggedIn = PrefUtils.getHasLoggedIn();

        if(hasLoggedIn)
        {
            Intent intent = new Intent();
            intent.setClass(IntroductionActivity.this, AddressActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            IntroductionActivity.this.finish();

        }
        MyFootprintApplication.getInstance().trackScreenView("Login Screen");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onBackPressed() {
        IntroductionActivity.this.finish();
    }

    public void getFacebookAccess(String userID, String authToken, String expires, final Context mContext) {

        String url = Controller.URLInitial + "auth/social/facebook/";

        JSONObject params3 = new JSONObject();
        JSONObject params2 = new JSONObject();
        JSONObject params1 = new JSONObject();

        try {
            params2.put("status", "connected");
            params3.put("accessToken", authToken);
            params3.put("userID", userID);
            params3.put("expiresIn", expires);
            params3.put("signedRequest", "hello");
            params2.put("authResponse", params3);
            params1.put("data", params2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    PrefUtils.setUserAccess(response.getJSONObject("auth").get("token").toString());
                    if(mProgressDialog != null){
                        if(mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Some error occurred. Please try again!", Toast.LENGTH_SHORT).show();

                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }

                LoginManager.getInstance().logOut();
                PrefUtils.setHasLoggedIn(false);
                MyFootprintApplication.getInstance().trackEvent("LoggedOut", "To login", "IntroductionActivity");
                Intent intent = new Intent();
                intent.setClass(IntroductionActivity.this, IntroductionActivity.class);
                startActivity(intent);

                MyFootprintApplication.getInstance().trackEvent("FacebookError", "Server", "LoginScreen");
            }
        }){

            @Override
            protected Response<JSONObject> parseNetworkResponse (NetworkResponse response){
                int mStatusCode = response.statusCode;
                if(mStatusCode == 200){
                    PrefUtils.setHasLoggedIn(true);
                    Intent intent = new Intent();
                    intent.setClass(IntroductionActivity.this, AddressActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    IntroductionActivity.this.finish();

                }else{

                    LoginManager.getInstance().logOut();
                    PrefUtils.setHasLoggedIn(false);
                    MyFootprintApplication.getInstance().trackEvent("LoggedOut", "To login", "IntroductionActivity");
                    Intent intent = new Intent();
                    intent.setClass(IntroductionActivity.this, IntroductionActivity.class);
                    startActivity(intent);
                    //IntroductionActivity.this.finish();
                }
                return super.parseNetworkResponse(response);
            }
        };

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(Controller.DEFAULT_RETRY_POLICY);
        MyFootprintApplication.getInstance().addToRequestQueue(jsonObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }
}

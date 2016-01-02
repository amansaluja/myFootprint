package in.myfootprint.myfootprint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.myfootprint.myfootprint.activities.BlueActivity;
import in.myfootprint.myfootprint.activities.CheckinActivity;
import in.myfootprint.myfootprint.activities.MainActivity;
import in.myfootprint.myfootprint.activities.ProfileActivity;

import in.myfootprint.myfootprint.models.MultipartRequest;
import in.myfootprint.myfootprint.utils.NetworkUtils;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 11/21/2015.
 */
public class Controller{

    static ProgressDialog mProgressDialog;
    static JSONArray suggestionActivity;
    static JSONArray suggestionNames;
    static JSONObject profileDetail;
    static JSONArray profileDetailFull;

    public static String URLInitial = "http://myfootprint.in/api/";

    public static final RetryPolicy DEFAULT_RETRY_POLICY = new RetryPolicy() {
        @Override
        public void retry(VolleyError error) throws VolleyError {
            if(error.networkResponse.statusCode == 401)
                throw new VolleyError(error);
        }

        @Override
        public int getCurrentTimeout() {
            return 0;
        }

        @Override
        public int getCurrentRetryCount() {
            return 3;
        }
    };

    public interface ERROR_CODES {
        int BAD_REQUEST = 400;
        int UNAUTHORISED = 401;
        int UNAUTHORIZED_ACCESS = 403;
        int NOT_FOUND = 404;
        int USERNAME_NOT_AVAILABLE = 409;
        int SOURCE_FILE_DOESNT_EXIST = 920;
        int CUSTOM_ERROR_CODE = 1001;
        int SHIT_HAPPENED = 1022;
        int FILES_MISSING = 1023;
        int NO_INTERNET = 1025;
        int EMPTY_RESULTS = 1026;
        int API_FAILURE = 1027;

    }

    public static void goToUrl (String url, Context context) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        launchBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchBrowser);
    }

    private static RequestQueue mRequestQueue;

    public static final void init(Context context) {
         mRequestQueue = Volley.newRequestQueue(context);
    }

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    /*public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }*/

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static void sendUserDetails(final String description, final String type, final Double longitude, final String locality,
                                       final Double latitude, final String address, final String full_address,
                                       final String pincode, final String imagePath, final Context mContext) {

        String url = URLInitial + "flats/flat/";

        JSONObject params4 = new JSONObject();
        JSONObject params3 = new JSONObject();
        JSONObject params2 = new JSONObject();
        JSONObject params1 = new JSONObject();

        try {

            params4.put("pincode", pincode);
            params4.put("full_address", full_address);
            params4.put("number", address);
            params4.put("country", "World");
            params4.put("latitude", latitude);
            params4.put("locality", locality);
            params4.put("longitude", longitude);
            params4.put("state", "Earth");
            params4.put("city", "Earth");

            params3.put("user_type", type);
            params3.put("description", description);

            params2.put("flat", params4);
            params2.put("user_flat", params3);

            params1.put("data", params2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                PrefUtils.setAddress("YES");
                try {
                    PrefUtils.setHomeID(response.get("id").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(imagePath.equals("")){
                    Intent intent = new Intent();
                    intent.setClass(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(intent);
                    MyFootprintApplication.getInstance().trackEvent("Registered", "Success", "AddressScreen");
                }else{
                    Controller.sendUserDetailsPhoto(mContext, PrefUtils.getHomeID(), imagePath);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                /*VolleyLog.d("Error", "Error: " + error.getMessage());
                Log.e("Error", "Error: " + error.getMessage());

                PrefUtils.setAddress("");
                String body;
                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);

                //get response body and parse with appropriate encoding
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                        Log.e("Error", statusCode + body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e("e", e.toString());
                    }
                }*/
                MyFootprintApplication.getInstance().trackEvent("AddressError", "Server", "AddressScreen");
                PrefUtils.setAddress("");

                Toast.makeText(mContext, "Your location details could not be stored. Please ensure that you have " +
                        "a good internet connection and try again!", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        jsonObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(jsonObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void sendUserDetailsPhoto(final Context mContext, final String homeID, final String imagePath) {

        String url = URLInitial + "flats/flat/" + homeID + "/image/";

        File sourceFile = new File(imagePath);

        MyFootprintApplication.getInstance().trackEvent("AddressImage", "Uploaded", "AddressScreen");
        MultipartRequest builder = new MultipartRequest(url, createErrorListener(),
                createSuccessListenerUser(mContext), sourceFile, ""){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        builder.setRetryPolicy(DEFAULT_RETRY_POLICY);
        MyFootprintApplication.getInstance().addToRequestQueue(builder);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void sendGuestCheckin(final String content, String activity, String flat_owner,
                                        final String privacy_level, Double latitude, Double longitude,
                                        final String imagePath, final Context mContext) {

        String url = URLInitial + "check-in/";

        JSONObject params2 = new JSONObject();
        JSONObject params1 = new JSONObject();

        try {
            params2.put("longitude", longitude);
            params2.put("latitude", latitude);
            params2.put("privacy_level", privacy_level);
            params2.put("flat_owner", flat_owner);
            params2.put("activity", activity);
            params2.put("content", content);

            params1.put("data", params2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                try {
                    PrefUtilsNew.setCheckInId(response.get("id").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrefUtilsNew.setCHECKIn_HOST(PrefUtilsNew.getHostName());
                MyFootprintApplication.getInstance().trackEvent("CheckedIn", "Success", "Check In");

                if(imagePath.equals("")){
                    Intent i = new Intent(mContext, BlueActivity.class);
                    i.putExtra("FragmentNumber", "2");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MyFootprintApplication.getInstance().trackEvent("CheckedIn", "Success", "CheckinScreen");
                    mContext.startActivity(i);
                }else{
                    Controller.sendCheckinPhoto(mContext, PrefUtilsNew.getCheckInId(), imagePath);
                }
                if(privacy_level.equals("friends")){

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("CheckInError", "Server", "CheckinScreen");
                //PrefUtilsNew.setCHECKIn_HOST("");
                //Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                String json = null;

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if(json != null) displayMessage(json, mContext);
                            break;
                        default:
                            Toast.makeText(mContext, "Please ensure you have good internet connection.", Toast.LENGTH_LONG).show();
                            break;
                    }
                    //Additional cases
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        jsonObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(jsonObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void sendGCMToken(final String token, final Context mContext) {

        String url = URLInitial + "auth/device/";

        JSONObject params2 = new JSONObject();
        JSONObject params1 = new JSONObject();

        try {
            params2.put("device_type", "a");
            params2.put("device_id", token);

            params1.put("data", params2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("GCMError", "Server", "PushMainScreen");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        jsonObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(jsonObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void sendCheckinPhoto(final Context mContext, final String checkinID, final String imagePath) {

        String url = URLInitial + "check-in/" + checkinID + "/image/";

        File sourceFile = new File(imagePath);

        MyFootprintApplication.getInstance().trackEvent("CIImage", "Uploaded", "CheckInScreen");
        MultipartRequest builder = new MultipartRequest(url, createErrorListener(),
                createSuccessListener(mContext), sourceFile, ""){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());

                return headers;
            }
        };

        builder.setRetryPolicy(DEFAULT_RETRY_POLICY);
        MyFootprintApplication.getInstance().addToRequestQueue(builder);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void getCheckinStatus(final Context mContext, final String checkinID) {

        String url = URLInitial + "check-in/" + checkinID + "/";

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(mProgressDialog != null){
                            if(mProgressDialog.isShowing()){
                                mProgressDialog.dismiss();
                            }
                        }

                        try {

                            if(response.get("is_verified") != null || !response.get("is_verified").equals(null)){
                                if(response.getBoolean("is_verified")){
                                    Intent i = new Intent(mContext, BlueActivity.class);
                                    i.putExtra("FragmentNumber", "3");
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mContext.startActivity(i);

                                }else {
                                    Toast.makeText(mContext, "Your host declined your Check-in request. Please try again.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(mContext, CheckinActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mContext.startActivity(i);
                                }
                            }else {
                                Toast.makeText(mContext, "Your Check-in request is pending. Ask your host to approve it.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                MyFootprintApplication.getInstance().trackEvent("CIStatusError", "Server", "CIStatusScreen");
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static void sendCheckinApproval(final Context mContext, final String decision, final String checkinID) {

        String url = URLInitial + "check-in/" + checkinID + "/verify/";

        JSONObject params = new JSONObject();
        try {
            if(decision.equals("true")){
                params.put("is_verified", true);
            }else{
                params.put("is_verified", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.POST,
                url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("FragmentNumber", "3");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(i);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("CIApprovalError", "Server", "NotificationScreen");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    public static JSONArray getFullProfile(final Context mContext, final String type) {

        String url = URLInitial + "auth/fullprofile/";

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                try {
                    profileDetailFull = response.getJSONArray("flat");
                    profileDetail = response.getJSONObject("user");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrefUtilsNew.setUserDetailsFull(profileDetailFull.toString());
                PrefUtilsNew.setUserDetails(profileDetail.toString());
                if(type.equals("person")){

                    Intent j = new Intent(mContext, ProfileActivity.class);
                    j.putExtra("FragmentNumber", "1");
                    j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(j);
                }else{
                    Intent j = new Intent(mContext, ProfileActivity.class);
                    j.putExtra("FragmentNumber", "2");
                    j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(j);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("GetProfileError", "Server", "ProfileScreen");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(true);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        return profileDetailFull;
    }

    public static JSONArray getAutocompleteNames(final CharSequence charSequence) {

        String url = URLInitial + "user/autocomplete/?query=";
        String latlong = "&latitude=21.456789&longitude=78.123121";

        url = url + charSequence + latlong;

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    suggestionNames = response.getJSONArray("users");
                    PrefUtils.setNameList(suggestionNames.toString());

                    if(suggestionNames.isNull(0)){
                        JSONObject jo = new JSONObject();
                        JSONObject yo = new JSONObject();
                        try {
                            jo.put("name", "No user found.");
                            jo.put("image_url", "");
                            jo.put("id", "inviteonwhatsapp");
                            yo.put("name", "Invite them on Whatsapp.");
                            yo.put("image_url", "");
                            yo.put("id", "inviteonwhatsapp1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        suggestionNames.put(jo);
                        suggestionNames.put(yo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                MyFootprintApplication.getInstance().trackEvent("AutocompleteError", "Server", "CheckinScreen");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);

        return suggestionNames;
    }

    private static Response.ErrorListener createErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("PhotoError", "Server", "NoUpload");
                /*String body;
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        };
    }

    private static Response.Listener<String> createSuccessListener(final Context mContext) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                Intent i = new Intent(mContext, BlueActivity.class);
                i.putExtra("FragmentNumber", "2");
                MyFootprintApplication.getInstance().trackEvent("CheckedIn", "Success", "CheckinScreen");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(i);
            }
        };
    }

    private static Response.Listener<String> createSuccessListenerUser(final Context mContext) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                Intent intent = new Intent();
                intent.setClass(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MyFootprintApplication.getInstance().trackEvent("Registered", "Success", "AddressScreen");
                mContext.startActivity(intent);
            }
        };
    }

    public static String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public static void displayMessage(String toastString, Context context){
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }

}
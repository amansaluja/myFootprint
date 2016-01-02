package in.myfootprint.myfootprint.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Adi on 7/2/2015.
 */

public final class PrefUtils {

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static String prefName = "user_pref";
    private static String HAS_LOGGED_IN = "has_logged_in";
    private static String USER_ADDRESS = "user_address";
    private static String AUTOCOMPLETE_STATUS = "autocomplete_status";
    private static String LATLONG_HOME = "lat_long_home";
    private static String LATLONG_CHECKIN = "lat_long_checkin";
    private static String FB_UserID = "fb_user_id";
    private static String Home_ID = "home_id";
    private static String FB_UserAccess = "fb_user_access";
    private static String Server_Activities = "server_activities";
    private static String Server_Names = "server_names";
    private static String Profile_Image_Path = "profile_image_path";
    private static String Award_Self_Position = "Award_self_position";
    private static String Award_Pal_Position = "Award_pal_position";
    private static String Number_Footprint = "Number_footprint";
    private static String COUPON_INFO = "coupon_info";
    private static String FEED_ITEMS = "feed_items";

    public static void init(Context ctx) {
        pref = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }

    public static boolean getHasLoggedIn() {
        return pref.getBoolean(HAS_LOGGED_IN, false);
    }

    public static void setHasLoggedIn(boolean pHasLogged) {
        editor.putBoolean(HAS_LOGGED_IN, pHasLogged);
        editor.apply();
    }

    public static String getAddress() {
        return pref.getString(USER_ADDRESS, "");
    }

    public static void setAddress(String address) {
        editor.putString(USER_ADDRESS, address);
        editor.apply();
    }

    public static String getProfileImagePath() {
        return pref.getString(Profile_Image_Path, "");
    }

    public static void setProfileImagePath(String profileImagePath) {
        editor.putString(Profile_Image_Path, profileImagePath);
        editor.apply();
    }

    public static boolean getAutocompleteStatus() {
        return pref.getBoolean(AUTOCOMPLETE_STATUS, false);
    }

    public static void setAutocompleteStatus(boolean pHasReviewed) {
        editor.putBoolean(AUTOCOMPLETE_STATUS, pHasReviewed);
        editor.apply();
    }

    public static String getLatLong() {
        return pref.getString(LATLONG_HOME, "");
    }

    public static void setLatLong(String pLatLong) {
        editor.putString(LATLONG_HOME, pLatLong);
        editor.apply();
    }

    public static String getLatlongCheckin() {
        return pref.getString(LATLONG_CHECKIN, "");
    }

    public static void setLatlongCheckin(String pLatLong) {
        editor.putString(LATLONG_CHECKIN, pLatLong);
        editor.apply();
    }

    public static void setFBUserID (String userID) {
        editor.putString(FB_UserID, userID);
        editor.apply();
    }

    public static String getFBUserID(){

        String userID = pref.getString(FB_UserID, "");
        return userID;
    }

    public static void setHomeID (String homeID) {
        editor.putString(Home_ID, homeID);
        editor.apply();
    }

    public static String getHomeID(){

        String userID = pref.getString(Home_ID, "");
        return userID;
    }

    public static void setUserAccess (String userAccess) {
        editor.putString(FB_UserAccess, userAccess);
        editor.apply();
    }

    public static String getUserAccess(){

        String userID = pref.getString(FB_UserAccess, "");
        return userID;
    }

    public static void setActivityList (String activityList) {
        editor.putString(Server_Activities, activityList);
        editor.apply();
    }

    public static String getActivityList(){

        String userID = pref.getString(Server_Activities, "");
        return userID;
    }

    public static void setAwardSelfPosition (int award_Self_Position) {
        editor.putInt(Award_Self_Position, award_Self_Position);
        editor.apply();
    }

    public static int getAwardSelfPosition(){

        int selfPos = pref.getInt(Award_Self_Position, 1000);
        return selfPos;
    }

    public static void setNumberFootprint (int number_footprint) {
        editor.putInt(Number_Footprint, number_footprint);
        editor.apply();
    }

    public static int getNumberFootprint(){

        int selfPos = pref.getInt(Number_Footprint, 10000);
        return selfPos;
    }

    public static void setAwardPalPosition (int award_Pal_Position) {
        editor.putInt(Award_Pal_Position, award_Pal_Position);
        editor.apply();
    }

    public static int getAwardPalPosition(){

        int selfPos = pref.getInt(Award_Pal_Position, 1000);
        return selfPos;
    }

    public static void setNameList (String nameList) {
        editor.putString(Server_Names, nameList);
        editor.apply();
    }

    public static String getNameList(){

        String userID = pref.getString(Server_Names, "");
        return userID;
    }

    public static void setCouponInfo (String string) {
        editor.putString(COUPON_INFO, string);
        editor.apply();
    }

    public static String getCouponInfo(){

        String string = pref.getString(COUPON_INFO, "");
        return string;
    }

    public static void setFeedItems (String string) {
        editor.putString(FEED_ITEMS, string);
        editor.apply();
    }

    public static String getFeedItems(){

        String string = pref.getString(FEED_ITEMS, "");
        return string;
    }

    public static void clearall() {
        editor.clear().commit();
    }
}

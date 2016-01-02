package in.myfootprint.myfootprint.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Adi on 7/2/2015.
 */

public final class PrefUtilsNew {

    private static SharedPreferences prefNew;
    private static SharedPreferences prefN;
    private static SharedPreferences.Editor editorN;
    private static SharedPreferences.Editor editorNew;
    private static String prefNameNew = "user_pref_new";
    private static String prefNameN = "user_pref_n";
    private static String SERVER_USER_DETAILS = "server_user_details";
    private static String SERVER_USER_DETAILS_FULL = "server_user_details_full";
    private static String VISITOR_CHECK_INS = "visitor_check_ins";
    private static String RESIDENT_CHECK_INS = "resident_check_ins";
    private static String CHECK_IN_ID = "check_in_id";
    private static String SET_ADDRESS_PHOTO = "photo_link";
    private static String REDEEM_INFO = "redeem_info";
    private static String HOST_NAME = "host_name";
    private static String NOTIF_NUMBER = "notif_no";
    private static String INVITE_TEXT = "invite_text";
    private static String CHECKIn_HOST = "checkin_host";


    public static void init(Context ctx) {
        prefNew = ctx.getSharedPreferences(prefNameNew, Context.MODE_PRIVATE);
        editorNew = prefNew.edit();
        editorNew.apply();
        prefN = ctx.getSharedPreferences(prefNameN, Context.MODE_PRIVATE);
        editorN = prefN.edit();
        editorN.apply();
    }

    public static String getUserDetails() {
        return prefNew.getString(SERVER_USER_DETAILS, "");
    }

    public static void setUserDetails(String userDetails) {
        editorNew.putString(SERVER_USER_DETAILS, userDetails);
        editorNew.apply();
    }

    public static String getUserDetailsFull() {
        return prefN.getString(SERVER_USER_DETAILS_FULL, "");
    }

    public static void setUserDetailsFull(String userDetails) {
        editorN.putString(SERVER_USER_DETAILS_FULL, userDetails);
        editorN.apply();
    }

    public static String getVisitorCheckIns() {
        return prefNew.getString(VISITOR_CHECK_INS, "");
    }

    public static void setVisitorCheckIns(String string) {
        editorNew.putString(VISITOR_CHECK_INS, string);
        editorNew.apply();
    }

    public static String getResidentCheckIns() {
        return prefNew.getString(RESIDENT_CHECK_INS, "");
    }

    public static void setResidentCheckIns(String string) {
        editorNew.putString(RESIDENT_CHECK_INS, string);
        editorNew.apply();
    }

    public static String getCheckInId() {
        return prefNew.getString(CHECK_IN_ID, "");
    }

    public static void setCheckInId(String string) {
        editorNew.putString(CHECK_IN_ID, string);
        editorNew.apply();
    }

    public static String getSetAddressPhoto() {
        return prefNew.getString(SET_ADDRESS_PHOTO, "");
    }

    public static void setSetAddressPhoto(String string) {
        editorNew.putString(SET_ADDRESS_PHOTO, string);
        editorNew.apply();
    }

    public static String getRedeemInfo() {
        return prefNew.getString(REDEEM_INFO, "");
    }

    public static void setRedeemInfo(String string) {
        editorNew.putString(REDEEM_INFO, string);
        editorNew.apply();
    }

    public static String getHostName() {
        return prefNew.getString(HOST_NAME, "");
    }

    public static void setHostName(String string) {
        editorNew.putString(HOST_NAME, string);
        editorNew.apply();
    }

    public static String getCHECKIn_HOST() {
        return prefNew.getString(CHECKIn_HOST, "");
    }

    public static void setCHECKIn_HOST(String string) {
        editorNew.putString(CHECKIn_HOST, string);
        editorNew.apply();
    }

    public static int getNotifNumber() {
        return prefNew.getInt(NOTIF_NUMBER, 0);
    }

    public static void setNotifNumber(int value) {
        editorNew.putInt(NOTIF_NUMBER, value);
        editorNew.apply();
    }

    public static int getInviteText() {
        return prefNew.getInt(INVITE_TEXT, 0);
    }

    public static void setInviteText(int value) {
        editorNew.putInt(INVITE_TEXT, value);
        editorNew.apply();
    }

    public static void clearspecific() {
        editorN.clear().commit();
    }

    public static void clearall() {
        editorNew.clear().commit();
    }
}

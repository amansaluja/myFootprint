package in.myfootprint.myfootprint.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.NotificationRecyclerViewAdapter;
import in.myfootprint.myfootprint.models.CouponItem;
import in.myfootprint.myfootprint.models.NotificationItem;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 11/17/2015.
 */
public class NotificationFragment extends Fragment{

    ImageView fab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    static ProgressDialog mProgressDialog, nProgressDialog;
    String[] notifTime;
    int[] notifImage;
    String coupMessage, coupValidity, coupCode;
    String[] updPlace;
    String[] updTime;
    String[] updMessage;
    int[] updImage;

    private String[] friendName;
    private int[] friendImage;
    ArrayList<NotificationItem> notifications;
    ArrayList<CouponItem> notificationCouponItem;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        fab = (ImageView) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        notifications = new ArrayList<NotificationItem>();
        notificationCouponItem = new ArrayList<CouponItem>();

        getNotificationInfo(getActivity(), "resident", "null");

        PrefUtilsNew.setNotifNumber(0);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NotificationRecyclerViewAdapter(getActivity(), notifications, notificationCouponItem);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Notification Screen");
    }

    public void getNotificationInfo(final Context mContext, final String role, final String is_verified) {

        String url = Controller.URLInitial + "check-in/?filter=role:" + role + "&filter=is_verified:" + is_verified;

        JsonArrayRequest stringObjReq = new JsonArrayRequest (Request.Method.GET,
                url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                mProgressDialog.dismiss();
                parseJsonFeed(response);
                PrefUtilsNew.setRedeemInfo(response.toString());
                getCouponInfo(getActivity());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                MyFootprintApplication.getInstance().trackEvent("GetCheckInError", "Server", "NotificationScreen");
                Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(Controller.DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }

    private void parseJsonFeed(JSONArray response) {
        try {

            for (int i = 0; i < response.length(); i++) {
                JSONObject feedObj = (JSONObject) response.get(i);

                NotificationItem item = new NotificationItem();

                item.setId(feedObj.getString("id"));
                item.setProfilePic(feedObj.getString("user_image_url"));
                item.setUserName(feedObj.getString("user_name"));
                item.setTimeStamp(feedObj.getString("created_on"));
                item.setUserID(feedObj.getString("user_id"));
                item.setFlatOID(feedObj.getString("flat_owner_id"));
                item.setFlatOName(feedObj.getString("flat_owner_name"));
                item.setFlatOPic(feedObj.getString("flat_owner_image_url"));

                // Image might be null sometimes
                String image = feedObj.isNull("image_url") ? null : feedObj
                        .getString("image_url");
                item.setImage(image);

                notifications.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCouponInfo(final Context mContext) {

        String url = Controller.URLInitial + "rewards/redeem/?page=1";

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                nProgressDialog.dismiss();
                PrefUtils.setCouponInfo(response.toString());
                parseJsonFeedCoupon(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                nProgressDialog.dismiss();
                MyFootprintApplication.getInstance().trackEvent("GetCouponsError", "Server", "NotificationScreen");
                Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        stringObjReq.setRetryPolicy(Controller.DEFAULT_RETRY_POLICY);
        stringObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(stringObjReq);
        nProgressDialog = new ProgressDialog(getActivity());
        nProgressDialog.setMessage("Please Wait...");
        nProgressDialog.show();
    }

    private void parseJsonFeedCoupon(JSONObject response) {
        try {

            JSONArray redemptions = response.getJSONArray("redemptions");

            for (int i = 0; i < redemptions.length(); i++) {
                JSONObject feedObj = (JSONObject) redemptions.get(i);

                CouponItem item = new CouponItem();

                item.setCompanyName(feedObj.getString("company_name"));
                item.setCouponCode(feedObj.getString("code"));
                item.setIsGifted(feedObj.getBoolean("is_gifted"));
                item.setValidity(feedObj.getString("valid_till"));
                item.setCost(feedObj.getInt("cost"));
                item.setCouponId(feedObj.getString("coupon_id"));
                item.setCreatedOn(feedObj.getString("created_on"));
                item.setDescription(feedObj.getString("description"));
                item.setLink(feedObj.getString("link"));
                item.setLocation(feedObj.getString("location"));
                item.setOfferText(feedObj.getString("offer_text"));
                item.setPhoto(feedObj.getString("photo"));
                item.setRedeemedOn(feedObj.getString("redeemed_on"));
                item.setTandc(feedObj.getString("tandc"));


                notificationCouponItem.add(item);
            }

            mAdapter = new NotificationRecyclerViewAdapter(getActivity(), notifications, notificationCouponItem);
            mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

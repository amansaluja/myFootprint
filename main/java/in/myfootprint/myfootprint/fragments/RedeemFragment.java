package in.myfootprint.myfootprint.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import in.myfootprint.myfootprint.adapters.RedeemRecyclerViewAdapter;
import in.myfootprint.myfootprint.models.RedeemItem;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 11/17/2015.
 */

public class RedeemFragment extends Fragment{

    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;
    static RecyclerView.LayoutManager mLayoutManager;

    String[] title = new String[2];
    String[] buttonTitle = new String[2];

    static JSONArray residentCoupons;
    static JSONArray visitorCoupons;
    static JSONObject userCoupons;
    static JSONObject flatOwnerCoupons;
    ArrayList<RedeemItem> redeemItems_visitor;
    ArrayList<RedeemItem> redeemItems_resident;
    int fpNumberValue_visitor, fpNumberValue_resident;
    double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_redeem, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        title[0] = "FOOTPRINTS AVAILABLE FOR YOUR USE";
        title[1] = "FOOTPRINTS AVAILABLE TO GIFT YOUR PAL";
        buttonTitle[0] = "Gift yourself!";
        buttonTitle[1] = "Gift your pal!";

        redeemItems_visitor = new ArrayList<RedeemItem>();
        redeemItems_resident = new ArrayList<RedeemItem>();
        fpNumberValue_visitor = 0;
        fpNumberValue_resident = 0;

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RedeemRecyclerViewAdapter(getActivity(), title, buttonTitle, redeemItems_visitor, redeemItems_resident,
                fpNumberValue_visitor, fpNumberValue_resident);
        mRecyclerView.setAdapter(mAdapter);

        if (!PrefUtils.getLatlongCheckin().equals("")) {

            String[] latlong = PrefUtils.getLatlongCheckin().split(",");
            latitude = Double.parseDouble(latlong[0]);
            longitude = Double.parseDouble(latlong[1]);
            if (latitude == 0.0 || longitude == 0.0) {
                latitude = 24.3434;
                longitude = 71.3434;
            }
        }else{
            latitude = 24.3434;
            longitude = 71.3434;
        }
        getRedeemInfo(getActivity(), latitude, longitude);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Redeem Screen");
    }

    public void getRedeemInfo(final Context mContext, final Double latitude, final Double longitude) {

        String url = Controller.URLInitial + "rewards/coupon/?latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude);

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                parseJsonFeed(response);
                PrefUtilsNew.setRedeemInfo(response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                MyFootprintApplication.getInstance().trackEvent("RedeemCouponError", "Server", "RedeemScreen");
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
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            residentCoupons = response.getJSONArray("resident_coupons");
            visitorCoupons = response.getJSONArray("visitor_coupons");
            userCoupons = response.getJSONObject("user");
            flatOwnerCoupons = response.getJSONObject("flat_owner");

            for (int i = 0; i < residentCoupons.length(); i++) {
                JSONObject feedObj = (JSONObject) residentCoupons.get(i);

                RedeemItem item = new RedeemItem();

                item.setCompanyName(feedObj.getString("company_name"));
                item.setDescription(feedObj.getString("description"));
                item.setId(feedObj.getString("id"));
                item.setCost(String.valueOf(feedObj.get("cost")));
                item.setUsageLeft(String.valueOf(feedObj.get("usage_left")));
                item.setOfferText(feedObj.getString("offer_text"));
                item.setTandc(feedObj.getString("tandc"));
                item.setLink(feedObj.getString("link"));
                item.setLocation(feedObj.getString("location"));

                // Image might be null sometimes
                String image = feedObj.isNull("photo") ? null : feedObj
                        .getString("photo");
                item.setPhoto(image);

                redeemItems_resident.add(item);
            }

            for (int i = 0; i < visitorCoupons.length(); i++) {
                JSONObject feedObj = (JSONObject) visitorCoupons.get(i);

                RedeemItem item = new RedeemItem();

                item.setCompanyName(feedObj.getString("company_name"));
                item.setDescription(feedObj.getString("description"));
                item.setId(feedObj.getString("id"));
                item.setCost(String.valueOf(feedObj.get("cost")));
                item.setUsageLeft(String.valueOf(feedObj.get("usage_left")));
                item.setOfferText(feedObj.getString("offer_text"));
                item.setTandc(feedObj.getString("tandc"));
                item.setLink(feedObj.getString("link"));
                item.setLocation(feedObj.getString("location"));

                // Image might be null sometimes
                String image = feedObj.isNull("photo") ? null : feedObj
                        .getString("photo");
                item.setPhoto(image);

                redeemItems_visitor.add(item);
            }

            fpNumberValue_visitor = Integer.parseInt(String.valueOf(userCoupons.get("fp_left")));
            fpNumberValue_resident = Integer.parseInt(String.valueOf(flatOwnerCoupons.get("fp_left")));

            mAdapter = new RedeemRecyclerViewAdapter(getActivity(), title, buttonTitle, redeemItems_visitor, redeemItems_resident,
                    fpNumberValue_visitor, fpNumberValue_resident);
            mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package in.myfootprint.myfootprint.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.AddressActivity;
import in.myfootprint.myfootprint.activities.CheckinActivity;
import in.myfootprint.myfootprint.adapters.FeedRecyclerViewAdapter;
import in.myfootprint.myfootprint.adapters.RedeemRecyclerViewAdapter;
import in.myfootprint.myfootprint.data_adapters.FeedListAdapter;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.models.RedeemItem;
import in.myfootprint.myfootprint.utils.NetworkUtils;
import in.myfootprint.myfootprint.utils.PrefUtils;

/**
 * Created by Aman on 11/17/2015.
 */
public class FeedFragment extends Fragment{

    static JSONArray feedItems;
    FloatingActionButton fab;
    private ListView mRecyclerView;
    private FeedListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String message;
    ArrayList<FeedItem> listFeed;
    TextView welcomeMessage;
    String url;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_new, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                if( NetworkUtils.checkGPS(getActivity())){
                    if(!PrefUtils.getAddress().equals("")){
                        Intent i = new Intent(getActivity(), CheckinActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else{
                        buildAlertAddress(getActivity());
                    }
                }else{
                    buildAlertMessageNoGps(getActivity());
                }
            }
        });

        listFeed = new ArrayList<FeedItem>();
        url = Controller.URLInitial + "feed/";

        message = "Click on the floating action button below to check-in.";

        welcomeMessage = (TextView) view.findViewById(R.id.welcomeMessage);

        mRecyclerView = (ListView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        //mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FeedListAdapter(getActivity(), listFeed);
        mRecyclerView.setAdapter(mAdapter);

        getFeed(getActivity(), url);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Feed Screen");
    }

    public void getFeed(final Context mContext, String url) {

        JsonObjectRequest stringObjReq = new JsonObjectRequest (Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    feedItems = response.getJSONArray("feed");
                    if(feedItems.length() == 0){
                        welcomeMessage.setVisibility(View.VISIBLE);
                        welcomeMessage.setText(message);
                    }
                    parseJsonFeed(feedItems);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrefUtils.setFeedItems(feedItems.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                MyFootprintApplication.getInstance().trackEvent("GetFeedError", "Server", "FeedScreen");
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

    public void parseJsonFeed(JSONArray response) {
        try {

            for (int i = 0; i < response.length(); i++) {
                JSONObject feedObj = (JSONObject) response.get(i);

                FeedItem item = new FeedItem();

                item.setProfilePic(feedObj.getString("user_image_url"));
                item.setId(feedObj.getString("id"));
                item.setName(feedObj.getString("user_name"));
                item.setStatus(feedObj.getString("content"));
                item.setFlatOwner(feedObj.getString("flat_owner_name"));
                item.setTimeStamp(feedObj.getString("created_on"));
                //item.setImage(feedObj.getString("photo"));

                // Image might be null sometimes
                String image = feedObj.isNull("photo") ? null : feedObj
                        .getString("photo");
                item.setImage(image);

                listFeed.add(item);
            }

            if(mAdapter == null){
                mAdapter = new FeedListAdapter(getActivity(), listFeed);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void buildAlertMessageNoGps(final Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //gpsSwitch = 1;

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        //gpsSwitch = 0;
                        Toast.makeText(mContext, "Turn on GPS and try again", Toast.LENGTH_LONG).show();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buildAlertAddress(final Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("You cannot check-in without giving us your home location. Would you like to give your location now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        Intent i = new Intent(getActivity(), AddressActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}

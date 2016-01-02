package in.myfootprint.myfootprint.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.FeedRecyclerViewAdapter;
import in.myfootprint.myfootprint.adapters.PlaceRecyclerViewAdapter;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.utils.PrefUtils;

/**
 * Created by Aman on 11/26/2015.
 */
public class PlaceProfileFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<FeedItem> listFeed;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_place_profile, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        listFeed = new ArrayList<FeedItem>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(!PrefUtils.getFeedItems().equals("")){
            try {
                parseJsonFeed(new JSONArray(PrefUtils.getFeedItems()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            mAdapter = new PlaceRecyclerViewAdapter(getActivity(), listFeed);
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Place Profile Screen");
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

                // Image might be null sometimes
                /*String image = feedObj.isNull("photo") ? null : feedObj
                        .getString("photo");
                item.setImage(image);*/

                listFeed.add(item);
            }

            mAdapter = new PlaceRecyclerViewAdapter(getActivity(), listFeed);
            mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

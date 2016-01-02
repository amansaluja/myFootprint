package in.myfootprint.myfootprint.fragments;

/**
 * Created by Aman on 23-11-2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class ReviewAddressFragment extends Fragment {

    public String addressText = "";
    LinearLayout approveLayout;
    public String type;

    Double latitude, longitude;

    public ReviewAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review_address, container, false);
        setHasOptionsMenu(true);

        approveLayout = (LinearLayout) rootView.findViewById(R.id.approveLayout);

        addressText = PrefUtils.getAddress() + ".";

        approveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = "tenant";

                String[] latlong =  PrefUtils.getLatLong().split(",");
                latitude = Double.parseDouble(latlong[0]);
                longitude = Double.parseDouble(latlong[1]);
                String description = "Peace";
                String locality = "India";
                String address = PrefUtils.getAddress();
                String pincode = "111000";
                String photoLink = PrefUtilsNew.getSetAddressPhoto();
                String full_address = addressText;

                Controller.sendUserDetails(description, type, longitude, locality, latitude,
                        address, full_address, pincode, photoLink, getActivity());

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Review Address Screen");
    }

}
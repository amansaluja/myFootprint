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
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class ReviewCheckinFragment extends Fragment {

    LinearLayout approveLayout;

    public ReviewCheckinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review_checkin, container, false);
        setHasOptionsMenu(true);

        final String checkinID = PrefUtilsNew.getCheckInId();

        approveLayout = (LinearLayout) rootView.findViewById(R.id.approveLayout);
        approveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller.getCheckinStatus(getActivity(), checkinID);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Review CheckIn Screen");
    }

}
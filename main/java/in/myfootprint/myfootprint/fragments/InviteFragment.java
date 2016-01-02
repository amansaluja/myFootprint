package in.myfootprint.myfootprint.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.adapters.InviteRecyclerViewAdapter;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 11/17/2015.
 */
public class InviteFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String[] greetingText;
    String[] messageText;
    String[] regardsText;
    String userNameText;
    String buttonText;
    String[] shareOptions;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite, container, false);

        greetingText = new String[3];
        messageText = new String[3];
        regardsText = new String[3];

        PrefUtilsNew.setInviteText(1);
        /**
         * Message 1
         */
        greetingText[0] = "Dearly Beloved,";
        messageText[0] = "I have never really told you how eagerly I want you to come over to my place, and spend some quality time" +
                " eating my bananas and apples and pomegranates. Also, if you are really planning to come over, do furnish a" +
                " bottle of wine with cheese, because you hardly ever cared to pay the bills. Don't worry about the food," +
                " we'll order and discounts are on me! I shall wait for you with all my doors open. Come soon.";
        regardsText[0] = "With love and fruits.";
        userNameText = "Yours truly";

        /**
         * Message 2
         */
        greetingText[1] = "Hey,";
        messageText[1] = "It's been quite sometime since we chilled at my place. " +
                "Why don't you drop by sometime this week? I will see if I can get some " +
                "of our other friends over as well, and we can have a mini get-together of sorts. " +
                "The food is on me, you get the drinks. How does that sound?";
        regardsText[1] = "Looking forward,";
        userNameText = "Yours truly";

        /**
         * Message 3
         */
        greetingText[2] = "Yaara,";
        messageText[2] = "Bahot time ho gaya hai since we last met. Let's meet soon. " +
                "Aaja ghar pe. Let's have some beer-sher, pizza-wizza and gup-shup. " +
                "Tu aaja bas, I will arrange all the necessary coupons for food and drinks, " +
                "and we'll have a blast partying like we used to. Zyada bhav mat khaana, chup chaap aaja, " +
                "I will get you discounts on your return journey too. Yes, don't be surprised! Kuch " +
                "aisa samajh le meri lottery nikal padi hai. See you soon.";
        regardsText[2] = "Holding onto your coupons,";
        userNameText = "Yours truly";

        buttonText = "SEND OVER WHATSAPP!";
        shareOptions = new String[2];
        shareOptions[0] = "SEND OVER WHATSAPP!";

        JSONObject name = null;
        try {
            name = new JSONObject(PrefUtilsNew.getUserDetails());
            userNameText = name.get("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new RedeemRecyclerViewAdapter(getActivity(), title, buttonTitle, fpText, availableFP);
        mAdapter = new InviteRecyclerViewAdapter(getActivity(), greetingText, messageText, regardsText, userNameText, buttonText, shareOptions);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Invite Screen");
    }
}

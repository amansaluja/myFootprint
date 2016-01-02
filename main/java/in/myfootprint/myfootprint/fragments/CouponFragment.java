package in.myfootprint.myfootprint.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.models.CouponItem;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.views.FeedImageView;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.models.RedeemItem;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 11/17/2015.
 */
public class CouponFragment extends Fragment{

    FeedImageView logo;
    TextView companyName;
    TextView description;
    TextView terms;
    TextView places;
    TextView validity;
    LinearLayout gift;

    int giftWhom, couponCompany, cameFrom;
    JSONArray fullInfo;

    ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coupon_info, container, false);

        logo = (FeedImageView) view.findViewById(R.id.companyLogo);
        companyName = (TextView) view.findViewById(R.id.companyName);
        description = (TextView) view.findViewById(R.id.offerDescription);
        terms = (TextView) view.findViewById(R.id.tnc);
        places = (TextView) view.findViewById(R.id.places);
        validity = (TextView) view.findViewById(R.id.validity);
        gift = (LinearLayout) view.findViewById(R.id.giftButton);

        imageLoader = MyFootprintApplication.getInstance().getImageLoader();

        cameFrom = getActivity().getIntent().getExtras().getInt("CameFrom");
        couponCompany = getActivity().getIntent().getExtras().getInt("CouponCompany");

        if(cameFrom == 1){
            giftWhom = getActivity().getIntent().getExtras().getInt("GiftWhom");

            gift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            try {
                JSONObject getRedeemInfo = new JSONObject(PrefUtilsNew.getRedeemInfo());
                parseJsonFeed(getRedeemInfo, giftWhom, couponCompany);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            gift.setVisibility(View.GONE);

            try {
                JSONObject getRedeemInfo = new JSONObject(PrefUtils.getCouponInfo());
                parseJsonFeedCoupon(getRedeemInfo, couponCompany);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyFootprintApplication.getInstance().trackScreenView("Coupon Detail Screen");
    }

    private void parseJsonFeed(JSONObject response, int giftWhom, int couponCompany) {
        try {
            if(giftWhom == 0){
                fullInfo = response.getJSONArray("visitor_coupons");
            }else{
                fullInfo = response.getJSONArray("resident_coupons");
            }

            JSONObject feedObj = (JSONObject) fullInfo.get(couponCompany);

            RedeemItem item = new RedeemItem();

            item.setCompanyName(feedObj.getString("company_name"));
            item.setDescription(feedObj.getString("description"));
            item.setId(feedObj.getString("id"));
            item.setCost(String.valueOf(feedObj.get("cost")));
            item.setUsageLeft(String.valueOf(feedObj.get("usage_left")));
            item.setOfferText(feedObj.getString("offer_text"));
            item.setTandc(feedObj.getString("tandc"));
            item.setLocation(feedObj.getString("location"));
            item.setLink(feedObj.getString("link"));
            item.setValidity(feedObj.getString("valid_till"));

            // Image might be null sometimes
            String image = feedObj.isNull("photo") ? null : feedObj
                    .getString("photo");
            item.setPhoto(image);

            companyName.setText(item.getCompanyName());
            description.setText(item.getOfferText());
            terms.setText(Html.fromHtml(item.getTandc()));
            places.setText(item.getLocation());

            /*String parts[] = item.getValidity().split("T");
            String parts2[] = parts[1].split("\\+");

            String firstPart = parts[0];
            String secondPart = parts2[0];

            String time = Controller.parseDateToddMMyyyy(firstPart + " " + secondPart);*/

            String time = item.getValidity();
            validity.setText(time);

            if (item.getPhoto() != null) {
                logo.setImageUrl(item.getPhoto(), imageLoader);
                logo.setVisibility(View.VISIBLE);
                logo.setResponseObserver(new FeedImageView.ResponseObserver() {
                    @Override
                    public void onError() {
                    }
                    @Override
                    public void onSuccess() {
                    }
                });
            } else {
                logo.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonFeedCoupon(JSONObject response, int couponCompany) {
        try {

            JSONArray redemptions = response.getJSONArray("redemptions");

            JSONObject feedObj = (JSONObject) redemptions.get(couponCompany);

            CouponItem item = new CouponItem();

            item.setLink(feedObj.getString("link"));
            item.setDescription(feedObj.getString("description"));
            item.setRedeemedOn(feedObj.getString("redeemed_on"));
            item.setTandc(String.valueOf(feedObj.get("tandc")));

            item.setValidity(feedObj.getString("valid_till"));
            item.setOfferText(feedObj.getString("offer_text"));
            item.setCompanyName(feedObj.getString("company_name"));
            item.setCost(feedObj.getInt("cost"));
            item.setCouponCode(feedObj.getString("code"));
            item.setCouponId(feedObj.getString("coupon_id"));
            item.setCreatedOn(feedObj.getString("created_on"));
            item.setIsGifted(feedObj.getBoolean("is_gifted"));
            item.setLocation(feedObj.getString("location"));

            // Image might be null sometimes
            String image = feedObj.isNull("photo") ? null : feedObj
                    .getString("photo");
            item.setPhoto(image);

            companyName.setText(item.getCompanyName());
            description.setText(item.getOfferText());
            terms.setText(Html.fromHtml(item.getTandc()));
            places.setText(item.getLocation());

            /*String parts[] = item.getValidity().split("T");
            String parts2[] = parts[1].split("\\+");

            String firstPart = parts[0];
            String secondPart = parts2[0];

            String time = Controller.parseDateToddMMyyyy(firstPart + " " + secondPart);*/

            String time = item.getValidity();
            validity.setText(time);

            if (item.getPhoto() != null) {
                logo.setImageUrl(item.getPhoto(), imageLoader);
                logo.setVisibility(View.VISIBLE);
                logo.setResponseObserver(new FeedImageView.ResponseObserver() {
                    @Override
                    public void onError() {
                    }
                    @Override
                    public void onSuccess() {
                    }
                });
            } else {
                logo.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package in.myfootprint.myfootprint.data_adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import in.myfootprint.myfootprint.views.FeedImageView;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.ProfileActivity;
import in.myfootprint.myfootprint.models.RedeemItem;
import in.myfootprint.myfootprint.utils.PrefUtils;

public class CouponGridAdapter extends BaseAdapter {

    private Context activity;
    private LayoutInflater inflater;
    private List<RedeemItem> redeemItems;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();

    int positionCard;
    int numberFP;
    int[] numberChosen;
    private int mSelectedPosition = -1;
    RadioButton mSelectedRB;
    coupDetail selectedCoupon;

    public CouponGridAdapter(Context activity, List<RedeemItem> redeemItems, int positionCard, int numberFP) {
        this.activity = activity;
        this.redeemItems = redeemItems;
        this.positionCard = positionCard;
        this.numberFP = numberFP;

        numberChosen = new int[redeemItems.size()];

        /*PrefUtils.setAwardSelfPosition(1000);
        PrefUtils.setAwardPalPosition(1000);
        PrefUtils.setNumberFootprint(10000);*/

        selectedCoupon = new coupDetail();
    }

    @Override
    public int getCount() {
        return redeemItems.size();
    }

    @Override
    public Object getItem(int position) {
        return redeemItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null)
            rowView = inflater.inflate(R.layout.item_coupon, null);

        if (imageLoader == null)
            imageLoader = MyFootprintApplication.getInstance().getImageLoader();

        FeedImageView imageLogo = (FeedImageView) rowView.findViewById(R.id.companyLogo);
        ImageView aboutOffer = (ImageView) rowView.findViewById(R.id.about);
        final RadioButton txtAward = (RadioButton) rowView.findViewById(R.id.awardText);
        LinearLayout llPlusMinus = (LinearLayout) rowView.findViewById(R.id.plusminusLayout);
        ImageView minus = (ImageView) rowView.findViewById(R.id.minus);
        final TextView number = (TextView) rowView.findViewById(R.id.number);
        ImageView plus = (ImageView) rowView.findViewById(R.id.plus);

        if(positionCard == 1){
            llPlusMinus.setVisibility(View.GONE);
        }

        RedeemItem item = redeemItems.get(position);

        txtAward.setText(item.getOfferText());

        if (item.getPhoto() != null) {
            imageLogo.setImageUrl(item.getPhoto(), imageLoader);
            imageLogo.setVisibility(View.VISIBLE);
            imageLogo.setResponseObserver(new FeedImageView.ResponseObserver() {
                @Override
                public void onError() {
                }
                @Override
                public void onSuccess() {
                }
            });
        } else {
            imageLogo.setVisibility(View.GONE);
        }

        txtAward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(txtAward.isChecked()){
                    if (positionCard == 0) {
                        PrefUtils.setAwardSelfPosition(mSelectedPosition);
                        notifyDataSetChanged();
                    }else {
                        PrefUtils.setAwardPalPosition(mSelectedPosition);
                        notifyDataSetChanged();
                    }
                }
            }
        });

        txtAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetInvalidated();
                numberChosen = new int[redeemItems.size()];

                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                if (positionCard == 0) {
                    selectedCoupon.setCouponNameID(mSelectedPosition);
                    PrefUtils.setAwardSelfPosition(mSelectedPosition);

                    number.setText(String.valueOf(0));

                    PrefUtils.setNumberFootprint(numberChosen[mSelectedPosition]);
                    notifyDataSetChanged();
                } else {
                    PrefUtils.setAwardPalPosition(mSelectedPosition);
                    notifyDataSetChanged();
                }

                mSelectedRB = (RadioButton) v;
            }
        });

        if(mSelectedPosition != position){
            txtAward.setChecked(false);
        }else{
            txtAward.setChecked(true);

            //PrefUtils.setAwardSelfPosition(position);
            if(mSelectedRB != null && txtAward != mSelectedRB){
                mSelectedRB = txtAward;
            }
        }

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedPosition != position){
                    Toast.makeText(activity, "Please select this coupon first", Toast.LENGTH_SHORT).show();
                }else {
                    if(numberFP == 0){
                        Toast.makeText(activity, "You have zero footprints. Please check-in at a friend's place.", Toast.LENGTH_SHORT).show();

                    } else if (numberChosen[position] < numberFP) {
                        numberChosen[position]++;
                        number.setText(String.valueOf(numberChosen[position]));
                        selectedCoupon.setCouponNumber(numberChosen[position]);
                        PrefUtils.setNumberFootprint(numberChosen[mSelectedPosition]);
                    }
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedPosition != position){
                    Toast.makeText(activity, "Please select this coupon first", Toast.LENGTH_SHORT).show();
                }else {
                    if (numberChosen[position] > 0) {
                        numberChosen[position]--;
                        number.setText(String.valueOf(numberChosen[position]));
                        selectedCoupon.setCouponNumber(numberChosen[position]);
                        PrefUtils.setNumberFootprint(numberChosen[mSelectedPosition]);
                    }
                }
            }
        });

        aboutOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent j = new Intent(activity, ProfileActivity.class);
                j.putExtra("FragmentNumber", "4");
                j.putExtra("CameFrom", 1);
                j.putExtra("GiftWhom", positionCard);
                j.putExtra("CouponCompany", position);
                activity.startActivity(j);
            }
        });

        return rowView;
    }

    public class coupDetail{
        int couponNameID;
        String couponName;
        int couponNumber;

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public int getCouponNameID() {
            return couponNameID;
        }

        public void setCouponNameID(int couponNameID) {
            this.couponNameID = couponNameID;
        }

        public int getCouponNumber() {
            return couponNumber;
        }

        public void setCouponNumber(int couponNumber) {
            this.couponNumber = couponNumber;
        }
    }
}
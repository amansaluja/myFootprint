package in.myfootprint.myfootprint.adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.MainActivity;
import in.myfootprint.myfootprint.data_adapters.CouponGridAdapter;
import in.myfootprint.myfootprint.models.RedeemItem;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class RedeemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    static String[] title;
    static String[] buttonTitle;
    String buttonText;

    static CouponGridAdapter adapterResident;
    static CouponGridAdapter adapterVisitor;
    List<RedeemItem> redeemItems_visitor;
    List<RedeemItem> redeemItems_resident;
    static int fpNumberValue_visitor;
    static int fpNumberValue_resident;

    static TextView fpTitle;
    static TextView giftButton;
    static TextView fpNumber;
    static GridView couponGrid;
    static LinearLayout giftButtonLayout;

    static TextView fpTitle1;
    static TextView giftButton1;
    static TextView fpNumber1;
    static GridView couponGrid1;
    static LinearLayout giftButtonLayout1;
    View view, view1;

    JSONObject getRedeemInfo;
    JSONArray fullInfo;
    static ProgressDialog mProgressDialog;

    private static String LOG_TAG = "RedeemRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder(View itemView) {
            super(itemView);
            fpTitle = (TextView) itemView.findViewById(R.id.fpTitle);
            giftButton = (TextView) itemView.findViewById(R.id.giftText);
            fpNumber = (TextView) itemView.findViewById(R.id.fpNumber);
            couponGrid = (GridView) itemView.findViewById(R.id.gridCoupons);
            giftButtonLayout = (LinearLayout) itemView.findViewById(R.id.giftButton);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public class DataObjectHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder1(View itemView) {
            super(itemView);
            fpTitle1 = (TextView) itemView.findViewById(R.id.fpTitle);
            giftButton1 = (TextView) itemView.findViewById(R.id.giftText);
            fpNumber1 = (TextView) itemView.findViewById(R.id.fpNumber);
            couponGrid1 = (GridView) itemView.findViewById(R.id.gridCoupons);
            giftButtonLayout1 = (LinearLayout) itemView.findViewById(R.id.giftButton);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RedeemRecyclerViewAdapter(Context context, String[] title, String[] buttonTitle, List<RedeemItem> redeemItems_visitor,
                                     List<RedeemItem> redeemItems_resident, int fpNumberValue_visitor, int fpNumberValue_resident){

        this.context = context;
        this.title = title;
        this.buttonTitle = buttonTitle;
        this.redeemItems_visitor = redeemItems_visitor;
        this.redeemItems_resident = redeemItems_resident;
        this.fpNumberValue_visitor = fpNumberValue_visitor;
        this.fpNumberValue_resident = fpNumberValue_resident;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_redeem, parent, false);

        view1 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_redeem, parent, false);


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        DataObjectHolder1 dataObjectHolder1 = new DataObjectHolder1(view1);

        switch (viewType) {
            case 0:return dataObjectHolder;
            case 1: return dataObjectHolder1;
            default: return dataObjectHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        switch (position) {
            case 0:

                fpTitle.setText(title[position]);
                giftButton.setText(buttonTitle[position]);
                fpNumber.setText(String.valueOf(fpNumberValue_visitor));

                adapterVisitor = new CouponGridAdapter(context, redeemItems_visitor, position, fpNumberValue_visitor);
                couponGrid.setAdapter(adapterVisitor);

                int length = redeemItems_visitor.size();
                int height;

                if(length%2 == 1){
                    height = Controller.convertDpToPixels(168, context) * (length/2 + 1);
                }else{
                    height = Controller.convertDpToPixels(168, context) * (length/2);
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height, 1f);
                couponGrid.setLayoutParams(lp);

                giftButtonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(PrefUtils.getAwardSelfPosition() == 1000){
                            Toast.makeText(context, "Please select a coupon", Toast.LENGTH_SHORT).show();
                        }else if(PrefUtils.getNumberFootprint() == 10000){
                            Toast.makeText(context, "Please select number of footprints to redeem", Toast.LENGTH_SHORT).show();
                        }else if(PrefUtils.getNumberFootprint() == 0){
                            Toast.makeText(context, "Please select a number greater than 0", Toast.LENGTH_SHORT).show();
                        } else{
                            giftCoupon(1, PrefUtils.getAwardSelfPosition(), PrefUtils.getNumberFootprint());
                        }
                    }
                });

                break;
            case 1:

                fpTitle1.setText(title[position]);
                if(!PrefUtilsNew.getCHECKIn_HOST().equals("")){
                    if(PrefUtilsNew.getCHECKIn_HOST().equals("Invite them on Whatsapp.") || PrefUtilsNew.getCHECKIn_HOST().equals("No user found.")){
                        buttonText = "Gift your pal!";
                    }else{
                        buttonText = "Gift " + PrefUtilsNew.getCHECKIn_HOST();
                    }
                }else{
                    buttonText = "Gift your pal!";
                }
                giftButton1.setText(buttonText);
                fpNumber1.setText(String.valueOf(fpNumberValue_resident));

                adapterResident = new CouponGridAdapter(context, redeemItems_resident, position, fpNumberValue_resident);
                couponGrid1.setAdapter(adapterResident);

                int length1 = redeemItems_resident.size();
                int height1;

                if(length1%2 == 1){
                    height1 = Controller.convertDpToPixels(133, context) * (length1/2 + 1);
                }else{
                    height1 = Controller.convertDpToPixels(133, context) * (length1/2);
                }

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height1, 1f);
                couponGrid1.setLayoutParams(lp1);

                giftButtonLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PrefUtils.getAwardPalPosition() == 1000) {
                            Toast.makeText(context, "Please select a coupon", Toast.LENGTH_SHORT).show();
                        }else{
                            giftCoupon(2, PrefUtils.getAwardPalPosition(), 1);
                        }
                    }
                });

                break;
        }
    }

    public void deleteItem(int index) {
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }

    private void giftCoupon(int giftWhom, int couponCompany, int numberCoupons) {
        try {
            getRedeemInfo = new JSONObject(PrefUtilsNew.getRedeemInfo());

            if(giftWhom == 1){
                fullInfo = getRedeemInfo.getJSONArray("visitor_coupons");
            }else{
                fullInfo = getRedeemInfo.getJSONArray("resident_coupons");
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

            // Image might be null sometimes
            String image = feedObj.isNull("photo") ? null : feedObj
                    .getString("photo");
            item.setPhoto(image);
            Toast.makeText(context, "Gift " + String.valueOf(numberCoupons) + " coupon(s) of " + item.getCompanyName(), Toast.LENGTH_SHORT).show();

            sendRedeemSelection(giftWhom, item.getId(), numberCoupons, context);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendRedeemSelection(final int GiftWhom, final String companyID, final int number, final Context mContext) {

        String url = Controller.URLInitial + "rewards/redeem/";

        JSONObject params3 = new JSONObject();
        JSONObject params2 = new JSONObject();
        JSONObject params1 = new JSONObject();

        try {

            params3.put(companyID, number);
            if(GiftWhom == 1){
                params2.put("visitor_coupons", params3);
            }else{
                params2.put("resident_coupons", params3);
            }

            params1.put("data", params2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }

                if(GiftWhom == 1){
                    MyFootprintApplication.getInstance().trackEvent("Redeemed", "Self", "RedeemScreen");
                    Toast.makeText(mContext, "Successfully redeemed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(mContext, MainActivity.class);
                    intent.putExtra("InviteTab", 1);
                    mContext.startActivity(intent);

                }else {
                    MyFootprintApplication.getInstance().trackEvent("Gifted", "Friend", "RedeemScreen");
                    Toast.makeText(mContext, "Successfully gifted!", Toast.LENGTH_SHORT).show();
                    fpNumberValue_resident = fpNumberValue_resident - 1;
                    fpNumber1.setText(String.valueOf(fpNumberValue_resident));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(mProgressDialog != null){
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
                MyFootprintApplication.getInstance().trackEvent("SendRedeemError", "Server", "RedeemScreen");
                Toast.makeText(mContext, "You have zero footprints now. " +
                        "Check-in at your friend's place to collect footprints.", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token " + PrefUtils.getUserAccess());
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(Controller.DEFAULT_RETRY_POLICY);
        jsonObjReq.setShouldCache(false);
        MyFootprintApplication.getInstance().addToRequestQueue(jsonObjReq);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
    }
}
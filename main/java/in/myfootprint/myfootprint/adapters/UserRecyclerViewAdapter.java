package in.myfootprint.myfootprint.adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.AddressActivity;
import in.myfootprint.myfootprint.data_adapters.UserPlaceListAdapter;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    static LinearLayout addNewHome;
    static TextView userCaption;
    static ListView userActivity;
    static GridView userFriend;
    static TextView homeNumber;
    static TextView ownershipStatus;
    static TextView homeAddress;

    ArrayList<FeedItem> feedList;
    ArrayList<FeedItem> feedList_new;

    JSONArray userDetailArray;
    String getDescription;

    JSONArray serverResults;
    String statusType;
    String addressText;

    private static String LOG_TAG = "UserRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder(View itemView) {
            super(itemView);

            homeNumber = (TextView) itemView.findViewById(R.id.homeNo);
            ownershipStatus = (TextView) itemView.findViewById(R.id.status);
            homeAddress = (TextView) itemView.findViewById(R.id.address);

            addNewHome = (LinearLayout) itemView.findViewById(R.id.addNewHome);
            userCaption = (TextView) itemView.findViewById(R.id.userMessage);
            /*userAddress = (GridView) itemView.findViewById(R.id.gridAddress);*/
            //userAddress.setNumColumns();

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public static class DataObjectHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder1(View itemView) {
            super(itemView);

            userActivity = (ListView) itemView.findViewById(R.id.list_user_activity);

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

    public UserRecyclerViewAdapter(Context context, JSONArray serverResults, ArrayList<FeedItem> feedList) {

        this.context = context;
        this.serverResults = serverResults;
        this.feedList = feedList;

        feedList_new = new ArrayList<FeedItem>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_user_history, parent, false);

        View view1 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_user_activity, parent, false);

        /*View view2 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_user_friend, parent, false);*/

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        DataObjectHolder1 dataObjectHolder1 = new DataObjectHolder1(view1);
        //DataObjectHolder2 dataObjectHolder2 = new DataObjectHolder2(view2);

        switch (viewType) {
            case 0:return dataObjectHolder;
            case 1: return dataObjectHolder1;
            //case 2: return dataObjectHolder2;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (position) {
            case 0:
                addNewHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrefUtilsNew.clearspecific();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder
                                .setTitle("Update Address")
                                .setMessage("Please wait for 15 days since first login before updating the address.")
                                .setCancelable(true)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /*PrefUtils.setAddress("");
                                        Intent j = new Intent(context, AddressActivity.class);
                                        j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(j);*/
                                        dialog.dismiss();
                                    }
                                })
                                /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })*/;

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
                try {
                    userDetailArray = new JSONArray(PrefUtilsNew.getUserDetailsFull());
                    getDescription = userDetailArray.getJSONObject(0).get("description").toString();
                    statusType = serverResults.getJSONObject(0).get("user_type").toString();
                    addressText = serverResults.getJSONObject(0).getJSONObject("flat").get("locality").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ownershipStatus.setText(statusType);
                homeAddress.setText(addressText);

                userCaption.setText(getDescription);

                break;
            case 1:

                for(int i = 0; i < feedList.size(); i++){
                    final FeedItem item = feedList.get(i);
                    String userName = item.getName();
                    if (userName.equals("You")) {
                        feedList_new.add(item);
                    }
                }

                int length1 = feedList_new.size();
                int height1;
                int number = 2;

                height1 = Controller.convertDpToPixels(60, context) * length1;

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height1, 1f);
                userActivity.setLayoutParams(lp1);

                userActivity.setAdapter(new UserPlaceListAdapter(context, feedList_new, number));

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
}
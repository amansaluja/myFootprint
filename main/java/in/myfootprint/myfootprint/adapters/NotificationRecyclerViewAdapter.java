package in.myfootprint.myfootprint.adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.data_adapters.NotificationCouponListAdapter;
import in.myfootprint.myfootprint.data_adapters.NotificationListAdapter;
import in.myfootprint.myfootprint.models.CouponItem;
import in.myfootprint.myfootprint.models.NotificationItem;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    static ListView notifications;
    static ListView notifications_coupon;

    List<NotificationItem> notificationListItem;
    List<CouponItem> notificationCouponItem;

    private static String LOG_TAG = "FeedRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder(View itemView) {
            super(itemView);

            notifications = (ListView) itemView.findViewById(R.id.list_feed_notification);

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

            notifications_coupon = (ListView) itemView.findViewById(R.id.list_notification_coupon);

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

    public NotificationRecyclerViewAdapter(Context context, List<NotificationItem> notificationListItem,
                                           List<CouponItem> notificationCouponItem) {

        this.context = context;
        this.notificationListItem = notificationListItem;
        this.notificationCouponItem = notificationCouponItem;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_notification, parent, false);

        View view1 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_notification_coupon, parent, false);


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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (position) {
            case 0:

                int length = notificationListItem.size();
                int height = 0;

                for(int i = 0; i < length; i++) {
                    NotificationItem item = notificationListItem.get(i);
                    if (item.getImage() == null || item.getImage().equals("")) {
                        height = height + Controller.convertDpToPixels(117, context);
                    } else {
                        height = height + Controller.convertDpToPixels(255, context);
                    }
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height, 1f);
                notifications.setLayoutParams(lp);
                notifications.setAdapter(new NotificationListAdapter(context, notificationListItem));

                break;
            case 1:

                int length1 = notificationCouponItem.size();
                int height1;

                height1 = Controller.convertDpToPixels(178, context) * length1;

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height1, 1f);
                notifications_coupon.setLayoutParams(lp1);
                notifications_coupon.setAdapter(new NotificationCouponListAdapter(context, notificationCouponItem));

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
package in.myfootprint.myfootprint.adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.network.FetchFBPhoto;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.fragments.FeedFragment;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.views.FeedImageView;
import in.myfootprint.myfootprint.views.RoundedImageView;
import in.myfootprint.myfootprint.views.RoundedNetworkImageView;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.DataObjectHolder> {

    private Context context;

    ArrayList<FeedItem> feedList;
    String statusMessage;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();

    private static String LOG_TAG = "FeedRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NetworkImageView profileImage;
        TextView feedMessage;
        TextView feedTime;
        //GridView imageGrid;
        //ImageView imageView;
        NetworkImageView imageView;

        public DataObjectHolder(View itemView) {
            super(itemView);

            profileImage = (NetworkImageView) itemView.findViewById(R.id.profileImage);
            feedMessage = (TextView) itemView.findViewById(R.id.feedMessage);
            feedTime = (TextView) itemView.findViewById(R.id.feedTime);
            imageView = (NetworkImageView) itemView.findViewById(R.id.feedImage);
            //imageGrid = (GridView)  itemView.findViewById(R.id.gridImages);

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

    public FeedRecyclerViewAdapter(Context context, ArrayList<FeedItem> feedList) {

        this.context = context;
        this.feedList = feedList;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_feed, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        if (imageLoader == null)
            imageLoader = MyFootprintApplication.getInstance().getImageLoader();

        String userName = feedList.get(position).getName();
        String flatOwner = feedList.get(position).getFlatOwner();

        if(userName.equals("You")){
            statusMessage = "<b>You</b>" +  " checked in at " + "<b>" + flatOwner + "'s place." + "</b>";
        }else{
            statusMessage = "<b>" + userName + "</b>" + " checked in at " + "<b>your place.</b>";
        }

        holder.feedMessage.setText(Html.fromHtml(statusMessage));
        holder.feedTime.setText(feedList.get(position).getTimeStamp());

        holder.profileImage.setImageUrl(feedList.get(position).getProfilePic(), imageLoader);

        String imageURL = feedList.get(position).getImage();

        int height;
        if (imageURL == null || imageURL.equals("")) {
            height = Controller.convertDpToPixels(50, context);
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            height = Controller.convertDpToPixels(165, context);

            imageLoader.get(imageURL, ImageLoader.getImageListener(holder.imageView,
                    R.color.white, R.color.white));
            holder.imageView.setImageUrl(imageURL, imageLoader);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height, 1f);
        holder.imageView.setLayoutParams(lp);

    }

    public void deleteItem(int index) {
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }
}
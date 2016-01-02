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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.data_adapters.UserPlaceListAdapter;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;
import in.myfootprint.myfootprint.views.FeedImageView;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    String caption, imageURL;
    ArrayList<FeedItem> feedList;
    ArrayList<FeedItem> feedList_new;

    static NetworkImageView imageView;
    static TextView captionView;

    static ListView placeCheckin;
    static TextView loadMore;

    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();

    JSONArray userDetailArray;

    private static String LOG_TAG = "PlaceRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder(View itemView) {
            super(itemView);

            imageView = (NetworkImageView) itemView.findViewById(R.id.placeImage);
            captionView = (TextView) itemView.findViewById(R.id.placeMessage);

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

            placeCheckin = (ListView) itemView.findViewById(R.id.list_place_checkin);
            loadMore = (TextView) itemView.findViewById(R.id.loadMore);

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

    public PlaceRecyclerViewAdapter(Context context, ArrayList<FeedItem> feedList) {

        this.context = context;
        this.feedList = feedList;
        feedList_new = new ArrayList<FeedItem>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_place_image, parent, false);

        View view1 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_place_checkin, parent, false);

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

                try {
                    userDetailArray = new JSONArray(PrefUtilsNew.getUserDetailsFull());
                    caption = userDetailArray.getJSONObject(0).get("description").toString();
                    imageURL = userDetailArray.getJSONObject(0).get("image_url").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imageLoader = MyFootprintApplication.getInstance().getImageLoader();

                if (imageURL == null || imageURL.equals("")) {
                    /*imageView.setImageUrl(imageURL, imageLoader);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });*/
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    imageLoader.get(imageURL, ImageLoader.getImageListener(imageView,
                            R.color.white, R.color.white));
                    imageView.setImageUrl(imageURL, imageLoader);
                }

                captionView.setText(caption);

                break;
            case 1:

                for(int i = 0; i < feedList.size(); i++){
                    final FeedItem item = feedList.get(i);
                    String userName = item.getName();
                    if (!userName.equals("You")) {
                        feedList_new.add(item);
                    }
                }

                int length1 = feedList_new.size();
                int height1;
                int number = 1;

                height1 = Controller.convertDpToPixels(60, context) * length1;

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, height1, 1f);
                placeCheckin.setLayoutParams(lp1);

                placeCheckin.setAdapter(new UserPlaceListAdapter(context, feedList_new, number));
                loadMore.setVisibility(View.GONE);
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
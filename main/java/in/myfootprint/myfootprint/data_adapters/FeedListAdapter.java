package in.myfootprint.myfootprint.data_adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.models.FeedItem;

public class FeedListAdapter extends BaseAdapter {

    //private Context context;

    private Activity activity;
    private LayoutInflater inflater;

    List<FeedItem> feedList;
    String statusMessage;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();
    ViewHolder holder;

    private static String LOG_TAG = "FeedRecyclerViewAdapter";

    public FeedListAdapter(Activity activity, List<FeedItem> feedList) {

        this.activity = activity;
        this.feedList = feedList;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        holder = new ViewHolder();

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_feed, null);

            holder.profileImage = (NetworkImageView) convertView.findViewById(R.id.profileImage);
            holder.feedMessage = (TextView) convertView.findViewById(R.id.feedMessage);
            holder.feedTime = (TextView) convertView.findViewById(R.id.feedTime);
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.feedImage);

            holder.profileImage.setTag(position);
            holder.feedMessage.setTag(position);
            holder.feedTime.setTag(position);
            holder.imageView.setTag(position);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        FeedItem item = feedList.get(position);

        String userName = item.getName();
        String flatOwner = item.getFlatOwner();

        if(userName.equals("You")){
            statusMessage = "<b>You</b>" +  " checked in at " + "<b>" + flatOwner + "'s place." + "</b>";
        }else{
            statusMessage = "<b>" + userName + "</b>" + " checked in at " + "<b>your place.</b>";
        }

        holder.feedMessage.setText(Html.fromHtml(statusMessage));
        holder.feedTime.setText(item.getTimeStamp());

        holder.profileImage.setImageUrl(item.getProfilePic(), imageLoader);

        String imageURL = feedList.get(position).getImage();
        if (item.getImage() == null || item.getImage().equals("")) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageUrl(imageURL, imageLoader);
        }

        return convertView;
    }

    private class ViewHolder {
        NetworkImageView profileImage;
        TextView feedMessage;
        TextView feedTime;
        NetworkImageView imageView;
    }
}
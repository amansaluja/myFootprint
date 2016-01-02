package in.myfootprint.myfootprint.data_adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.models.FeedItem;
import in.myfootprint.myfootprint.network.FetchFBPhoto;

public class UserPlaceListAdapter extends BaseAdapter {

    private Context context;

    LayoutInflater inflater;
    List<FeedItem> feedList;
    String statusMessage;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();
    int number;

    public UserPlaceListAdapter(Context context, List<FeedItem> feedList, int number) {

        this.context = context;
        this.feedList = feedList;
        this.number = number;
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
    public View getView(int position, View rowView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null)
            rowView = inflater.inflate(R.layout.item_checkin_message, null, true);

        //View rowView= inflater.inflate(R.layout.item_checkin_message, null, true);

        final NetworkImageView imageView = (NetworkImageView) rowView.findViewById(R.id.profileImage);
        TextView txtMessage = (TextView) rowView.findViewById(R.id.notifMessage);
        TextView txtTime = (TextView) rowView.findViewById(R.id.notifTime);

        final FeedItem item = feedList.get(position);
        String userName = item.getName();
        String flatOwner = item.getFlatOwner();

        statusMessage = "";
        if (userName.equals("You")) {
            statusMessage = "<b>You</b>" + " checked in at " + "<b>" + flatOwner + "'s place." + "</b>";
        } else {
            statusMessage = "<b>" + userName + "</b>" + " checked in at " + "<b>your place.</b>";
        }

        txtMessage.setText(Html.fromHtml(statusMessage));
        txtTime.setText(item.getTimeStamp());

        /*String fbparts[] = item.getProfilePic().split("com/");
        String fbparts2[] = fbparts[1].split("/pic");

        String fbUserID = fbparts2[0];
        FetchFBPhoto asyncTask = (FetchFBPhoto) new FetchFBPhoto(context, fbUserID, new FetchFBPhoto.AsyncResponse() {

            @Override
            public void processFinish(Bitmap output) {

                imageView.setImageBitmap(output);
            }
        }).execute();*/
        imageView.setImageUrl(item.getProfilePic(), imageLoader);

        return rowView;
    }
}
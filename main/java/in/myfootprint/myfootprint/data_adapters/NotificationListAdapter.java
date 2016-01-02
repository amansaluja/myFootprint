package in.myfootprint.myfootprint.data_adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.views.FeedImageView;
import in.myfootprint.myfootprint.network.FetchFBPhoto;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.models.NotificationItem;

public class NotificationListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();

    List<NotificationItem> notificationListItem;
    private String notifMessage;
    ArrayList<String> checkInID;

    public NotificationListAdapter(Context context, List<NotificationItem> notificationListItem) {

        this.context = context;
        this.notificationListItem = notificationListItem;
        checkInID = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return notificationListItem.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null)
            rowView= inflater.inflate(R.layout.item_notifications, null, true);

        /*if (imageLoader == null)
            imageLoader = MyFootprintApplication.getInstance().getImageLoader();*/

        final NetworkImageView profileImage = (NetworkImageView) rowView.findViewById(R.id.profileImage);
        TextView notificationMessage = (TextView) rowView.findViewById(R.id.notifMessage);
        TextView notificationTime = (TextView) rowView.findViewById(R.id.notifTime);
        final NetworkImageView notificationImage1 = (NetworkImageView) rowView.findViewById(R.id.image1);

        LinearLayout approve = (LinearLayout) rowView.findViewById(R.id.approve);
        LinearLayout reject = (LinearLayout) rowView.findViewById(R.id.reject);

        NotificationItem item = notificationListItem.get(position);

        checkInID.add(item.getId());
        notifMessage = "<b>" + item.getUserName() + "</b>" +
                " checked in at your " + "<b>" + "place." + "</b>";


        /*String fbparts[] = item.getProfilePic().split("com/");
        String fbparts2[] = fbparts[1].split("/pic");

        String fbUserID = fbparts2[0];
        new FetchFBPhoto(context, fbUserID, new FetchFBPhoto.AsyncResponse() {

            @Override
            public void processFinish(Bitmap output) {

                profileImage.setImageBitmap(output);
            }
        }).execute();*/
        profileImage.setImageUrl(item.getProfilePic(), imageLoader);

        notificationMessage.setText(Html.fromHtml(notifMessage));
        /*String parts[] = item.getTimeStamp().split("T");
        String parts2[] = parts[1].split("\\+");

        String firstPart = parts[0];
        String secondPart = parts2[0];

        String time = Controller.parseDateToddMMyyyy(firstPart + " " + secondPart);*/

        String time = item.getTimeStamp();
        notificationTime.setText(time);

        /*if (imageLoader == null)
            imageLoader = MyFootprintApplication.getInstance().getImageLoader();
        if (item.getImage() != null) {
            notificationImage1.setImageUrl(item.getImage(), imageLoader);
            notificationImage1.setVisibility(View.VISIBLE);
            notificationImage1.setResponseObserver(new FeedImageView.ResponseObserver() {
                @Override
                public void onError() {
                }

                @Override
                public void onSuccess() {
                }
            });
        } else {
            notificationImage1.setVisibility(View.GONE);
        }*/

        ////imageLoader = MyFootprintApplication.getInstance().getImageLoader();
        if (item.getImage() == null || item.getImage().equals("")) {

            notificationImage1.setVisibility(View.GONE);

        } else {
            notificationImage1.setVisibility(View.VISIBLE);
            imageLoader.get(item.getImage(), ImageLoader.getImageListener(notificationImage1,
                    R.color.white, R.color.white));
            notificationImage1.setImageUrl(item.getImage(), imageLoader);
        }

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller.sendCheckinApproval(context, "true", checkInID.get(position));
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller.sendCheckinApproval(context, "false", checkInID.get(position));
            }
        });

        return rowView;
    }
}
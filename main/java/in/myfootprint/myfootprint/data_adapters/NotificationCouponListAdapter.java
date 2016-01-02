package in.myfootprint.myfootprint.data_adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.ProfileActivity;
import in.myfootprint.myfootprint.models.CouponItem;

public class NotificationCouponListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();

    List<CouponItem> notificationCouponItem;
    private String notifMessage;
    String offerCode;

    public NotificationCouponListAdapter(Context context, List<CouponItem> notificationCouponItem) {

        this.context = context;
        this.notificationCouponItem = notificationCouponItem;
    }

    @Override
    public int getCount() {
        return notificationCouponItem.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationCouponItem.get(position);
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
            rowView= inflater.inflate(R.layout.item_coupon_code, null, true);

        if (imageLoader == null)
            imageLoader = MyFootprintApplication.getInstance().getImageLoader();

        TextView couponMessage = (TextView) rowView.findViewById(R.id.couponMessage);
        TextView couponValidity = (TextView) rowView.findViewById(R.id.couponValidity);
        ImageView aboutOffer = (ImageView) rowView.findViewById(R.id.about);
        final TextView couponCode = (TextView) rowView.findViewById(R.id.couponCode);
        TextView copyCode = (TextView) rowView.findViewById(R.id.copyCode);
        TextView goToApp = (TextView) rowView.findViewById(R.id.gotoApp);

        final CouponItem item = notificationCouponItem.get(position);
        notifMessage = "Here is your unique " + "<b>" + item.getCompanyName() + " Coupon Code " + "</b>";

        couponMessage.setText(Html.fromHtml(notifMessage));

        /*String parts[] = item.getValidity().split("T");
        String parts2[] = parts[1].split("\\+");

        String firstPart = parts[0];
        String secondPart = parts2[0];

        String time = Controller.parseDateToddMMyyyy(firstPart + " " + secondPart);*/
        String time = item.getValidity();
        couponValidity.setText("Valid till " + time);

        offerCode = item.getCouponCode();

        couponCode.setText(offerCode);

        final int sdk = android.os.Build.VERSION.SDK_INT;
        copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(couponCode.getText());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Coupon Code", couponCode.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(context, "Coupon code copied", Toast.LENGTH_SHORT).show();
                MyFootprintApplication.getInstance().trackEvent("Code Copied", "Coupon", "NotificationScreen");
            }
        });

        final android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);


        goToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.goToUrl(item.getLink(), context);
                MyFootprintApplication.getInstance().trackEvent("Go To App", "To app", "NotificationScreen");
            }
        });

        aboutOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent j = new Intent(context, ProfileActivity.class);
                j.putExtra("FragmentNumber", "4");
                j.putExtra("CameFrom", 2);
                j.putExtra("CouponCompany", position);
                context.startActivity(j);
            }
        });

        return rowView;
    }
}
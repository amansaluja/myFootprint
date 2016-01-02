package in.myfootprint.myfootprint.adapters;

/**
 * Created by Aman on 20-11-2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class InviteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    static int loop;
    String[] shareOptions;
    String gText, mText, rText;
    String completeMessage;

    String[] greetingText;
    String[] messageText;
    String[] regardsText;
    String userNameText;
    String buttonText;

    static TextView greeting;
    static TextView inviteText;
    static TextView withLove;
    static TextView nameUser;
    static Button inviteButton;
    static ImageView right;
    static ImageView left;

    View view;

    static ListView listViewInvitees;
    static ListView listViewInvited;

    private static String LOG_TAG = "InviteRecyclerViewAdapter";

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DataObjectHolder(View itemView) {
            super(itemView);

            greeting = (TextView) itemView.findViewById(R.id.greeting);
            inviteText = (TextView) itemView.findViewById(R.id.inviteText);
            withLove = (TextView) itemView.findViewById(R.id.withLove);
            nameUser = (TextView) itemView.findViewById(R.id.nameUser);
            inviteButton = (Button) itemView.findViewById(R.id.inviteButton);
            right = (ImageView) itemView.findViewById(R.id.right);
            left = (ImageView) itemView.findViewById(R.id.left);

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

    public InviteRecyclerViewAdapter(Context context, String greetingText[], String messageText[],
                                     String regardsText[], String userNameText, String buttonText, String[] shareOptions) {

        this.context = context;
        this.greetingText = greetingText;
        this.messageText = messageText;
        this.regardsText = regardsText;
        this.userNameText = userNameText;
        this.buttonText = buttonText;
        this.shareOptions = shareOptions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_invite, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        switch (viewType) {
            case 0:return dataObjectHolder;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (position) {
            case 0:
                loop = PrefUtilsNew.getInviteText();

                nameUser.setText(userNameText);
                inviteButton.setText("SEND OVER WHATSAPP!");

                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loop < messageText.length-1) {
                            loop++;
                        }else if (loop == messageText.length -1){
                            loop = 0;
                        }
                        PrefUtilsNew.setInviteText(loop);
                        notifyDataSetChanged();
                        notifyItemChanged(0);
                    }
                });

                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loop > 0) {
                            loop--;
                        }else if (loop == 0){
                            loop = messageText.length -1;
                        }
                        PrefUtilsNew.setInviteText(loop);
                        notifyDataSetChanged();
                        notifyItemChanged(0);
                    }
                });

                gText = greetingText[loop];
                mText = messageText[loop];
                rText = regardsText[loop];

                greeting.setText(gText);
                inviteText.setText(mText);
                withLove.setText(rText);

                completeMessage = gText + "\n\n" + mText + "\n\n" + rText + "\n" + userNameText + "\n" +
                        "(Now get rewarded for checking-in at my place on www.bit.ly/MFPrint )";

                inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MyFootprintApplication.getInstance().trackEvent("Invite Friend", "To whatsapp", "InviteScreen");
                        PackageManager pm = context.getPackageManager();
                        try {

                            Intent waIntent = new Intent(Intent.ACTION_SEND);
                            waIntent.setType("text/plain");

                            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                            waIntent.setPackage("com.whatsapp");

                            waIntent.putExtra(Intent.EXTRA_TEXT, completeMessage);
                            context.startActivity(Intent.createChooser(waIntent, "Share with"));

                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                                    .show();
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
        return 1;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }

}
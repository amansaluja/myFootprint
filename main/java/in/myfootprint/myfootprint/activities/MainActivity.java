package in.myfootprint.myfootprint.activities;

/**
 * Created by Aman on 17-11-2015.
 */
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.fragments.FeedFragment;
import in.myfootprint.myfootprint.fragments.InviteFragment;
import in.myfootprint.myfootprint.fragments.RedeemFragment;
import in.myfootprint.myfootprint.navigation.FragmentDrawerFragment;
import in.myfootprint.myfootprint.push.QuickstartPreferences;
import in.myfootprint.myfootprint.push.RegistrationIntentService;
import in.myfootprint.myfootprint.utils.NetworkUtils;
import in.myfootprint.myfootprint.utils.PrefUtils;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity implements FragmentDrawerFragment.FragmentDrawerListener{

    private Toolbar toolbar;
    private FragmentDrawerFragment drawerFragment;
    private TabLayout tabLayout;
    public ViewPager viewPager;

    int notifNumber;
    TextView count;
    ImageView cart;

    private int hot_number = 0;
    private int hot_number_old = 0;
    private TextView ui_hot = null;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    String fbUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerFragment = (FragmentDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Intent i = getIntent();
        int tabNumber = i.getIntExtra("InviteTab", 99);
        if(tabNumber == 2){
            viewPager.setCurrentItem(2);
        }else if(tabNumber == 1){
            viewPager.setCurrentItem(1);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //Log.e("GCM Response", getString(R.string.gcm_send_message));
                } else {
                    //Log.e("GCM Error", getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "FEED");
        adapter.addFragment(new RedeemFragment(), "REDEEM");
        adapter.addFragment(new InviteFragment(), "INVITE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        switch (position) {
            case 0:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                MyFootprintApplication.getInstance().trackEvent("MailToCEO", "To mailClient", "NavDrawerActivity");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ceo@myfootprint.in"});
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

                break;
            case 1:
                Controller.goToUrl("http://myfootprint.in/", MainActivity.this);
                MyFootprintApplication.getInstance().trackEvent("WebsiteMyFootprint", "To website", "NavDrawerActivity");
                break;
            case 2:
                Intent j = new Intent(MainActivity.this, ProfileActivity.class);
                j.putExtra("FragmentNumber", "5");
                MyFootprintApplication.getInstance().trackEvent("TermsOfService", "To webview", "NavDrawerActivity");
                j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(j);
                break;
            case 3:
                Intent k = new Intent(MainActivity.this, ProfileActivity.class);
                k.putExtra("FragmentNumber", "6");
                MyFootprintApplication.getInstance().trackEvent("PrivacyPolicy", "To webview", "NavDrawerActivity");
                k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(k);
                break;
            case 4:
                LoginManager.getInstance().logOut();
                PrefUtils.setHasLoggedIn(false);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, IntroductionActivity.class);
                MyFootprintApplication.getInstance().trackEvent("LoggedOut", "To login", "NavDrawerActivity");
                startActivity(intent);
                MainActivity.this.finish();

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        final View menu_hotlist = menu.findItem(R.id.action_notification).getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.numberItems);
        if(PrefUtilsNew.getNotifNumber() !=0){

            hot_number_old = PrefUtilsNew.getNotifNumber();
        }

        updateHotCount(hot_number_old);
        new MyMenuItemStuffListener(menu_hotlist, String.valueOf(hot_number)+" unread messages") {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("FragmentNumber", "3");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        };

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_place:
                Controller.getFullProfile(MainActivity.this, "place");
                break;

            case R.id.action_notification:
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("FragmentNumber", "3");
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void updateHotCount(final int new_hot_number) {

        hot_number = new_hot_number;
        if (ui_hot == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_hot_number == 0)
                    ui_hot.setVisibility(View.INVISIBLE);
                else {
                    ui_hot.setVisibility(View.VISIBLE);
                    ui_hot.setText(Integer.toString(new_hot_number));
                }
            }
        });
    }

    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override abstract public void onClick(View v);

        @Override public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        int tabSelected = viewPager.getCurrentItem();
        if(tabSelected== 0) {
            MainActivity.this.finish();
        }else{
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        getApplicationContext().unregisterReceiver(mMessageReceiver);
        AppEventsLogger.deactivateApp(this);
        super.onPause();
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            hot_number = PrefUtilsNew.getNotifNumber();;
            updateHotCount(hot_number);
        }
    };

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}
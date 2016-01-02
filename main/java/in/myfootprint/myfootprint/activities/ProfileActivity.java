package in.myfootprint.myfootprint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.fragments.CouponFragment;
import in.myfootprint.myfootprint.fragments.NotificationFragment;
import in.myfootprint.myfootprint.fragments.PlaceProfileFragment;
import in.myfootprint.myfootprint.fragments.PrivacyFragment;
import in.myfootprint.myfootprint.fragments.TermsFragment;
import in.myfootprint.myfootprint.fragments.UserProfileFragment;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

/**
 * Created by Aman on 26-11-2015.
 */
public class ProfileActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    Fragment fragment = null;

    TextView pageTitle;
    TextView pageSubtitle;
    String userProfileTitle;
    String placeProfileTitle;
    String placeUserType;
    String fragmentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pageTitle = (TextView) findViewById(R.id.title);
        pageSubtitle = (TextView) findViewById(R.id.subtitle);

        try {
            JSONObject name = new JSONObject(PrefUtilsNew.getUserDetails());
            userProfileTitle = name.get("name").toString();
            JSONArray place = new JSONArray(PrefUtilsNew.getUserDetailsFull());
            placeUserType = place.getJSONObject(0).get("user_type").toString();
            if(userProfileTitle.contains(" ")){
                String[] parts = userProfileTitle.split(" ");
                placeProfileTitle = parts[0] + "'s place";
            }else{
                placeProfileTitle = userProfileTitle + "'s place";
            }
            placeUserType = Character.toUpperCase(placeUserType.charAt(0)) + placeUserType.substring(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fragmentNumber = getIntent().getStringExtra("FragmentNumber");

        switch (fragmentNumber){
            case "1":
                pageTitle.setText(userProfileTitle);
                pageSubtitle.setText("");
                fragment = new UserProfileFragment();
                break;
            case "2":
                pageTitle.setText(placeProfileTitle);
                pageSubtitle.setText(placeUserType);
                fragment = new PlaceProfileFragment();
                break;
            case "3":
                pageTitle.setText("Notifications");
                pageSubtitle.setText("");
                fragment = new NotificationFragment();
                break;
            case "4":
                pageTitle.setText("Coupon");
                pageSubtitle.setText("");
                fragment = new CouponFragment();
                break;
            case "5":
                pageTitle.setText("Terms of service");
                pageSubtitle.setText("");
                fragment = new TermsFragment();
                break;
            case "6":
                pageTitle.setText("Privacy Policy");
                pageSubtitle.setText("");
                fragment = new PrivacyFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!fragmentNumber.equals("4")){
                    Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    ProfileActivity.this.finish();
                }else{

                    ProfileActivity.this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if(!fragmentNumber.equals("4")){
            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            ProfileActivity.this.finish();
        }else{

            ProfileActivity.this.finish();
        }
    }
}

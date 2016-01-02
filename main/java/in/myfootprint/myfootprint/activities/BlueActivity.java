package in.myfootprint.myfootprint.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.fragments.CheckinConfirmFragment;
import in.myfootprint.myfootprint.fragments.ReviewAddressFragment;
import in.myfootprint.myfootprint.fragments.ReviewCheckinFragment;
import in.myfootprint.myfootprint.utils.PrefUtils;

public class BlueActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    Fragment fragment = null;
    String fragmentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentNumber = getIntent().getStringExtra("FragmentNumber");

        switch (fragmentNumber){
            case "1":
                fragment = new ReviewAddressFragment();
                break;
            case "2":
                fragment = new ReviewCheckinFragment();
                break;
            case "3":
                fragment = new CheckinConfirmFragment();
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
                switch (fragmentNumber){
                    case "1":
                        Intent i = new Intent(BlueActivity.this, AddressActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PrefUtils.setLatLong(String.valueOf(0.0) + "," + String.valueOf(0.0));
                        PrefUtils.setAddress("");
                        startActivity(i);
                        BlueActivity.this.finish();
                        break;
                    case "2":
                        Intent j = new Intent(BlueActivity.this, MainActivity.class);
                        j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(j);
                        BlueActivity.this.finish();
                        break;
                    case "3":
                        Intent k = new Intent(BlueActivity.this, MainActivity.class);
                        k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(k);
                        BlueActivity.this.finish();
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        switch (fragmentNumber){
            case "1":
                Intent i = new Intent(BlueActivity.this, AddressActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PrefUtils.setLatLong(String.valueOf(0.0) + "," + String.valueOf(0.0));
                PrefUtils.setAddress("");
                startActivity(i);
                BlueActivity.this.finish();
                break;
            case "2":
                Intent j = new Intent(BlueActivity.this, MainActivity.class);
                j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);
                BlueActivity.this.finish();
                break;
            case "3":
                Intent k = new Intent(BlueActivity.this, MainActivity.class);
                k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(k);
                BlueActivity.this.finish();
                break;
        }

    }
}

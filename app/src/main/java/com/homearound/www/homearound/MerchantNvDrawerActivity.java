package com.homearound.www.homearound;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MerchantNvDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    DrawerLayout drawer = null;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_nv_drawer);

        FragmentJobPostedM fragmentJobPostedM = new FragmentJobPostedM();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentJobPostedM);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar_merchant_nv);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_merchant);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_merchant);
        navigationView.setNavigationItemSelectedListener(this);

        userDefaultManager = new UserDefaultManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView = (NavigationView) findViewById(R.id.nav_view_merchant);

        View headerV = navigationView.getHeaderView(0);
        TextView nameV = (TextView)headerV.findViewById(R.id.txt_profile_name_merchant);
        String nameStr = userDefaultManager.getUserName();
        if (!TextUtils.isEmpty(nameStr)) {
            nameV.setText(nameStr);
        }

        ImageView imgV =
                (ImageView) headerV.findViewById(R.id.img_profile_photo_merchant);

        String strImg = userDefaultManager.getUserPhoto();
        if (!TextUtils.isEmpty(strImg)) {
            byte[] decodedByte = Base64.decode(strImg, Base64.DEFAULT);
            Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            imgV.setImageBitmap(bitmapImg);
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_merchant);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_jobspostedM) {
            // Handle the camera action
            FragmentJobPostedM fragmentJobPostedM = new FragmentJobPostedM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentJobPostedM);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_myjobsM) {
            FragmentMyJobsM fragmentMyJobsM = new FragmentMyJobsM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentMyJobsM);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_messageboxM) {
            FragmentMsgBoxM fragmentMsgBoxM = new FragmentMsgBoxM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentMsgBoxM);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_rating_reviewM) {
            FragmentRatingsReviewsM fragmentRatingsReviewsM = new FragmentRatingsReviewsM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentRatingsReviewsM);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_contactusM) {
            FragmentContactUsC fragmentContactUsC = new FragmentContactUsC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentContactUsC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_settingsM) {
            FragmentSettingsM fragmentSettingsM = new FragmentSettingsM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentSettingsM);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_logoutM) {
            FragmentLogoutM fragmentLogoutM = new FragmentLogoutM();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_merchant, fragmentLogoutM);
            fragmentTransaction.commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_merchant);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

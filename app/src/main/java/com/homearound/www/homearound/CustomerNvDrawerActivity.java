package com.homearound.www.homearound;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomerNvDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    DrawerLayout drawer = null;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_nv_drawer);
      // set fragment
        FragmentMyJobsC fragmentMyJobsC = new FragmentMyJobsC();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentMyJobsC);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar_customer_nv);
        setSupportActionBar(toolbar);
/**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_customer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_customer);
        navigationView.setNavigationItemSelectedListener(this);

        userDefaultManager = new UserDefaultManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigationView = (NavigationView) findViewById(R.id.nav_view_customer);

        View headerV = navigationView.getHeaderView(0);
        TextView nameV = (TextView)headerV.findViewById(R.id.txt_profile_name_customer);
        String nameStr = userDefaultManager.getUserName();
        if (!TextUtils.isEmpty(nameStr)) {
            nameV.setText(nameStr);
        }

        ImageView imgV =
                (ImageView) headerV.findViewById(R.id.img_profile_photo_customer);

        String strImg = userDefaultManager.getUserPhoto();
        if (!TextUtils.isEmpty(strImg)) {
            byte[] decodedByte = Base64.decode(strImg, Base64.DEFAULT);
            Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            imgV.setImageBitmap(bitmapImg);
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_customer);
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

        if (id == R.id.nav_myjobsC) {
            // set fragment
            FragmentMyJobsC fragmentMyJobsC = new FragmentMyJobsC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentMyJobsC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_myservicesC) {
            FragmentMyServiceC fragmentMyServiceC = new FragmentMyServiceC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentMyServiceC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_messageboxC) {
            FragmentMsgBoxC fragmentMsgBoxC = new FragmentMsgBoxC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentMsgBoxC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_promotionsC) {
            FragmentPromotionsC fragmentPromotionsC = new FragmentPromotionsC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentPromotionsC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_contactusC) {
            FragmentContactUsC fragmentContactUsC = new FragmentContactUsC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentContactUsC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_settingsC) {
            FragmentSettingsC fragmentSettingsC = new FragmentSettingsC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentSettingsC);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_logoutC) {
            FragmentLogoutC fragmentLogoutC = new FragmentLogoutC();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nv_fragment_holder_customer, fragmentLogoutC);
            fragmentTransaction.commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_customer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toDismissKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

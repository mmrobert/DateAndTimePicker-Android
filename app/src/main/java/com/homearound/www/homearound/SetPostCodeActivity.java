package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetPostCodeActivity extends AppCompatActivity {

    static final int SET_COUNTRY_CHOOSE_REQUEST = 1000;

    private EditText txtPostCode;
    private TextView txtCountry;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_post_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_post_code);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        txtPostCode = (EditText)findViewById(R.id.txt_set_post_code);
        txtCountry = (TextView)findViewById(R.id.txt_set_country);

        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetPostCodeActivity.this,
                        SetCountryListActivity.class);
             //   intent.putExtra("jobtitle", jobTitle);

                SetPostCodeActivity.this.startActivityForResult(intent,
                        SET_COUNTRY_CHOOSE_REQUEST, null);
            }
        });

        Button btnLater = (Button)findViewById(R.id.btn_later_set_post_code);

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetPhotoPage();
            }
        });

        RelativeLayout rel = (RelativeLayout) findViewById(R.id.content_set_post_code);
        rel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toDismissKeyboard();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_post_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_set_post_code) {

            savePostCode();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_COUNTRY_CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnTitle = data.getStringExtra("country");
                if (!TextUtils.isEmpty(returnTitle)) {
                    txtCountry.setText(returnTitle);
                }
            }
        }
    }

    private void savePostCode() {

        String mPostCode = txtPostCode.getText().toString().trim();
        String mCountry = txtCountry.getText().toString();

        if (TextUtils.isEmpty(mPostCode)) {
            String alertTitle = "Enter your postal code to save.";
            FragmentManager fm = getSupportFragmentManager();
            OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
            mDialog.show(fm, "Cheng");
        } else if (TextUtils.isEmpty(mCountry)) {
            String alertTitle = "Choose your country to save.";
            FragmentManager fm = getSupportFragmentManager();
            OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
            mDialog.show(fm, "Cheng");
        } else {
            if (mCountry.equals("Canada")) {
                if (mPostCode.length() > 3 && mPostCode.length() < 7) {
                    StringBuilder strH = new StringBuilder(mPostCode);
                    strH.insert(3, " ");
                    txtPostCode.setText(strH.toString().toUpperCase(Locale.ENGLISH));
                    mPostCode = strH.toString().toUpperCase(Locale.ENGLISH);
                } else {
                    txtPostCode.setText(mPostCode.toUpperCase(Locale.ENGLISH));
                    mPostCode = mPostCode.toUpperCase(Locale.ENGLISH);
                }

                String canadaRegex = "^[a-zA-Z][0-9][a-zA-Z][- ]*[0-9][a-zA-Z][0-9]$";
                if (!mPostCode.matches(canadaRegex)) {
                    String alertTitle = "Not right postal code format.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                    mDialog.show(fm, "Cheng");
                } else {
                    savePostCodeOnServer();
                 //   toSetPhotoPage();
                }
            } else if (mCountry.equals("United States")) {
                txtPostCode.setText(mPostCode);
                String usaRegex = "^[0-9]{5}(-[0-9]{4})?$";
                if (!mPostCode.matches(usaRegex)) {
                    String alertTitle = "Not right postal code format.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                    mDialog.show(fm, "Cheng");
                } else {
                    savePostCodeOnServer();
                  //  toSetPhotoPage();
                }
            }
        }
    }

    private void savePostCodeOnServer() {

        final String TAG_NET = "SAVE_SET_POST_CODE";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Saving postal code and country...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/setpostcode";
        final String mPostCode = txtPostCode.getText().toString();
        final String mCountry = txtCountry.getText().toString();
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();
        //  Log.d(TAG_NET, mEmail);
        params.put("postcode", mPostCode);
        params.put("country", mCountry);
        params.put("token", mToken);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            userDefaultManager.setUserPostCode(mPostCode);
                            userDefaultManager.setUserCountry(mCountry);

                            toSetPhotoPage();
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                            FragmentManager fm = getSupportFragmentManager();
                            OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(mMessage);
                            okFG.show(fm, "Cheng");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                VolleyLog.d(TAG_NET, "Error: " + error.getMessage());
            }
        });

        HAApplication.getInstance().addToRequestQueue(jsonReq, TAG_NET);
    }

    private void toSetPhotoPage() {

        toDismissKeyboard();
        Intent intent = new Intent(this, SetPhotoActivity.class);
        startActivity(intent);
        finish();
    }

    private void toDismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

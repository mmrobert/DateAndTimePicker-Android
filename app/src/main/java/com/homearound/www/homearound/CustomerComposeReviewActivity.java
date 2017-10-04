package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.datetimepicker.DateTime;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerComposeReviewActivity extends AppCompatActivity {

    private EditText txtRates;
    private EditText txtComments;

    private String emailForReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_compose_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_compose_review_c);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        emailForReview = intent.getStringExtra("emailforreview");

        txtRates = (EditText)findViewById(R.id.txt_compose_review_rate_c);
        txtComments = (EditText)findViewById(R.id.txt_compose_review_comment_c);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.content_compose_review_c);
        rel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toDismissKeyBoard();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_review_c, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post_compose_review_c) {
            String rateHere = txtRates.getText().toString().trim();
            if (TextUtils.isEmpty(rateHere)) {
                String alertTitle = "Please give a rate value.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                okFG.show(fm, "Cheng");
            } else if (!rateHere.matches("[-+]?\\d*\\.?\\d*")) {
                String alertTitle = "Rate value need to be decimal.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                okFG.show(fm, "Cheng");
            } else {
                float rateF = Float.parseFloat(rateHere);
                if (rateF < 0.0 || rateF > 5.0) {
                    String alertTitle = "Rate value must be between 0.0 and 5.0.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                    okFG.show(fm, "Cheng");
                } else {
                    postOnServer();
                  //  Log.d("RObert", "it is ok");
                }
            }
            return true;
        } else if (id == R.id.action_cancel_compose_review_c) {
            toDismissKeyBoard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postOnServer() {

        final String TAG_NET = "POST_REVIEW_C";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Posting review...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/reviewpost";

        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        String mComments = txtComments.getText().toString().trim();
        if (TextUtils.isEmpty(mComments)) {
            mComments = "No comment.";
        }

        Date now = new Date();
        DateTime mDateTime = new DateTime(now);
        String dateFormatHere = "yyyy-MM-dd HH:mm";
        String timePosting = mDateTime.getDateString(dateFormatHere);

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("emailforpost", emailForReview);
        params.put("rating", txtRates.getText().toString());
        params.put("timePosted", timePosting);
        params.put("comment", mComments);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            notifyRenewReview();
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

    private void notifyRenewReview() {
        toDismissKeyBoard();
        Intent responseIntent = new Intent();
        responseIntent.putExtra("newvalue", txtRates.getText().toString());
        setResult(RESULT_OK, responseIntent);
        finish();
    }

    private void toDismissKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

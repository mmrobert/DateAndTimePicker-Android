package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetBioActivity extends AppCompatActivity {

    private EditText txtBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_bio);
        setSupportActionBar(toolbar);

        txtBio = (EditText)findViewById(R.id.txt_set_bio);

        Button btnLater = (Button)findViewById(R.id.btn_later_set_bio);

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMerchantNvPage();
            }
        });

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.content_set_bio);
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
        getMenuInflater().inflate(R.menu.menu_set_bio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_set_bio) {
            String mBio = txtBio.getText().toString();
            if (TextUtils.isEmpty(mBio)) {
                String alertTitle = "Enter your bio details to save.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                mDialog.show(fm, "Cheng");
            } else {
                saveBioOnServer();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveBioOnServer() {

        final String TAG_NET = "SAVE_SET_BIO";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Saving bio details...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/setbiodetail";

        final String mBio = txtBio.getText().toString();

        final UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("biodetail", mBio);
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
                            userDefaultManager.setUserBio(mBio);

                            toMerchantNvPage();
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

    private void toMerchantNvPage() {

        toDismissKeyboard();
        Intent intent = new Intent(this, MerchantNvDrawerActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
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

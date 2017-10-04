package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashAuthActivity extends AppCompatActivity {

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userDefaultManager = new UserDefaultManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        String mEmail = userDefaultManager.getUserEmail();
        String mPassword = userDefaultManager.getUserPassword();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {

            Intent intent = new Intent(this, LoginActivity.class);
            //  Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            updateDeviceInfosOnServer();
         //   authToMainPage();
        }
    }

    private void updateDeviceInfosOnServer() {

        final String TAG_NET = "UPDATE_DEVICE_INFOS";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Connecting server...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/updatedeviceinfo";

        final String mToken = userDefaultManager.getUserToken();
        boolean mRemoteAllow = userDefaultManager.getUserRemoteNotificationAllow();
        final String mDeviceToken;
        if (mRemoteAllow) {
            mDeviceToken = userDefaultManager.getUserDeviceToken();
        } else {
            mDeviceToken = "0";
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("token", mToken);
        params.put("platform", "android");
        params.put("devicetoken", mDeviceToken);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();

                            authToMainPage();
                        } else {
                            pDialog.hide();
                            /*
                            FragmentManager fm = getSupportFragmentManager();
                            OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(mMessage);
                            okFG.show(fm, "Cheng");
                            */
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                VolleyLog.d(TAG_NET, "Error: " + error.getMessage());
            }
        });
        /*
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //  return super.getParams();
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG_NET, mToken);

                params.put("token", mToken);
                params.put("platform", "android");
                params.put("devicetoken", mDeviceToken);

                return params;
            }
        };
        */
        HAApplication.getInstance().addToRequestQueue(jsonReq, TAG_NET);
    }

    private void authToMainPage() {
        String mRole = userDefaultManager.getUserRole();
        if (mRole.equals("customer")) {
            Intent intent = new Intent(this, CustomerNvDrawerActivity.class);
            //  Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MerchantNvDrawerActivity.class);
            //  Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

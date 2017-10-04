package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtPassword;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        txtEmail = (EditText)findViewById(R.id.et_login_email);
        txtPassword = (EditText)findViewById(R.id.et_login_password);

        Button btnLogin = (Button)findViewById(R.id.btn_login_login);
        Button btnForgotPW = (Button)findViewById(R.id.btn_login_forgot_password);
        Button btnRegister = (Button)findViewById(R.id.btn_login_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,4}$";
                String mEmailVal = txtEmail.getText().toString().trim();
                String mPasswordVal = txtPassword.getText().toString().trim();
                if (mEmailVal.length() > 0 && mPasswordVal.length() > 0) {
                    if (mEmailVal.matches(emailReg)) {
                        loginOnServer();
                     //   toMainPage("customer");
                    } else {
                        String alertTitle = "Not right email format.";
                        FragmentManager fm = getSupportFragmentManager();
                        OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                        okFG.show(fm, "Cheng");
                    }
                } else {
                    String alertTitle = "Empty email or password.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                    okFG.show(fm, "Cheng");
                }
            }
        });

        btnForgotPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                Context context = LoginActivity.this;
                context.startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDismissKeyBoard();
                Intent intent = new Intent(LoginActivity.this, RoleChooseActivity.class);
                //  Intent intent = new Intent(this, SignupActivity.class);
                Context context = LoginActivity.this;
                context.startActivity(intent);
                finish();
            }
        });

        ScrollView scrollView = (ScrollView)findViewById(R.id.content_login);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toDismissKeyBoard();
                return true;
            }
        });
    }

    private void loginOnServer() {

        final String TAG_NET = "LOG_IN";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Logging in...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/login";

        final String mEmail = txtEmail.getText().toString().trim().toLowerCase(Locale.ENGLISH);
        final String mPassword = txtPassword.getText().toString().trim();

        boolean mRemoteAllow = userDefaultManager.getUserRemoteNotificationAllow();
        final String mDeviceToken;
        if (mRemoteAllow) {
            mDeviceToken = userDefaultManager.getUserDeviceToken();
        } else {
            mDeviceToken = "0";
        }

        Map<String, String> params = new HashMap<String, String>();

      //  Log.d(TAG_NET, mEmail);
        params.put("email", mEmail);
        params.put("password", mPassword);
        params.put("platform", "android");
        params.put("devicetoken", mDeviceToken);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            String mToken = response.optString("token");
                            String mRole = response.optString("role");

                            userDefaultManager.setUserEmail(mEmail);
                            userDefaultManager.setUserPassword(mPassword);
                            userDefaultManager.setUserToken(mToken);
                            userDefaultManager.setUserRole(mRole);

                            JSONObject mUserInfos = response.optJSONObject("userprofile");

                            if (mRole.equals("customer")) {
                                userDefaultManager.setUserName(mUserInfos.optString("name"));
                                userDefaultManager.setUserPostCode(mUserInfos.optString("postcode"));
                                userDefaultManager.setUserCountry(mUserInfos.optString("country"));
                            } else {
                                userDefaultManager.setUserName(mUserInfos.optString("name"));
                                userDefaultManager.setUserPostCode(mUserInfos.optString("postcode"));
                                userDefaultManager.setUserCountry(mUserInfos.optString("country"));
                                userDefaultManager.setUserPhone(mUserInfos.optString("phone"));
                                userDefaultManager.setUserBio(mUserInfos.optString("biodetail"));

                                JSONArray expertsH = mUserInfos.optJSONArray("expertise");
                                String expertiseH;
                                if (expertsH != null) {
                                    int arrLength = expertsH.length();
                                    if (arrLength > 0) {
                                        StringBuilder expertsBuilder = new StringBuilder();
                                        for (int ii = 0; ii < arrLength; ii++) {
                                            if (ii < arrLength - 1) {
                                                expertsBuilder.append(expertsH.optString(ii));
                                                expertsBuilder.append(",\n");
                                            } else {
                                                expertsBuilder.append(expertsH.optString(ii));
                                            }
                                        }
                                        expertiseH = expertsBuilder.toString();
                                    } else {
                                        expertiseH = "";
                                    }
                                } else {
                                    expertiseH = "";
                                }

                                userDefaultManager.setUserExpertise(expertiseH);
                            }
                            toMainPage(mRole);
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
        /*
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //  return super.getParams();
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG_NET, mEmail);
                params.put("email", mEmail);
                params.put("password", mPassword);
                params.put("platform", "android");
                params.put("devicetoken", mDeviceToken);

                return params;
            }
        };
        */
        HAApplication.getInstance().addToRequestQueue(jsonReq, TAG_NET);
    }

    private void toMainPage(String role) {
        toDismissKeyBoard();
        if (role.equals("customer")) {
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

    private void toDismissKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

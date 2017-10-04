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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;

    private String role;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        role = intent.getStringExtra("role");

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        txtEmail = (EditText)findViewById(R.id.et_register_email);
        txtPassword = (EditText)findViewById(R.id.et_register_password);
        txtPasswordConfirm = (EditText)findViewById(R.id.et_register_password_confirm);

        Button btnRegister = (Button)findViewById(R.id.btn_register_register);
        Button btnTerms = (Button)findViewById(R.id.btn_register_term);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,4}$";
                String mEmailVal = txtEmail.getText().toString().trim();
                String mPasswordVal = txtPassword.getText().toString().trim();
                String mPasswordConfirm = txtPasswordConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(mEmailVal) || TextUtils.isEmpty(mPasswordVal)) {
                    String alertTitle = "Empty email or password.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                    okFG.show(fm, "Cheng");
                } else {
                    if (!mEmailVal.matches(emailReg)) {
                        String alertTitle = "Not right email format.";
                        FragmentManager fm = getSupportFragmentManager();
                        OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                        okFG.show(fm, "Cheng");
                        //   toMainPage("customer");
                    } else {
                        if (!mPasswordConfirm.equals(mPasswordVal)) {
                            String alertTitle = "Not right password confirmation.";
                            FragmentManager fm = getSupportFragmentManager();
                            OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                            okFG.show(fm, "Cheng");
                        } else {
                         //   Log.d("2016m11", "this ok");
                            registerOnServer();
                        }
                    }
                }
            }
        });

        btnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDismissKeyboard();
                Intent intent = new Intent(RegisterActivity.this, UserAgreementActivity.class);
                //  Intent intent = new Intent(this, SignupActivity.class);
                Context context = RegisterActivity.this;
                context.startActivity(intent);
            //    finish();
            }
        });

        ScrollView scrollView = (ScrollView)findViewById(R.id.content_register);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
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
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_register) {
            toDismissKeyboard();
            Intent intent = new Intent(this, LoginActivity.class);
            //  Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerOnServer() {

        final String TAG_NET = "REGISTER";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Registering...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/register";

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
        params.put("role", role);
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
                            userDefaultManager.setUserEmail(mEmail);
                            userDefaultManager.setUserPassword(mPassword);
                            userDefaultManager.setUserToken(mToken);
                            userDefaultManager.setUserRole(role);

                            toSetNamePage();
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
            //    Log.d("2016m11d22222", "this is very wrong");
                VolleyLog.d(TAG_NET, "Error: " + error.getMessage());
            }
        });

        HAApplication.getInstance().addToRequestQueue(jsonReq, TAG_NET);
    }

    private void toSetNamePage() {
        toDismissKeyboard();
        Intent intent = new Intent(this, SetNameActivity.class);
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

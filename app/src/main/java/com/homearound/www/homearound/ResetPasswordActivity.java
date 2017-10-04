package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reset_password);
        setSupportActionBar(toolbar);

        txtEmail = (EditText)findViewById(R.id.et_reset_password_email);
        Button btnReset = (Button)findViewById(R.id.btn_reset_password_reset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,4}$";
                String mEmailVal = txtEmail.getText().toString().trim();
                if (mEmailVal.length() > 0) {
                    if (mEmailVal.matches(emailReg)) {
                        resetPWOnServer();
                        //   toMainPage("customer");
                    } else {
                        String alertTitle = "Not right email format.";
                        FragmentManager fm = getSupportFragmentManager();
                        OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                        okFG.show(fm, "Cheng");
                    }
                } else {
                    String alertTitle = "Empty email.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                    okFG.show(fm, "Cheng");
                }
            }
        });

        ScrollView scrollView = (ScrollView)findViewById(R.id.content_reset_password);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toDismissKeyboard();
                return true;
            }
        });
    }

    private void resetPWOnServer() {

        final String TAG_NET = "RESET_PASSWORD";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Connecting server...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/resetpw";

        final String mEmail = txtEmail.getText().toString().trim().toLowerCase(Locale.ENGLISH);

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("email", mEmail);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            String alertTitle = "We've sent you an email with a link to reset your password.";
                            FragmentManager fm = getSupportFragmentManager();

                            OkActionDialogFragment dReset =
                                    OkActionDialogFragment.newInstance(alertTitle);
                            dReset.setOnOkClickListener(new OkActionDialogFragment.OnOkClickListener() {
                                @Override
                                public void onOkClick() {
                                    toLoginPage();
                                }
                            });
                            dReset.show(fm, "Cheng");
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

    private void toLoginPage() {
      //  Intent intent = new Intent(this, LoginActivity.class);
      //  startActivity(intent);
        toDismissKeyboard();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_reset_password) {
          //  Intent intent = new Intent(this, LoginActivity.class);
          //  startActivity(intent);
            toDismissKeyboard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toDismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

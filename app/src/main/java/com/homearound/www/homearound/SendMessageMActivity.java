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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.datetimepicker.DateTime;
import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantMessage;
import com.homearound.www.homearound.db.MerchantMessageBox;
import com.homearound.www.homearound.db.MerchantMessageBoxDao;
import com.homearound.www.homearound.db.MerchantMessageDao;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMessageMActivity extends AppCompatActivity {

    private String nameToSend;
    private String emailToSend;
    private EditText etMessage;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_m);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_send_message_m);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        nameToSend = intent.getStringExtra("nametosend");
        emailToSend = intent.getStringExtra("emailtosend");

        TextView txtToSend = (TextView)findViewById(R.id.txt_send_message_recipient_m);
        txtToSend.setText(nameToSend);

        etMessage = (EditText)findViewById(R.id.txt_send_message_msg_m);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.content_send_msg_m);
        rel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toDismissKeyboard();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String mName = userDefaultManager.getUserName();

        if (TextUtils.isEmpty(mName)) {
            String alertStr = "Please enter your name in the Settings to send message.";
            FragmentManager fm = getSupportFragmentManager();
            OkActionDialogFragment okFG = OkActionDialogFragment.newInstance(alertStr);
            okFG.setOnOkClickListener(new OkActionDialogFragment.OnOkClickListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            okFG.show(fm, "Cheng");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message_m, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_send_message_m) {
            String msgHere = etMessage.getText().toString();
            if (TextUtils.isEmpty(msgHere)) {
                String alertTitle = "There is no message to send.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                okFG.show(fm, "Cheng");
            } else {
                sendMsgOnServer();
            }
            return true;
        } else if (id == R.id.action_cancel_send_message_m) {
            toDismissKeyboard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMsgOnServer() {

        final String TAG_NET = "SEND_MESSAGE_M";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending message...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/merchant/sendmessage";

        String mToken = userDefaultManager.getUserToken();

        final String msgToSend = etMessage.getText().toString();

        Date now = new Date();
        DateTime mDateTime = new DateTime(now);
        String dateFormatHere = "yyyy-MM-dd HH:mm";
        final String timeSending = mDateTime.getDateString(dateFormatHere);

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("emailToSend", emailToSend);
        params.put("msg", msgToSend);
        params.put("timeMsg", timeSending);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            saveMsgOnDatabase(timeSending, msgToSend);
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

    private void saveMsgOnDatabase(String timeToSend, String msgSending) {

        DaoSession daoSession = ((HAApplication)getApplication()).getDaoSession();
        MerchantMessageDao merchantMessageDao = daoSession.getMerchantMessageDao();
        MerchantMessageBoxDao merchantMessageBoxDao = daoSession.getMerchantMessageBoxDao();

        MerchantMessage merchantMessage = new MerchantMessage();
        merchantMessage.setName(nameToSend);
        merchantMessage.setEmail(emailToSend);
        merchantMessage.setDirection("out");
        merchantMessage.setMessagebody(msgSending);
        merchantMessage.setMessagetime(timeToSend);
        merchantMessageDao.insert(merchantMessage);

        List<MerchantMessageBox> tempArr =
                merchantMessageBoxDao.queryBuilder()
                        .where(MerchantMessageBoxDao.Properties.Email.eq(emailToSend)).list();

        if (tempArr != null) {
            if (tempArr.size() > 0) {
                MerchantMessageBox merchantMessageBox = tempArr.get(0);
                merchantMessageBox.setLastmessage(msgSending);
                merchantMessageBox.setTimelastmessage(timeToSend);

                merchantMessageBoxDao.update(merchantMessageBox);
            } else {
                MerchantMessageBox merchantMessageBox = new MerchantMessageBox();
                merchantMessageBox.setName(nameToSend);
                merchantMessageBox.setEmail(emailToSend);
                merchantMessageBox.setLastmessage(msgSending);
                merchantMessageBox.setTimelastmessage(timeToSend);
                merchantMessageBoxDao.insert(merchantMessageBox);
            }
        } else {
            MerchantMessageBox merchantMessageBox = new MerchantMessageBox();
            merchantMessageBox.setName(nameToSend);
            merchantMessageBox.setEmail(emailToSend);
            merchantMessageBox.setLastmessage(msgSending);
            merchantMessageBox.setTimelastmessage(timeToSend);
            merchantMessageBoxDao.insert(merchantMessageBox);
        }

        toDismissKeyboard();
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

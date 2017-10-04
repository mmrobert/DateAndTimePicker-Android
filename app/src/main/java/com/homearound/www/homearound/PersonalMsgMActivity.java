package com.homearound.www.homearound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.datetimepicker.DateTime;
import com.homearound.www.homearound.db.CustomerMessage;
import com.homearound.www.homearound.db.CustomerMessageBox;
import com.homearound.www.homearound.db.CustomerMessageBoxDao;
import com.homearound.www.homearound.db.CustomerMessageDao;
import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantMessage;
import com.homearound.www.homearound.db.MerchantMessageBox;
import com.homearound.www.homearound.db.MerchantMessageBoxDao;
import com.homearound.www.homearound.db.MerchantMessageDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalMsgMActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterPersonalMsgM adapterPersonalMsgM;
    private List<MerchantMessage> messageList;
    private MerchantMessageDao messageDao;
    private MerchantMessageBoxDao messageBoxDao;

    private RVLayoutManager rvLayoutManager;

    private String emailOther;
    private String nameOther;

    private UserDefaultManager userDefaultManager;

    private EditText msgEtToSend;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_msg_m);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_personal_msg_m);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        emailOther = intent.getStringExtra("email");
        nameOther = intent.getStringExtra("name");

        getSupportActionBar().setTitle(nameOther);

        userDefaultManager = new UserDefaultManager(getApplicationContext());
        String imgStr = userDefaultManager.getUserPhoto();

        msgEtToSend = (EditText)findViewById(R.id.et_personal_msg_m);
        sendBtn = (Button)findViewById(R.id.btn_personal_msg_m);

        DaoSession daoSession = ((HAApplication)getApplication()).getDaoSession();
        messageDao = daoSession.getMerchantMessageDao();
        messageBoxDao = daoSession.getMerchantMessageBoxDao();

        messageList = new ArrayList<MerchantMessage>();

        List<MerchantMessage> listLoaded = messageDao.queryBuilder()
                .where(MerchantMessageDao.Properties.Email.eq(emailOther))
                .orderAsc(MerchantMessageDao.Properties.Messagetime).list();
        messageList.addAll(listLoaded);

        recyclerView = (RecyclerView)findViewById(R.id.rvItem_personal_msg_m);

        //   LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLayoutManager = new RVLayoutManager(this);
        rvLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(rvLayoutManager);

        adapterPersonalMsgM = new AdapterPersonalMsgM(messageList, imgStr);

        recyclerView.setAdapter(adapterPersonalMsgM);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgS = msgEtToSend.getText().toString();
                if (TextUtils.isEmpty(msgS)) {
                    String alertTitle = "There is no message to send.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
                    okFG.show(fm, "Cheng");
                } else {
                    sendMsgOnServer();
                    /*
                    String msgSending = msgEtToSend.getText().toString();
                    Date now = new Date();
                    DateTime mDateTime = new DateTime(now);
                    String dateFormatHere = "yyyy-MM-dd HH:mm";
                    String timeSending = mDateTime.getDateString(dateFormatHere);
                    saveMsgOnDatabase(timeSending, msgSending);
                    */
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
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
        getMenuInflater().inflate(R.menu.menu_personal_msg_m, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_personal_msg_m) {
            toDismissKeyboard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMsgOnServer() {

        final String TAG_NET = "SEND_PERSONAL_MESSAGE_M";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending message...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/merchant/sendmessage";

        String mToken = userDefaultManager.getUserToken();

        final String msgSending = msgEtToSend.getText().toString();

        Date now = new Date();
        DateTime mDateTime = new DateTime(now);
        String dateFormatHere = "yyyy-MM-dd HH:mm";
        final String timeSending = mDateTime.getDateString(dateFormatHere);

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("emailToSend", emailOther);
        params.put("msg", msgSending);
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
                            saveMsgOnDatabase(timeSending, msgSending);
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

        MerchantMessage merchantMessage = new MerchantMessage();
        merchantMessage.setName(nameOther);
        merchantMessage.setEmail(emailOther);
        merchantMessage.setDirection("out");
        merchantMessage.setMessagebody(msgSending);
        merchantMessage.setMessagetime(timeToSend);
        messageDao.insert(merchantMessage);

        List<MerchantMessageBox> tempArr =
                messageBoxDao.queryBuilder()
                        .where(MerchantMessageBoxDao.Properties.Email.eq(emailOther)).list();
        if (tempArr != null) {
            if (tempArr.size() > 0) {
                MerchantMessageBox messageBoxH = tempArr.get(0);
                messageBoxH.setLastmessage(msgSending);
                messageBoxH.setTimelastmessage(timeToSend);

                messageBoxDao.update(messageBoxH);
            }
        }

        msgEtToSend.setText("");

        messageList.add(merchantMessage);
        adapterPersonalMsgM.notifyItemInserted(messageList.size() - 1);
        int totalItemsH = rvLayoutManager.getItemCount();
        recyclerView.smoothScrollToPosition(totalItemsH - 1);
    }

    private void toDismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
    }
}

package com.homearound.www.homearound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class UpdateNamePostCodePhoneActivity extends AppCompatActivity {

    final int UPDATE_NAME_M_REQUEST = 808;
    final int UPDATE_POST_CODE_M_REQUEST = 818;
    final int UPDATE_PHONE_M_REQUEST = 828;

    private EditText etUpdateItem;

    private int requestWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name_post_code_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_update_name_post_phone);
        setSupportActionBar(toolbar);

        etUpdateItem = (EditText) findViewById(R.id.et_update_name_post_code_phone);

        Intent intent = getIntent();
        requestWhat = intent.getIntExtra("requestWhat", 0);

        if (requestWhat == UPDATE_NAME_M_REQUEST) {
            getSupportActionBar().setTitle("Name");
            String strUserName = intent.getStringExtra("paramStr");
            if (TextUtils.isEmpty(strUserName)) {
                etUpdateItem.setHint("Enter your name");
            } else {
                etUpdateItem.setText(strUserName);
            }
        } else if (requestWhat == UPDATE_POST_CODE_M_REQUEST) {
            getSupportActionBar().setTitle("Postal Code");
            String strUserPostCode = intent.getStringExtra("paramStr");
            if (TextUtils.isEmpty(strUserPostCode)) {
                etUpdateItem.setHint("Enter your postal code");
            } else {
                etUpdateItem.setText(strUserPostCode);
            }
        } else if (requestWhat == UPDATE_PHONE_M_REQUEST) {
            getSupportActionBar().setTitle("Phone");
            String strUserPhone = intent.getStringExtra("paramStr");
            if (TextUtils.isEmpty(strUserPhone)) {
                etUpdateItem.setHint("Enter your phone");
            } else {
                etUpdateItem.setText(strUserPhone);
            }
        }

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.content_update_name_post_code_phone);
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
        getMenuInflater().inflate(R.menu.menu_update_name_post_code_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done_update_name_post_code_phone) {
            toDismissKeyboard();
            Intent responseIntent = new Intent();
            responseIntent.putExtra("newValue", etUpdateItem.getText().toString());
            setResult(RESULT_OK, responseIntent);
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

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

public class UpdateBioDetailActivity extends AppCompatActivity {

    private EditText txtBioDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bio_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_update_bio_detail);
        setSupportActionBar(toolbar);

        txtBioDetail = (EditText) findViewById(R.id.et_update_bio_detail);

        Intent intent = getIntent();
        String strBioDetail = intent.getStringExtra("paramStr");

        if (TextUtils.isEmpty(strBioDetail)) {
            txtBioDetail.setHint("Enter your bio detail");
        } else {
            txtBioDetail.setText(strBioDetail);
        }

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.content_update_bio_detail);
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
        getMenuInflater().inflate(R.menu.menu_update_bio_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done_update_bio_detail) {
            toDismissKeyboard();
            Intent responseIntent = new Intent();
            responseIntent.putExtra("newValue", txtBioDetail.getText().toString());
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

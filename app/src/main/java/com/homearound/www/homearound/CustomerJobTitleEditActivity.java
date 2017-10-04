package com.homearound.www.homearound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class CustomerJobTitleEditActivity extends AppCompatActivity {

    private String jobTitle;

    private EditText etJobTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_job_title_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_job_title_edit_c);
        setSupportActionBar(toolbar);

     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        jobTitle = intent.getStringExtra("jobtitle");

        etJobTitle = (EditText) findViewById(R.id.et_job_title_edit_c);

        etJobTitle.setText(jobTitle);

        RelativeLayout rel = (RelativeLayout)findViewById(R.id.rel_job_title_edit_c);
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
        getMenuInflater().inflate(R.menu.menu_customer_job_title_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_job_title_c) {
     //       updateJobOnServer();
            toDismissKeyBoard();
            Intent responseIntent = new Intent();
            responseIntent.putExtra("newJobTitle", etJobTitle.getText().toString().trim());
            setResult(RESULT_OK, responseIntent);
            finish();
            return true;
        } else if (id == R.id.action_cancel_title_edit_c) {
            toDismissKeyBoard();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toDismissKeyBoard() {
      //  if (etJobTitle.hasFocus()) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //   imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        //   Log.d("Black 5 25", getCurrentFocus().toString());
       // }
    }
}

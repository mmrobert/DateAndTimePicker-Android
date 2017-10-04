package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.datetimepicker.DateTime;
import com.homearound.www.datetimepicker.DateTimePicker;
import com.homearound.www.datetimepicker.SimpleDateTimePicker;
import com.homearound.www.homearound.db.CustomerJob;
import com.homearound.www.homearound.db.CustomerJobDao;
import com.homearound.www.homearound.db.DaoSession;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerJobCreationActivity extends AppCompatActivity implements
        DialogFragmentJobCreationC.OnFragmentJobCreationListener, DateTimePicker.OnDateTimeSetListener {

    static final int JOB_CREATION_TITLE_EDIT_C_REQUEST = 1000;
    static final int JOB_CREATION_DETAIL_EDIT_C_REQUEST = 2000;

  //  private Long jobId;
    private String jobStatus;
    private String jobCategory;
    private String jobTitle;
    private String jobTimeFinish;
    private String jobDetail;

    private String jobTimeCreated;

 //   private TextView tvJobStatus;
    private TextView tvJobCategory;
    private TextView tvJobTitle;
    private TextView tvJobTimeFinish;
    private TextView tvJobDetail;

    private DaoSession daoSession;
    private CustomerJobDao customerJobDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_job_creation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_job_creation_c);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        jobCategory = intent.getStringExtra("jobcategory");

     //   tvJobStatus = (TextView)findViewById(R.id.job_detail_status_c);
        tvJobCategory = (TextView)findViewById(R.id.job_creation_category_c);
        tvJobTitle = (TextView)findViewById(R.id.job_creation_title_c);
        tvJobTimeFinish = (TextView)findViewById(R.id.job_creation_timefinish_c);
        tvJobDetail = (TextView)findViewById(R.id.job_creation_detail_c);

     //   tvJobStatus.setText(jobStatus);
        tvJobCategory.setText(jobCategory);
        jobTitle = "";
        jobDetail = "";

        tvJobTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerJobCreationActivity.this,
                        CustomerJobTitleEditActivity.class);
                intent.putExtra("jobtitle", jobTitle);

                CustomerJobCreationActivity.this.startActivityForResult(intent,
                        JOB_CREATION_TITLE_EDIT_C_REQUEST, null);
            }
        });

        tvJobTimeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateHere;
                if (TextUtils.isEmpty(jobTimeFinish)) {
                    dateHere = new Date();
                } else {
                    String dateFormatHere = "yyyy-MM-dd HH:mm";
                    DateTime dateTimeHere = new DateTime(dateFormatHere, jobTimeFinish);
                    dateHere = dateTimeHere.getDate();
                }
                SimpleDateTimePicker simpleDateTimePicker = SimpleDateTimePicker.make("Set Date & Time",
                        dateHere, CustomerJobCreationActivity.this,
                        CustomerJobCreationActivity.this.getSupportFragmentManager());
                simpleDateTimePicker.show();
            }
        });

        tvJobDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerJobCreationActivity.this,
                        CustomerJobDetailEditActivity.class);
                intent.putExtra("jobdetail", jobDetail);

                CustomerJobCreationActivity.this.startActivityForResult(intent,
                        JOB_CREATION_DETAIL_EDIT_C_REQUEST, null);
            }
        });

        daoSession = ((HAApplication)getApplication()).getDaoSession();
        customerJobDao = daoSession.getCustomerJobDao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_create_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_job_create_c) {
            savePostJob();
      //      Toast.makeText(this, jobCategory, Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_cancel_job_create_c) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePostJob() {

        if (TextUtils.isEmpty(jobDetail)) {
            String alertTitle = "Please enter your job detail.";
            FragmentManager fm = getSupportFragmentManager();
            OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
            okFG.show(fm, "Cheng");
        } else if (TextUtils.isEmpty(jobTimeFinish)) {
            String alertTitle = "Please choose the time to finish.";
            FragmentManager fm = getSupportFragmentManager();
            OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(alertTitle);
            okFG.show(fm, "Cheng");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentJobCreationC mDialogF = DialogFragmentJobCreationC.newInstance();
            mDialogF.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomerDialog);
            mDialogF.show(fm, "Cheng");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JOB_CREATION_TITLE_EDIT_C_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnTitle = data.getStringExtra("newJobTitle");
                if (!TextUtils.isEmpty(returnTitle)) {
                    jobTitle = returnTitle;
                    tvJobTitle.setText(returnTitle);
                }
            }
        } else if (requestCode == JOB_CREATION_DETAIL_EDIT_C_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnDetail = data.getStringExtra("newJobDetail");
                if (!TextUtils.isEmpty(returnDetail)) {
                    jobDetail = returnDetail;
                    tvJobDetail.setText(returnDetail);
                }
            }
        }
    }

    @Override
    public void dateTimeSet(Date date) {
        DateTime mDateTime = new DateTime(date);
        String dateFormatHere = "yyyy-MM-dd HH:mm";
        jobTimeFinish = mDateTime.getDateString(dateFormatHere);
        tvJobTimeFinish.setText(jobTimeFinish);
    }

    @Override
    public void onSaveJobC() {
        timeCreatingJob();
        jobStatusAndTitleSet("Saved");
        saveJobOnDatabase();
    }

    @Override
    public void onSaveAndPostJobC() {
        timeCreatingJob();
        jobStatusAndTitleSet("Posted");
        saveAndPostJobOnServer();
    }

    private void saveAndPostJobOnServer() {

        final String TAG_NET = "POST_JOB_JOB_CREATION";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Posting job...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/topostjob";

        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("jobcat", jobCategory);
        params.put("createdtime", jobTimeCreated);
        params.put("jobstatus", jobStatus);
        params.put("jobtitle", jobTitle);
        params.put("timetofinish", jobTimeFinish);
        params.put("jobdetail", jobDetail);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            saveJobOnDatabase();
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

    private void saveJobOnDatabase() {

        CustomerJob customerJob = new CustomerJob();
        customerJob.setJobtitle(jobTitle);
        customerJob.setTimecreated(jobTimeCreated);
        customerJob.setJobstatus(jobStatus);
        customerJob.setJobdetail(jobDetail);
        customerJob.setJobcategory(jobCategory);
        customerJob.setTimefinish(jobTimeFinish);
     //   customerJobDao = daoSession.getCustomerJobDao();
        customerJobDao.insert(customerJob);

        finish();
    }

    private void timeCreatingJob() {
        Date now = new Date();
        DateTime mDateTime = new DateTime(now);
        jobTimeCreated = mDateTime.getDateString();
    }

    private void jobStatusAndTitleSet(String jobStatusH) {

        if (TextUtils.isEmpty(jobTitle)) {
            jobTitle = jobCategory;
        }
        jobStatus = jobStatusH;
    }
}

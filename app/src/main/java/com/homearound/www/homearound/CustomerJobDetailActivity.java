package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerJobDetailActivity extends AppCompatActivity implements
        DialogFragmentMyJobsC.OnFragmentOkActionListener, DateTimePicker.OnDateTimeSetListener {

    static final int JOB_DETAIL_TITLE_EDIT_C_REQUEST = 100;
    static final int JOB_DETAIL_DETAIL_EDIT_C_REQUEST = 200;

    private Long jobId;
    private String jobStatus;
    private String jobCategory;
    private String jobTitle;
    private String jobTimeFinish;
    private String jobDetail;
    private String jobTimeCreated;

    private TextView tvJobStatus;
    private TextView tvJobCategory;
    private TextView tvJobTitle;
    private TextView tvJobTimeFinish;
    private TextView tvJobDetail;

    private TextView tvJobStatusChange;
    private Button bnJobDelete;

    private DaoSession daoSession;
    private CustomerJobDao customerJobDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_job_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_job_detail_c);
        setSupportActionBar(toolbar);

      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        jobId = intent.getLongExtra("jobid", 0);
        jobStatus = intent.getStringExtra("jobstatus");
        jobCategory = intent.getStringExtra("jobcategory");
        jobTitle = intent.getStringExtra("jobtitle");
        jobTimeFinish = intent.getStringExtra("jobtimefinish");
        jobDetail = intent.getStringExtra("jobdetail");
        jobTimeCreated = intent.getStringExtra("jobtimecreated");

        tvJobStatus = (TextView)findViewById(R.id.job_detail_status_c);
        tvJobCategory = (TextView)findViewById(R.id.job_detail_category_c);
        tvJobTitle = (TextView)findViewById(R.id.job_detail_title_c);
        tvJobTimeFinish = (TextView)findViewById(R.id.job_detail_timefinish_c);
        tvJobDetail = (TextView)findViewById(R.id.job_detail_detail_c);

        tvJobStatus.setText(jobStatus);
        tvJobCategory.setText(jobCategory);

        if (!TextUtils.isEmpty(jobTitle)) {
            tvJobTitle.setText(jobTitle);
        }

        if (!TextUtils.isEmpty(jobTimeFinish)) {
            tvJobTimeFinish.setText(jobTimeFinish);
        }

        if (!TextUtils.isEmpty(jobDetail)) {
            tvJobDetail.setText(jobDetail);
        }

        tvJobStatusChange = (TextView)findViewById(R.id.job_detail_change_status_c);

        if (jobStatus.equals("Saved")) {
            tvJobStatusChange.setText("Post");
            tvJobStatusChange.setClickable(true);
            tvJobStatusChange.setEnabled(true);
        } else if (jobStatus.equals("Posted")) {
            tvJobStatusChange.setText("Contract Out");
            tvJobStatusChange.setClickable(true);
            tvJobStatusChange.setEnabled(true);
        } else if (jobStatus.equals("In Progress")) {
            tvJobStatusChange.setText("Complete");
            tvJobStatusChange.setClickable(true);
            tvJobStatusChange.setEnabled(true);
        } else {
            tvJobStatusChange.setText("Take a Break");
            tvJobStatusChange.setClickable(false);
            tvJobStatusChange.setEnabled(false);
        }

        tvJobStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeJobStatusAct();
            }
        });

        bnJobDelete = (Button)findViewById(R.id.job_detail_deletejob_c);

        bnJobDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJobAct();
            }
        });

        tvJobTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerJobDetailActivity.this, CustomerJobTitleEditActivity.class);
                intent.putExtra("jobtitle", jobTitle);

                CustomerJobDetailActivity.this.startActivityForResult(intent, JOB_DETAIL_TITLE_EDIT_C_REQUEST, null);
            }
        });

        tvJobTimeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateHere;
                if (jobTimeFinish != null) {
                    if (!(jobTimeFinish.isEmpty()) && !(jobTimeFinish.equals(" "))) {
                        String dateFormatHere = "yyyy-MM-dd HH:mm";
                        DateTime dateTimeHere = new DateTime(dateFormatHere, jobTimeFinish);
                        dateHere = dateTimeHere.getDate();
                    } else {
                        dateHere = new Date();
                    }
                } else {
                    dateHere = new Date();
                }
                SimpleDateTimePicker simpleDateTimePicker = SimpleDateTimePicker.make("Set Date & Time",
                        dateHere, CustomerJobDetailActivity.this,
                        CustomerJobDetailActivity.this.getSupportFragmentManager());
                simpleDateTimePicker.show();
            }
        });

        tvJobDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerJobDetailActivity.this, CustomerJobDetailEditActivity.class);
                intent.putExtra("jobdetail", jobDetail);

                CustomerJobDetailActivity.this.startActivityForResult(intent, JOB_DETAIL_DETAIL_EDIT_C_REQUEST, null);
            }
        });

        daoSession = ((HAApplication)getApplication()).getDaoSession();
        customerJobDao = daoSession.getCustomerJobDao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_job_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_job_c) {
            if (jobStatus.equals("Saved")) {
                updateJobDatabase();
            } else {
                updateJobOnServer();
            }
            return true;
        } else if (id == R.id.action_cancel_jobupdate_c) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JOB_DETAIL_TITLE_EDIT_C_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnTitle = data.getStringExtra("newJobTitle");
                if (!TextUtils.isEmpty(returnTitle)) {
                    jobTitle = returnTitle;
                    tvJobTitle.setText(returnTitle);
                }
            }
        } else if (requestCode == JOB_DETAIL_DETAIL_EDIT_C_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnDetail = data.getStringExtra("newJobDetail");
                if (TextUtils.isEmpty(returnDetail)) {
                    jobDetail = returnDetail;
                    tvJobDetail.setText(returnDetail);
                }
            }
        }
    }

    private void updateJobOnServer() {

        final String TAG_NET = "UPDATE_JOB_JOB_DETAIL";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating job...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/updatejob";

        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
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
                            updateJobDatabase();
                            finish();
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

    private void updateJobDatabase() {
        List<CustomerJob> customerjoblist = customerJobDao.queryBuilder()
                .where(CustomerJobDao.Properties.Id.eq(jobId)).list();

        if (customerjoblist != null) {
            if (customerjoblist.size() > 0) {
                CustomerJob jobHere = customerjoblist.get(0);

                jobHere.setJobstatus(jobStatus);
                jobHere.setJobtitle(jobTitle);
                jobHere.setTimefinish(jobTimeFinish);
                jobHere.setJobdetail(jobDetail);

                customerJobDao.update(jobHere);
            }
        }
    }

    private void changeJobStatusAct() {
        String titleHere;
        String actionHere;

        titleHere = "Congratulation!";

        if (jobStatus.equals("Saved")) {
            actionHere = "Post Job";
        } else if (jobStatus.equals("Posted")) {
            actionHere = "Contract Job";
        } else if (jobStatus.equals("In Progress")) {
            actionHere = "Complete Job";
        } else {
            actionHere = "OK";
        }

        FragmentManager fm = getSupportFragmentManager();
        DialogFragmentMyJobsC myDialogF1 = DialogFragmentMyJobsC.newInstance(titleHere, actionHere);
        myDialogF1.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomerDialog);
        myDialogF1.show(fm, "Cheng");
    }

    private void deleteJobAct() {
        String titleHere;
        String actionHere;

        titleHere = "Delete This Job?";
        actionHere = "Delete";

        FragmentManager fm = getSupportFragmentManager();
        DialogFragmentMyJobsC myDialogF1 = DialogFragmentMyJobsC.newInstance(titleHere, actionHere);
        myDialogF1.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomerDialog);
        myDialogF1.show(fm, "Cheng");
    }

    public void onUpdateJobStatus() {
     //   Toast.makeText(this, "Cheng", Toast.LENGTH_LONG).show();
        if (jobStatus.equals("Saved")) {
            updateJobPostOnServer();
        } else {
            updateJobStatusOnServer();
        }
    }

    private void updateJobPostOnServer() {

        final String TAG_NET = "POST_JOB_JOB_DETAIL";

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
        params.put("jobstatus", "Posted");
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
                            updateJobStatusForUI();
                            updateJobDatabase();
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

    private void updateJobStatusOnServer() {

        final String TAG_NET = "UPDATE_JOB_STATUS_JOB_DETAIL";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating job status...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/updatejobstatus";

        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        String newJobStatusString = "";

        if (jobStatus.equals("Saved")) {
            newJobStatusString = "Posted";
        } else if (jobStatus.equals("Posted")) {
            newJobStatusString = "In Progress";
        } else if (jobStatus.equals("In Progress")) {
            newJobStatusString = "Completed";
        }

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("jobstatus", newJobStatusString);
        params.put("createdtime", jobTimeCreated);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            updateJobStatusForUI();
                            updateJobDatabase();
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

    private void updateJobStatusForUI() {
      //     String oldJobStatus = jobStatus;
        String newJobStatusStr = "";

        if (jobStatus.equals("Saved")) {
            newJobStatusStr = "Posted";
        } else if (jobStatus.equals("Posted")) {
            newJobStatusStr = "In Progress";
        } else if (jobStatus.equals("In Progress")) {
            newJobStatusStr = "Completed";
        }

        tvJobStatus.setText(newJobStatusStr);  // capitalizedString

        if (newJobStatusStr.equals("Posted")) {
            tvJobStatusChange.setText("Contract Out");
            tvJobStatusChange.setClickable(true);
            tvJobStatusChange.setEnabled(true);
        } else if (newJobStatusStr.equals("In Progress")) {
            tvJobStatusChange.setText("Complete");
            tvJobStatusChange.setClickable(true);
            tvJobStatusChange.setEnabled(true);
        } else {
            tvJobStatusChange.setText("Take a Break");
            tvJobStatusChange.setClickable(false);
            tvJobStatusChange.setEnabled(false);
        }

        jobStatus = newJobStatusStr;
    }

    public void onDeleteJob() {

        final String TAG_NET = "DELETE_JOB_JOB_DETAIL";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Deleting job...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/deletejob";

        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("createdtime", jobTimeCreated);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            deleteJobDatabase();
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

    private void deleteJobDatabase() {
        List<CustomerJob> customerjoblist = customerJobDao.queryBuilder()
                .where(CustomerJobDao.Properties.Id.eq(jobId)).list();
        if (customerjoblist != null) {
            if (customerjoblist.size() > 0) {
                CustomerJob jobHere = customerjoblist.get(0);
                customerJobDao.delete(jobHere);
            }
        }

        finish();
    }

    @Override
    public void dateTimeSet(Date date) {
        DateTime mDateTime = new DateTime(date);
        String dateFormatHere = "yyyy-MM-dd HH:mm";
        jobTimeFinish = mDateTime.getDateString(dateFormatHere);
        tvJobTimeFinish.setText(jobTimeFinish);
    }
}

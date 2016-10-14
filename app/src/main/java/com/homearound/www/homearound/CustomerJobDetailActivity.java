package com.homearound.www.homearound;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.homearound.www.datetimepicker.DateTime;
import com.homearound.www.datetimepicker.DateTimePicker;
import com.homearound.www.datetimepicker.SimpleDateTimePicker;
import com.homearound.www.homearound.db.CustomerJob;
import com.homearound.www.homearound.db.CustomerJobDao;
import com.homearound.www.homearound.db.DaoSession;

import java.util.Date;
import java.util.List;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        jobId = intent.getLongExtra("jobid", 0);
        jobStatus = intent.getStringExtra("jobstatus");
        jobCategory = intent.getStringExtra("jobcategory");
        jobTitle = intent.getStringExtra("jobtitle");
        jobTimeFinish = intent.getStringExtra("jobtimefinish");
        jobDetail = intent.getStringExtra("jobdetail");

        tvJobStatus = (TextView)findViewById(R.id.job_detail_status_c);
        tvJobCategory = (TextView)findViewById(R.id.job_detail_category_c);
        tvJobTitle = (TextView)findViewById(R.id.job_detail_title_c);
        tvJobTimeFinish = (TextView)findViewById(R.id.job_detail_timefinish_c);
        tvJobDetail = (TextView)findViewById(R.id.job_detail_detail_c);

        tvJobStatus.setText(jobStatus);
        tvJobCategory.setText(jobCategory);
        tvJobTitle.setText(jobTitle);
        tvJobTimeFinish.setText(jobTimeFinish);
        tvJobDetail.setText(jobDetail);

        tvJobStatusChange = (TextView)findViewById(R.id.job_detail_change_status_c);

        if (jobStatus.equals("Saved")) {
            tvJobStatusChange.setText("Post");
            tvJobStatusChange.setClickable(true);
        } else if (jobStatus.equals("Posted")) {
            tvJobStatusChange.setText("Contract Out");
            tvJobStatusChange.setClickable(true);
        } else if (jobStatus.equals("In Progress")) {
            tvJobStatusChange.setText("Complete");
            tvJobStatusChange.setClickable(true);
        } else {
            tvJobStatusChange.setText("Take a Break");
            tvJobStatusChange.setClickable(false);
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
                    if (!(jobTimeFinish.isEmpty()) || !(jobTimeFinish.equals(" "))) {
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
            updateJobOnServer();
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
                jobTitle = returnTitle;
                tvJobTitle.setText(returnTitle);
            }
        } else if (requestCode == JOB_DETAIL_DETAIL_EDIT_C_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnDetail = data.getStringExtra("newJobDetail");
                jobDetail = returnDetail;
                tvJobDetail.setText(returnDetail);
            }
        }
    }

    private void updateJobOnServer() {
        updateJobDatabase();
    }

    private void updateJobDatabase() {
        List<CustomerJob> customerjoblist = customerJobDao.queryBuilder()
                .where(CustomerJobDao.Properties.Id.eq(jobId)).list();
        CustomerJob jobHere = customerjoblist.get(0);

        jobHere.setJobstatus(jobStatus);
        jobHere.setJobtitle(jobTitle);
        jobHere.setTimefinish(jobTimeFinish);
        jobHere.setJobdetail(jobDetail);

        customerJobDao.update(jobHere);
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

        updateJobStatusForUI();
    }

    private void updateJobStatusOnServer() {

        updateJobStatusForUI();
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
        } else if (newJobStatusStr.equals("In Progress")) {
            tvJobStatusChange.setText("Complete");
            tvJobStatusChange.setClickable(true);
        } else {
            tvJobStatusChange.setText("Take a Break");
            tvJobStatusChange.setClickable(false);
        }

        jobStatus = newJobStatusStr;
    }

    public void onDeleteJob() {
        List<CustomerJob> customerjoblist = customerJobDao.queryBuilder()
                .where(CustomerJobDao.Properties.Id.eq(jobId)).list();
        CustomerJob jobHere = customerjoblist.get(0);
        customerJobDao.delete(jobHere);

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

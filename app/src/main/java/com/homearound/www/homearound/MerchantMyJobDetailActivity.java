package com.homearound.www.homearound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantJob;
import com.homearound.www.homearound.db.MerchantJobDao;

import java.util.List;

public class MerchantMyJobDetailActivity extends AppCompatActivity
        implements DialogFragmentMyJobsM.OnFragmentOkActionListener {

    private MerchantJobDao merchantJobDao;

    private Long jobId;
    private String jobTitle;
    private String jobName;
    private String jobTime;
    private String jobDistance;

    private String jobTimeFinish;
    private String jobDetail;

    private String jobEmail;

    private String jobStatus;

    private TextView txtStatus;
    private TextView txtChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_my_job_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_my_job_detail_m);
        setSupportActionBar(toolbar);

        DaoSession daoSession = ((HAApplication)(getApplication())).getDaoSession();
        merchantJobDao = daoSession.getMerchantJobDao();

        Intent intent = getIntent();
        jobId = intent.getLongExtra("jobid", 0);
        jobTitle = intent.getStringExtra("jobtitle");
        jobName = intent.getStringExtra("jobname");
        jobTime = intent.getStringExtra("timeposted");
        jobDistance = intent.getStringExtra("jobdistance");

        jobTimeFinish = intent.getStringExtra("timefinish");
        jobDetail = intent.getStringExtra("jobdetail");

        jobEmail = intent.getStringExtra("jobemail");

        jobStatus = intent.getStringExtra("jobstatus");

        TextView txtTitle = (TextView)findViewById(R.id.txt_my_job_detail_title_m);
        TextView txtName = (TextView)findViewById(R.id.txt_my_job_detail_name_m);
        TextView txtTime = (TextView)findViewById(R.id.txt_my_job_detail_time_m);
        TextView txtDistance = (TextView)findViewById(R.id.txt_my_job_detail_distance_m);

        TextView txtTimeFinish = (TextView)findViewById(R.id.txt_my_job_detail_timefinish_m);
        TextView txtDetail = (TextView)findViewById(R.id.txt_my_job_detail_detail_m);

        txtTitle.setText(jobTitle);
        txtName.setText(jobName);
        txtTime.setText(jobTime);
        txtDistance.setText(jobDistance);

        txtTimeFinish.setText(jobTimeFinish);
        txtDetail.setText(jobDetail);

        txtStatus = (TextView)findViewById(R.id.my_job_detail_status_m);
        txtChangeStatus = (TextView)findViewById(R.id.my_job_detail_change_status_m);

        txtStatus.setText(jobStatus.toUpperCase());

        if (jobStatus.equals("contacting")) {
            txtChangeStatus.setText("Get Contracted");
            txtChangeStatus.setClickable(true);
            txtChangeStatus.setEnabled(true);
        } else if (jobStatus.equals("active")) {
            txtChangeStatus.setText("Complete the Job");
            txtChangeStatus.setClickable(true);
            txtChangeStatus.setEnabled(true);
        } else {
            txtChangeStatus.setText("Take a Break");
            txtChangeStatus.setClickable(false);
            txtChangeStatus.setEnabled(false);
        }

        txtChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeJobStatus();
            }
        });

        Button btnSendMsg = (Button)findViewById(R.id.btn_my_job_detail_sendmsg_m);
        Button btnDeleteJob = (Button)findViewById(R.id.btn_my_job_detail_delete_m);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MerchantMyJobDetailActivity.this, SendMessageMActivity.class);
                intent1.putExtra("nametosend", jobName);
                intent1.putExtra("emailtosend", jobEmail);
                MerchantMyJobDetailActivity.this.startActivity(intent1);
            }
        });

        btnDeleteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJob();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merchant_my_job_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_my_job_detail_m) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeJobStatus() {
        String titleHere;
        String actionHere;

        titleHere = "Congratulation!";

        if (jobStatus.equals("contacting")) {
            actionHere = "Get Contracted";
        } else if (jobStatus.equals("active")) {
            actionHere = "Complete the Job";
        } else {
            actionHere = "OK";
        }

        FragmentManager fm = getSupportFragmentManager();
        DialogFragmentMyJobsM myDialogF1 = DialogFragmentMyJobsM.newInstance(titleHere, actionHere);
        myDialogF1.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomerDialog);
        myDialogF1.show(fm, "Cheng");
    }

    @Override
    public void onUpdateJobStatus() {

        String newJobStatusStr = "";
        if (jobStatus.equals("contacting")) {
            newJobStatusStr = "active";
        } else if (jobStatus.equals("active")) {
            newJobStatusStr = "closed";
        }
        txtStatus.setText(newJobStatusStr.toUpperCase());

        if (newJobStatusStr.equals("contacting")) {
            txtChangeStatus.setText("Get Contracted");
            txtChangeStatus.setClickable(true);
            txtChangeStatus.setEnabled(true);
        } else if (newJobStatusStr.equals("active")) {
            txtChangeStatus.setText("Complete the Job");
            txtChangeStatus.setClickable(true);
            txtChangeStatus.setEnabled(true);
        } else {
            txtChangeStatus.setText("Take a Break");
            txtChangeStatus.setClickable(false);
            txtChangeStatus.setEnabled(false);
        }

        jobStatus = newJobStatusStr;

        List<MerchantJob> jobList = merchantJobDao.queryBuilder()
                .where(MerchantJobDao.Properties.Id.eq(jobId)).list();

        if (jobList != null) {
            if (jobList.size() > 0) {
                MerchantJob jobHere = jobList.get(0);

                jobHere.setJobstatus(jobStatus);

                merchantJobDao.update(jobHere);
            }
        }
    }

    private void deleteJob() {
        String titleHere;
        String actionHere;

        titleHere = "Delete This Job?";
        actionHere = "Delete";

        FragmentManager fm = getSupportFragmentManager();
        DialogFragmentMyJobsM myDialogF1 = DialogFragmentMyJobsM.newInstance(titleHere, actionHere);
        myDialogF1.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomerDialog);
        myDialogF1.show(fm, "Cheng");
    }

    @Override
    public void onDeleteJob() {
        List<MerchantJob> jobList = merchantJobDao.queryBuilder()
                .where(MerchantJobDao.Properties.Id.eq(jobId)).list();
        if (jobList != null) {
            if (jobList.size() > 0) {
                MerchantJob jobHere = jobList.get(0);
                merchantJobDao.delete(jobHere);

                finish();
            }
        }
    }
}

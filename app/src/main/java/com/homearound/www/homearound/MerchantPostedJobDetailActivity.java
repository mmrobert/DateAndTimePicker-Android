package com.homearound.www.homearound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantJob;
import com.homearound.www.homearound.db.MerchantJobDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class MerchantPostedJobDetailActivity extends AppCompatActivity {

    private MerchantJobDao merchantJobDao;

    private String jobTitle;
    private String jobName;
    private String jobTime;
    private String jobDistance;

    private String jobTimeFinish;
    private String jobDetail;

    private String jobEmail;

    private ImageView imgHeart;

    private List<MerchantJob> matchedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_posted_job_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_posted_job_detail_m);
        setSupportActionBar(toolbar);

        DaoSession daoSession = ((HAApplication)(getApplication())).getDaoSession();
        merchantJobDao = daoSession.getMerchantJobDao();

        matchedEmail = new ArrayList<MerchantJob>();

        Intent intent = getIntent();
        jobTitle = intent.getStringExtra("jobtitle");
        jobName = intent.getStringExtra("jobname");
        jobTime = intent.getStringExtra("timeposted");
        jobDistance = intent.getStringExtra("jobdistance");

        jobTimeFinish = intent.getStringExtra("timefinish");
        jobDetail = intent.getStringExtra("jobdetail");

        jobEmail = intent.getStringExtra("jobemail");

        TextView txtTitle = (TextView)findViewById(R.id.txt_posted_job_detail_title_m);
        TextView txtName = (TextView)findViewById(R.id.txt_posted_job_detail_name_m);
        TextView txtTime = (TextView)findViewById(R.id.txt_posted_job_detail_time_m);
        TextView txtDistance = (TextView)findViewById(R.id.txt_posted_job_detail_distance_m);

        TextView txtTimeFinish = (TextView)findViewById(R.id.txt_posted_job_detail_timefinish_m);
        TextView txtDetail = (TextView)findViewById(R.id.txt_posted_job_detail_detail_m);

        txtTitle.setText(jobTitle);
        txtName.setText(jobName);
        txtTime.setText(jobTime);
        txtDistance.setText(jobDistance);

        txtTimeFinish.setText(jobTimeFinish);
        txtDetail.setText(jobDetail);

        imgHeart = (ImageView)findViewById(R.id.img_posted_job_detail_heart_m);
        Button btnSendMsg = (Button)findViewById(R.id.btn_posted_job_detail_sendmsg_m);

        imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHeart.setImageResource(R.drawable.heartfull);
                imgHeart.setClickable(false);
                imgHeart.setEnabled(false);

                MerchantJob merchantJobNew = new MerchantJob();
                merchantJobNew.setEmail(jobEmail);
                merchantJobNew.setName(jobName);
                merchantJobNew.setDistance(jobDistance);
                merchantJobNew.setJobtitle(jobTitle);
                merchantJobNew.setTimepost(jobTime);
                merchantJobNew.setTimefinish(jobTimeFinish);
                merchantJobNew.setJobdetail(jobDetail);
                merchantJobNew.setJobstatus("contacting");
                merchantJobDao.insert(merchantJobNew);
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MerchantPostedJobDetailActivity.this, SendMessageMActivity.class);
                intent1.putExtra("nametosend", jobName);
                intent1.putExtra("emailtosend", jobEmail);
                MerchantPostedJobDetailActivity.this.startActivity(intent1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        matchedEmail.clear();

        List<MerchantJob> matched = merchantJobDao.queryBuilder()
                .where(MerchantJobDao.Properties.Email.eq(jobEmail),
                        MerchantJobDao.Properties.Timepost.eq(jobTime)).list();
        matchedEmail.addAll(matched);

        if (matchedEmail.size() > 0) {
            imgHeart.setImageResource(R.drawable.heartfull);
            imgHeart.setClickable(false);
            imgHeart.setEnabled(false);
        } else {
            imgHeart.setImageResource(R.drawable.heart);
            imgHeart.setClickable(true);
            imgHeart.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merchant_posted_job_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_posted_job_detail_m) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

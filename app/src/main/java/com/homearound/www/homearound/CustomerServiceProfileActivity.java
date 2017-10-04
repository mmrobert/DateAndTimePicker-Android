package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.homearound.db.CustomerService;
import com.homearound.www.homearound.db.CustomerServiceDao;
import com.homearound.www.homearound.db.DaoSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceProfileActivity extends AppCompatActivity {

    final int SERVICE_DETAIL_REQUEST = 5885;
    final int SERVICE_FIND_REQUEST = 6886;

    private RecyclerView recyclerView;
    private AdapterServiceProfileC adapter;
    private int detailFindCode;
    private String serviceName;
    private String serviceEmail;
    private String serviceDistance;
    private String serviceCat;
    private String serviceDetail;
    private boolean hasReview;
    private String rating;
    private String reviewNo;

    private boolean alreadyMyService;
    private JSONArray reviewList;

    private CustomerServiceDao customerServiceDao;
    private List<CustomerService> matchSvcEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_service_profile_c);
        setSupportActionBar(toolbar);

        DaoSession daoSession = ((HAApplication)(getApplication())).getDaoSession();
        customerServiceDao = daoSession.getCustomerServiceDao();

        Intent intent = getIntent();
        detailFindCode = intent.getIntExtra("detailfindcode", 0);
        serviceName = intent.getStringExtra("servicename");
        serviceEmail = intent.getStringExtra("serviceemail");
        serviceDistance = intent.getStringExtra("servicedistance");
        serviceCat = intent.getStringExtra("servicecategory");
        serviceDetail = intent.getStringExtra("servicedetail");
        hasReview = intent.getBooleanExtra("hasReview", false);
        rating = intent.getStringExtra("rating");
        reviewNo = intent.getStringExtra("reviewNo");

        matchSvcEmail = customerServiceDao.queryBuilder()
                .where(CustomerServiceDao.Properties.Email.eq(serviceEmail)).list();

        if (detailFindCode == SERVICE_DETAIL_REQUEST) {
            alreadyMyService = true;
        } else {
            if (matchSvcEmail != null) {
                if (matchSvcEmail.size() > 0) {
                    alreadyMyService = true;
                } else {
                    alreadyMyService = false;
                }
            } else {
                alreadyMyService = false;
            }
        }
        reviewList = new JSONArray();

        recyclerView = (RecyclerView)findViewById(R.id.rvItem_service_profile_c);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new AdapterServiceProfileC(hasReview, rating, reviewNo, serviceName,
                serviceDistance, serviceDetail, serviceCat, alreadyMyService, reviewList,
                new AdapterServiceProfileC.OnLikeSendClickListener() {
                    @Override
                    public void onLikeClick() {
                        CustomerService customerService = new CustomerService();
                        customerService.setEmail(serviceEmail);
                        customerService.setName(serviceName);
                        customerService.setDistance(serviceDistance);
                        customerService.setDetail(serviceDetail);
                        customerService.setCategory(serviceCat);
                        customerServiceDao.insert(customerService);

                        matchSvcEmail = customerServiceDao.queryBuilder()
                                .where(CustomerServiceDao.Properties.Email.eq(serviceEmail)).list();
                        alreadyMyService = true;
                        adapter.setAlreadyMyService(alreadyMyService);
                     //   adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSendClick() {
                        Intent intent1 = new Intent(CustomerServiceProfileActivity.this, SendMessageCActivity.class);
                        intent1.putExtra("nametosend", serviceName);
                        intent1.putExtra("emailtosend", serviceEmail);
                        CustomerServiceProfileActivity.this.startActivity(intent1);
                    }
                });
        recyclerView.setAdapter(adapter);

        if (hasReview) {
            loadingReviews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_service_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int COMPOSE_REVIEW_REQUEST = 222;

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_writing_rating_c) {
            Intent intent = new Intent(this, CustomerComposeReviewActivity.class);
            intent.putExtra("emailforreview", serviceEmail);
            startActivityForResult(intent, COMPOSE_REVIEW_REQUEST);
            return true;
        } else if (id == R.id.action_cancel_service_profile_c) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadingReviews() {

        final String TAG_NET = "LOAD_REVIEWS_SERVICE_PROFILE_C";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading reviews...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/reviewload";
        UserDefaultManager userDefaultManager =
                new UserDefaultManager(getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);
        params.put("emailreview", serviceEmail);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();

                            int iexisted = reviewList.length();
                            if (iexisted > 0) {
                                for (int ii = iexisted; ii > 0; ii--) {
                                    reviewList.remove(ii - 1);
                                }
                            }

                            JSONArray returnListH = response.optJSONArray("reviewlist");
                            if (returnListH != null) {
                                int arrLengthH = returnListH.length();
                                if (arrLengthH > 0) {
                                    for (int i = 0; i < arrLengthH; i++) {
                                        reviewList.put(returnListH.optJSONObject(i));
                                    }
                                    //       adapter.notifyDataSetChanged();
                                }
                            }

                            hasReview = response.optBoolean("hasReview");
                            rating = response.optString("ratings");
                            reviewNo = response.optString("reviewNo");
                            adapter.setHasReview(hasReview);
                            adapter.setOverallRate(rating);
                            adapter.setReviewNo(reviewNo);
                            adapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        final int COMPOSE_REVIEW_REQUEST = 222;
        if (requestCode == COMPOSE_REVIEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                loadingReviews();
            }
        }
    }
}

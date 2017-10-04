package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.homearound.www.homearound.db.CustomerService;
import com.homearound.www.homearound.db.CustomerServiceDao;
import com.homearound.www.homearound.db.DaoSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerFindServiceLoadedActivity extends AppCompatActivity {

    final int SERVICE_FIND_REQUEST = 6886;

    private RVLayoutManager rvLayoutManager;

    private String jobCategory;

    private boolean moreLoading;
    private String lastLoadingNo;
    private int lastShowRowNo;

    private RecyclerView recyclerView;
    private AdapterServicesLoadedC adapterServicesLoadedC;
    private List<CustomerService> collectedServiceList;
    private CustomerServiceDao customerServiceDao;

    private JSONArray downLoadedServiceList;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_find_service_loaded);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_find_service_loaded_c);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        jobCategory = intent.getStringExtra("jobcategory");
        moreLoading =  false;
        lastShowRowNo = 0;

        getSupportActionBar().setTitle(jobCategory);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        collectedServiceList = new ArrayList<CustomerService>();
        downLoadedServiceList = new JSONArray();

        DaoSession daoSession = ((HAApplication)getApplication()).getDaoSession();
        customerServiceDao = daoSession.getCustomerServiceDao();

     //   customerServiceList = customerServiceDao.queryBuilder()
     //           .orderDesc(CustomerServiceDao.Properties.Distance).list();

        recyclerView = (RecyclerView)findViewById(R.id.rvItem_services_loaded_c);

      //  LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLayoutManager = new RVLayoutManager(this);
        recyclerView.setLayoutManager(rvLayoutManager);

        adapterServicesLoadedC = new AdapterServicesLoadedC(jobCategory, downLoadedServiceList,
                collectedServiceList, new AdapterServicesLoadedC.OnItemClickAndLikeListener() {
            @Override
            public void onItemClick(JSONObject item, int itemClickPos) {

                lastShowRowNo = itemClickPos;

                Intent intent = new Intent(CustomerFindServiceLoadedActivity.this,
                        CustomerServiceProfileActivity.class);
                intent.putExtra("detailfindcode", SERVICE_FIND_REQUEST);
                intent.putExtra("servicename", item.optString("name"));
                intent.putExtra("serviceemail", item.optString("email"));
                intent.putExtra("servicedistance", item.optString("distance"));
                intent.putExtra("servicecategory", jobCategory);
                intent.putExtra("servicedetail", item.optString("detail"));
                intent.putExtra("hasReview", item.optBoolean("hasReview"));
                intent.putExtra("rating", item.optString("rating"));
                intent.putExtra("reviewNo", item.optString("reviewNo"));

                Context context = CustomerFindServiceLoadedActivity.this;
                context.startActivity(intent);
            }

            @Override
            public void onLike(JSONObject item, int itemClickPos) {
                CustomerService customerService = new CustomerService();
                customerService.setEmail(item.optString("email"));
                customerService.setName(item.optString("name"));
                customerService.setDistance(item.optString("distance"));
                customerService.setDetail(item.optString("detail"));
                customerService.setCategory(jobCategory);
                customerServiceDao.insert(customerService);

                collectedServiceList.clear();
                List<CustomerService> newServiceList = customerServiceDao.queryBuilder()
                        .orderDesc(CustomerServiceDao.Properties.Distance).list();
                collectedServiceList.addAll(newServiceList);
            }
        });
        recyclerView.setAdapter(adapterServicesLoadedC);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (moreLoading) {
                    if (dy > 0) {
                        int totalItems = rvLayoutManager.getItemCount();
                        int lastVisibleItem = rvLayoutManager.findLastVisibleItemPosition();

                        if (lastVisibleItem == totalItems - 1) {
                            loadServiceMore(lastLoadingNo, "20");
                        }
                    }
                }
            }
        });

        lastLoadingNo = "0";
        loadServiceMore(lastLoadingNo, "20");
    }

    @Override
    protected void onResume() {
        super.onResume();
        collectedServiceList.clear();
        List<CustomerService> newServiceList = customerServiceDao.queryBuilder()
                .orderDesc(CustomerServiceDao.Properties.Distance).list();
        collectedServiceList.addAll(newServiceList);
        adapterServicesLoadedC.notifyDataSetChanged();
        if (adapterServicesLoadedC.getItemCount() > 0 && lastShowRowNo > 0) {
            recyclerView.smoothScrollToPosition(lastShowRowNo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_find_service_loaded, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_find_service_loaded_c) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadServiceMore(String loadedNo, String noOfLoad) {

        final String TAG_NET = "LOAD_SERVICES_FIND_C";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Services...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/serviceload";
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        params.put("token", mToken);
        params.put("loadednumber", loadedNo);
        params.put("numberofLoad", noOfLoad);
        params.put("servicecat", jobCategory);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");

                        if (success) {
                            pDialog.hide();
                            moreLoading = response.optBoolean("moreloads");
                            lastLoadingNo = Integer.toString(response.optInt("loadedsvc"));
                            JSONArray tempReturnList = response.optJSONArray("svclist");
                            if (tempReturnList != null) {
                                int tempCount = tempReturnList.length();
                                if (tempCount > 0) {
                                    for (int i = 0; i < tempCount; i++) {
                                        downLoadedServiceList.put(tempReturnList.optJSONObject(i));
                                    }
                                    lastShowRowNo = downLoadedServiceList.length() - tempCount;
                                    adapterServicesLoadedC.notifyItemRangeInserted(lastShowRowNo, tempCount);
                                    //   adapterServicesLoadedC.notifyDataSetChanged();
                                    if (lastShowRowNo > 0) {
                                        recyclerView.smoothScrollToPosition(lastShowRowNo);
                                    }
                                }
                            }
                        } else {
                            pDialog.hide();
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
}

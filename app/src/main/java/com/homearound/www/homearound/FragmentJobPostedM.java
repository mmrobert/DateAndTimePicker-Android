package com.homearound.www.homearound;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentJobPostedM extends Fragment {

    static final int JOB_POSTED_SVC_CAT_CHOOSE_M = 13038;

    private JSONArray jobPostedList;
    private AdapterJobPostedM adapterJobPostedM;

    private RecyclerView recyclerView;

    private RVLayoutManager rvLayoutManager;

    private boolean moreLoading;
    private String lastLoadingNo;
    private int lastShowRowNo;

    private String jobCatStr;
    private TextView txtJobCatShow;
    private RelativeLayout relJobCat;

    private UserDefaultManager userDefaultManager;

    public FragmentJobPostedM() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_posted_m, container, false);

        getActivity().setTitle("Jobs Posted");

        userDefaultManager = new UserDefaultManager(FragmentJobPostedM.this
                .getActivity().getApplicationContext());

        String temppp = userDefaultManager.getMerchantField();
        if (TextUtils.isEmpty(temppp)) {
            String tempxxx = userDefaultManager.getUserExpertise();
            if (TextUtils.isEmpty(tempxxx)) {
                jobCatStr = "All";
            } else {
                String[] tempArr = tempxxx.split(",\n");
                jobCatStr = tempArr[0];
            }
        } else {
            jobCatStr = temppp;
        }
        txtJobCatShow = (TextView)view.findViewById(R.id.txt_job_posted_cat_m);
        txtJobCatShow.setText(jobCatStr);

        relJobCat = (RelativeLayout)view.findViewById(R.id.rel_job_posted_cat_m);

        relJobCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentJobPostedM.this.getActivity(),
                        MerchantSvcCatActivity.class);
                //   intent.putExtra("jobtitle", jobTitle);
                intent.putExtra("paramStr", jobCatStr);
                intent.putExtra("requestWhat", JOB_POSTED_SVC_CAT_CHOOSE_M);
                FragmentJobPostedM.this.startActivityForResult(intent,
                        JOB_POSTED_SVC_CAT_CHOOSE_M, null);
            }
        });

        moreLoading =  false;
        lastShowRowNo = 0;

        jobPostedList = new JSONArray();

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_job_posted_m);

        rvLayoutManager = new RVLayoutManager(FragmentJobPostedM.this.getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);

        adapterJobPostedM = new AdapterJobPostedM(jobPostedList, new AdapterJobPostedM.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject jobPostedItem, int itemClickPos) {

                Intent intent = new Intent(FragmentJobPostedM.this.getActivity(),
                        MerchantPostedJobDetailActivity.class);
                intent.putExtra("jobtitle", jobPostedItem.optString("jobTitle"));
                intent.putExtra("jobname", jobPostedItem.optString("name"));
                intent.putExtra("timeposted", jobPostedItem.optString("timePosted"));
                intent.putExtra("jobdistance", jobPostedItem.optString("distance"));

                intent.putExtra("timefinish", jobPostedItem.optString("timeFinish"));
                intent.putExtra("jobdetail", jobPostedItem.optString("detail"));

                intent.putExtra("jobemail", jobPostedItem.optString("email"));

                Context context = FragmentJobPostedM.this.getActivity();
                context.startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapterJobPostedM);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (moreLoading) {
                    if (dy > 0) {
                        int totalItems = rvLayoutManager.getItemCount();
                        int lastVisibleItem = rvLayoutManager.findLastVisibleItemPosition();

                        if (lastVisibleItem == totalItems - 1) {
                            loadingJobPostedMore(lastLoadingNo, "20");
                        }
                    }
                }
            }
        });

        lastLoadingNo = "0";
        loadingJobPostedMore(lastLoadingNo, "20");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_job_posted_m, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh_job_posted_m) {

            moreLoading =  false;
            lastShowRowNo = 0;

            int arrLength = jobPostedList.length();
            for (int i = arrLength; i > 0; i--) {
                jobPostedList.remove(i - 1);
            }
            adapterJobPostedM.notifyDataSetChanged();

            lastLoadingNo = "0";
            loadingJobPostedMore(lastLoadingNo, "20");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JOB_POSTED_SVC_CAT_CHOOSE_M) {
            if (resultCode == Activity.RESULT_OK) {
                jobCatStr = userDefaultManager.getMerchantField();
                txtJobCatShow.setText(jobCatStr);

                moreLoading =  false;
                lastShowRowNo = 0;

                int arrLength = jobPostedList.length();
                for (int i = arrLength; i > 0; i--) {
                    jobPostedList.remove(i - 1);
                }
                adapterJobPostedM.notifyDataSetChanged();

                lastLoadingNo = "0";
                loadingJobPostedMore(lastLoadingNo, "20");
                //  Log.d("REQUEST_CAMERA_LOG", imgString.length() + "");
                // Toast.makeText(this, imgString.length(), Toast.LENGTH_LONG);
            }
        }
    }

    private void loadingJobPostedMore(String loadedNo, String noOfLoad) {

        final String TAG_NET = "LOAD_Jobs_Posted_M";

        final ProgressDialog pDialog = new ProgressDialog(FragmentJobPostedM.this.getActivity());
        pDialog.setMessage("Loading Posted Jobs...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/merchant/jobsload";

        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        params.put("token", mToken);
        params.put("loadednumber", loadedNo);
        params.put("numberofLoad", noOfLoad);
        params.put("jobcat", jobCatStr);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");

                        if (success) {
                            pDialog.hide();
                            moreLoading = response.optBoolean("moreloads");
                            lastLoadingNo = Integer.toString(response.optInt("loadedjobs"));
                            JSONArray tempReturnList = response.optJSONArray("list");
                            if (tempReturnList != null) {
                                int tempCount = tempReturnList.length();
                                if (tempCount > 0) {
                                    for (int i = 0; i < tempCount; i++) {
                                        jobPostedList.put(tempReturnList.optJSONObject(i));
                                    }
                                    lastShowRowNo = jobPostedList.length() - tempCount;
                                    adapterJobPostedM.notifyItemRangeInserted(lastShowRowNo, tempCount);
                                    //  adapterJobPostedM.notifyDataSetChanged();
                                    if (lastShowRowNo > 0) {
                                        recyclerView.smoothScrollToPosition(lastShowRowNo);
                                    }
                                }
                            }
                        } else {
                            pDialog.hide();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
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

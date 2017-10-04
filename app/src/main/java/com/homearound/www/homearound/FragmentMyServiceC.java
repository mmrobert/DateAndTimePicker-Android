package com.homearound.www.homearound;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.homearound.www.homearound.db.CustomerService;
import com.homearound.www.homearound.db.CustomerServiceDao;
import com.homearound.www.homearound.db.DaoSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyServiceC extends Fragment {

    final int SERVICE_DETAIL_REQUEST = 5885;

    private RecyclerView recyclerView;
    private AdapterMyServicesC adapterMyServicesC;
    private List<CustomerService> customerServiceList;
    private CustomerServiceDao customerServiceDao;

    private ArrayList<String> emailSvc;
    private JSONArray ratingDownloaded;

    public FragmentMyServiceC() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_service_c, container, false);

        getActivity().setTitle("My Services");

        DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();
        customerServiceDao = daoSession.getCustomerServiceDao();

     //   generateDummyData();

      //  customerServiceList = customerServiceDao.queryBuilder()
      //          .orderDesc(CustomerServiceDao.Properties.Distance).list();
        customerServiceList = new ArrayList<CustomerService>();
        emailSvc = new ArrayList<String>();
        ratingDownloaded = new JSONArray();

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_MyServices_C);

        LinearLayoutManager manager = new LinearLayoutManager(FragmentMyServiceC.this.getActivity());
        recyclerView.setLayoutManager(manager);

        adapterMyServicesC = new AdapterMyServicesC(FragmentMyServiceC.this.getActivity(),
                customerServiceList, ratingDownloaded,
                new AdapterMyServicesC.OnItemClickListener() {
            @Override
            public void onItemClick(CustomerService customerService, JSONObject rates) {
                //         Toast.makeText(FragmentMyJobsC.this.getActivity(),
                //                 "Item Clicked" + customerJob.getJobdetail(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FragmentMyServiceC.this.getActivity(),
                        CustomerServiceProfileActivity.class);
                intent.putExtra("detailfindcode", SERVICE_DETAIL_REQUEST);
                intent.putExtra("servicename", customerService.getName());
                intent.putExtra("serviceemail", customerService.getEmail());
                intent.putExtra("servicedistance", customerService.getDistance());
                intent.putExtra("servicecategory", customerService.getCategory());
                intent.putExtra("servicedetail", customerService.getDetail());
                intent.putExtra("hasReview", rates.optBoolean("hasReview"));
                intent.putExtra("rating", rates.optString("rating"));
                intent.putExtra("reviewNo", rates.optString("reviewNo"));

                Context context = FragmentMyServiceC.this.getActivity();
                context.startActivity(intent);

            }

            @Override
            public void onDeleteSvc(final CustomerService customerService) {
                String alertTitle = "Delete the Service?";
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragmentMyServicesC dFMyServicesC = DialogFragmentMyServicesC.newInstance(alertTitle);

                dFMyServicesC.setOnDeleteServiceListener(new DialogFragmentMyServicesC.OnDeleteServiceListener() {
                    @Override
                    public void onDeleteService() {
                     //   List<CustomerJob> customerjoblist = customerJobDao.queryBuilder()
                     //           .where(CustomerJobDao.Properties.Id.eq(jobId)).list();
                     //   CustomerJob jobHere = customerjoblist.get(0);
                        customerServiceDao.delete(customerService);
                        customerServiceList.clear();
                        List<CustomerService> newServiceList = customerServiceDao.queryBuilder()
                                .orderDesc(CustomerServiceDao.Properties.Distance).list();
                        customerServiceList.addAll(newServiceList);
                        adapterMyServicesC.notifyDataSetChanged();
                    }
                });

                dFMyServicesC.show(fm, "Cheng");
            }
        });
        recyclerView.setAdapter(adapterMyServicesC);

        Button btnFindService = (Button)view.findViewById(R.id.btn_find_service_c);
        btnFindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentMyServiceC.this.getActivity(),
                        CustomerFindServiceCategoryActivity.class);
                //   intent.putExtra("jobid", customerJob.getId());

                Context context = FragmentMyServiceC.this.getActivity();
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        customerServiceList.clear();
        emailSvc.clear();
        List<CustomerService> newServiceList = customerServiceDao.queryBuilder()
                .orderDesc(CustomerServiceDao.Properties.Distance).list();
        customerServiceList.addAll(newServiceList);
      //  adapterMyServicesC.notifyDataSetChanged();
        //  recyclerView.invalidate();
        int svcNo = customerServiceList.size();
        if (svcNo > 0) {
            for (int i = 0; i < svcNo; i++) {
                String tempEmail = customerServiceList.get(i).getEmail();
                if (!emailSvc.contains(tempEmail)) {
                    emailSvc.add(tempEmail);
                }
            }
            loadingRating();
        }
    }

    private void loadingRating() {

        final String TAG_NET = "LOAD_RATES_MY_SERVICE_C";

        final ProgressDialog pDialog = new ProgressDialog(FragmentMyServiceC.this.getActivity());
        pDialog.setMessage("Loading Services...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/servicerating";
        UserDefaultManager userDefaultManager =
                new UserDefaultManager(FragmentMyServiceC.this.getActivity().getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        JSONArray mySvcEmail = new JSONArray();
        int emailNo = emailSvc.size();
        for (int ii = 0; ii < emailNo; ii++) {
            mySvcEmail.put(emailSvc.get(ii));
        }

        JSONObject postedData = new JSONObject();
        try {
            postedData.put("token", mToken);
            postedData.put("emailarr", mySvcEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, postedData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            int iexisted = ratingDownloaded.length();
                            if (iexisted > 0) {
                                for (int ii = iexisted; ii > 0; ii--) {
                                    ratingDownloaded.remove(ii - 1);
                                }
                            }
                            JSONArray ratingReturnH = response.optJSONArray("ratelist");
                            if (ratingReturnH != null) {
                                int arrLengthH = ratingReturnH.length();
                                if (arrLengthH > 0) {
                                    for (int i = 0; i < arrLengthH; i++) {
                                        ratingDownloaded.put(ratingReturnH.optJSONObject(i));
                                    }
                                    adapterMyServicesC.notifyDataSetChanged();
                                }
                            }
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                         //   FragmentManager fm = getActivity().getSupportFragmentManager();
                         //   OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(mMessage);
                         //   okFG.show(fm, "Cheng");
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

    private void generateDummyData() {
        CustomerService customerService = new CustomerService();
        customerService.setCategory("Duct Cleaning");
        customerService.setName("Richmond Inc.");
        customerService.setDistance("50.6 Km");
        customerService.setDetail("This is a professional company which is specilized in house cleaning and remodel the bathroom and garage. Good quality and price.");
        customerService.setEmail("home@home.ca");

        customerServiceDao.insert(customerService);
    }
}

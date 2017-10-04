package com.homearound.www.homearound;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.homearound.www.homearound.db.CustomerJob;
import com.homearound.www.homearound.db.CustomerJobDao;
import com.homearound.www.homearound.db.DaoSession;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyJobsC extends Fragment {

    private RecyclerView recyclerView;
    private AdapterMyJobsC adapterMyJobsC;
    private List<CustomerJob> customerjoblist;
    private CustomerJobDao customerJobDao;

    public FragmentMyJobsC() {
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
        View view = inflater.inflate(R.layout.fragment_my_jobs_c, container, false);

        getActivity().setTitle("My Jobs");

        DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();

        //   generateDummyData();

        customerJobDao = daoSession.getCustomerJobDao();

        customerjoblist = new ArrayList<CustomerJob>();

      //  List<CustomerJob> customerjoblist = customerJobDao.loadAll();
        List<CustomerJob> existedlistH = customerJobDao.queryBuilder()
                .orderDesc(CustomerJobDao.Properties.Timecreated).list();
        customerjoblist.addAll(existedlistH);

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_MyJobs_C);

      //  recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(FragmentMyJobsC.this.getActivity());
        recyclerView.setLayoutManager(manager);
        adapterMyJobsC = new AdapterMyJobsC(FragmentMyJobsC.this.getActivity(),
                customerjoblist, new AdapterMyJobsC.OnItemClickListener() {
            @Override
            public void onItemClick(CustomerJob customerJob) {
       //         Toast.makeText(FragmentMyJobsC.this.getActivity(),
       //                 "Item Clicked" + customerJob.getJobdetail(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FragmentMyJobsC.this.getActivity(), CustomerJobDetailActivity.class);
                intent.putExtra("jobid", customerJob.getId());
                intent.putExtra("jobstatus", customerJob.getJobstatus());
                intent.putExtra("jobcategory", customerJob.getJobcategory());
                intent.putExtra("jobtitle", customerJob.getJobtitle());
                intent.putExtra("jobtimefinish", customerJob.getTimefinish());
                intent.putExtra("jobdetail", customerJob.getJobdetail());
                intent.putExtra("jobtimecreated", customerJob.getTimecreated());

                Context context = FragmentMyJobsC.this.getActivity();
                context.startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapterMyJobsC);

        Button createJob = (Button)view.findViewById(R.id.btn_create_new_job_c);
        createJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentMyJobsC.this.getActivity(),
                        CustomerServiceCategorySelectionActivity.class);
                //   intent.putExtra("jobid", customerJob.getId());

                Context context = FragmentMyJobsC.this.getActivity();
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        customerjoblist.clear();
        List<CustomerJob> newJobList = customerJobDao.queryBuilder()
                .orderDesc(CustomerJobDao.Properties.Timecreated).list();
        customerjoblist.addAll(newJobList);
        adapterMyJobsC.notifyDataSetChanged();
      //  recyclerView.invalidate();
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      //  super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_customer_myjobs_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_new_job_c) {
            Intent intent = new Intent(FragmentMyJobsC.this.getActivity(),
                    CustomerServiceCategorySelectionActivity.class);
         //   intent.putExtra("jobid", customerJob.getId());

            Context context = FragmentMyJobsC.this.getActivity();
            context.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private void generateDummyData() {
        CustomerJob customerJob = new CustomerJob();
        customerJob.setJobtitle("Cleaning");
        customerJob.setTimecreated("Oct 18 2016");
        customerJob.setJobstatus("Posted");
        customerJob.setJobdetail("This is a new job, and total three floors all.");

        customerJobDao.insert(customerJob);
    }
}

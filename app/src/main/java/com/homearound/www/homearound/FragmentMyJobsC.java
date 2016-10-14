package com.homearound.www.homearound;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.homearound.www.homearound.db.CustomerJob;
import com.homearound.www.homearound.db.CustomerJobDao;
import com.homearound.www.homearound.db.DaoSession;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_jobs_c, container, false);

        DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();

     //   generateDummyData(daoSession);

        customerJobDao = daoSession.getCustomerJobDao();

      //  List<CustomerJob> customerjoblist = customerJobDao.loadAll();
        customerjoblist = customerJobDao.queryBuilder()
                .orderDesc(CustomerJobDao.Properties.Timecreated).list();

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

                Context context = FragmentMyJobsC.this.getActivity();
                context.startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapterMyJobsC);

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

    private void generateDummyData(DaoSession daoSession) {
        CustomerJob customerJob = new CustomerJob();
        customerJob.setJobtitle("Cleaning");
        customerJob.setTimecreated("Oct 18 2016");
        customerJob.setJobstatus("Posted");
        customerJob.setJobdetail("This is a new job, and total three floors all.");
        CustomerJobDao customerJobDao = daoSession.getCustomerJobDao();
        customerJobDao.insert(customerJob);
    }
}

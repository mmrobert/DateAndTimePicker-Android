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

import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantJob;
import com.homearound.www.homearound.db.MerchantJobDao;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PVFGMyJobsActiveM extends Fragment {

    private RecyclerView recyclerView;
    private PVFGAdapterRVMyJobsM pvfgAdapterRVMyJobsM;
    private List<MerchantJob> merchantJobList;
    private MerchantJobDao merchantJobDao;

    public PVFGMyJobsActiveM() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pvfg_my_jobs_active_m, container, false);

        DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();
        merchantJobDao = daoSession.getMerchantJobDao();

     //   generateDummyData();

        merchantJobList = new ArrayList<MerchantJob>();

        List<MerchantJob> mListed = merchantJobDao.queryBuilder()
                .where(MerchantJobDao.Properties.Jobstatus.eq("active"))
                .orderAsc(MerchantJobDao.Properties.Name).list();

        merchantJobList.addAll(mListed);

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_my_jobs_active_m);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        pvfgAdapterRVMyJobsM = new PVFGAdapterRVMyJobsM(merchantJobList,
                new PVFGAdapterRVMyJobsM.OnItemClickListener() {
            @Override
            public void onItemClick(MerchantJob jobItem, int itemClickPos) {
                Intent intent = new Intent(getActivity(),
                        MerchantMyJobDetailActivity.class);
                intent.putExtra("jobid", jobItem.getId());
                intent.putExtra("jobtitle", jobItem.getJobtitle());
                intent.putExtra("jobname", jobItem.getName());
                intent.putExtra("timeposted", jobItem.getTimepost());
                intent.putExtra("jobdistance", jobItem.getDistance());

                intent.putExtra("timefinish", jobItem.getTimefinish());
                intent.putExtra("jobdetail", jobItem.getJobdetail());

                intent.putExtra("jobemail", jobItem.getEmail());

                intent.putExtra("jobstatus", jobItem.getJobstatus());

                Context context = getActivity();
                context.startActivity(intent);
            }
        });

        recyclerView.setAdapter(pvfgAdapterRVMyJobsM);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        merchantJobList.clear();
        List<MerchantJob> mListed = merchantJobDao.queryBuilder()
                .where(MerchantJobDao.Properties.Jobstatus.eq("active"))
                .orderAsc(MerchantJobDao.Properties.Name).list();

        merchantJobList.addAll(mListed);

        pvfgAdapterRVMyJobsM.notifyDataSetChanged();
    }

    private void generateDummyData() {
        MerchantJob mj = new MerchantJob();
        mj.setJobtitle("House Moving");
        mj.setJobstatus("active");
        mj.setDistance("6.6 km");
        mj.setName("R 222");
        mj.setTimefinish("1 22 2016");
        merchantJobDao.insert(mj);
    }
}

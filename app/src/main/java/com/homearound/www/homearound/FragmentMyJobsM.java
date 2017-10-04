package com.homearound.www.homearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyJobsM extends Fragment {


    public FragmentMyJobsM() {
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
        View view = inflater.inflate(R.layout.fragment_my_jobs_m, container, false);

        getActivity().setTitle("My Jobs");

        ViewPager viewPager = (ViewPager)view.findViewById(R.id.vp_my_jobs_m);

      //  DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();
      //  Context context = getActivity();

     //   PVFGAdapterMyJobsM adapter =
     //           new PVFGAdapterMyJobsM(getActivity().getSupportFragmentManager());
        PVFGAdapterMyJobsM adapter =
                new PVFGAdapterMyJobsM(getChildFragmentManager());

        adapter.addPVFGTitle("Contacting");
        adapter.addPVFGTitle("Active");
        adapter.addPVFGTitle("Completed");

        PVFGMyJobsContactingM contactingM = new PVFGMyJobsContactingM();
        PVFGMyJobsActiveM activeM = new PVFGMyJobsActiveM();
        PVFGMyJobsCompletedM completedM = new PVFGMyJobsCompletedM();

        adapter.addPVFGFragment(contactingM);
        adapter.addPVFGFragment(activeM);
        adapter.addPVFGFragment(completedM);

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tl_my_jobs_m);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}

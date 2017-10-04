package com.homearound.www.homearound;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentContactUsC extends Fragment {


    public FragmentContactUsC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contact_us_c, container, false);

        getActivity().setTitle("Contact Us");

        Button btnEmail = (Button)view.findViewById(R.id.btn_contact_us_c);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      Log.d("Robert Cheng", "This is ok");
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"boqian.cheng@yahoo.com"});
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Report issues");
                intentEmail.putExtra(Intent.EXTRA_TEXT, "Report here");

                intentEmail.setType("message/rfc822");

                if (intentEmail.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intentEmail, "Choose email client..."));
                }
            }
        });

        return view;
    }

}

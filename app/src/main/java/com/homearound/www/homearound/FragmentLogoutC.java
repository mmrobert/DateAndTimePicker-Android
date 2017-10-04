package com.homearound.www.homearound;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogoutC extends Fragment {

    private UserDefaultManager userDefaultManager;

    public FragmentLogoutC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout_c, container, false);

        getActivity().setTitle("Log Out");

        userDefaultManager =
                new UserDefaultManager(FragmentLogoutC.this.getActivity().getApplicationContext());

        Button btnLogout = (Button)view.findViewById(R.id.btn_logout_logout_c);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               logoutC();
            }
        });

        return view;
    }

    private void logoutC() {

        final String TAG_NET = "LOG_OUT_C";

        final ProgressDialog pDialog = new ProgressDialog(FragmentLogoutC.this.getActivity());
        pDialog.setMessage("Logging out...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/logout";
        final String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", mToken);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();
                            userDefaultManager.logoutCustomer();
                            toFlashAuth();
                        } else {
                            pDialog.hide();
                            FragmentManager fm = FragmentLogoutC.this.getActivity().getSupportFragmentManager();
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
        /*
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //  return super.getParams();
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", mToken);

                return params;
            }
        };
        */
        HAApplication.getInstance().addToRequestQueue(jsonReq, TAG_NET);
    }

    private void toFlashAuth() {
        Intent intent = new Intent(FragmentLogoutC.this.getActivity(), SplashAuthActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
        Context context = FragmentLogoutC.this.getActivity();
        context.startActivity(intent);
    }
}

package com.homearound.www.homearound;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
public class FragmentRatingsReviewsM extends Fragment {

    private RecyclerView recyclerView;
    private AdapterRatingsReviewsM adapter;
    private boolean hasReview;
    private String rating;
    private String reviewNo;

    private JSONArray reviewList;

    public FragmentRatingsReviewsM() {
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
        View view = inflater.inflate(R.layout.fragment_ratings_reviews_m, container, false);

        getActivity().setTitle("Ratings and Reviews");

        reviewList = new JSONArray();
        hasReview = false;
        rating = "0";
        reviewNo = "0";

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_ratings_reviews_m);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new AdapterRatingsReviewsM(hasReview, rating, reviewNo, reviewList);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingReviews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_ratings_reviews_m, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh_ratings_reviews_m) {

            loadingReviews();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadingReviews() {

        final String TAG_NET = "LOAD_REVIEWS_RATINGS_REVIEWS_M";

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading reviews...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/merchant/reviewload";
        UserDefaultManager userDefaultManager =
                new UserDefaultManager(getActivity().getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("token", mToken);

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
                         //   FragmentManager fm = getChildFragmentManager();
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
}

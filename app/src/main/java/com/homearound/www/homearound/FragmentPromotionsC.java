package com.homearound.www.homearound;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPromotionsC extends Fragment {

    private ArrayList<String> downLoadedProList;
    private AdapterPromotionsC adapterPromotionsC;

    private RecyclerView recyclerView;

    private RVLayoutManager rvLayoutManager;

    private boolean moreLoading;
    private String lastLoadingNo;
    private int lastShowRowNo;

    public FragmentPromotionsC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promotions_c, container, false);

        getActivity().setTitle("Promotions");

        moreLoading = false;
        lastShowRowNo = 0;

        downLoadedProList = new ArrayList<String>();
/*
        Bitmap bmHere = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmHere.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String strHere = Base64.encodeToString(b, Base64.DEFAULT);

        downLoadedProList.add(strHere);
        downLoadedProList.add(strHere);
*/
        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_promotions_c);

        rvLayoutManager = new RVLayoutManager(FragmentPromotionsC.this.getActivity());
        recyclerView.setLayoutManager(rvLayoutManager);

        adapterPromotionsC = new AdapterPromotionsC(downLoadedProList, new AdapterPromotionsC.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int itemClickPos) {

            }
        });

        recyclerView.setAdapter(adapterPromotionsC);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (moreLoading) {
                    if (dy > 0) {
                        int totalItems = rvLayoutManager.getItemCount();
                        int lastVisibleItem = rvLayoutManager.findLastVisibleItemPosition();

                        if (lastVisibleItem == totalItems - 1) {
                            loadingProMore(lastLoadingNo, "20");
                        }
                    }
                }
            }
        });

        lastLoadingNo = "0";
        loadingProMore(lastLoadingNo, "20");

        return view;
    }

    private void loadingProMore(String loadedNo, String noOfLoad) {

        final String TAG_NET = "LOAD_PROMOTIONS_C";

        final ProgressDialog pDialog = new ProgressDialog(FragmentPromotionsC.this.getActivity());
        pDialog.setMessage("Loading Promotions...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/promotionload";
        UserDefaultManager userDefaultManager =
                new UserDefaultManager(FragmentPromotionsC.this.getActivity().getApplicationContext());
        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        params.put("token", mToken);
        params.put("loadednumber", loadedNo);
        params.put("numberofLoad", noOfLoad);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");

                        if (success) {
                            pDialog.hide();
                            moreLoading = response.optBoolean("morepromotion");
                            lastLoadingNo = Integer.toString(response.optInt("loadedpromo"));
                            JSONArray tempReturnList = response.optJSONArray("list");
                            if (tempReturnList != null) {
                                int tempCount = tempReturnList.length();
                                if (tempCount > 0) {
                                    for (int i = 0; i < tempCount; i++) {
                                        downLoadedProList.add(tempReturnList.optString(i));
                                    }
                                    lastShowRowNo = downLoadedProList.size() - tempCount;

                                  //  adapterPromotionsC.notifyItemRangeInserted(lastShowRowNo, tempCount);
                                    adapterPromotionsC.notifyDataSetChanged();
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

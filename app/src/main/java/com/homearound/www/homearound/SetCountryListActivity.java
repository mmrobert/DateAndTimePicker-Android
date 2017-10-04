package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SetCountryListActivity extends AppCompatActivity {

    private ArrayList<String> mCountries;
    private ArrayAdapter<String> adapter;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_country_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_country_list);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        ListView mListView = (ListView)findViewById(R.id.lv_set_country_list);

        mCountries = new ArrayList<String>();
      //  mCountries.add("Canada");
      //  mCountries.add("United States");
        //  Arrays.sort(svcCategory);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCountries);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = adapter.getItem(position);
                //      Toast.makeText(CustomerServiceCategorySelectionActivity.this,
                //              selectedItem + " selected!", Toast.LENGTH_LONG).show();

                Intent responseIntent = new Intent();
                responseIntent.putExtra("country", selectedCountry);
                setResult(RESULT_OK, responseIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadingCountryList();
    }

    private void loadingCountryList() {

        final String TAG_NET = "LOAD_COUNTRY_LIST_SET";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading country list...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/countrylist";
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

                            JSONArray countryLists = response.optJSONArray("list");
                            int arrLength = countryLists.length();
                            if (arrLength > 0) {
                                mCountries.clear();
                                for (int ii = 0; ii < arrLength; ii++) {
                                    mCountries.add(countryLists.optString(ii));
                                }
                                Collections.sort(mCountries);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                         //   FragmentManager fm = getSupportFragmentManager();
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

package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by boqiancheng on 2016-10-16.
 */

public class CustomerServiceCategorySelectionActivity extends AppCompatActivity {

    private ArrayList<String> svcCategory;
    private ArrayAdapter<String> adapter;
    private ListView mListView;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_category_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_service_selection_c);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        mListView = (ListView)findViewById(R.id.lv_service_selection_c);

        svcCategory = new ArrayList<String>();
      //  svcCategory = new String[] {"General Cleaning", "Duct Cleaning", "Window Cleaning"};
      //  Arrays.sort(svcCategory);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, svcCategory);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = adapter.getItem(position);
          //      Toast.makeText(CustomerServiceCategorySelectionActivity.this,
          //              selectedItem + " selected!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CustomerServiceCategorySelectionActivity.this,
                        CustomerJobCreationActivity.class);
                intent.putExtra("jobcategory", selectedCategory);

                Context context = CustomerServiceCategorySelectionActivity.this;
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String mName = userDefaultManager.getUserName();
        String mPostCode = userDefaultManager.getUserPostCode();

        if (TextUtils.isEmpty(mPostCode)) {
            String alertStr = "Please enter your postal code in the Settings to create a job.";
            FragmentManager fm = getSupportFragmentManager();
            OkActionDialogFragment okFG = OkActionDialogFragment.newInstance(alertStr);
            okFG.setOnOkClickListener(new OkActionDialogFragment.OnOkClickListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            okFG.show(fm, "Cheng");
        } else if (TextUtils.isEmpty(mName)) {
            String alertStr = "Please enter your name in the setting to create a job.";
            FragmentManager fm = getSupportFragmentManager();
            OkActionDialogFragment okFG = OkActionDialogFragment.newInstance(alertStr);
            okFG.setOnOkClickListener(new OkActionDialogFragment.OnOkClickListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            okFG.show(fm, "Cheng");
        } else {
            loadingServiceList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_select_service_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_service_selection_c) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
      //  String selectedItem = svcCategory[position];
        String selectedItem = (String)getListAdapter().getItem(position);
        Toast.makeText(this, selectedItem + " selected!", Toast.LENGTH_LONG).show();
    }
*/

    private void loadingServiceList() {

        final String TAG_NET = "LOAD_SERVICE_LIST_CREATE_JOB";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading service list...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/servicelist";
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

                            JSONArray svcLists = response.optJSONArray("list");
                            int arrLength = svcLists.length();
                            if (arrLength > 0) {
                                svcCategory.clear();
                                for (int ii = 0; ii < arrLength; ii++) {
                                    svcCategory.add(svcLists.optString(ii));
                                }
                                Collections.sort(svcCategory);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                        //    FragmentManager fm = getSupportFragmentManager();
                        //    OkConfirmDialogFragment okFG = OkConfirmDialogFragment.newInstance(mMessage);
                        //    okFG.show(fm, "Cheng");
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

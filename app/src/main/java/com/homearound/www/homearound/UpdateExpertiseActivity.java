package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

public class UpdateExpertiseActivity extends AppCompatActivity {

    private ArrayList<String> svcCategory;
    private ArrayAdapter<String> adapter;
    private ListView mListView;

    private ArrayList<String> checkedSvcList;

    private List<String> expertiseSettedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expertise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_update_expertise);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String expertiseSetted = intent.getStringExtra("paramStr");

        expertiseSettedList = new ArrayList<String>();

        if (!TextUtils.isEmpty(expertiseSetted)) {
            String[] tempArr = expertiseSetted.split(",\n");
            expertiseSettedList = Arrays.asList(tempArr);
        }

        checkedSvcList = new ArrayList<String>();

        mListView = (ListView)findViewById(R.id.lv_update_expertise);

        svcCategory = new ArrayList<String>();
     //   svcCategory.add("Duct Cleaning");
     //   svcCategory.add("General Cleaning");
     //   svcCategory.add("Sofa Cleaning");
     //   svcCategory.add("Table Cleaning");
     //   svcCategory.add("Window Cleaning");
        //  svcCategory = new String[] {"General Cleaning", "Duct Cleaning", "Window Cleaning"};
        //  Arrays.sort(svcCategory);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, svcCategory);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(adapter);

        loadingServiceList();
      //  preselectRow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_expertise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done_update_expertise) {

            SparseBooleanArray checkedSvc = mListView.getCheckedItemPositions();
            int checkedCount = checkedSvc.size();
            for (int ii = 0; ii < checkedCount; ii++) {
                int checkedPosition = checkedSvc.keyAt(ii);
                if (checkedSvc.valueAt(ii)) {
                    checkedSvcList.add(adapter.getItem(checkedPosition));
                }
            }

            if (checkedSvcList.size() < 1) {
                String alertTitle = "Choose your expertise to save.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                mDialog.show(fm, "Cheng");
            } else {
              //  saveExpertiseOnServer();
                StringBuilder expertsBuilder = new StringBuilder();
                int loopNo = checkedSvcList.size();
                for (int i = 0; i < loopNo; i++) {
                    if (i < loopNo - 1) {
                        expertsBuilder.append(checkedSvcList.get(i));
                        expertsBuilder.append(",\n");
                    } else {
                        expertsBuilder.append(checkedSvcList.get(i));
                    }
                }

                Intent responseIntent = new Intent();
                responseIntent.putExtra("newValue", expertsBuilder.toString());
                setResult(RESULT_OK, responseIntent);
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadingServiceList() {

        final String TAG_NET = "LOAD_SERVICE_LIST_UPDATE_EXPERTISE";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading service list...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/servicelist";
        UserDefaultManager userDefaultManager = new UserDefaultManager(getApplicationContext());
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

                                preselectRow();
                            }
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                            FragmentManager fm = getSupportFragmentManager();
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

    private void preselectRow() {
        if (expertiseSettedList != null) {
            int totalSvc = svcCategory.size();
            for (int i = 0; i < totalSvc; i++) {
                String tempStr = svcCategory.get(i);
                if (expertiseSettedList.contains(tempStr)) {
                    mListView.setItemChecked(i, true);
                }
            }
        }
    }
}

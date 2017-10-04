package com.homearound.www.homearound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SetExpertiseActivity extends AppCompatActivity {

    private ArrayList<String> svcCategory;
    private ArrayAdapter<String> adapter;
    private ListView mListView;

    private ArrayList<String> checkedSvcList;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_expertise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_expertise);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        checkedSvcList = new ArrayList<String>();

        mListView = (ListView)findViewById(R.id.lv_set_expertise);

        svcCategory = new ArrayList<String>();
        //  svcCategory = new String[] {"General Cleaning", "Duct Cleaning", "Window Cleaning"};
        //  Arrays.sort(svcCategory);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, svcCategory);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(adapter);

        Button btnLater = (Button)findViewById(R.id.btn_later_set_expertise);

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetBioPage();
            }
        });

        loadingServiceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_expertise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_set_expertise) {

            SparseBooleanArray checkedSvc = mListView.getCheckedItemPositions();
            if (checkedSvc != null) {
                int checkedCount = checkedSvc.size();
                for (int ii = 0; ii < checkedCount; ii++) {
                    int checkedPosition = checkedSvc.keyAt(ii);
                    if (checkedSvc.valueAt(ii)) {
                        checkedSvcList.add(adapter.getItem(checkedPosition));
                    }
                }
            }

            if (checkedSvcList.size() < 1) {
                String alertTitle = "Choose your expertise to save.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                mDialog.show(fm, "Cheng");
            } else {
                saveExpertiseOnServer();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadingServiceList() {

        final String TAG_NET = "LOAD_SERVICE_LIST_SET_EXPERTISE";

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

    private void saveExpertiseOnServer() {

        final String TAG_NET = "SAVE_SET_EXPERTISE";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Saving expertise...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/setexpertise";
        String mToken = userDefaultManager.getUserToken();

        JSONArray selectedSvc = new JSONArray();
        int selectedNo = checkedSvcList.size();
        for (int ii = 0; ii < selectedNo; ii++) {
            selectedSvc.put(checkedSvcList.get(ii));
        }

        JSONObject postedData = new JSONObject();
        try {
            postedData.put("expertise", selectedSvc);
            postedData.put("token", mToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, postedData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();

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
                            userDefaultManager.setUserExpertise(expertsBuilder.toString());
                            toSetBioPage();
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

    private void toSetBioPage() {

        Intent intent = new Intent(this, SetBioActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}

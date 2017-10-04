package com.homearound.www.homearound;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.homearound.www.homearound.db.DaoSession;
import com.homearound.www.homearound.db.MerchantMessage;
import com.homearound.www.homearound.db.MerchantMessageBox;
import com.homearound.www.homearound.db.MerchantMessageBoxDao;
import com.homearound.www.homearound.db.MerchantMessageDao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMsgBoxM extends Fragment {

    private RecyclerView recyclerView;
    private AdapterMsgBoxM adapterMsgBoxM;
    private List<MerchantMessageBox> messageBoxList;
    private int editDeleteCode;

    private MerchantMessageBoxDao messageBoxDao;
    private MerchantMessageDao messageDao;

    public FragmentMsgBoxM() {
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
        View view = inflater.inflate(R.layout.fragment_msg_box_m, container, false);

        getActivity().setTitle("Message Box");

        DaoSession daoSession = ((HAApplication)(getActivity().getApplication())).getDaoSession();
        messageBoxDao = daoSession.getMerchantMessageBoxDao();
        messageDao = daoSession.getMerchantMessageDao();

        //  generateDummyData();

        messageBoxList = new ArrayList<MerchantMessageBox>();
        messageBoxList.clear();

        List<MerchantMessageBox> boxesListH = messageBoxDao.queryBuilder()
                .orderDesc(MerchantMessageBoxDao.Properties.Timelastmessage).list();
        messageBoxList.addAll(boxesListH);

        recyclerView = (RecyclerView)view.findViewById(R.id.rvItem_msg_box_m);

        editDeleteCode = 0;

        //  recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapterMsgBoxM = new AdapterMsgBoxM(messageBoxList, editDeleteCode,
                new AdapterMsgBoxM.OnItemClickAndDeleteListener() {
                    @Override
                    public void onItemClick(MerchantMessageBox boxItem, int itemClickPos) {
                        Intent intent = new Intent(getActivity(),
                                PersonalMsgMActivity.class);

                        intent.putExtra("name", boxItem.getName());
                        intent.putExtra("email", boxItem.getEmail());

                        Context context = getActivity();
                        context.startActivity(intent);

                        //   Log.d("Robert =", "this is ok 99click");
                    }

                    @Override
                    public void onDelete(MerchantMessageBox boxItem, int itemClickPos) {
                        String emailForDelete = boxItem.getEmail();

                        messageBoxDao.delete(boxItem);
                        List<MerchantMessageBox> newMList = messageBoxDao.queryBuilder()
                                .orderDesc(MerchantMessageBoxDao.Properties.Timelastmessage).list();
                        messageBoxList.clear();
                        messageBoxList.addAll(newMList);

                        deleteMsgs(emailForDelete);

                        adapterMsgBoxM.notifyDataSetChanged();
                        //  Log.d("Robert =", "this is ok 88--delete");
                    }
                });

        recyclerView.setAdapter(adapterMsgBoxM);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingMsg();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_msg_box_m, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_msg_box_m) {

            if (editDeleteCode == 0) {
                editDeleteCode = 1;
                item.setTitle("Done");
                adapterMsgBoxM.setNormalOrDelete(editDeleteCode);
                adapterMsgBoxM.notifyDataSetChanged();
            } else if (editDeleteCode == 1) {
                editDeleteCode = 0;
                item.setTitle("Edit");
                adapterMsgBoxM.setNormalOrDelete(editDeleteCode);
                adapterMsgBoxM.notifyDataSetChanged();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadingMsg() {

        final String TAG_NET = "LOADING_MSG_BOX_M";

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading messages...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/merchant/messagesload";
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
                            boolean hasNewMsg = response.optBoolean("hasTheReturn");
                            if (hasNewMsg) {
                                JSONArray newMsgList = response.optJSONArray("msglist");
                                int arrLength = newMsgList.length();
                                for (int ii = 0; ii < arrLength; ii++) {
                                    JSONObject msgItem = newMsgList.optJSONObject(ii);
                                    String nameStr = msgItem.optString("name");
                                    String emailStr = msgItem.optString("email");
                                    String timeStr = msgItem.optString("time");
                                    String msgStr = msgItem.optString("msg");

                                    saveMsgDatabase(nameStr, emailStr, timeStr, msgStr);
                                }
                                fetchBoxList();
                            }

                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
                            //   FragmentManager fm = FragmentMsgBoxC.this.getActivity().getSupportFragmentManager();
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

    private void saveMsgDatabase(String nameS, String emailS, String timeS, String msgS) {

        MerchantMessage merchantMessage = new MerchantMessage();

        merchantMessage.setName(nameS);
        merchantMessage.setEmail(emailS);
        merchantMessage.setMessagetime(timeS);
        merchantMessage.setMessagebody(msgS);
        merchantMessage.setDirection("in");

        messageDao.insert(merchantMessage);

        List<MerchantMessageBox> existedBox = messageBoxDao.queryBuilder()
                .where(MerchantMessageBoxDao.Properties.Email.eq(emailS)).list();
        if (existedBox != null) {
            if (existedBox.size() > 0) {
                MerchantMessageBox oldBox = existedBox.get(0);
                oldBox.setTimelastmessage(timeS);
                oldBox.setLastmessage(msgS);
                messageBoxDao.update(oldBox);
            } else {
                MerchantMessageBox newBox = new MerchantMessageBox();
                newBox.setName(nameS);
                newBox.setEmail(emailS);
                newBox.setTimelastmessage(timeS);
                newBox.setLastmessage(msgS);
                messageBoxDao.insert(newBox);
            }
        } else {
            MerchantMessageBox newBox = new MerchantMessageBox();
            newBox.setName(nameS);
            newBox.setEmail(emailS);
            newBox.setTimelastmessage(timeS);
            newBox.setLastmessage(msgS);
            messageBoxDao.insert(newBox);
        }
    }

    private void fetchBoxList() {
        List<MerchantMessageBox> newMList = messageBoxDao.queryBuilder()
                .orderDesc(MerchantMessageBoxDao.Properties.Timelastmessage).list();
        messageBoxList.clear();
        messageBoxList.addAll(newMList);

        adapterMsgBoxM.notifyDataSetChanged();
    }

    private void deleteMsgs(String emailSS) {

        List<MerchantMessage> msgsToDelete = messageDao.queryBuilder()
                .where(MerchantMessageDao.Properties.Email.eq(emailSS)).list();
        if (msgsToDelete != null) {
            int totalDelete = msgsToDelete.size();
            if (totalDelete > 0) {
                for (int i = 0; i < totalDelete; i++) {
                    messageDao.delete(msgsToDelete.get(i));
                }
            }
        }
    }
}

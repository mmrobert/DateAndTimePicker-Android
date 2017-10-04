package com.homearound.www.homearound;

import android.annotation.TargetApi;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.homearound.www.homearound.db.DaoMaster;
import com.homearound.www.homearound.db.DaoSession;

/**
 * Created by boqiancheng on 2016-10-07.
 */
public class HAApplication extends Application {

    public static final String TAG = HAApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static HAApplication mInstance;

    private DaoSession daoSession;

    private String urlHttpHome;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "homehome", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        mInstance = this;

       // urlHttpHome = "http://208.75.74.101:3000";
        urlHttpHome = "http://208.75.74.101:3000";
    }

    public static synchronized HAApplication getInstance() {
        return mInstance;
    }

    public String getUrlHttpHome() {
        return urlHttpHome;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}

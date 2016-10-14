package com.homearound.www.homearound;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.homearound.www.homearound.db.DaoMaster;
import com.homearound.www.homearound.db.DaoSession;

/**
 * Created by boqiancheng on 2016-10-07.
 */
public class HAApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "homehome", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

package com.homearound.www.homearound;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by boqiancheng on 2016-10-23.
 */

public class UserDefaultManager {

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_FILE_NAME = "HomeAroundPref";

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;

    private static final String MY_TOKEN = "userToken";
    private static final String MY_EMAIL = "userEmail";
    private static final String MY_PASSWORD = "userPassword";
    private static final String MY_ROLE = "userRole";
    private static final String MY_NICKNAME = "userNickname";
    private static final String MY_PHOTO = "userPhoto";
    private static final String MY_EXPERTISE = "userExpertise";
    private static final String MY_BIO = "userBio";
    private static final String MY_POSTAL_CODE = "userPostalCode";
    private static final String MY_PHONE = "userPhone";
    private static final String MY_COUNTRY = "userCountry";
    private static final String MY_DEVICE_TOKEN = "userDeviceToken";
    private static final String MY_REMOTE_NOTIFICATION_ALLOW = "userRemoteNotificationAllow";
    private static final String MERCHANT_FIELD = "merchantField";
    private static final String MY_ID = "userId";
    private static final String MY_SEX = "userSex";

    public UserDefaultManager(Context context) {
        this.mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    public void setUserToken(String token) {
        mEditor.putString(MY_TOKEN, token);
        mEditor.commit();
    }

    public String getUserToken() {
        return mPreferences.getString(MY_TOKEN, "0");
    }

    public void setUserEmail(String email) {
        mEditor.putString(MY_EMAIL, email);
        mEditor.commit();
    }

    public String getUserEmail() {
        return mPreferences.getString(MY_EMAIL, null);
    }

    public void setUserPassword(String password) {
        mEditor.putString(MY_PASSWORD, password);
        mEditor.commit();
    }

    public String getUserPassword() {
        return mPreferences.getString(MY_PASSWORD, null);
    }

    public void setUserRole(String role) {
        mEditor.putString(MY_ROLE, role);
        mEditor.commit();
    }

    public String getUserRole() {
        return mPreferences.getString(MY_ROLE, "0");
    }

    public void setUserName(String name) {
        mEditor.putString(MY_NICKNAME, name);
        mEditor.commit();
    }

    public String getUserName() {
        return mPreferences.getString(MY_NICKNAME, null);
    }

    public void setUserPostCode(String postcode) {
        mEditor.putString(MY_POSTAL_CODE, postcode);
        mEditor.commit();
    }

    public String getUserPostCode() {
        return mPreferences.getString(MY_POSTAL_CODE, null);
    }

    public void setUserCountry(String country) {
        mEditor.putString(MY_COUNTRY, country);
        mEditor.commit();
    }

    public String getUserCountry() {
        return mPreferences.getString(MY_COUNTRY, null);
    }

    public void setUserPhone(String phone) {
        mEditor.putString(MY_PHONE, phone);
        mEditor.commit();
    }

    public String getUserPhone() {
        return mPreferences.getString(MY_PHONE, null);
    }

    public void setUserExpertise(String expertise) {
        mEditor.putString(MY_EXPERTISE, expertise);
        mEditor.commit();
    }

    public String getUserExpertise() {
        return mPreferences.getString(MY_EXPERTISE, null);
    }

    public void setUserBio(String bio) {
        mEditor.putString(MY_BIO, bio);
        mEditor.commit();
    }

    public String getUserBio() {
        return mPreferences.getString(MY_BIO, null);
    }

    public void setUserPhoto(String photo) {
        mEditor.putString(MY_PHOTO, photo);
        mEditor.commit();
    }

    public String getUserPhoto() {
        return mPreferences.getString(MY_PHOTO, null);
    }

    public void setUserDeviceToken(String deviceToken) {
        mEditor.putString(MY_DEVICE_TOKEN, deviceToken);
        mEditor.commit();
    }

    public String getUserDeviceToken() {
        return mPreferences.getString(MY_DEVICE_TOKEN, null);
    }

    public void setUserRemoteNotificationAllow(boolean remoteNotificationAllow) {
        mEditor.putBoolean(MY_REMOTE_NOTIFICATION_ALLOW, remoteNotificationAllow);
        mEditor.commit();
    }

    public boolean getUserRemoteNotificationAllow() {
        return mPreferences.getBoolean(MY_REMOTE_NOTIFICATION_ALLOW, false);
    }

    public void setMerchantField(String merchantField) {
        mEditor.putString(MERCHANT_FIELD, merchantField);
        mEditor.commit();
    }

    public String getMerchantField() {
        return mPreferences.getString(MERCHANT_FIELD, null);
    }

    public void logoutCustomer() {
        mEditor.remove(MY_TOKEN);
        mEditor.remove(MY_EMAIL);
        mEditor.remove(MY_PASSWORD);
        mEditor.remove(MY_ROLE);

        mEditor.commit();
    }

    public void logoutMerchant() {
        mEditor.remove(MY_TOKEN);
        mEditor.remove(MY_EMAIL);
        mEditor.remove(MY_PASSWORD);
        mEditor.remove(MY_ROLE);
        mEditor.remove(MY_EXPERTISE);
        mEditor.remove(MY_BIO);

        mEditor.commit();
    }
}

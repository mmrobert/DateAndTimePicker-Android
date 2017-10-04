package com.homearound.www.homearound;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettingsC extends Fragment implements DialogFragmentPhoto.OnFragmentPhotoListener,
        DialogFragmentPhotoPermission.OnPhotoPermissionListener {

    static final int UPDATE_COUNTRY_CHOOSE_C_REQUEST = 1066;
    final int UPDATE_NAME_C_REQUEST = 52808;
    final int UPDATE_POST_CODE_C_REQUEST = 52818;

    private UserDefaultManager userDefaultManager;

    private String cameraOrGallery;

    private ImageView imgPhoto;
    private String imgString;

    private TextView txtName;
    private TextView txtPostCode;

    private TextView txtCountry;

    public FragmentSettingsC() {
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
        View view = inflater.inflate(R.layout.fragment_settings_c, container, false);

        getActivity().setTitle("Settings");

        userDefaultManager = new UserDefaultManager(getActivity().getApplicationContext());

        imgPhoto = (ImageView)view.findViewById(R.id.img_settings_photo_customer);

        String strImg = userDefaultManager.getUserPhoto();
        if (!TextUtils.isEmpty(strImg)) {
            byte[] decodedByte = Base64.decode(strImg, Base64.DEFAULT);
            Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            imgPhoto.setImageBitmap(bitmapImg);
        }

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = FragmentSettingsC.this.getActivity().getSupportFragmentManager();
                DialogFragmentPhoto mDialogF = DialogFragmentPhoto.newInstance();
                mDialogF.setOnFragmentPhotoListener(FragmentSettingsC.this);
                mDialogF.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomerDialog);
                mDialogF.show(fm, "Cheng");
            }
        });

        txtName = (TextView) view.findViewById(R.id.txt_settings_name_c);
        txtPostCode = (TextView) view.findViewById(R.id.txt_settings_post_code_c);

        txtCountry = (TextView)view.findViewById(R.id.txt_settings_country_c);

        String strName = userDefaultManager.getUserName();
        if (!TextUtils.isEmpty(strName)) {
            txtName.setText(strName);
        }
        String strPostCode = userDefaultManager.getUserPostCode();
        if (!TextUtils.isEmpty(strPostCode)) {
            txtPostCode.setText(strPostCode);
        }
        String strCountry = userDefaultManager.getUserCountry();
        if (!TextUtils.isEmpty(strCountry)) {
            txtCountry.setText(strCountry);
        }

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentSettingsC.this.getActivity(),
                        UpdateNamePostCodeCustomerActivity.class);
                String strHereName = txtName.getText().toString();
                intent.putExtra("paramStr", strHereName);
                intent.putExtra("requestWhat", UPDATE_NAME_C_REQUEST);

                FragmentSettingsC.this.startActivityForResult(intent,
                        UPDATE_NAME_C_REQUEST, null);
            }
        });

        txtPostCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentSettingsC.this.getActivity(),
                        UpdateNamePostCodeCustomerActivity.class);
                String strHerePostCode = txtPostCode.getText().toString();
                intent.putExtra("paramStr", strHerePostCode);
                intent.putExtra("requestWhat", UPDATE_POST_CODE_C_REQUEST);

                FragmentSettingsC.this.startActivityForResult(intent,
                        UPDATE_POST_CODE_C_REQUEST, null);
            }
        });

        txtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentSettingsC.this.getActivity(),
                        SetCountryListActivity.class);
                //   intent.putExtra("jobtitle", jobTitle);
                String strHereCountry = txtCountry.getText().toString();
                intent.putExtra("paramStr", strHereCountry);
                intent.putExtra("requestWhat", UPDATE_COUNTRY_CHOOSE_C_REQUEST);
                FragmentSettingsC.this.startActivityForResult(intent,
                        UPDATE_COUNTRY_CHOOSE_C_REQUEST, null);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_settings_customer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save_settings_customer) {

            updateSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSettings() {

        String mName = txtName.getText().toString().trim();
        String mPostCode = txtPostCode.getText().toString().trim();
        String mCountry = txtCountry.getText().toString();

        if (TextUtils.isEmpty(mName)) {
            String alertTitle = "Enter your name to update.";
            FragmentManager fm = getActivity().getSupportFragmentManager();
            OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
            mDialog.show(fm, "Cheng");
        } else if (TextUtils.isEmpty(mPostCode)) {
            String alertTitle = "Enter your postal code to update.";
            FragmentManager fm = getActivity().getSupportFragmentManager();
            OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
            mDialog.show(fm, "Cheng");
        } else if (TextUtils.isEmpty(mCountry)) {
            String alertTitle = "Choose your country to update.";
            FragmentManager fm = getActivity().getSupportFragmentManager();
            OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
            mDialog.show(fm, "Cheng");
        } else {
            if (mCountry.equals("Canada")) {
                if (mPostCode.length() > 3 && mPostCode.length() < 7) {
                    StringBuilder strH = new StringBuilder(mPostCode);
                    strH.insert(3, " ");
                    txtPostCode.setText(strH.toString().toUpperCase(Locale.ENGLISH));
                    mPostCode = strH.toString().toUpperCase(Locale.ENGLISH);
                } else {
                    txtPostCode.setText(mPostCode.toUpperCase(Locale.ENGLISH));
                    mPostCode = mPostCode.toUpperCase(Locale.ENGLISH);
                }

                String canadaRegex = "^[a-zA-Z][0-9][a-zA-Z][- ]*[0-9][a-zA-Z][0-9]$";
                if (!mPostCode.matches(canadaRegex)) {
                    String alertTitle = "Not right postal code format.";
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                    mDialog.show(fm, "Cheng");
                } else {
                    updateSettingsOnServer();
                    //   toSetPhotoPage();
                }
            } else if (mCountry.equals("United States")) {
                txtPostCode.setText(mPostCode);
                String usaRegex = "^[0-9]{5}(-[0-9]{4})?$";
                if (!mPostCode.matches(usaRegex)) {
                    String alertTitle = "Not right postal code format.";
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                    mDialog.show(fm, "Cheng");
                } else {
                    updateSettingsOnServer();
                    //  toSetPhotoPage();
                }
            }
        }
    }

    private void updateSettingsOnServer() {

        final String TAG_NET = "UPDATE_SETTINGS_CUSTOMER";

        final ProgressDialog pDialog = new ProgressDialog(FragmentSettingsC.this.getActivity());
        pDialog.setMessage("Updating profile...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/customer/profileupdate";

        final String mName = txtName.getText().toString();
        final String mPostCode = txtPostCode.getText().toString();
        final String mCountry = txtCountry.getText().toString();
        String mToken = userDefaultManager.getUserToken();

        if (TextUtils.isEmpty(imgString)) {
            imgString = "";
        }

        Map<String, String> params = new HashMap<String, String>();
        //  Log.d(TAG_NET, mEmail);
        params.put("name", mName);
        params.put("token", mToken);
        params.put("country", mCountry);
        params.put("postcode", mPostCode);
        params.put("photo", imgString);

        InternetCustomRequest jsonReq = new InternetCustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG_NET, mEmail);
                        boolean success = response.optBoolean("success");
                        String mMessage = response.optString("message");
                        if (success) {
                            pDialog.hide();

                            userDefaultManager.setUserName(mName);
                            userDefaultManager.setUserPostCode(mPostCode);
                            userDefaultManager.setUserCountry(mCountry);
                            userDefaultManager.setUserPhoto(imgString);

                            updateMenuProfile();
                        } else {
                            pDialog.hide();
                            // String tempp = Boolean.toString(success);
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

    @Override
    public void onChooseFromGallery() {

        cameraOrGallery = "gallery";
        boolean photoPermissionG = checkPhotoPermission();

        if (photoPermissionG) {
            galleryShowing();
        }
    }

    @Override
    public void onTakePhoto() {

        cameraOrGallery = "camera";
        boolean photoPermissionC = checkPhotoPermission();

        if (photoPermissionC) {
            cameraShowing();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPhotoPermission() {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 123;

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(FragmentSettingsC.this.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(FragmentSettingsC.this.getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    String mTitle = "Permission Required";
                    DialogFragmentPhotoPermission mDialogP = DialogFragmentPhotoPermission.newInstance(mTitle);
                    mDialogP.setOnPhotoPermissionListener(FragmentSettingsC.this);
                    mDialogP.show(fm, "Cheng");
                } else {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 123;

      //  Log.d("Robert B Cheng", "this ok");

        switch (requestCode) {
            case PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (cameraOrGallery.equals("camera")) {
                        cameraShowing();
                    } else if (cameraOrGallery.equals("gallery")) {
                        galleryShowing();
                    }
                } else {
                    /*
                    String alertTitle = "Please try again and grant the permission to take or choose the photo.";
                    FragmentManager fm = getSupportFragmentManager();
                    OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                    mDialog.show(fm, "Cheng");
                    */
                }
                break;
            }
        }
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cameraShowing() {

        final int REQUEST_CAMERA = 1;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void galleryShowing() {

        final int REQUEST_GALLERY = 2;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPhotoPermission() {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 123;

        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        final int REQUEST_CAMERA = 1;
        final int REQUEST_GALLERY = 2;

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extra = data.getExtras();
                Bitmap photo = (Bitmap)extra.get("data");

                imgString = photoToString(photo);

                imgPhoto.setImageBitmap(photo);
                //  Log.d("REQUEST_CAMERA_LOG", imgString.length() + "");
                // Toast.makeText(this, imgString.length(), Toast.LENGTH_LONG);
            }
        } else if (requestCode == REQUEST_GALLERY) {
            Bitmap bm = null;
            if (data != null) {
                try {
                    Uri imgFilePath = data.getData();
                    bm = MediaStore.Images.Media.getBitmap(
                            FragmentSettingsC.this.getActivity().getApplicationContext().getContentResolver(),
                            imgFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgPhoto.setImageBitmap(bm);
                imgString = photoToString(bm);
                //  Log.d("REQUEST_GALLERY_LOG", imgString.length() + "");
                //  Toast.makeText(this, imgString.length(), Toast.LENGTH_LONG);
            }
        } else if (requestCode == UPDATE_COUNTRY_CHOOSE_C_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String returnCountry = data.getStringExtra("country");
                if (!TextUtils.isEmpty(returnCountry)) {
                    txtCountry.setText(returnCountry);
                }
            }
        } else if (requestCode == UPDATE_NAME_C_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String returnName = data.getStringExtra("newValue");
                if (!TextUtils.isEmpty(returnName)) {
                    txtName.setText(returnName);
                }
            }
        } else if (requestCode == UPDATE_POST_CODE_C_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String returnPostCode = data.getStringExtra("newValue").toUpperCase(Locale.ENGLISH);
                if (!TextUtils.isEmpty(returnPostCode)) {
                    txtPostCode.setText(returnPostCode);
                }
            }
        }
    }

    private String photoToString(Bitmap photo) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void updateMenuProfile() {

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view_customer);

        View headerV = navigationView.getHeaderView(0);
        TextView nameV = (TextView)headerV.findViewById(R.id.txt_profile_name_customer);
        String nameStr = userDefaultManager.getUserName();
        if (!TextUtils.isEmpty(nameStr)) {
            nameV.setText(nameStr);
        }

        ImageView imgV =
                (ImageView) headerV.findViewById(R.id.img_profile_photo_customer);

        String strImg = userDefaultManager.getUserPhoto();
        if (!TextUtils.isEmpty(strImg)) {
            byte[] decodedByte = Base64.decode(strImg, Base64.DEFAULT);
            Bitmap bitmapImg = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            imgV.setImageBitmap(bitmapImg);
        }
    }
}

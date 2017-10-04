package com.homearound.www.homearound;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetPhotoActivity extends AppCompatActivity implements
        DialogFragmentPhoto.OnFragmentPhotoListener, DialogFragmentPhotoPermission.OnPhotoPermissionListener {

    private ImageView imgPhoto;

    private String imgString;

    private String cameraOrGallery;

    private UserDefaultManager userDefaultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_set_photo);
        setSupportActionBar(toolbar);

        userDefaultManager = new UserDefaultManager(getApplicationContext());

        imgPhoto = (ImageView)findViewById(R.id.img_set_photo);

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragmentPhoto mDialogF = DialogFragmentPhoto.newInstance();
                mDialogF.setOnFragmentPhotoListener(SetPhotoActivity.this);
                mDialogF.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomerDialog);
                mDialogF.show(fm, "Cheng");
            }
        });

        Button btnLater = (Button)findViewById(R.id.btn_later_set_photo);

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   String mRole = "merchant";
                String mRole = userDefaultManager.getUserRole();
                if (mRole.equals("customer")) {
                    toCustomerNv();
                } else {
                    toSetPhonePage();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_set_photo) {
            if (TextUtils.isEmpty(imgString)) {
                String alertTitle = "Choose your photo to save.";
                FragmentManager fm = getSupportFragmentManager();
                OkConfirmDialogFragment mDialog = OkConfirmDialogFragment.newInstance(alertTitle);
                mDialog.show(fm, "Cheng");
            } else {
              savePhotoOnServer();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTakePhoto() {

        cameraOrGallery = "camera";
        boolean photoPermissionC = checkPhotoPermission();

        if (photoPermissionC) {
            cameraShowing();
        }
    }

    private void cameraShowing() {

        final int REQUEST_CAMERA = 861;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onChooseFromGallery() {

        cameraOrGallery = "gallery";
        boolean photoPermissionG = checkPhotoPermission();

        if (photoPermissionG) {
            galleryShowing();
        }
    }

    private void galleryShowing() {

        final int REQUEST_GALLERY = 862;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPhotoPermission() {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 8123;

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    FragmentManager fm = getSupportFragmentManager();
                    String mTitle = "Permission Required";
                    DialogFragmentPhotoPermission mDialogP = DialogFragmentPhotoPermission.newInstance(mTitle);
                    mDialogP.setOnPhotoPermissionListener(this);
                    mDialogP.show(fm, "Cheng");
                } else {
                    ActivityCompat.requestPermissions(this, new String[]
                                    {Manifest.permission.READ_EXTERNAL_STORAGE},
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPhotoPermission() {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 8123;

        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE},
                PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 8123;

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
            //    break;
            }
        }
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        final int REQUEST_CAMERA = 861;
        final int REQUEST_GALLERY = 862;
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
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                            imgFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgPhoto.setImageBitmap(bm);
                imgString = photoToString(bm);
              //  Log.d("REQUEST_GALLERY_LOG", imgString.length() + "");
              //  Toast.makeText(this, imgString.length(), Toast.LENGTH_LONG);
            }
        }
    }

    private String photoToString(Bitmap photo) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void savePhotoOnServer() {

        final String TAG_NET = "SAVE_SET_PHOTO";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Saving photo...");
        pDialog.show();

        String url = HAApplication.getInstance().getUrlHttpHome() + "/setphoto";

        String mToken = userDefaultManager.getUserToken();

        Map<String, String> params = new HashMap<String, String>();

        //  Log.d(TAG_NET, mEmail);
        params.put("photo", imgString);
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
                            userDefaultManager.setUserPhoto(imgString);

                            String mRole = userDefaultManager.getUserRole();
                            if (mRole.equals("customer")) {
                                toCustomerNv();
                            } else {
                                toSetPhonePage();
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

    private void toCustomerNv() {
        Intent intent = new Intent(this, CustomerNvDrawerActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void toSetPhonePage() {
        Intent intent = new Intent(this, SetPhoneActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}

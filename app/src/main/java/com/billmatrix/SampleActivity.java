package com.billmatrix;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.billmatrix.models.Payments;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SampleActivity extends AppCompatActivity {

    Context mContext;
    ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        mContext = this;

        imageView = (ImageView) findViewById(R.id.imView);
    }

    public String getPath(Uri uri, Context mContext) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }

    public void savePhoto(View v) {
        if (Utils.isInternetAvailable(mContext)) {
            if (!mayRequestStorage(REQUEST_READ_STORAGE)) {
                return;
            }

            if (imageUri == null) {
                Log.e("TAG", "imageUri == null: ");
                return;
            }

            Log.e("TAG", "imageUri " + imageUri.toString());

            File file;
            try {
                URI uri = new URI(imageUri.toString());
                file = new File(uri);
            } catch (Exception e) {
                file = null;
            }

            if (file == null) {
                file = new File(getPath(imageUri, mContext));
            }

            if (file == null) {
                Log.e("TAG", "file == null: ");
                return;
            }

            try {

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("imagePath", file.getName(), requestFile).build();
                MultipartBody.Part body = MultipartBody.Part.createFormData("imagePath", file.getName(), requestFile);

                /*Call<String> call = Utils.getBillMatrixAPI(this).uploadImage("revanth1@gmail.com", "revanth@gmail.com", body);
                call.enqueue(new Callback<String>() {

                    *//**
                     * Successful HTTP response.
                     * @param call server call
                     * @param response server response
                     *//*
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        Log.e("TAG", "onResponse: " + response.body());
                        if (response.body() != null) {
                            String payments = response.body();
                        }

                    }

                    *//**
                     *  Invoked when a network or unexpected exception occurred during the HTTP request.
                     * @param call server call
                     * @param t error
                     *//*
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("TAG", "FAILURE RESPONSE" + t.getMessage());
                    }
                });*/
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void browsePhoto(View v) {
        startActivityForResult(getPickImageChooserIntent(), RESULT_LOAD_LICENCEONE);
    }

    private boolean mayRequestStorage(final int licenceNumber) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(imageView, R.string.storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, licenceNumber);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, licenceNumber);
        }
        return false;
    }

    private static int RESULT_LOAD_LICENCEONE = 1;
    private static final int REQUEST_READ_STORAGE = 12;

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = mContext.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_READ_STORAGE) {
                savePhoto(null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_LICENCEONE && resultCode == Activity.RESULT_OK) {
            imageUri = getPickImageResultUri(data);
            imageView.setImageURI(imageUri);
        }
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<Intent>();
        PackageManager packageManager = mContext.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

}

package com.billmatrix.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.billmatrix.R;
import com.billmatrix.database.BillMatrixDaoImpl;
import com.billmatrix.models.Discount;
import com.billmatrix.utils.Constants;
import com.billmatrix.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private static int RESULT_UPLOAD_LOGO = 1;
    private static int RESULT_EDIT_LOGO = 2;
    private static final int REQUEST_READ_STORAGE = 14;
    private Context mContext;
    private Uri logoUri;
    private BillMatrixDaoImpl billMatrixDaoImpl;

    @BindView(R.id.im_upload_logo)
    public SimpleDraweeView logoDraweeView;
    @BindView(R.id.et_storeName)
    public EditText storeNameEditText;
    @BindView(R.id.et_address_One)
    public EditText addressOneEditText;
    @BindView(R.id.et_address_Two)
    public EditText addressTwoEditText;
    @BindView(R.id.et_city_state)
    public EditText cityEditText;
    @BindView(R.id.et_zipCode)
    public EditText zipCodeEditText;
    @BindView(R.id.et_vatTIN)
    public EditText vatTINEditText;
    @BindView(R.id.et_CSTNo)
    public EditText cstNOEditText;
    @BindView(R.id.et_phone)
    public EditText phoneEditText;
    @BindView(R.id.tv_header_store_Name)
    public TextView headerStoreNameTextView;
    @BindView(R.id.tv_header_store_addONE)
    public TextView headerStoreAddOneTextView;
    @BindView(R.id.tv_header_store_addTWO)
    public TextView headerStoreAddTwoTextView;
    @BindView(R.id.tv_header_store_city)
    public TextView headerStoreCityTextView;
    @BindView(R.id.tv_header_store_zipCode)
    public TextView headerStoreZipTextView;
    @BindView(R.id.tv_header_store_vat)
    public TextView headerStoreVatTextView;
    @BindView(R.id.tv_header_store_cst)
    public TextView headerStoreCSTTextView;
    @BindView(R.id.dra_header_logo)
    public SimpleDraweeView headerLogoDraweeView;
    @BindView(R.id.rbtn_thnk_footer)
    public RadioButton thankYouFooterRadioButton;
    @BindView(R.id.rbth_discount_footer)
    public RadioButton discountFooterRadioButton;
    @BindView(R.id.ll_store_discounts)
    public LinearLayout storeDiscountsLayout;
    @BindView(R.id.tv_no_discounts)
    public TextView noDiscountsTextView;
    @BindView(R.id.et_thank_footer)
    public EditText thankFooterEditText;
    @BindView(R.id.im_save_thank_footer_details)
    public ImageButton saveThankFooterButton;
    @BindView(R.id.view_discount_disable)
    public View discountDisableView;

    private static StoreFragment storeFragment;

    public static StoreFragment getInstance() {
        if (storeFragment != null) {
            return storeFragment;
        }

        storeFragment = new StoreFragment();
        return storeFragment;
    }

    public StoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, v);

        mContext = getActivity();
        billMatrixDaoImpl = new BillMatrixDaoImpl(mContext);

        zipCodeEditText.setFilters(Utils.getInputFilter(6));
        phoneEditText.setFilters(Utils.getInputFilter(10));
        noDiscountsTextView.setText("Add Discounts in Discounts Page");
        thankFooterEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        showDiscounts();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Utils.getSharedPreferences(mContext).getBoolean(Constants.PREF_IS_DISCOUNT_FOOTER_SELECTED, false)) {
            discountFooterRadioButton.setChecked(true);
            discountDisableView.setVisibility(View.GONE);
            thankYouFooterRadioButton.setChecked(false);
        } else {
            discountFooterRadioButton.setChecked(false);
            discountDisableView.setVisibility(View.VISIBLE);
            thankYouFooterRadioButton.setChecked(true);
        }

        discountFooterRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    discountDisableView.setVisibility(View.GONE);
                    thankYouFooterRadioButton.setChecked(false);
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_IS_DISCOUNT_FOOTER_SELECTED, true).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_FOOTER_TEXT, null).apply();
                }
            }
        });
        thankYouFooterRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    discountDisableView.setVisibility(View.VISIBLE);
                    discountFooterRadioButton.setChecked(false);
                    Utils.getSharedPreferences(mContext).edit().putBoolean(Constants.PREF_IS_DISCOUNT_FOOTER_SELECTED, false).apply();
                    Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_FOOTER_TEXT, thankFooterEditText.getText().toString()).apply();
                }
            }
        });
    }

    @OnClick(R.id.im_edit_thank_footer_details)
    public void editThankFooter() {
        thankFooterEditText.setEnabled(true);
        thankFooterEditText.setBackgroundResource(R.drawable.edit_text_border);
        saveThankFooterButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.im_save_thank_footer_details)
    public void saveThankFooter() {
        thankFooterEditText.setEnabled(false);
        thankFooterEditText.setBackgroundResource(android.R.color.transparent);
        saveThankFooterButton.setVisibility(View.INVISIBLE);
    }

    public void showDiscounts() {
        ArrayList<Discount.DiscountData> discounts = billMatrixDaoImpl.getDiscount();
        RadioGroup radioGroup = new RadioGroup(mContext);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        if (discounts != null && discounts.size() > 0) {
            noDiscountsTextView.setVisibility(View.GONE);
            for (final Discount.DiscountData discountData : discounts) {
                RadioButton radioButton = new RadioButton(mContext);
                radioButton.setText(discountData.discount_description);
                radioGroup.addView(radioButton);

                /**
                 * to show previously selected discount as selected
                 */
                String selectedDiscountCode = Utils.getSharedPreferences(mContext).getString(Constants.PREF_DISCOUNT_CODE, "");

                if (!TextUtils.isEmpty(selectedDiscountCode)) {
                    if (selectedDiscountCode.equalsIgnoreCase(discountData.discount_code)) {
                        radioButton.setChecked(true);
                    } else {
                        radioButton.setChecked(false);
                    }
                }

                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (TextUtils.isDigitsOnly(discountData.discount)) {
                                Utils.getSharedPreferences(mContext).edit().putFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, Float.parseFloat(discountData.discount)).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_DISCOUNT_CODE, discountData.discount_code).apply();
                            } else {
                                Utils.getSharedPreferences(mContext).edit().putFloat(Constants.PREF_DISCOUNT_FLOAT_VALUE, 0.0f).apply();
                                Utils.getSharedPreferences(mContext).edit().putString(Constants.PREF_DISCOUNT_CODE, "").apply();
                            }
                        }
                    }
                });
            }
            storeDiscountsLayout.addView(radioGroup);
        } else {
            noDiscountsTextView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.im_edit_store_details)
    public void editStoreDetails() {
        storeNameEditText.setText(headerStoreNameTextView.getText());
        addressOneEditText.setText(headerStoreAddOneTextView.getText());
        addressTwoEditText.setText(headerStoreAddTwoTextView.getText());
        cityEditText.setText(headerStoreCityTextView.getText());
        zipCodeEditText.setText(headerStoreZipTextView.getText());
        vatTINEditText.setText(headerStoreVatTextView.getText());
        cstNOEditText.setText(headerStoreCSTTextView.getText());
//        phoneEditText.setText(header.getText());
    }


    @OnClick(R.id.im_upload_logo)
    public void browseStoreLogo() {
        startActivityForResult(getPickImageChooserIntent(), RESULT_UPLOAD_LOGO);
    }

    @OnClick(R.id.im_edit_storeImage)
    public void editStoreLogo() {
        startActivityForResult(getPickImageChooserIntent(), RESULT_EDIT_LOGO);
    }

    @OnClick(R.id.im_remove_storeLogo)
    public void removeStoreLogo() {
        headerLogoDraweeView.setImageURI(Uri.EMPTY);
        headerLogoDraweeView.setBackgroundResource(R.drawable.avatar);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_READ_STORAGE) {
//                uploadLicenceOne();
            }
        }
    }

    private boolean mayRequestStorage(final int licenceNumber) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(logoDraweeView, R.string.storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
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

    @OnClick(R.id.btn_saveStore)
    public void saveStore() {
        String storeName = storeNameEditText.getText().toString();

        if (TextUtils.isEmpty(storeName)) {
            Utils.showToast("Enter Store Name", mContext);
            return;
        }

        String addONE = addressOneEditText.getText().toString();

        /*if (TextUtils.isEmpty(addONE)) {
            Utils.showToast("Enter Address Line One", mContext);
            return;
        }*/

        String addTWO = addressTwoEditText.getText().toString();

        /*if (TextUtils.isEmpty(addTWO)) {
            Utils.showToast("Enter Address Line Two", mContext);
            return;
        }*/

        String cityState = cityEditText.getText().toString();

        /*if (TextUtils.isEmpty(cityState)) {
            Utils.showToast("Enter City and State", mContext);
            return;
        }*/

        String zipCode = zipCodeEditText.getText().toString();

        /*if (TextUtils.isEmpty(zipCode)) {
            Utils.showToast("Enter Zip Code", mContext);
            return;
        }*/

        String vatTIN = vatTINEditText.getText().toString();

        /*if (TextUtils.isEmpty(vatTIN)) {
            Utils.showToast("Enter VAT TIN", mContext);
            return;
        }*/

        String cstNo = cstNOEditText.getText().toString();

        /*if (TextUtils.isEmpty(cstNo)) {
            Utils.showToast("Enter CST No", mContext);
            return;
        }*/

        String phone = phoneEditText.getText().toString();

        /*if (TextUtils.isEmpty(phone)) {
            Utils.showToast("Enter Phone Number", mContext);
            return;
        }*/

        headerStoreNameTextView.setText(storeName);
        headerStoreAddOneTextView.setText(addONE);
        headerStoreAddTwoTextView.setText(addTWO);
        headerStoreCityTextView.setText(cityState);
        headerStoreZipTextView.setText(zipCode);
        headerStoreVatTextView.setText(!TextUtils.isEmpty(vatTIN) ? "VAT TIN: " + vatTIN : "");
        headerStoreCSTTextView.setText(!TextUtils.isEmpty(cstNo) ? "CST NO: " + cstNo : "");

        if (logoUri != null) {
            headerLogoDraweeView.setImageURI(logoUri);
        }

    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            logoUri = getPickImageResultUri(data);
            if (requestCode == RESULT_UPLOAD_LOGO) {
                logoDraweeView.setImageURI(logoUri);
            } else if (requestCode == RESULT_EDIT_LOGO) {
                headerLogoDraweeView.setImageURI(logoUri);
            }
        }
    }

}

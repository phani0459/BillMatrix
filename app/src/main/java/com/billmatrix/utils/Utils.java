package com.billmatrix.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.billmatrix.R;
import com.billmatrix.interfaces.BillMatrixAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Utils {

    public static Retrofit retrofit;
    public static BillMatrixAPI billMatrixAPI;
    private static SharedPreferences sharedPreferences;

    public static BillMatrixAPI getBillMatrixAPI(Context mContext) {
        if (billMatrixAPI == null) {
            billMatrixAPI = getRetrofit(mContext).create(BillMatrixAPI.class);
        }
        return billMatrixAPI;
    }

    public static DatePickerDialog dateDialog(Context mContext, final EditText fromEditText, boolean onlyPastDates) {
        hideSoftKeyboard(fromEditText);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromEditText.setText(Constants.getDateFormat().format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if (onlyPastDates) {
            fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        return fromDatePickerDialog;
    }

    public static void loadSpinner(Spinner spinner, Context mContext, int spinnerArray) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, spinnerArray, R.layout.spinner_text_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static void loadSpinner(Spinner spinner, Context mContext, ArrayList<String> spinnerArray) {
        ArrayAdapter<String> foodadapter = new ArrayAdapter<String>(mContext, R.layout.spinner_text_item, spinnerArray);
        foodadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(foodadapter);
    }

    public static Retrofit getRetrofit(Context mContext) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BillMatrixAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(Constants.getGson()))
                    .client(getOkHttpClient(mContext))
                    .build();
        }
        return retrofit;
    }

    public static boolean isInternetAvailable(Context mContext) {
        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        /*if (!isConnected) {
            Toast.makeText(mContext, "We are unable connect to our servers, please check your internet connection", Toast.LENGTH_LONG).show();
        }*/
        return isConnected;
    }

    public static SharedPreferences getSharedPreferences(Context mContext) {
        if (sharedPreferences == null) {
            sharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void hideSoftKeyboard(EditText editText) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText()) {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void showToast(String msg, Context mContext) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public static ProgressDialog getProgressDialog(Context mContext) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");
        return progressDialog;
    }

    public static OkHttpClient getOkHttpClient(final Context mContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        final OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    }
}

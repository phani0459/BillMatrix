package com.billmatrix.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.billmatrix.interfaces.BillMatrixAPI;

import java.io.IOException;
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

    public static BillMatrixAPI getBillMatrixAPI(Context mContext) {
        if (billMatrixAPI == null) {
            billMatrixAPI = getRetrofit(mContext).create(BillMatrixAPI.class);
        }
        return billMatrixAPI;
    }

    public static Retrofit getRetrofit(Context mContext) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BillMatrixAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient(mContext))
                    .build();
        }
        return retrofit;
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

    public static ProgressDialog showProgressDialog(Context mContext) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
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

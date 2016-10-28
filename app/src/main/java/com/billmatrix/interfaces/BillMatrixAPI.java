package com.billmatrix.interfaces;

import com.billmatrix.models.Profile;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public interface BillMatrixAPI {

    public static final String BASE_URL = "http://mkrinfotech.com/billmatrix/";

    @FormUrlEncoded
    @POST("login")
    Call<HashMap<String, String>> login(@Field("username") String username, @Field("password") String password,
                                        @Field("licence_key") String licenceKey, @Field("imei_number") String imeiNumber);

    @GET("profile/1")
    Call<Profile> getProfile();

}

package com.billmatrix.interfaces;

import com.billmatrix.models.Employee;
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

    @FormUrlEncoded
    @POST("profile")
    Call<Profile> getProfile(@Field("id") String id);

    @FormUrlEncoded
    @POST("employee")
    Call<Employee> getEmployees(@Field("id") String id);

    @FormUrlEncoded
    @POST("create_employee")
    Call<HashMap<String, String>> addEmployees(@Field("name") String empName, @Field("password") String password,
                                               @Field("number") String mobileNumber);

}

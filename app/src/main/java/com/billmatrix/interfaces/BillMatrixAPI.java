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
    Call<HashMap<String, String>> login(@Field("username") String username, @Field("password") String password, @Field("imei_number") String imei_number);

    @FormUrlEncoded
    @POST("profile")
    Call<Profile> getProfile(@Field("id") String id);

    @FormUrlEncoded
    @POST("employee")
    Call<Employee> getEmployeeProfile(@Field("id") String empId);

    @FormUrlEncoded
    @POST("admin_employees")
    Call<Employee> getAdminEmployees(@Field("admin_id ") String adminId);

    @FormUrlEncoded
    @POST("delete_employee")
    Call<HashMap<String, String>> deleteEmployee(@Field("id ") String empId);

    @FormUrlEncoded
    @POST("create_employee")
    Call<HashMap<String, String>> addEmployee(@Field("username") String empName, @Field("password") String password,
                                              @Field("mobile_number") String mobileNumber, @Field("admin_id") String admin_id);

}

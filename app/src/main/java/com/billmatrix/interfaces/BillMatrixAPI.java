package com.billmatrix.interfaces;

import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Profile;
import com.billmatrix.models.Vendor;

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
    Call<HashMap<String, String>> login(@Field("login_id") String username, @Field("password") String password, @Field("imei_number") String imei_number);

    @FormUrlEncoded
    @POST("profile")
    Call<Profile> getProfile(@Field("id") String id);

    @FormUrlEncoded
    @POST("employee")
    Call<Employee> getEmployeeProfile(@Field("id") String empId);

    @FormUrlEncoded
    @POST("admin_employees")
    Call<Employee> getAdminEmployees(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("admin_customers")
    Call<Customer> getAdminCustomers(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("delete_employee")
    Call<HashMap<String, String>> deleteEmployee(@Field("id") String empId);

    @FormUrlEncoded
    @POST("update_employee")
    Call<HashMap<String, String>> updateEmployee(@Field("id") String id, @Field("username") String empName, @Field("password") String password,
                                                 @Field("mobile_number") String mobileNumber,
                                                 @Field("login_id") String login_id, @Field("imei_number") String imei_number,
                                                 @Field("location") String location, @Field("branch") String branch, @Field("status") String status);
    @FormUrlEncoded
    @POST("create_employee")
    Call<HashMap<String, String>> addEmployee(@Field("username") String empName, @Field("password") String password,
                                              @Field("mobile_number") String mobileNumber, @Field("admin_id") String admin_id,
                                              @Field("login_id") String login_id, @Field("imei_number") String imei_number,
                                              @Field("location") String location, @Field("branch") String branch);

    @FormUrlEncoded
    @POST("admin_vendors")
    Call<Vendor> getAdminVendors(@Field("admin_id") String adminId);

}

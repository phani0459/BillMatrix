package com.billmatrix.interfaces;

import com.billmatrix.models.Customer;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
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
    @POST("admin_customers")
    Call<Customer> getAdminCustomers(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("create_customer")
    Call<HashMap<String, String>> addCustomer(@Field("username") String custName,
                                              @Field("mobile_number") String mobileNumber, @Field("location") String location,
                                              @Field("status") String status, @Field("date") String date, @Field("admin_id") String admin_id);

    @FormUrlEncoded
    @POST("update_customer")
    Call<HashMap<String, String>> updateCustomer(@Field("id") String id, @Field("username") String custName,
                                                 @Field("mobile_number") String mobileNumber, @Field("location") String location, @Field("status") String status,
                                                 @Field("date") String date);

    @FormUrlEncoded
    @POST("delete_customer")
    Call<HashMap<String, String>> deleteCustomer(@Field("id") String customerId);

    @FormUrlEncoded
    @POST("create_vendor")
    Call<HashMap<String, String>> addVendor(@Field("name") String vendorName,
                                            @Field("email") String email, @Field("phone") String phone,
                                            @Field("since") String since, @Field("address") String address, @Field("status") String status, @Field("admin_id") String admin_id);

    @FormUrlEncoded
    @POST("update_vendor")
    Call<HashMap<String, String>> updateVendor(@Field("id") String id, @Field("email") String email,
                                               @Field("phone") String phone, @Field("since") String since, @Field("status") String status,
                                               @Field("address") String address, @Field("name") String name);

    @FormUrlEncoded
    @POST("admin_vendors")
    Call<Vendor> getAdminVendors(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("delete_vendor")
    Call<HashMap<String, String>> deleteVendor(@Field("id") String vendorID);


    @FormUrlEncoded
    @POST("admin_inventories")
    Call<Inventory> getAdminInventory(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("create_inventory")
    Call<HashMap<String, String>> addInventory(@Field("admin_id") String adminId, @Field("item_code") String item_code, @Field("item_name") String item_name,
                                               @Field("unit") String unit, @Field("qty") String qty, @Field("price") String price, @Field("mycost") String mycost,
                                               @Field("date") String date, @Field("warehouse") String warehouse, @Field("vendor") String vendor,
                                               @Field("barcode") String barcode, @Field("photo") String photo, @Field("status") String status);

    @FormUrlEncoded
    @POST("update_inventory")
    Call<HashMap<String, String>> updateInventory(@Field("id") String id, @Field("item_code") String item_code, @Field("item_name") String item_name,
                                                  @Field("unit") String unit, @Field("qty") String qty, @Field("price") String price, @Field("mycost") String mycost,
                                                  @Field("date") String date, @Field("warehouse") String warehouse, @Field("vendor") String vendor,
                                                  @Field("barcode") String barcode, @Field("photo") String photo, @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_inventory")
    Call<HashMap<String, String>> deleteInventory(@Field("id") String inventoryID);
}

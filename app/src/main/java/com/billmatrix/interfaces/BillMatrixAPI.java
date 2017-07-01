package com.billmatrix.interfaces;

import com.billmatrix.models.CreateEmployee;
import com.billmatrix.models.CreateJob;
import com.billmatrix.models.Customer;
import com.billmatrix.models.Discount;
import com.billmatrix.models.Employee;
import com.billmatrix.models.Inventory;
import com.billmatrix.models.Payments;
import com.billmatrix.models.Profile;
import com.billmatrix.models.Tax;
import com.billmatrix.models.Transport;
import com.billmatrix.models.Vendor;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/*
 * Created by KANDAGATLAs on 28-10-2016.
 */

public interface BillMatrixAPI {

    String BASE_URL = "http://mkrinfotech.com/billmatrix/";

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
    Call<CreateEmployee> updateEmployee(@Field("id") String id, @Field("username") String empName, @Field("password") String password,
                                        @Field("mobile_number") String mobileNumber,
                                        @Field("login_id") String login_id, @Field("imei_number") String imei_number,
                                        @Field("location") String location, @Field("branch") String branch, @Field("status") String status,
                                        @Field("contact_person") String contact_person);

    @FormUrlEncoded
    @POST("update_employee")
    Call<CreateEmployee> updateStore(@Field("id") String id, @Field("address_two") String address_two,
                                     @Field("address_one") String address_one, @Field("zipcode") String zipcode,
                                     @Field("city_state") String city_state, @Field("vat_tin") String vat_tin,
                                     @Field("cst_no") String cst_no, @Field("store_name") String store_name,
                                     @Field("branch") String branch, @Field("location") String location);

    @FormUrlEncoded
    @POST("create_employee")
    Call<CreateEmployee> addEmployee(@Field("username") String empName, @Field("password") String password,
                                     @Field("mobile_number") String mobileNumber, @Field("admin_id") String admin_id,
                                     @Field("login_id") String login_id, @Field("imei_number") String imei_number,
                                     @Field("location") String location, @Field("branch") String branch);

    @FormUrlEncoded
    @POST("admin_customers")
    Call<Customer> getAdminCustomers(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("create_customer")
    Call<CreateJob> addCustomer(@Field("username") String custName,
                                @Field("mobile_number") String mobileNumber, @Field("location") String location,
                                @Field("status") String status, @Field("date") String date, @Field("admin_id") String admin_id);

    @FormUrlEncoded
    @POST("update_customer")
    Call<CreateJob> updateCustomer(@Field("id") String id, @Field("username") String custName,
                                   @Field("mobile_number") String mobileNumber, @Field("location") String location, @Field("status") String status,
                                   @Field("date") String date);

    @FormUrlEncoded
    @POST("delete_customer")
    Call<HashMap<String, String>> deleteCustomer(@Field("id") String customerId);

    @FormUrlEncoded
    @POST("create_vendor")
    Call<CreateJob> addVendor(@Field("name") String vendorName,
                              @Field("email") String email, @Field("phone") String phone,
                              @Field("since") String since, @Field("address") String address, @Field("status") String status, @Field("admin_id") String admin_id);

    @FormUrlEncoded
    @POST("update_vendor")
    Call<CreateJob> updateVendor(@Field("id") String id, @Field("email") String email,
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
    @POST("create_transaction")
    Call<CreateJob> addTransaction(@Field("admin_id") String adminId, @Field("customer_id") String customer_id, @Field("billnumber") String billnumber,
                                   @Field("totalamount") String totalamount, @Field("discountcodeapplied") String disccodeapplied,
                                   @Field("discountpercentapplied") String discpercentapplied, @Field("subtotal") String subtotal, @Field("date") String date,
                                   @Field("taxcalculated") String taxcalculated, @Field("totaldiscount") String totaldiscount, @Field("amountpaid") String amountpaid,
                                   @Field("amountdue") String amountdue, @Field("items") String items);

    @FormUrlEncoded
    @POST("create_inventory")
    Call<CreateJob> addInventory(@Field("admin_id") String adminId, @Field("item_code") String item_code, @Field("item_name") String item_name,
                                 @Field("unit") String unit, @Field("qty") String qty, @Field("price") String price, @Field("mycost") String mycost,
                                 @Field("date") String date, @Field("warehouse") String warehouse, @Field("vendor") String vendor,
                                 @Field("barcode") String barcode, @Field("photo") String photo, @Field("status") String status);

    @FormUrlEncoded
    @POST("update_inventory")
    Call<CreateJob> updateInventory(@Field("id") String id, @Field("item_code") String item_code, @Field("item_name") String item_name,
                                    @Field("unit") String unit, @Field("qty") String qty, @Field("price") String price, @Field("mycost") String mycost,
                                    @Field("date") String date, @Field("warehouse") String warehouse, @Field("vendor") String vendor,
                                    @Field("barcode") String barcode, @Field("photo") String photo, @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_inventory")
    Call<HashMap<String, String>> deleteInventory(@Field("id") String inventoryID);

    @FormUrlEncoded
    @POST("create_tax")
    Call<CreateJob> addTax(@Field("admin_id") String adminId, @Field("tax_type") String tax_type, @Field("tax_description") String tax_description,
                           @Field("tax_rate") String tax_rate, @Field("status") String status);

    @FormUrlEncoded
    @POST("admin_taxes")
    Call<Tax> getAdminTaxes(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("update_tax")
    Call<CreateJob> updateTax(@Field("id") String id, @Field("admin_id") String admin_id, @Field("tax_type") String tax_type,
                              @Field("tax_description") String tax_description, @Field("tax_rate") String tax_rate, @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_tax")
    Call<HashMap<String, String>> deleteTax(@Field("id") String id);

    @FormUrlEncoded
    @POST("create_discount")
    Call<CreateJob> addDiscount(@Field("admin_id") String adminId, @Field("discount_code") String discount_code, @Field("discount_description") String description,
                                @Field("discount") String tax_rate, @Field("status") String status);

    @FormUrlEncoded
    @POST("admin_discounts")
    Call<Discount> getAdminDiscounts(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("update_discount")
    Call<CreateJob> updateDiscount(@Field("id") String id, @Field("admin_id") String admin_id, @Field("discount_code") String discount_code,
                                   @Field("discount_description") String description, @Field("discount") String discount, @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_discount")
    Call<HashMap<String, String>> deleteDiscount(@Field("id") String id);

    @FormUrlEncoded
    @POST("delete_payment")
    Call<HashMap<String, String>> deletePayment(@Field("id") String id);

    @FormUrlEncoded
    @POST("create_payment")
    Call<CreateJob> addPayment(@Field("admin_id") String adminId, @Field("payee_name") String payee_name, @Field("mode_of_payment") String mode_of_payment,
                               @Field("date_of_payment") String date_of_payment, @Field("amount") String amount, @Field("status") String status,
                               @Field("purpose_of_payment") String purpose_of_payment, @Field("payment_type") String payment_type);

    @FormUrlEncoded
    @POST("update_payment")
    Call<CreateJob> updatePayment(@Field("id") String id, @Field("admin_id") String adminId, @Field("payee_name") String payee_name, @Field("mode_of_payment") String mode_of_payment,
                                  @Field("date_of_payment") String date_of_payment, @Field("amount") String amount, @Field("status") String status,
                                  @Field("purpose_of_payment") String purpose_of_payment, @Field("payment_type") String payment_type);

    @FormUrlEncoded
    @POST("admin_payments")
    Call<Payments> getAdminPayments(@Field("admin_id") String adminId, @Field("payment_type") String payment_type);

    @FormUrlEncoded
    @POST("create_transport")
    Call<CreateJob> addTransport(@Field("admin_id") String adminId, @Field("transportName") String transportName, @Field("phone") String phone,
                                 @Field("location") String location, @Field("status") String status);

    @FormUrlEncoded
    @POST("update_transport")
    Call<CreateJob> updateTransport(@Field("id") String id, @Field("admin_id") String adminId, @Field("transportName") String transportName,
                                    @Field("phone") String phone, @Field("location") String location, @Field("status") String status);

    @FormUrlEncoded
    @POST("delete_transport")
    Call<HashMap<String, String>> deleteTransport(@Field("id") String id);

    @FormUrlEncoded
    @POST("admin_transports")
    Call<Transport> getAdminTransports(@Field("admin_id") String adminId);

}

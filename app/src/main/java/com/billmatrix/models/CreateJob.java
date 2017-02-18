package com.billmatrix.models;

/**
 * Created by KANDAGATLAs on 28-01-2017.
 */

public class CreateJob {
    public String status;
    public String create_customer;
    public String update_customer;
    public String update_employee;
    public String create_employee;
    public String create_vendor;
    public String create_payment;
    public String update_payment;
    public String update_vendor;
    public String update_discount;
    public String create_discount;
    public CreatedJob data;

    public class CreatedJob {
        public String discount_code;
        public String discount_description;
        public String discount;
        public String payee_name;
        public String date_of_payment;
        public String amount;
        public String purpose_of_payment;
        public String mode_of_payment;
        public String payment_type;
        public String username;
        public String login_id;
        public String mobile_number;
        public String password;
        public String imei_number;
        public String type;
        public String branch;
        public String location;
        public String name;
        public String since;
        public String address;
        public String phone;
        public String email;
        public String id;
        public String admin_id;
        public String item_code;
        public String item_name;
        public String unit;
        public String qty;
        public String selectedQTY;
        public String price;
        public String mycost;
        public String date;
        public String warehouse;
        public String vendor;
        public String barcode;
        public String photo;
        public String status;
        public String create_date;
        public String update_date;
        public String add_update;
    }
}

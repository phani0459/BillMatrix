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
    public String update_vendor;
    public CreatedJob data;

    public class CreatedJob {
        public String date;
        public String username;
        public String login_id;
        public String mobile_number;
        public String password;
        public String imei_number;
        public String type;
        public String branch;
        public String location;
        public String id;
        public String name;
        public String since;
        public String address;
        public String phone;
        public String admin_id;
        public String status;
        public String email;
        public String create_date;
        public String update_date;
    }
}

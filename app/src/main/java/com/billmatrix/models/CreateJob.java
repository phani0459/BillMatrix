package com.billmatrix.models;

/**
 * Created by KANDAGATLAs on 28-01-2017.
 */

public class CreateJob {
    public String status;
    public String create_customer;
    public String update_customer;
    public CreatedJob data;

    public class CreatedJob {
        public String id;
        public String username;
        public String mobile_number;
        public String date;
        public String location;
        public String status;
        public String admin_id;
        public String create_date;
        public String update_date;
    }
}

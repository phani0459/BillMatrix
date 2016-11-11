package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Customer implements Serializable {
    public int status;
    public String Customerdata;
    public ArrayList<Customer.CustomerData> data;

    public class CustomerData {
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

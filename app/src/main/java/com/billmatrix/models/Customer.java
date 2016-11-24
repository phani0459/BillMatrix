package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Customer implements Serializable {
    public int status;
    public String Customerdata;
    public ArrayList<CustomerData> data;

    public class CustomerData implements Serializable {
        public String id;
        public String username;
        public String mobile_number;
        public String date;
        public String location;
        public String status;
        public String admin_id;
        public String create_date;
        public String update_date;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n username=" + username
                    + "\n mobile_number=" + mobile_number + "\n date=" + date
                    + "\n location=" + location + "\n status=" + status
                    + "\n create_date=" + create_date + "\n update_date=" + update_date;
        }

    }
}

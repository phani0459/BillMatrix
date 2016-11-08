package com.billmatrix.models;

import java.io.Serializable;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Vendor implements Serializable {
    public int status;
    public String vendordata;
    public VendorData data;

    public class VendorData {
        public String id;
        public String name;
        public String since;
        public String address;
        public String phone;
        public String email;
        public String create_date;
        public String update_date;
    }
}

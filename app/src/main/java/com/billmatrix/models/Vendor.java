package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Vendor implements Serializable {
    public int status;
    public String Vendordata;
    public ArrayList<VendorData> data;

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

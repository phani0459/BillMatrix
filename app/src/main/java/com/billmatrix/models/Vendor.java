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
        public String admin_id;
        public String status;
        public String email;
        public String create_date;
        public String update_date;
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n name=" + name
                    + "\n since=" + since + "\n address=" + address
                    + "\n phone=" + phone + "\n status=" + status + "\n email=" + email
                    + "\n create_date=" + create_date + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }

    }
}

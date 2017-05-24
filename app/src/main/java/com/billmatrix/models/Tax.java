package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 20-11-2016.
 */

public class Tax implements Serializable {
    public int status;
    public String Taxdata;
    public ArrayList<TaxData> data;

    public class TaxData implements Serializable {
        public String id;
        public String tax_type;
        public String tax_description;
        public String tax_rate;
        public String admin_id;
        public String status;
        public String create_date;
        public String update_date;
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n tax_type=" + tax_type + "\n tax_description=" + tax_description
                    + "\n tax_rate=" + tax_rate + "\n status=" + status + "\n create_date=" + create_date
                    + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }
    }
}

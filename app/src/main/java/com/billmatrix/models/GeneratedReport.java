package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class GeneratedReport implements Serializable {
    public int status;
    public String Reportdata;
    public ArrayList<ReportData> data;

    public class ReportData implements Serializable {
        public String id;
        public String itemName;
        public String vendor;
        public String date;
        public String cost;
        public String quantity;
        public String admin_id;
        public String discount;
        public String total;
        public String create_date;
        public String update_date;
        /**
         * 0 - if add
         * 1 - if update
         * -1 - from server
         */
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n itemName=" + itemName
                    + "\n vendor=" + vendor + "\n date=" + date
                    + "\n cost=" + cost + "\n quantity=" + quantity
                    + "\n admin_id=" + admin_id + "\n discount=" + discount + "\n total=" + total
                    + "\n create_date=" + create_date + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }

    }
}

package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 20-11-2016.
 */

public class Discount implements Serializable {
    public int status;
    public String Discountdata;
    public ArrayList<DiscountData> data;

    public class DiscountData implements Serializable {
        public String id;
        public String discount_code;
        public String discount_description;
        public String discount;
        public String admin_id;
        public String status;
        public String create_date;
        public String update_date;
    }
}

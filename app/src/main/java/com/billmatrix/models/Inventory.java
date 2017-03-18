package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 19-11-2016.
 */

public class Inventory implements Serializable {
    public int status;
    public String InventoryData;
    public ArrayList<Inventory.InventoryData> data;

    public class InventoryData implements Serializable{
        public String id;
        public String admin_id;
        public String item_code;
        public String item_name;
        public String unit;
        public String qty;
        public String price;
        public String mycost;
        public String date;
        public String warehouse;
        public String vendor;
        public String barcode;
        public String photo;
        public String status;
        public String create_date;
        public String update_date;
        public String add_update;

        /**
         * fields required for pos and customer transactions
         */
        public String customerName;
        public String selectedQTY;
        public String discountCode;
        public String discountValue;
        public boolean isZbillChecked;
    }
}

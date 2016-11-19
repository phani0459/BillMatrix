package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 19-11-2016.
 */

public class Inventory implements Serializable {
    public int status;
    public String inventorydata;
    public ArrayList<Inventory.InventoryData> data;

    public class InventoryData {
        public String id;
        public String itemcode;
        public String itemname;
        public String unit;
        public String quantity;
        public String admin_id;
        public String price;
        public String mycost;
        public String create_date;
        public String update_date;
        public String date;
        public String ware_house;
        public String vendor;
        public String barcode;
        public String photo;
    }
}

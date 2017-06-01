package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 19-11-2016.
 */

public class Inventory implements Serializable {
    public int status;
    public String InventoryData;
    public ArrayList<Inventory.InventoryData> data;

    public class InventoryData implements Serializable {
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

        /*
         * fields required for pos and customer transactions
         */
        public String customerName;
        public String selectedQTY;
        public String discountCode;
        public String discountValue;
        public boolean isZbillChecked;

        /**
         * used in reports
         */
        public String transactionDate;
        public float totalSales;
        public float totalProfit;


        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n item_code=" + item_code + "\n item_name=" + item_name
                    + "\n unit=" + unit + "\n qty=" + qty
                    + "\n price=" + price + "\n mycost=" + mycost
                    + "\n date =" + date + "\n warehouse=" + warehouse
                    + "\n status=" + status + "\n create_date=" + create_date
                    + "\n vendor=" + vendor + "\n barcode=" + barcode
                    + "\n update_date=" + update_date + "\n add_update=" + add_update
                    + "\n photo=" + photo + "\n customerName=" + customerName
                    + "\n selectedQTY=" + selectedQTY + "\n discountCode=" + discountCode
                    + "\n discountValue=" + discountValue + "\n isZbillChecked=" + isZbillChecked;

        }
    }
}

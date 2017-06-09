package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Transaction implements Serializable {
    public String status;
    public String id;
    public String admin_id;
    public String billNumber;
    public String customerName;
    public String date;
    public String inventoryJson;
    public String totalAmount;
    public String discountCodeApplied;
    public float discountPercentApplied;
    public String subTotal;
    public String taxCalculated;
    public String totalDiscount;
    public String amountPaid;
    public String amountDue;
    public String create_date;
    public String update_date;
    public boolean isZbillChecked;
    /*
     * 0 - if add
     * 1 - if update
     * -1 - from server
     */
    public String add_update;

    /*
     * For Reports
     */
    public String totalDiscountedAmt;

    @Override
    public String toString() {
        return "\nid = " + id + "\n admin id=" + admin_id
                + "\n billNumber=" + billNumber + "\n inventoryJson=" + inventoryJson + "\n customerName=" + customerName
                + "\n totalAmount=" + totalAmount + "\n amountPaid=" + amountPaid + "\n date=" + date + "\n create_date=" + create_date
                + "\n status=" + status + "\n discountCodeApplied=" + discountCodeApplied + "\n discountPercentApplied=" + discountPercentApplied
                + "\n amountDue=" + amountDue + "\n update_date=" + update_date + "\n add_update=" + add_update + "\n isZbillChecked=" + isZbillChecked;
    }
}

package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Payments implements Serializable {
    public int status;
    public String payInData;
    public ArrayList<PaymentData> data;

    public class PaymentData implements Serializable {
        public String status;
        public String id;
        public String admin_id;
        public String payee_name;
        public String date_of_payment;
        public String amount;
        public String create_date;
        public String update_date;
        public String purpose_of_payment;
        public String mode_of_payment;
        public String payment_type;
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n payee_name=" + payee_name
                    + "\n date_of_payment=" + date_of_payment + "\n amount=" + amount
                    + "\n admin_id=" + admin_id + "\n status=" + status
                    + "\n purpose_of_payment=" + purpose_of_payment + "\n mode_of_payment=" + mode_of_payment + "\n payment_type=" + payment_type
                    + "\n create_date=" + create_date + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }

    }
}

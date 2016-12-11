package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class PayIn implements Serializable {
    public int status;
    public String payInData;
    public ArrayList<PayInData> data;

    public class PayInData {
        public String id;
        public String name;
        public String date;
        public String amount;
        public String admin_id;
        public String create_date;
        public String update_date;
    }
}

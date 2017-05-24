package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 20-11-2016.
 */

public class Transport implements Serializable {
    public int status;
    public String Transportdata;
    public ArrayList<TransportData> data;

    public class TransportData implements Serializable {
        public String id;
        public String transportName;
        public String phone;
        public String location;
        public String admin_id;
        public String status;
        public String create_date;
        public String update_date;
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n transportName=" + transportName
                    + "\n phone=" + phone + "\n location=" + location
                    + "\n status=" + status
                    + "\n create_date=" + create_date + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }
    }
}

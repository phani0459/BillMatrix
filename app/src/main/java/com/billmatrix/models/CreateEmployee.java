package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by KANDAGATLAs on 28-01-2017.
 */

public class CreateEmployee implements Serializable {
    public String status;
    public String update_employee;
    public String create_employee;
    public ArrayList<CreatedEmployee> data;

    public class CreatedEmployee implements Serializable {
        public String id;
        public String admin_id;
        public String username;
        public String login_id;
        public String mobile_number;
        public String password;
        public String imei_number;
        public String type;
        public String branch;
        public String location;
        public String status;
        public String create_date;
        public String update_date;
        /*
         * 0 - if add
         * 1 - if update
         * -1 - from server
         */
        public String add_update;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n username=" + username + "\n login_id=" + login_id
                    + "\n mobile_number=" + mobile_number + "\n password=" + password
                    + "\n imei_number=" + imei_number + "\n type=" + type
                    + "\n branch =" + branch + "\n location=" + location
                    + "\n status=" + status + "\n create_date=" + create_date
                    + "\n update_date=" + update_date + "\n add_update=" + add_update;
        }
    }
}

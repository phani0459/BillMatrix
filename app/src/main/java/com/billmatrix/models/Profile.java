package com.billmatrix.models;

import java.io.Serializable;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Profile implements Serializable {
    public int status;
    public String userdata;
    public ProfileData data;
    public Store store_data;

    public class ProfileData implements Serializable {
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

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n username=" + username + "\n login_id=" + login_id
                    + "\n mobile_number=" + mobile_number + "\n password=" + password
                    + "\n imei_number=" + imei_number + "\n type=" + type
                    + "\n branch =" + branch + "\n location=" + location
                    + "\n status=" + status + "\n create_date=" + create_date + "\n update_date=" + update_date;
        }
    }
}

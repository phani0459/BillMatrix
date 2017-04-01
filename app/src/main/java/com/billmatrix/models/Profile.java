package com.billmatrix.models;

import java.io.Serializable;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Profile implements Serializable {
    public int status;
    public String userdata;
    public ProfileData data;

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
        public String cst_no;
        public String vat_tin;
        public String zipcode;
        public String city_state;
        public String address_one;
        public String address_two;
        public String contact_person;
        public String store_name;

        @Override
        public String toString() {
            return "\nid = " + id + "\n admin id=" + admin_id
                    + "\n username=" + username + "\n login_id=" + login_id
                    + "\n mobile_number=" + mobile_number + "\n password=" + password
                    + "\n imei_number=" + imei_number + "\n type=" + type
                    + "\n branch =" + branch + "\n location=" + location
                    + "\n status=" + status + "\n create_date=" + create_date + "\n update_date=" + update_date
                    + "\n store_name=" + store_name + "\n cst_no=" + cst_no + "\n vat_tin=" + vat_tin
                    + "\n zipcode=" + zipcode + "\n city_state=" + city_state
                    + "\n branch =" + branch + "\n address_two=" + address_two
                    + "\n address_one=" + address_one
                    + "\n contact_person=" + contact_person;
        }
    }
}

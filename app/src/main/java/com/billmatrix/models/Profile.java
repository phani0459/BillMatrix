package com.billmatrix.models;

import com.google.gson.annotations.SerializedName;

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
        public String mobile_number;
        public String type;
        public String email;
        public String username;
        public String password;
        public String licence_key;
        public String imei_number;
        public String status;
        public String create_data;
        public String update_date;

    }
}

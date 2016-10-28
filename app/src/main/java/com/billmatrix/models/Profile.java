package com.billmatrix.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Profile {
    @SerializedName("status")
    public String mStatus;

    @SerializedName("userdata")
    public String mUserdata;

    @SerializedName("data")
    ProfileData profileData;

    public Profile(String status, String mUserdata, ProfileData profileData) {
        this.mStatus = status;
        this.mUserdata = mUserdata;
        this.profileData = profileData;
    }

    public class ProfileData {

        @SerializedName("id")
        public String id;

        @SerializedName("email")
        public String mEmail;

        @SerializedName("username")
        public String mUserName;

        @SerializedName("password")
        public String mPassword;

        @SerializedName("licence_key")
        public String mLicenceKey;

        @SerializedName("imei_number")
        public String mIMEINumber;

        @SerializedName("status")
        public String mStatus;

        @SerializedName("create_data")
        public String mCreateData;

        @SerializedName("update_date")
        public String mUpdateDate;

        public ProfileData(String id, String email, String username,
                           String password, String licence_key, String imei_number, String status, String create_data,
                           String update_date) {
            this.mEmail = email;
            this.mUpdateDate = update_date;
            this.mCreateData = create_data;
            this.mStatus = status;
            this.mIMEINumber = imei_number;
            this.id = id;
            this.mLicenceKey = licence_key;
            this.mPassword = password;
            this.mUserName = username;
        }


    }
}

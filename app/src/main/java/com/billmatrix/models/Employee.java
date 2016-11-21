package com.billmatrix.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Employee implements Serializable {
    public int status;
    public String employeedata;
    public ArrayList<EmployeeData> data;

    public class EmployeeData implements Serializable{
        public String id;
        public String username;
        public String email;
        public String mobile_number;
        public String admin_id;
        public String password;
        public String status;
        public String create_date;
        public String update_date;
        public String imei_number;
        public String branch;
        public String location;
        public String type;
    }
}

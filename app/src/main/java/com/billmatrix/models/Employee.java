package com.billmatrix.models;

import java.io.Serializable;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Employee implements Serializable{
    public int status;
    public String employeedata;
    public EmployeeData data;

    public class EmployeeData {
        public String id;
        public String name;
        public String email;
        public String number;
        public String password;
        public String status;
        public String create_date;
        public String update_date;
    }
}

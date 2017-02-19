package com.billmatrix.models;

import java.io.Serializable;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public class Store implements Serializable {
    public String id;
    public String admin_id;
    public String cst_no;
    public String vat_tin;
    public String phone;
    public String zipcode;
    public String city_state;
    public String address_two;
    public String branch;
    public String address_one;
    public String status;
    public String create_date;
    public String update_date;
    public String store_name;

    @Override
    public String toString() {
        return "\nid = " + id + "\n admin id=" + admin_id
                + "\n store_name=" + store_name + "\n cst_no=" + cst_no
                + "\n vat_tin=" + vat_tin + "\n phone=" + phone
                + "\n zipcode=" + zipcode + "\n city_state=" + city_state
                + "\n branch =" + branch + "\n address_two=" + address_two
                + "\n address_one=" + address_one
                + "\n status=" + status + "\n create_date=" + create_date + "\n update_date=" + update_date;
    }
}

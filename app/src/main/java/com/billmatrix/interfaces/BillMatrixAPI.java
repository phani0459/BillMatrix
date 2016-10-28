package com.billmatrix.interfaces;

import com.billmatrix.models.Profile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by KANDAGATLAs on 28-10-2016.
 */

public interface BillMatrixAPI {

    public static final String BASE_URL = "http://mkrinfotech.com/billmatrix/";

    @GET("profile/1")
    Call<Profile> getProfile();

}

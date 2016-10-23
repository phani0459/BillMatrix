package com.billmatrix.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by KANDAGATLAs on 22-10-2016.
 */

public class Constants {
    public static String PROFILE_FILE_NAME = "Profile";
    public static String EMPLOYEES_FILE_NAME = "Employees";
    public static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
}

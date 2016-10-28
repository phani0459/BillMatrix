package com.billmatrix.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * Created by KANDAGATLAs on 22-10-2016.
 */

public class FileUtils {
    public static void writeToFile(Context context, String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static void saveLogin(Context mContext) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("isAdmin", "true");
        hashMap.put("status", "1");
        hashMap.put("message", "Logged in successfully");
        hashMap.put("adminName", "Phani Kumar");
        hashMap.put("branch", "MAIN");
        hashMap.put("location", "Hyderabad");
        hashMap.put("userImage", null);

        String profileJSON = Constants.getGson().toJson(hashMap);
        FileUtils.writeToFile(Constants.PROFILE_FILE_NAME, profileJSON, mContext);
    }*/

    /*
    HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("isAdmin", "true");
        hashMap.put("status", "1");
        hashMap.put("message", "Logged in successfully");
        hashMap.put("adminName", "Phani Kumar");
        hashMap.put("branch", "MAIN");
        hashMap.put("location", "Hyderabad");
        hashMap.put("userImage", null);
;
        String s = Constants.getGson().toJson(hashMap);
        FileUtils.writeToFile(Constants.PROFILE_FILE_NAME, s, mContext);
        Log.e("TAG", "before: " + FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext));

        String json = FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext);

        HashMap<String, String> stringHashMap = Constants.getGson().fromJson(json, hashMapType);
        if (stringHashMap.containsKey("location")) {
            stringHashMap.put("location", "NALGONDA");
        }

        String str = Constants.getGson().toJson(stringHashMap);
        FileUtils.writeToFile(Constants.PROFILE_FILE_NAME, str, mContext);
        Log.e("TAG", "after: " + FileUtils.readFromFile(Constants.PROFILE_FILE_NAME, mContext));
     */

    public static String readFromFile(String fileName, Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}

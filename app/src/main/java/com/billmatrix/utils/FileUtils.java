package com.billmatrix.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
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

    public static boolean isFileExists(String fileName, Context context) {
        File file = context.getFileStreamPath(fileName);
        if(file != null && file.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteFile(Context context, String fileName) {
        File file = context.getFileStreamPath(fileName);
        if(file != null && file.exists()) {
            context.deleteFile(fileName);
        }
    }

    public static String readFromFile(String fileName, Context context) {
        String jsonString = null;
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
                jsonString = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            jsonString = null;
            e.printStackTrace();
        } catch (IOException e) {
            jsonString = null;
            e.printStackTrace();
        }

        return jsonString;
    }
}

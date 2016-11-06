package com.billmatrix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.billmatrix.models.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KANDAGATLAs on 06-11-2016.
 */

public class BillMatrixDaoImpl implements BillMatrixDao {

    private SQLiteDatabase db;
    private BillMatrixDBHandler dbHandler;

    public BillMatrixDaoImpl(Context context) {
        dbHandler = new BillMatrixDBHandler(context);
        db = dbHandler.getWriteDB();
    }


    public void addEmployee(Employee.EmployeeData employeeData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.EMPLOYEE_NAME, employeeData.name);
        contentValues.put(DBConstants.EMPLOYEE_MOBILE, employeeData.mobile_number);
        contentValues.put(DBConstants.EMPLOYEE_LOGINID, employeeData.email);
        contentValues.put(DBConstants.EMPLOYEE_PASSWORD, employeeData.password);
        contentValues.put(DBConstants.EMPLOYEE_STATUS, employeeData.status);
        db.insert(DBConstants.EMPLOYEES_TABLE, null, contentValues);
    }

    public ArrayList<Employee.EmployeeData> getEmployees() {
        Cursor cursor = null;
        try {
            cursor = db.query(DBConstants.EMPLOYEES_TABLE, null,
                    DBConstants.SNO + "<>?", new String[]{"0"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                List<Employee.EmployeeData> employeesData = new ArrayList<>();
                do {

                    Employee.EmployeeData employeeData = new Employee().new EmployeeData();
                    employeeData.name = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_NAME)));
                    employeeData.email = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_LOGINID)));
                    employeeData.mobile_number = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_MOBILE)));
                    employeeData.password = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_PASSWORD)));
                    employeeData.status = (cursor.getString(cursor
                            .getColumnIndexOrThrow(DBConstants.EMPLOYEE_STATUS)));
                    employeesData.add(employeeData);

                } while (cursor.moveToNext());

                return (ArrayList<Employee.EmployeeData>) employeesData;
            }
        } catch (IllegalArgumentException e) {
            Log.d(DBConstants.TAG, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }

    @Override
    public void beginTransaction() {
        db.beginTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        db.endTransaction();
    }
}
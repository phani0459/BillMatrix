package com.billmatrix.database;

public interface BillMatrixDao {
    // To handle transaction
    public void beginTransaction();

    public void setTransactionSuccessful();

    public void endTransaction();

}

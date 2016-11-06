package com.billmatrix.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DBHandler {
	private int version;
	private String database;
	private String logTag;
	
	private DBGen dbGen;
	
	@SuppressWarnings("unused")
	private DBHandler(){}
	
	public DBHandler(Context context, String database, int version, String logTag){
		this.database=database;
		this.version=version;
		this.logTag=logTag;
		
		dbGen = DBGen.getInstance(context, this);
	}
	
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getLogTag() {
		return logTag;
	}

	public void setLogTag(String logTag) {
		this.logTag = logTag;
	}
	
	public SQLiteDatabase getReadDB(){
		return dbGen.open();
	}

	public SQLiteDatabase getWriteDB(){
		return dbGen.open();
	}
		
	public void close(){
		try{
			dbGen.close();
		}
		catch(SQLException sqlEx){
			Log.e(logTag, "Exception while closing connection",sqlEx);
		}
	}

	public abstract void onCreate(SQLiteDatabase db);
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}

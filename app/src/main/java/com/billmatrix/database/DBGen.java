package com.billmatrix.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DBGen {
	
	private Context ctx;
	
	//constants
	public final static int MODE_READ = 0;
	public final static int MODE_WRITE = 1;
	
	//SQLite Database 
	private SQLiteDatabase myDB = null;

	private DBHelper dbHelper;
	
	private DBHandler dbHandler;
	
	private static boolean isClosed = false;
	
	private static DBGen dbGen = null;
	private DBGen(Context ctx, DBHandler dbHandler){
		this.ctx=ctx;
		this.dbHandler=dbHandler;
		this.dbHelper = new DBHelper(ctx, dbHandler.getDatabase(), null, dbHandler.getVersion());
	}
	
	public static DBGen getInstance(Context ctx, DBHandler dbHandler){
		if(dbGen == null || isClosed){
			dbGen = new DBGen(ctx, dbHandler);
			isClosed = false;
		}
		return dbGen;
	}
	
	public SQLiteDatabase open(int mode){
		// To close the connection before opening a new one.
		if(myDB != null){
			close();
		}
	  	if(mode == MODE_WRITE){
	       myDB = dbHelper.getWritableDatabase();
	  	}
	  	else{
	  		myDB = dbHelper.getReadableDatabase();
	  	}
	  	return myDB;
	}
	
	
	public SQLiteDatabase open(){
		if((myDB == null)|| (! myDB.isOpen())){
			myDB = dbHelper.getWritableDatabase();
		}
		return myDB;
	}
	
	public boolean isDBExists(){
		boolean isDBExist=false;
		
		try{
			File dbPath=ctx.getDatabasePath(dbHandler.getDatabase());
			isDBExist = dbPath.exists();
		}
		catch(Exception e){
			e.printStackTrace();
			Log.e(dbHandler.getLogTag(),"ERROR DELETING THE FILE",e);
		}
		
		return isDBExist;
	}

	public boolean removeDB(){
		boolean isDBClosed=removeHardDB();
		return isDBClosed;
	}
	
	private boolean removeHardDB(){
		boolean isDBdeleted = false;
		
		try{
			File dbFilePath=ctx.getDatabasePath(dbHandler.getDatabase());
			close();
			isDBdeleted=dbFilePath.delete();
		}
		catch(Exception e){
			e.printStackTrace();
			Log.e(dbHandler.getLogTag(),"EXCEPTION RAISED IN DELETING DB");
		}
		
		return isDBdeleted;
	}
	
	public static void closeCursor(Cursor c){
		if(c != null){
			c.close();
		}
	}
	
	
	public void close(){
		isClosed = true;
		try{
			myDB.close();
		}
		catch(SQLException sqlex){
			Log.e(dbHandler.getLogTag(),sqlex.getMessage(),sqlex);
		}
		
		try{
			dbHelper.close();
		}
		catch(SQLException sqlex){
			Log.e(dbHandler.getLogTag(),sqlex.getMessage(),sqlex);
		}
	}
	
	class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			dbHandler.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dbHandler.onUpgrade(db, oldVersion, newVersion);
		}

	}
}

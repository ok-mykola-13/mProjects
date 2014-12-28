package com.mh.mprojects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	static final String DB_NAME = "mProjectsDB";
	static final int DB_VERSION = 1;
	
	static final String DB_TABLE_PROJECTS = "projects";
	static final String DB_TABLE_LISTS = "lists";
	static final String DB_TABLE_TASKS = "tasks";
	
	private static final String DB_CREATE_PROJECTS = "create table " +
			DB_TABLE_PROJECTS + " (" + MainProvider.KEY_ID + 
			" integer primary key autoincrement, " +
			MainProvider.PROJECTS_NAME + " text not null, " + 
			MainProvider.PROJECTS_GROUP + " integer default 1," +
			MainProvider.PROJECTS_CREATE_DATE + " long not null);";
	
	private static final String DB_CREATE_LISTS = "create table " +
			DB_TABLE_LISTS + " (" + MainProvider.KEY_ID + 
			" integer primary key autoincrement, " +
			MainProvider.LISTS_NAME + " text not null, " + 
			MainProvider.LISTS_DONE + " integer default 0," +
			MainProvider.LISTS_ORDER_NUM + " integer," +
			MainProvider.LISTS_PROJECT + " integer," +
			MainProvider.LISTS_TYPE + " integer default 0," +
			"foreign key (" + MainProvider.LISTS_PROJECT + ") "
					+ "references " + DB_TABLE_PROJECTS + "(" + MainProvider.KEY_ID + "));";
	
	private static final String DB_CREATE_TASKS = "create table " +
			DB_TABLE_TASKS + "( " + MainProvider.KEY_ID +
			" integer primary key autoincrement, " +
			MainProvider.TASKS_NAME + " text not null," +
			MainProvider.TASKS_DONE + " integer default 0," +
			MainProvider.TASKS_PARENT + " integer, " +
			MainProvider.TASKS_PRIOR + " integer default 0, " +
			"foreign key (" + MainProvider.TASKS_PARENT + ") "
					+ "references " + DB_TABLE_LISTS + "(" + MainProvider.KEY_ID + "));";
	
	public DBHelper(Context context, String name,
			CursorFactory factory, int version){
		super(context, name, factory, version);			
	}
	
	public DBHelper(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(DB_CREATE_PROJECTS);
		db.execSQL(DB_CREATE_LISTS);
		db.execSQL(DB_CREATE_TASKS);
		Log.w("DataBase","Creating");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		/*Log.w("OrganizerDB", "Upgrading from " + 
						oldVersion + " to " + newVersion);*/
	}
}

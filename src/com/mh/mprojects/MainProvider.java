package com.mh.mprojects;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MainProvider extends ContentProvider{
	
	//All Uri's
	public static final String PROVIDER_NAME = "com.mh.mprojects";
	public static final Uri CONTENT_URI_PROJECTS = Uri.parse("content://" + PROVIDER_NAME + "/projects");
	public static final Uri CONTENT_URI_LISTS = Uri.parse("content://" + PROVIDER_NAME + "/lists");
	public static final Uri CONTENT_URI_TASKS = Uri.parse("content://" + PROVIDER_NAME + "/tasks");
	
	
	public static final String KEY_ID = "_id";
	//Table projects
	public static final String PROJECTS_NAME = "p_name";
	public static final String PROJECTS_GROUP = "p_group";
	public static final String PROJECTS_CREATE_DATE = "p_create_date";
	//Table lists
	public static final String LISTS_NAME = "l_name";
	public static final String LISTS_DONE = "l_done";
	public static final String LISTS_ORDER_NUM = "l_order_name";
	public static final String LISTS_PROJECT = "l_project";
	public static final String LISTS_TYPE = "l_type";
	//Table tasks
	public static final String TASKS_NAME = "t_name";
	public static final String TASKS_DONE = "t_done";
	public static final String TASKS_PARENT = "t_parent";
	public static final String TASKS_PRIOR = "t_prior";
	
	
	private DBHelper dbHelper; 
	
	private static final int PROJECTS = 1;
	private static final int PROJECTS_ID = 2;
	private static final int LISTS = 3;
	private static final int LISTS_ID = 4;
	private static final int TASKS = 5;
	private static final int TASKS_ID = 6;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "projects", PROJECTS);
		uriMatcher.addURI(PROVIDER_NAME, "projects/#", PROJECTS_ID);
		uriMatcher.addURI(PROVIDER_NAME, "lists", LISTS);
		uriMatcher.addURI(PROVIDER_NAME, "lists/#", LISTS_ID);
		uriMatcher.addURI(PROVIDER_NAME, "tasks", TASKS);
		uriMatcher.addURI(PROVIDER_NAME, "tasks/#", TASKS_ID);
	}
	
	@Override
	public boolean onCreate(){
		dbHelper = new DBHelper(getContext(),
				DBHelper.DB_NAME, null,
				DBHelper.DB_VERSION);
		
		return true;
	}
	
	@Override
	public String getType(Uri uri){
		switch(uriMatcher.match(uri)){
		case PROJECTS: return "vnd.android.cursor.dir/vnd.mh.mprojects";
		case PROJECTS_ID: return "vnd.android.cursor.item/vnd.mh.mprojects";
		case LISTS: return "vnd.android.cursor.dir/vnd.mh.mprojects";
		case LISTS_ID: return "vnd.android.cursor.item/vnd.mh.mprojects";
		case TASKS: return "vnd.android.cursor.dir/vnd.mh.mprojects";
		case TASKS_ID: return "vnd.android.cursor.item/vnd.mh.mprojects";
		
		default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder){

		String groupBy = null;
		String having = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		switch(uriMatcher.match(uri)){
		case PROJECTS:
			queryBuilder.setTables(DBHelper.DB_TABLE_PROJECTS);
			break;
		case LISTS:			
			queryBuilder.setTables(DBHelper.DB_TABLE_LISTS);
			break;
		case TASKS:			
			queryBuilder.setTables(DBHelper.DB_TABLE_TASKS);
			break;
		default: 
			break;
		}
		
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, 
				selectionArgs, groupBy, having, sortOrder);
		
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String nullColumnHack = null;
		long id = -1;
		Uri insertedId;
		
		switch(uriMatcher.match(uri)){
		case PROJECTS:
			id = db.insert(DBHelper.DB_TABLE_PROJECTS, 
						nullColumnHack, values); 
			if (id > -1){
				insertedId = ContentUris.withAppendedId(CONTENT_URI_PROJECTS, id);
			}else 	
				insertedId = null;
			break;
			
		case LISTS:
			id = db.insert(DBHelper.DB_TABLE_LISTS, 
					nullColumnHack, values);
			if (id > -1){
				insertedId = ContentUris.withAppendedId(CONTENT_URI_LISTS, id);
			}else
				insertedId = null;
			break;
		
		case TASKS:
			id = db.insert(DBHelper.DB_TABLE_TASKS, 
					nullColumnHack, values);
			if (id > -1){
				insertedId = ContentUris.withAppendedId(CONTENT_URI_TASKS, id);
			}else
				insertedId = null;
			break;
			
		default: 
			return null;
		}
		
		getContext().getContentResolver().notifyChange(insertedId, null);
		return insertedId;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int deleteCount = -1;

		switch(uriMatcher.match(uri)){
		case PROJECTS_ID:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? 
					" AND (" + selection + ")" : "");

			deleteCount = db.delete(DBHelper.DB_TABLE_PROJECTS,
					selection, selectionArgs);
			break;
			
		case LISTS_ID:
			String a_rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + a_rowID + (!TextUtils.isEmpty(selection) ? 
					" AND (" + selection + ")" : "");

			deleteCount = db.delete(DBHelper.DB_TABLE_LISTS,
					selection, selectionArgs);
			break;

		case TASKS_ID:
			String nrowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + nrowID + (!TextUtils.isEmpty(selection) ? 
					" AND (" + selection + ")" : "");

			deleteCount = db.delete(DBHelper.DB_TABLE_TASKS,
					selection, selectionArgs);
			break;
			
		default:
			break;
		}		
		
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}
	
	@Override
	public int update(Uri uri, ContentValues contentValues,
			String selection, String[] selectionArgs){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int updateCount = -1;
		String rowId  = "";
		
		switch(uriMatcher.match(uri)){
		case PROJECTS_ID: 
			rowId = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowId + 
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			updateCount = db.update(DBHelper.DB_TABLE_PROJECTS, 
					contentValues, selection, selectionArgs);
			break;

		case LISTS_ID:
			rowId = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowId + 
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			updateCount = db.update(DBHelper.DB_TABLE_LISTS, 
					contentValues, selection, selectionArgs);
			break;
			
		case TASKS_ID:
			rowId = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowId + 
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
			updateCount = db.update(DBHelper.DB_TABLE_TASKS, 
					contentValues, selection, selectionArgs);
			break;
			
		default: 
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}
}
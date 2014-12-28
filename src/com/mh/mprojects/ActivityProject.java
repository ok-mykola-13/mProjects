package com.mh.mprojects;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

public class ActivityProject extends FragmentActivity {
	
	public FragmentProjectPlan fragmentPlan;
	public FragmentProjectTodolists fragmentLists;
	public FragmentProjectNote fragmentNote;
	
	public int p_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		p_id = getIntent().getIntExtra("p_id", -1);
		Toast.makeText(this, Integer.toString(p_id), Toast.LENGTH_SHORT).show();
		
		FragmentManager fm = getSupportFragmentManager();
		
		fragmentPlan = 
				(FragmentProjectPlan)fm.findFragmentById(R.id.f_project_plan);
		fragmentLists = 
				(FragmentProjectTodolists)fm.findFragmentById(R.id.f_project_lists);
		fragmentNote = new FragmentProjectNote();
	}
	
	public void addListToPlan(String name){
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(MainProvider.LISTS_NAME, name);
		values.put(MainProvider.LISTS_DONE, 0);
		values.put(MainProvider.LISTS_ORDER_NUM, fragmentPlan.getLastOrderNum() + 1);
		values.put(MainProvider.LISTS_PROJECT, p_id);
		values.put(MainProvider.LISTS_TYPE, 0);
		cr.insert(MainProvider.CONTENT_URI_LISTS, values);	
		
		fragmentPlan.updatePlanLoader();
	}
	
	public void addItemToList(String name, int id){
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_NAME, name);
		values.put(MainProvider.TASKS_DONE, 0);
		values.put(MainProvider.TASKS_PARENT, id);
		values.put(MainProvider.TASKS_PRIOR, 0);
		cr.insert(MainProvider.CONTENT_URI_TASKS, values);	
		
		fragmentLists.updateItemsLoader();
	}
	
	public void renameList(String name, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.LISTS_NAME, name);
		Uri rowURI = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		
		ContentResolver cr_u = getContentResolver();
		
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		if(updatedRowCount > -1){
			fragmentPlan.updatePlanLoader();
		}
	}
	
	public void renameItem(String name, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_NAME, name);
		Uri rowURI = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		
		ContentResolver cr_u = getContentResolver();
		
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		if(updatedRowCount > -1){
			fragmentLists.updateItemsLoader();
		}
	}
	
	public int getSelectedListId(){
		return fragmentPlan.selected_list_id;
	}
	public boolean getSelectedListDone(){
		return fragmentPlan.selected_list_done;
	}

}

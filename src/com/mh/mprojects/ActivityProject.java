package com.mh.mprojects;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;

public class ActivityProject extends FragmentActivity {
	
	private int currentVersion;
	private int screenSize;
	
	//to support small screens
	private boolean plan_mode = true;
	public void setPlanMode(boolean mode){
		plan_mode = mode;
		if(!mode){
			getSupportFragmentManager().beginTransaction()
					.hide(fragmentPlan)
					.show(fragmentLists)
					.commit();
		}
	}
	public boolean getPlanMode(){
		return plan_mode;
	}
	
	public FragmentProjectPlan fragmentPlan;
	public FragmentProjectTodolists fragmentLists;
	
	//iD of current project
	private int p_id;
	private String p_name;
	public int getProjectId(){
		return p_id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		currentVersion = android.os.Build.VERSION.SDK_INT;
		screenSize = getResources().getConfiguration().screenLayout &
					Configuration.SCREENLAYOUT_SIZE_MASK;
		
		//get project id from incoming Intent
		p_id = getIntent().getIntExtra("p_id", -1);
		p_name = getIntent().getStringExtra("p_name");
		
		//initialize fragment 
		FragmentManager fm = getSupportFragmentManager();
		fragmentPlan = 
				(FragmentProjectPlan)fm.findFragmentById(R.id.f_project_plan);
		fragmentLists = 
				(FragmentProjectTodolists)fm.findFragmentById(R.id.f_project_lists);
		if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL){
			fm.beginTransaction().hide(fragmentLists).commit();
			plan_mode = true;
		}
		
		//set color to ActionBar
		if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			ActionBar actionBar = getActionBar();
			actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar));
			actionBar.setTitle(p_name);
		}
		
		//set in and out animation of Activity
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		fragmentPlan.restartPlanLoader();
		fragmentLists.restartItemsLoader();
	}
	
	@Override
	public void onBackPressed() {
		if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL){
		    if(!plan_mode){
		    	getSupportFragmentManager().beginTransaction()
		    				.hide(fragmentLists)
		    				.show(fragmentPlan)
		    				.commit();
		    	plan_mode = true;
		    }else
		    	super.onBackPressed();	
		}else
			super.onBackPressed();
	}
	
	public int getSelectedListId(){
		return fragmentPlan.selected_list_id;
	}
	public boolean getSelectedListDone(){
		return fragmentPlan.selected_list_done;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
	       ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, view, menuInfo);
	    if(view.getId() == R.id.lv_plan){
	    	menu.add(0,0,0,"Delete");//getResources().getString(R.string.delete));
			menu.add(0,1,1,"Rename");//getResources().getString(R.string.rename));
			menu.add(0,2,2,"Move Up");//getResources().getString(R.string.rename));
			menu.add(0,3,3,"Move Down");//getResources().getString(R.string.rename));
	    }else if(view.getId() == R.id.lv_todolists){
	    	menu.add(0,4,4,"Delete");//getResources().getString(R.string.delete));
			menu.add(0,5,5,"Rename");//getResources().getString(R.string.rename));
			menu.add(0,6,7,"Move to List..");
	    }
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item){
		ContextMenuInfo menuInfo = item.getMenuInfo();
		int itemPosition = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
		MList list;
		MTask task;
		switch (item.getItemId()){
		case 0:
			list = fragmentPlan.getPlanList().get(itemPosition);
			fragmentPlan.menuDelete(list.get_id(), list.getOrderNum());
        	return true;
		case 1:
			list = fragmentPlan.getPlanList().get(itemPosition);
			fragmentPlan.menuRename(list.get_id(), list.getName());
			return true;
		case 2:
			list = fragmentPlan.getPlanList().get(itemPosition);
			fragmentPlan.menuMoveListUp(list.get_id(), list.getOrderNum());
			return true;	
		case 3:
			list = fragmentPlan.getPlanList().get(itemPosition);
			fragmentPlan.menuMoveListDown(list.get_id(), list.getOrderNum());
			return true;
		case 4:
			task = fragmentLists.getListsList().get(itemPosition);
			fragmentLists.menuDeleteItem(task.get_id());
			return true;
		case 5:
			task = fragmentLists.getListsList().get(itemPosition);
			fragmentLists.menuRenameItem(task.get_id(), task.getName());
			return true;
		case 6:
			task = fragmentLists.getListsList().get(itemPosition);
			fragmentLists.menuMoveItemToList(task.get_id(), task.getParent(), task.getName());
			return true;
		}
		return false;
    }
}
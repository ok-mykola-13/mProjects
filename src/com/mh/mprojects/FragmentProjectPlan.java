package com.mh.mprojects;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentProjectPlan extends Fragment 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int currentVersion;
	private int screenSize;
	private Typeface tf; //custom font
	private ActivityProject activity;
	
	TextView tv_plan_no;
	
	private ListView lv;
	private ArrayList<MList> planList;
	private TasksAdapter tasksAdapter;
	public ArrayList<MList> getPlanList(){
		return planList;
	}
	
	//some data for FragmentTodolists
	public int selected_list_id = -1;
	public boolean selected_list_done = false;
	
	
	/** Initialize views and main variables */
/******************************************************************************************/	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(
                R.layout.fragment_project_plan, container, false);
        
        currentVersion = android.os.Build.VERSION.SDK_INT;
        screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;
        activity = (ActivityProject)getActivity();
        tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
        
        planList = new ArrayList<MList>();
        tasksAdapter = new TasksAdapter(getActivity(), R.layout.item_lv_plan, planList);
        
        lv = (ListView)rootView.findViewById(R.id.lv_plan);
        lv.setAdapter(tasksAdapter);
        lv.setDrawSelectorOnTop(true);
        lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selected_list_id = ((MList)lv.getItemAtPosition(position)).get_id();
				selected_list_done = ((MList)lv.getItemAtPosition(position)).isDone();
				activity.fragmentLists.restartItemsLoader();
				tasksAdapter.notifyDataSetChanged();
				if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL){
					activity.setPlanMode(false);
				}
			}
        	
        });
        
        //Plan title: just set font
        TextView tv_title = (TextView)rootView.findViewById(R.id.tv_plan_title);
        tv_title.setTypeface(tf);
        
        tv_plan_no = (TextView)rootView.findViewById(R.id.tv_f_plan_no);
        tv_plan_no.setTypeface(tf);
        
        // image-button add new List to plan
        ImageView img_add = (ImageView)rootView.findViewById(R.id.img_edit_plan);
		img_add.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogInput dialog = DialogInput.newInstance(
						getResources().getString(R.string.enter_name_of_new_list),
						"", 
						0, 
						0);
				dialog.show(getFragmentManager(), "dialog rename item");	
			}
		});
		
        return rootView;
    }
/******************************************************************************************/	

	/** Plan's lists LOADER */ 
/******************************************************************************************/
	// init loader when Activity will be started
	//			if api < 11 - register for context menu listView
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
		
		if (currentVersion < android.os.Build.VERSION_CODES.HONEYCOMB){
			registerForContextMenu(lv);
		}
	}
	
	// cover over restartLoader to call it from listeners
	public void restartPlanLoader(){
		getActivity().getSupportLoaderManager().restartLoader(0, null, this);
	}
	
	//Initialize Loader for Lists
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity(),
				MainProvider.CONTENT_URI_LISTS,
				null,
				MainProvider.LISTS_PROJECT+"=?",
				new String[] {Integer.toString(activity.getProjectId())},
				MainProvider.LISTS_ORDER_NUM);
		
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
		int IdIndex = cursor.getColumnIndexOrThrow(MainProvider.KEY_ID);
		int NameIndex = cursor.getColumnIndexOrThrow(MainProvider.LISTS_NAME);
		int NumIndex = cursor.getColumnIndexOrThrow(MainProvider.LISTS_ORDER_NUM);
		int DoneIndex = cursor.getColumnIndexOrThrow(MainProvider.LISTS_DONE);
		int ProjectIndex = cursor.getColumnIndexOrThrow(MainProvider.LISTS_PROJECT);
		int TypeIndex = cursor.getColumnIndexOrThrow(MainProvider.LISTS_TYPE);

		// refill arrayList with MLists
		planList.clear();
		while(cursor.moveToNext()){
			int id = cursor.getInt(IdIndex);
			MList p = new MList(id,
					cursor.getString(NameIndex),
					cursor.getInt(DoneIndex),
					cursor.getInt(NumIndex),
					cursor.getInt(ProjectIndex),
					cursor.getInt(TypeIndex));
				
			planList.add(p);
		}		
		//select first list to show its items in fragmentItems
		if(planList.size() !=0 && selected_list_id == -1){
			selected_list_id = planList.get(0).get_id();
			selected_list_done = planList.get(0).isDone();
		}
		
		if(planList.size() == 0)
			tv_plan_no.setVisibility(View.VISIBLE);
		else
			tv_plan_no.setVisibility(View.GONE);
		
		//update views
		tasksAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {	
	}
/******************************************************************************************/
	
	/** Methods to work with lists in plan: create, update, move, etc...*/
/******************************************************************************************/
	//calls when user want move down the last list,
	//			           add new list, ect...
	public int getLastOrderNum(){
		int lastIndex = planList.toArray().length-1;
		if(lastIndex < 0)
			return 0;
		else
			return planList.get(lastIndex).getOrderNum();	
	}
	
	//add new List - just add to db & update loader
	public void addListToPlan(String name){
		ContentResolver cr = getActivity().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(MainProvider.LISTS_NAME, name);
		values.put(MainProvider.LISTS_DONE, 0);
		values.put(MainProvider.LISTS_ORDER_NUM, getLastOrderNum() + 1);
		values.put(MainProvider.LISTS_PROJECT, activity.getProjectId());
		values.put(MainProvider.LISTS_TYPE, 0);
		cr.insert(MainProvider.CONTENT_URI_LISTS, values);	
		
		restartPlanLoader();
	}
	
	// to rename List - put data to db & update loader
	public void renameList(String name, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.LISTS_NAME, name);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		Uri rowURI = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);		
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		
		if(updatedRowCount > -1){
			restartPlanLoader();
		}
	}
	
	// set list done - update db & restart items loader to check all items
	public void updateListDone(boolean done, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.LISTS_DONE, done);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		Uri rowURI = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		
		if(updatedRowCount > -1){
			restartPlanLoader();
			
			if(id == selected_list_id)
				selected_list_done = done;
			
			activity.fragmentLists.restartItemsLoader();
		}
	}
	
	//Delete list from plan - change order_nums in lists below this,
	//						  delete from db,
	//						  restart loader
	public void deleteListFromPlan(int id, int pos){
		String where = null;
		String[] whereArgs = null;
		ContentResolver cr_d = getActivity().getContentResolver();
		
		// change all order_nums in lists below this list
		int max_pos = getLastOrderNum();	
		if (max_pos != 0){
			//update all later numbers
			for(int i = pos; i < max_pos; i++){
				ContentValues values = new ContentValues();
				values.put(MainProvider.LISTS_ORDER_NUM, i);
				int id_u = planList.get(i).get_id();
				Uri rowURI_update = 
					ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_u);

				int updatedRowCount = cr_d.update(rowURI_update, 
												values,
												null, 
												null);
				if(updatedRowCount < 0)
					break;
			}
		}
		
		// delete from db
		int deletedTasks = cr_d.delete(MainProvider.CONTENT_URI_TASKS, 
										MainProvider.TASKS_PARENT + "=?", 
										new String[] {Integer.toString(id)});
		if(deletedTasks > -1){
			Uri rowURI = ContentUris.withAppendedId(
									MainProvider.CONTENT_URI_LISTS,
									id);
			int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
			
			if(deletedRowCount > -1){
				selected_list_id = -1;
				restartPlanLoader();
				activity.fragmentLists.restartItemsLoader();
			}
		}
	}
	
	//Move list up in plan - higher order_num of the prev list,
	//						 lower order_num of this list
	public void moveListUp(int id, int pos){
		
		//is it not the first list?
		if(pos == 1) return;
		
		// get id of the previous list and change is order_num to higher
		int id_prev = planList.get(pos-2).get_id();
		
		ContentValues values_1 = new ContentValues();
		values_1.put(MainProvider.LISTS_ORDER_NUM, pos-1);
		Uri rowURI_1 = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		ContentResolver cr_u = getActivity().getContentResolver();
		int updatedRowCount_1 = cr_u.update(rowURI_1, values_1, null, null);
		
		// order_num of the prev list is changed, it's time to lower this list
		if(updatedRowCount_1 > -1){
			ContentValues values_2 = new ContentValues();
			values_2.put(MainProvider.LISTS_ORDER_NUM, pos);
			Uri rowURI_2 = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_prev);
			
			int updatedRowCount_2 = cr_u.update(rowURI_2, values_2, null, null);
			
			if(updatedRowCount_2 > -1)
				restartPlanLoader();
		}
	}
	
	//move list down - lower order_num of next list
	//				   high order_num of this list
	public void moveListDown(int id, int pos){
		// isn't it the last list?
		if(pos == getLastOrderNum()) return;
		
		//lower order_num of next list
		int id_next = planList.get(pos).get_id();
		ContentValues values_1 = new ContentValues();
		values_1.put(MainProvider.LISTS_ORDER_NUM, pos+1);
		Uri rowURI_1 = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		ContentResolver cr_u = getActivity().getContentResolver();
		int updatedRowCount_1 = cr_u.update(rowURI_1, values_1, null, null);
		//success - update order_num of this list (+1)
		if(updatedRowCount_1 > -1){
			ContentValues values_2 = new ContentValues();
			values_2.put(MainProvider.LISTS_ORDER_NUM, pos);
			Uri rowURI_2 = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_next);
			
			int updatedRowCount_2 = cr_u.update(rowURI_2, values_2, null, null);
			
			if(updatedRowCount_2 > -1)
				restartPlanLoader();
		}
	}
/*****************************************************************************************/	
	
	/** ArrayAdapter for items in Plan */
/*****************************************************************************************/
	private class TasksAdapter extends ArrayAdapter<MList>{
		
		private int resource;
			
		public TasksAdapter(Context context,
				int resource,
				List<MList> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			//get MList object and fetch some properties to use it in listeners
			MList item = getItem(position);
			final int id = item.get_id();
			final String name = item.getName();
			final int order_num = item.getOrderNum();
			final boolean done = item.isDone();
			
			//get root view
			RelativeLayout rootView;
			if (convertView == null){
				rootView = new RelativeLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater)getContext().getSystemService(inflater);
				li.inflate(resource, rootView,true);
			}else{
				rootView = (RelativeLayout) convertView;
			}
			
			//hightlight active plan-item
			if(selected_list_id == id)
				rootView.setBackgroundColor(0x6691c5f1);
			else
				rootView.setBackgroundColor(0x0000000);
			
			//fetch all needed view
			TextView numView = (TextView)rootView.findViewById(
											R.id.tv_project_plan_task_num);
			TextView nameView = (TextView)rootView.findViewById(
											R.id.tv_project_plan_task_name);
			CheckBox ch_done = (CheckBox)rootView.findViewById(R.id.chb_plan_item);
			ImageView img_menu = (ImageView)rootView.findViewById(
											R.id.img_project_plan_task_menu);
			
			//set order_num of list
			numView.setText(Integer.toString(order_num) + ".");
		    numView.setTypeface(tf);
			
			//set name
			nameView.setText(name);
			nameView.setTypeface(tf);
			
			// check or uncheck CheckBox & add Listener to it
			ch_done.setChecked(done);
			ch_done.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateListDone(!done, id);
				}
			});
			
			//create Popup menu and attach it to menu-image
			if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
				//PopupMenu
				final PopupMenu popupMenu = new PopupMenu(getActivity(), img_menu);
				Menu menu = popupMenu.getMenu();
				menu.add(0,0,0,getResources().getString(R.string.m_delete));
				menu.add(0,1,1,getResources().getString(R.string.m_rename));
				menu.add(0,2,2,getResources().getString(R.string.m_move_up));
				menu.add(0,3,3,getResources().getString(R.string.m_move_down));
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch(item.getItemId()){
						//delete list
						case 0:
							menuDelete(id, order_num);
							return true;
						//rename list
						case 1:
							menuRename(id, name);			
							return true;
						case 2:
							menuMoveListUp(id, order_num);
							return true;
						case 3:
							menuMoveListDown(id, order_num);
							return true;
						}
						return false;
					}
				});
				img_menu.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						popupMenu.show();					
					}
				});
			}
			return rootView;
		}
	}
	/** Methods for menu ******************************************************************/
	public void menuDelete(int id, int order_num){
		deleteListFromPlan(id, order_num);
	}
	public void menuRename(int id, String name){
		DialogInput dialog = DialogInput.newInstance(
				getResources().getString(R.string.rename_list) + " " + name, 
				name, 
				id, 
				2);
		dialog.show(getFragmentManager(), "dialog rename item");
	}
	public void menuMoveListUp(int id, int order_num){
		moveListUp(id, order_num);
	}
	public void menuMoveListDown(int id, int order_num){
		moveListDown(id, order_num);
	}
	/**************************************************************************************/
/******************************************************************************************/	
}
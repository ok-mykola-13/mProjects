package com.mh.mprojects;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentProjectPlan extends Fragment 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int currentVersion;
	
	private Typeface tf;
	
	ArrayList<MList> planList;
	private TasksAdapter tasksAdapter;
	
	public int selected_list_id = -1;
	public boolean selected_list_done = false;
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_project_plan, container, false);
        
        currentVersion = android.os.Build.VERSION.SDK_INT;
        
        planList = new ArrayList<MList>();

        // set fontFamily to title
        tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
        
        tasksAdapter = new TasksAdapter(getActivity(), R.layout.item_lv_plan, planList);
        
        final ListView lv = (ListView)rootView.findViewById(R.id.lv_plan);
        lv.setAdapter(tasksAdapter);
        
        lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selected_list_id = ((MList)lv.getItemAtPosition(position)).get_id();
				((ActivityProject)getActivity()).fragmentLists.updateItemsLoader();
			}
        	
        });
        
        // make items in ListView clickable
        lv.setDrawSelectorOnTop(true);
        
        TextView tv_title = (TextView)rootView.findViewById(R.id.tv_plan_title);
        tv_title.setTypeface(tf);
        
        ImageView img_add = (ImageView)rootView.findViewById(R.id.img_edit_plan);
		img_add.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "add list", Toast.LENGTH_SHORT).show();
				DialogInput dialog = DialogInput.newInstance(
						//getResources().getString(R.string.enter_new_name_item),
						"Enter name of list: ",
						"", 
						0, 
						0);
				dialog.show(getFragmentManager(), "dialog rename item");	
			}
			
		});
		
        return rootView;
    }
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}
	
	public void updatePlanLoader(){
		getActivity().getSupportLoaderManager().restartLoader(0, null, this);
	}
	
	public int getLastOrderNum(){
		int lastIndex = planList.toArray().length-1;
		if(lastIndex < 0)
			return 0;
		else
			return planList.get(lastIndex).getOrderNum();	
	}
	
	public void deleteListFromPlan(int id, int pos){
		String where = null;
		String[] whereArgs = null;
		
		ContentResolver cr_d = getActivity().getContentResolver();
		//check if has items !!!
		Cursor cursor_check = cr_d.query(
				MainProvider.CONTENT_URI_TASKS, 
				new String[] {"_id"},
				MainProvider.TASKS_PARENT + "=?",
				new String[] {Long.toString(id)}, 
				null);
		if(cursor_check.getCount() > 0){
			Toast.makeText(getActivity(), 
					"List is not empty!", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		Uri rowURI = ContentUris.withAppendedId(
				MainProvider.CONTENT_URI_LISTS,
				id);
		
		int max_pos = getLastOrderNum();	
		if (max_pos != 0){
			//update all later numbers
			for(int i = pos; i < max_pos; i++){
				ContentValues values = new ContentValues();
				values.put(MainProvider.LISTS_ORDER_NUM, i);
				int id_u = planList.get(i).get_id();
				Uri rowURI_update = 
					ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_u);
				Toast.makeText(getActivity(), Integer.toString(i), Toast.LENGTH_SHORT).show();
				int updatedRowCount = cr_d.update(rowURI_update, 
												values,
												null, 
												null);
				if(updatedRowCount < 0)
					break;
			}
		}
		
		int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
		
		if(deletedRowCount > -1){
			updatePlanLoader();
		}
	}
	
	public void moveListUp(int id, int pos){
		
		if(pos == 1) return;
		
		int id_prev = planList.get(pos-2).get_id();
		
		ContentValues values_1 = new ContentValues();
		values_1.put(MainProvider.LISTS_ORDER_NUM, pos-1);
		Uri rowURI_1 = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		
		int updatedRowCount_1 = cr_u.update(rowURI_1, values_1, null, null);
		if(updatedRowCount_1 > -1){
			ContentValues values_2 = new ContentValues();
			values_2.put(MainProvider.LISTS_ORDER_NUM, pos);
			Uri rowURI_2 = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_prev);
			
			int updatedRowCount_2 = cr_u.update(rowURI_2, values_2, null, null);
			
			if(updatedRowCount_2 > -1)
				updatePlanLoader();
		}
	}
	
	public void moveListDown(int id, int pos){
		
		if(pos == getLastOrderNum()) return;
		
		int id_next = planList.get(pos).get_id();
		
		ContentValues values_1 = new ContentValues();
		values_1.put(MainProvider.LISTS_ORDER_NUM, pos+1);
		Uri rowURI_1 = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		
		int updatedRowCount_1 = cr_u.update(rowURI_1, values_1, null, null);
		if(updatedRowCount_1 > -1){
			ContentValues values_2 = new ContentValues();
			values_2.put(MainProvider.LISTS_ORDER_NUM, pos);
			Uri rowURI_2 = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_LISTS, id_next);
			
			int updatedRowCount_2 = cr_u.update(rowURI_2, values_2, null, null);
			
			if(updatedRowCount_2 > -1)
				updatePlanLoader();
		}
	}
	
	//Initialize Loader for Projects
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Toast.makeText(getActivity(), "loader", Toast.LENGTH_SHORT).show();
			CursorLoader loader = new CursorLoader(getActivity(),
					MainProvider.CONTENT_URI_LISTS,
					null,
					MainProvider.LISTS_PROJECT+"=?",
					new String[] {Integer.toString(((ActivityProject)getActivity()).p_id)},
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
			
			if(selected_list_id == -1 && planList.size() != 0){
				selected_list_id = planList.get(0).get_id();
				selected_list_done = planList.get(0).isDone();
			}
			
			tasksAdapter.notifyDataSetChanged();
		}
		
		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {	
		}
	
	//Adapter for ListView of Plan
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
			
			RelativeLayout rootView;
			
			MList item = getItem(position);
			final int id = item.get_id();
			final String name = item.getName();
			final int order_num = item.getOrderNum();
			
			if (convertView == null){
				rootView = new RelativeLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater)getContext().getSystemService(inflater);
				li.inflate(resource, rootView,true);
			}else{
				rootView = (RelativeLayout) convertView;
			}
			
			// customFont
			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
		            "fonts/comic.ttf");
			
			//set name
			TextView numView = (TextView)rootView.findViewById(R.id.tv_project_plan_task_num);
			numView.setText(Integer.toString(order_num) + ".");
		    numView.setTypeface(tf);
			
			//set name
			TextView nameView = (TextView)rootView.findViewById(R.id.tv_project_plan_task_name);
			nameView.setText(name);
			nameView.setTypeface(tf);
			
			ImageView img_menu = (ImageView)rootView.findViewById(R.id.img_project_plan_task_menu);
			
			if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
				//PopupMenu
				final PopupMenu popupMenu = new PopupMenu(getActivity(), img_menu);
				Menu menu = popupMenu.getMenu();
				menu.add(0,0,0,"Delete");//getResources().getString(R.string.delete));
				menu.add(0,1,1,"Rename");//getResources().getString(R.string.rename));
				menu.add(0,2,2,"Move Up");//getResources().getString(R.string.rename));
				menu.add(0,3,3,"Move Down");//getResources().getString(R.string.rename));
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch(item.getItemId()){
						//delete list
						case 0:
							deleteListFromPlan(id, order_num);
							return true;
						//rename list
						case 1:
							DialogInput dialog = DialogInput.newInstance(
									"Rename list "+ name,//getResources().getString(R.string.enter_new_name_item), 
									name, 
									id, 
									2);
							dialog.show(getFragmentManager(), "dialog rename item");			
							return true;
						case 2:
							moveListUp(id, order_num);
							return true;
						case 3:
							moveListDown(id, order_num);
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

}
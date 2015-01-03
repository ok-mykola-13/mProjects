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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;


public class FragmentProjectTodolists extends Fragment 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int currentVersion;
	private int screenSize;
	private Typeface tf;
	private ActivityProject activity;
	
	private ListsAdapter listsAdapter;
	private ListView lv;
	private ArrayList<MTask> listsList;
	public ArrayList<MTask> getListsList(){
		return listsList;
	}
	
	private String sort_order;
	
	/** set view and initialize main variables */
/*****************************************************************************************/	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(
                R.layout.fragment_todolists, container, false);
        
        activity = (ActivityProject)getActivity();
        currentVersion = android.os.Build.VERSION.SDK_INT;
        screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;
        tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
        
        // just set font
        TextView tv_sort = (TextView)rootView.findViewById(R.id.tv_f_todos_sort);
        tv_sort.setTypeface(tf);
        
        //create & init sort-spinner. OnItemSelected - sort & update loader
        final ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(getResources().getString(R.string.by_name));
        arrayList.add(getResources().getString(R.string.by_priority));
        arrayList.add(getResources().getString(R.string.by_done));
        sort_order = arrayList.get(0);
        
        Spinner s_sort = (Spinner)rootView.findViewById(R.id.s_lists_sort);
        s_sort.setAdapter(new SpinnerAdapter(getActivity(), R.layout.item_spinner,arrayList));
        s_sort.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				sort_order = arrayList.get(position);
				restartItemsLoader();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        listsList = new ArrayList<MTask>();
        listsAdapter = new ListsAdapter(getActivity(),R.layout.item_lv_lists, listsList);
        lv = (ListView)rootView.findViewById(R.id.lv_todolists);
        lv.setDrawSelectorOnTop(true);
        lv.setAdapter(listsAdapter);
        
        
        // "button" add new item
        TextView tv_add_todo = (TextView)rootView.findViewById(R.id.tv_add_todolist);
        tv_add_todo.setTypeface(tf);
        if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL)
        	tv_add_todo.setVisibility(View.GONE);
        else
        	tv_add_todo.setVisibility(View.VISIBLE);
        
        LinearLayout ll_add = (LinearLayout)rootView.findViewById(R.id.ll_add_todolist);
        if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL)
        	ll_add.setBackgroundColor(0xfff);
        
        ll_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(activity.getSelectedListId() == -1) return;
				DialogInput dialog = DialogInput.newInstance(
						getResources().getString(R.string.enter_name_of_new_item),
						"", 
						activity.getSelectedListId(), 
						1);
				dialog.show(getFragmentManager(), "dialog rename item");				
			}
		});
        
        return rootView;
    }
	
/******************************************************************************************/
	
	/** Items LOADER */
/******************************************************************************************/
	// init loader when Activity will be started
	//			if api < 11 - register for context menu listView
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
		
		if (currentVersion < android.os.Build.VERSION_CODES.HONEYCOMB){
			registerForContextMenu(lv);
		}
	}
	// just cover on restartLoader to use in Listeners
	public void restartItemsLoader(){
		getActivity().getSupportLoaderManager().restartLoader(1, null, this);
	}
	
	//Initialize Loader for Items
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//select sort order
		String sort = MainProvider.TASKS_PRIOR + " DESC";
		if(sort_order.equals(getResources().getString(R.string.by_name)))
			sort = MainProvider.TASKS_NAME;
		else if (sort_order.equals(getResources().getString(R.string.by_done)))
			sort = MainProvider.TASKS_DONE + " DESC";
			
		CursorLoader loader = new CursorLoader(getActivity(),
				MainProvider.CONTENT_URI_TASKS,
				null,
				MainProvider.TASKS_PARENT+"=?",
				new String[] {Integer.toString(activity.getSelectedListId())},
				sort);
		
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
		int IdIndex = cursor.getColumnIndexOrThrow(MainProvider.KEY_ID);
		int NameIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_NAME);
		int DoneIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_DONE);
		int ParentIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_PARENT);
		int PriorIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_PRIOR);

		//refill items list
		listsList.clear();
		while(cursor.moveToNext()){
			int id = cursor.getInt(IdIndex);
			MTask p = new MTask(id,
					cursor.getString(NameIndex),
					cursor.getInt(DoneIndex),
					cursor.getInt(ParentIndex),
					cursor.getInt(PriorIndex));
				
			listsList.add(p);
		}		
		//update views
		listsAdapter.notifyDataSetChanged();
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {	
	}
/*******************************************************************************************/
	
	/** Methods to impact on items*/
/*******************************************************************************************/
	// add item to db & restart loader
	public void addItemToList(String name, int id){
		ContentResolver cr = getActivity().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_NAME, name);
		values.put(MainProvider.TASKS_DONE, 0);
		values.put(MainProvider.TASKS_PARENT, id);
		values.put(MainProvider.TASKS_PRIOR, 0);
		cr.insert(MainProvider.CONTENT_URI_TASKS, values);	
		
		restartItemsLoader();
	}
	
	// put new name to db
	public void renameItem(String name, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_NAME, name);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		Uri rowURI = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		
		if(updatedRowCount > -1){
			restartItemsLoader();
		}
	}
	//change item's checked state
	public void updateItemDone(boolean done, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_DONE, done);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		Uri rowURI = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		
		if(updatedRowCount > -1){
			restartItemsLoader();
		}
	}
	//change item's priority
	public void updateItemPrior(int prior, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_PRIOR, prior);
		
		ContentResolver cr = getActivity().getContentResolver();
		Uri rowURI = 
				ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		int updatedRowCount = cr.update(rowURI, values, null, null);
		
		if(updatedRowCount > -1){
			restartItemsLoader();
		}
	}
	// delete item from db
	public void deleteItemFromList(int id){
		String where = null;
		String[] whereArgs = null;
		ContentResolver cr_d = getActivity().getContentResolver();
		Uri rowURI = ContentUris.withAppendedId(
				MainProvider.CONTENT_URI_TASKS,
				id);
		int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
		
		if(deletedRowCount > -1){
			restartItemsLoader();
		}
	}
/*******************************************************************************************/
	
	/** ArrayAdapters for ListView, sort Spinner and Priority Spinner*/
/*******************************************************************************************/
	
	//Adapter for ListView of itmes
	private class ListsAdapter extends ArrayAdapter<MTask>{
		private int resource;
			
		public ListsAdapter(Context context,
				int resource,
				List<MTask> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			//fetch MTask & some it's properties to use them in listeners
			MTask item = getItem(position);
			final int i_id = item.get_id();
			final String name = item.getName();
			final int prior = item.getPrior();
			final int done = item.getDone();
			final int i_parent = item.getParent();
			
			//get root View
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
			
			//fetch needed views
			TextView nameView = (TextView)rootView.findViewById(R.id.tv_project_list_name);
			CheckBox chb_done = (CheckBox)rootView.findViewById(R.id.chb_project_list);
			final Spinner s_prior = (Spinner)rootView.findViewById(R.id.s_list_prior);
			ImageView img_menu = (ImageView)rootView.findViewById(R.id.img_project_list_menu);
			
			//set name
			nameView.setText(name);
			nameView.setTypeface(tf);
			
			// checkbox - done action
			if(done == 1 || ((ActivityProject)getActivity()).getSelectedListDone())
				chb_done.setChecked(true);
			else 
				chb_done.setChecked(false);
			
			chb_done.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateItemDone((done == 0), i_id);
				}
			});
			
			//setup priority spinner
			ArrayList<Integer> arrayDrawable = new ArrayList<Integer>();
			arrayDrawable.add(R.drawable.prior_green);
			arrayDrawable.add(R.drawable.prior_orange);
			arrayDrawable.add(R.drawable.prior_red);
			SpinnerPriorAdapter adapter = new SpinnerPriorAdapter(
									getActivity(), 
									arrayDrawable);
			s_prior.setAdapter(adapter);
			s_prior.setSelection(prior);
			s_prior.getAdapter();
			// doing something to prevent circle of self selcting on creating
			s_prior.setOnItemSelectedListener(new OnItemSelectedListener(){
				protected Adapter initializedAdapter = null;

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					if(initializedAdapter == parent.getAdapter())
						updateItemPrior(position, i_id);
					else
		                initializedAdapter = parent.getAdapter();
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {}
			});
			
			// popup menu creation & attaching it to menu img
			if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
				//PopupMenu
				final PopupMenu popupMenu = new PopupMenu(getActivity(), img_menu);
				Menu menu = popupMenu.getMenu();
				menu.add(0,0,0,getResources().getString(R.string.m_delete));
				menu.add(0,1,1,getResources().getString(R.string.m_rename));
				menu.add(0,2,2,getResources().getString(R.string.m_move_to_list));
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch(item.getItemId()){
						//delete list
						case 0:
							menuDeleteItem(i_id);
							return true;
						//rename item
						case 1:
							menuRenameItem(i_id, name);			
							return true;
						case 2:
							menuMoveItemToList(i_id, i_parent, name);
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
	/** Methods for menu ***********************************************************************/
	public void menuDeleteItem(int id){
		deleteItemFromList(id);
	}
	public void menuRenameItem(int id, String name){
		DialogInput dialog = DialogInput.newInstance(
				getResources().getString(R.string.rename_item)+ name, 
				name, 
				id, 
				3);
		dialog.show(getFragmentManager(), "dialog rename item");
	}
	public void menuMoveItemToList(int id, int parent, String name){
		DialogPickList dialog_pick = DialogPickList.newInstance(
				getResources().getString(R.string.pick_new_list)+ name, 
				id, 
				parent);
		dialog_pick.show(getFragmentManager(), "dialog rename item");
	}
	/*******************************************************************************************/
	
	
	//Adapter for sort spinner - just set font type & size
	private class SpinnerAdapter extends ArrayAdapter<String>{
		
		public SpinnerAdapter(Context context,
				int resource,
				List<String> items){
			super(context, resource, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			TextView tv; 
			String item = getItem(position);
			
			if (convertView == null){
				tv = new TextView(getContext());
			}else{
				tv = (TextView) convertView;
			}
			
			tv.setText(item);
			tv.setTextSize(20);
			tv.setTypeface(tf);
					
			return tv;
		}
	}
	
	//Adapter for priority Spinner
	private class SpinnerPriorAdapter extends BaseAdapter 
							implements android.widget.SpinnerAdapter {

		private ArrayList<Integer> list;

		public SpinnerPriorAdapter(Context context,
				ArrayList<Integer> list) {
			this.list = list;
		}
		
		public int getCount() {
			return list.size();
		}
		
		public Object getItem(int position) {
			return list.get(position);
		}
		
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				// This a new view we inflate the new layout
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_spinner_prior,
																	parent, false);
			}	
			//set image to item
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.iv_spinner_prior);
			iv.setImageResource(list.get(position));

			return convertView;
		}
		
	}
/*******************************************************************************************/
}
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;


public class FragmentProjectTodolists extends Fragment 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int currentVersion;
	
	private Typeface tf;
	
	private ArrayList<MTask> listsList;
	private ListsAdapter listsAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_todolists, container, false);
        
        currentVersion = android.os.Build.VERSION.SDK_INT;
        
        listsList = new ArrayList<MTask>();

        // set fontFamily to title
        tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
        
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("by name");
        arrayList.add("by priority");
        arrayList.add("by done");
        
        Spinner s_sort = (Spinner)rootView.findViewById(R.id.s_lists_sort);
        s_sort.setAdapter(new SpinnerAdapter(getActivity(), R.layout.item_spinner, arrayList));
        
        listsAdapter = new ListsAdapter(getActivity(),R.layout.item_lv_lists, listsList);
        
        ListView lv = (ListView)rootView.findViewById(R.id.lv_todolists);
        lv.setAdapter(listsAdapter);
        
        TextView tv_sort = (TextView)rootView.findViewById(R.id.tv_f_todos_sort);
        tv_sort.setTypeface(tf);
        TextView tv_add_todo = (TextView)rootView.findViewById(R.id.tv_add_todolist);
        tv_add_todo.setTypeface(tf);
        
        LinearLayout ll_add = (LinearLayout)rootView.findViewById(R.id.ll_add_todolist);
        ll_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogInput dialog = DialogInput.newInstance(
						//getResources().getString(R.string.enter_new_name_item),
						"Enter name of item: ",
						"", 
						((ActivityProject)getActivity()).getSelectedListId(), 
						1);
				dialog.show(getFragmentManager(), "dialog rename item");				
			}
		});
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		getActivity().getSupportLoaderManager().initLoader(1, null, this);
	}
	
	public void updateItemsLoader(){
		getActivity().getSupportLoaderManager().restartLoader(1, null, this);
	}
	
	//Initialize Loader for Projects
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		CursorLoader loader = new CursorLoader(getActivity(),
				MainProvider.CONTENT_URI_TASKS,
				null,
				MainProvider.TASKS_PARENT+"=?",
				new String[] {Integer.toString(((ActivityProject)getActivity()).getSelectedListId())},
				MainProvider.TASKS_PRIOR + " DESC");
		
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
		int IdIndex = cursor.getColumnIndexOrThrow(MainProvider.KEY_ID);
		int NameIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_NAME);
		int DoneIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_DONE);
		int ParentIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_PARENT);
		int PriorIndex = cursor.getColumnIndexOrThrow(MainProvider.TASKS_PRIOR);
		
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
		listsAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {	
	}
	
	public void deleteItemFromList(int id){
		String where = null;
		String[] whereArgs = null;
		
		ContentResolver cr_d = getActivity().getContentResolver();
		Uri rowURI = ContentUris.withAppendedId(
				MainProvider.CONTENT_URI_TASKS,
				id);
		int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
		if(deletedRowCount > -1){
			updateItemsLoader();
		}
	}
	
	//Adapter for ListView of Plan
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
			
			RelativeLayout rootView;
			
			MTask item = getItem(position);
			final int i_id = item.get_id();
			final String name = item.getName();
			final int prior = item.getPrior();
			final int done = item.getDone();
			final int i_parent = item.getParent();
			
			if (convertView == null){
				rootView = new RelativeLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater)getContext().getSystemService(inflater);
				li.inflate(resource, rootView,true);
			}else{
				rootView = (RelativeLayout) convertView;
			}
			
			//set name
			TextView nameView = (TextView)rootView.findViewById(R.id.tv_project_list_name);
			nameView.setText(name);
			nameView.setTypeface(tf);
			
			final Spinner s_prior = (Spinner)rootView.findViewById(R.id.s_list_prior);
			ArrayList<Integer> arrayDrawable = new ArrayList<Integer>();
			arrayDrawable.add(R.drawable.status_green);
			arrayDrawable.add(R.drawable.status_orange);
			arrayDrawable.add(R.drawable.status_red);
			SpinnerPriorAdapter adapter = new SpinnerPriorAdapter(
									getActivity(), 
									arrayDrawable);
			s_prior.setAdapter(adapter);
			s_prior.setSelection(prior);
			s_prior.getAdapter();
			s_prior.setOnItemSelectedListener(new OnItemSelectedListener(){
				protected Adapter initializedAdapter = null;

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					
					if(initializedAdapter == parent.getAdapter())
						updateItemPrior(position, i_id);
					
					else{
		                initializedAdapter = parent.getAdapter();
		            }
					Toast.makeText(getContext(), "qwer", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
				
			});
			
			ImageView img_menu = (ImageView)rootView.findViewById(R.id.img_project_list_menu);
			
			if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
				//PopupMenu
				final PopupMenu popupMenu = new PopupMenu(getActivity(), img_menu);
				Menu menu = popupMenu.getMenu();
				menu.add(0,0,0,"Delete");//getResources().getString(R.string.delete));
				menu.add(0,1,1,"Rename");//getResources().getString(R.string.rename));
				menu.add(0,2,2,"Move to List..");
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch(item.getItemId()){
						//delete list
						case 0:
							deleteItemFromList(i_id);
							return true;
						//rename list
						case 1:
							DialogInput dialog = DialogInput.newInstance(
									"Rename item "+ name,//getResources().getString(R.string.enter_new_name_item), 
									name, 
									i_id, 
									3);
							dialog.show(getFragmentManager(), "dialog rename item");			
							return true;
						case 2:
							DialogPickList dialog_pick = DialogPickList.newInstance(
									"Pick new List: "+ name,//getResources().getString(R.string.enter_new_name_item), 
									i_id, 
									i_parent,
									0);
							dialog_pick.show(getFragmentManager(), "dialog rename item");	
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
			
			/* checkbox - done action */
			CheckBox chb_done = (CheckBox)rootView.findViewById(R.id.chb_project_list);
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
			
					
			return rootView;
		}
	}
	
	public void updateItemDone(boolean done, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_DONE, done);
		Uri rowURI = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		
		ContentResolver cr_u = getActivity().getContentResolver();
		
		int updatedRowCount = cr_u.update(rowURI, values, null, null);
		if(updatedRowCount > -1){
			updateItemsLoader();
		}
	}
	
	public void updateItemPrior(int prior, int id){		
		ContentValues values = new ContentValues();
		values.put(MainProvider.TASKS_PRIOR, prior);
		Uri rowURI = 
			ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, id);
		
		ContentResolver cr = getActivity().getContentResolver();
		
		int updatedRowCount = cr.update(rowURI, values, null, null);
		if(updatedRowCount > -1){
			updateItemsLoader();
		}
	}
	
	//Adapter for ListView of Plan
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
	
	//Adapter for ListView of Plan
	private class SpinnerPriorAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

		private ArrayList<Integer> lenguages;

		public SpinnerPriorAdapter(Context context,
				ArrayList<Integer> lenguages) {
			this.lenguages = lenguages;
		}

		public int getCount() {
			return lenguages.size();
		}

		public Object getItem(int position) {
			return lenguages.get(position);
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

			ImageView iv = (ImageView) convertView
					.findViewById(R.id.iv_spinner_prior);
			iv.setImageResource(lenguages.get(position));

			return convertView;
		}
		
	}
}
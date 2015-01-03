package com.mh.mprojects;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DialogPickList extends DialogFragment{
	
	private Typeface tf;
	private int i_id; //id of list
	private int old_parent; // old parent's id of the list
	private ActivityProject activity;

	//method for create new dialog
	static DialogPickList newInstance(String title, int _id, int old_parent){
		
		DialogPickList dialog = new DialogPickList();
		Bundle args = new Bundle();
		args.putString("title", title);	//title in dialog
		args.putInt("id", _id);	//id of editing element
		args.putInt("old_parent", old_parent);	//id of editing element
		dialog.setArguments(args);
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		//initialize Views
		String title = getArguments().getString("title");
		i_id = getArguments().getInt("id");
		old_parent = getArguments().getInt("old_parent");
		
		tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
		activity = (ActivityProject)getActivity();
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_list, null);
		final ListView lv = (ListView)v.findViewById(R.id.lv_pick_list);
		lv.setAdapter(new PickListAdapter(getActivity(), 
									R.layout.item_lv_dialog, 
									activity.fragmentPlan.getPlanList()));
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int selected_item_id = activity.fragmentPlan.getPlanList()
												.get(position).get_id();
				if(old_parent != selected_item_id){
					ContentValues values = new ContentValues();
					values.put(MainProvider.TASKS_PARENT, selected_item_id);
					ContentResolver cr_u = getActivity().getContentResolver();
					
					Uri rowURI = 
						ContentUris.withAppendedId(MainProvider.CONTENT_URI_TASKS, i_id);
					int updatedRowCount = cr_u.update(rowURI, values, null, null);
					if(updatedRowCount > -1){
						((ActivityProject)getActivity()).fragmentLists.restartItemsLoader();
					}
					dismiss();
				}
			}
		});
		
		AlertDialog dialog =  new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setView(v)
		.setNegativeButton(getResources().getString(android.R.string.cancel), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//just close dialog
					}
				})
		.create();
		
		return dialog;
	}
/******************************************************************************************/
	
	/**ArrayAdapter for lists in Dialog*/
/******************************************************************************************/
	//Adapter for ListView of Plan
	private class PickListAdapter extends ArrayAdapter<MList>{	
		
		public PickListAdapter(Context context,
				int resource,
				List<MList> items){
			super(context, resource, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			TextView tv; 
			MList item = getItem(position);
			
			if (convertView == null){
				tv = new TextView(getContext());
			}else{
				tv = (TextView) convertView;
			}
			tv.setPadding(15, 15, 15, 15);
			tv.setText(item.toString());
			if(old_parent == item.get_id())
				tv.setEnabled(false);
			tv.setTextSize(20);
			tv.setTypeface(tf);
					
			return tv;
		}
	}
/*******************************************************************************************/
}
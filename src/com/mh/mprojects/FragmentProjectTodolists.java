package com.mh.mprojects;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentProjectTodolists extends Fragment{
	
	private ArrayList<MList> listsList;
	private ListsAdapter listsAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_todolists, container, false);
        
        listsList = new ArrayList<MList>();
        listsList.add(new MList(1, "List_1", false, 0, 0, 0));
        listsList.add(new MList(2, "List_2", false, 0, 0, 0));
        listsList.add(new MList(3, "List_3", false, 0, 0, 0));
        listsList.add(new MList(4, "List_4", false, 0, 0, 0));
        listsList.add(new MList(5, "List_5", false, 0, 0, 0));
        listsList.add(new MList(6, "List_6", false, 0, 0, 0));
        listsList.add(new MList(7, "List_7", false, 0, 0, 0));
        listsList.add(new MList(8, "List_8", false, 0, 0, 0));
        listsList.add(new MList(9, "List_9", false, 0, 0, 0));
        
        listsAdapter = new ListsAdapter(getActivity(),R.layout.item_lv_lists, listsList);
        
        ListView lv = (ListView)rootView.findViewById(R.id.lv_todolists);
        lv.setAdapter(listsAdapter);
        
        return rootView;
    }
	
	//Adapter for ListView of Plan
	private class ListsAdapter extends ArrayAdapter<MList>{
		private int resource;
			
		public ListsAdapter(Context context,
				int resource,
				List<MList> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			RelativeLayout rootView;
			
			MList item = getItem(position);
			final String name = item.getName();
			
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
			
			
			return rootView;
		}
	}

}

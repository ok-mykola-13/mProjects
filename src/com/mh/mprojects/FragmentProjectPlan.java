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

public class FragmentProjectPlan extends Fragment{
	
	private ArrayList<MTask> planList;
	private TasksAdapter tasksAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_project_plan, container, false);
        
        planList = new ArrayList<MTask>();
        planList.add(new MTask(1, "Task_1", 0, 0, 1));
        planList.add(new MTask(2, "Task_2", 0, 0, 2));
        planList.add(new MTask(3, "Task_3", 0, 0, 3));
        planList.add(new MTask(4, "Task_4", 0, 0, 4));
        planList.add(new MTask(5, "Task_5", 0, 0, 5));
        planList.add(new MTask(6, "Task_6", 0, 0, 6));
        
        tasksAdapter = new TasksAdapter(getActivity(), R.layout.item_lv_plan, planList);
        
        ListView lv = (ListView)rootView.findViewById(R.id.lv_plan);
        lv.setAdapter(tasksAdapter);
        
        return rootView;
    }
	
	//Adapter for ListView of Plan
	private class TasksAdapter extends ArrayAdapter<MTask>{
		private int resource;
			
		public TasksAdapter(Context context,
				int resource,
				List<MTask> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			RelativeLayout rootView;
			
			MTask item = getItem(position);
			final String name = item.getName();
			final int pos = item.getOrder_num();
			
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
			TextView numView = (TextView)rootView.findViewById(R.id.tv_project_plan_task_num);
			numView.setText(Integer.toString(pos) + ".");
			
			//set name
			TextView nameView = (TextView)rootView.findViewById(R.id.tv_project_plan_task_name);
			nameView.setText(name);
			
			return rootView;
		}
	}

}

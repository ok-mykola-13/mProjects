package com.mh.mprojects;

import com.example.mprojects.R;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.annotation.SuppressLint;
import android.os.Bundle;

public class ActivityMain extends ActionBarActivity {
	
	int currentVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		currentVersion = android.os.Build.VERSION.SDK_INT;
		
		
		// Projects button - follow to Projects activity
		// set onClickListener
		ImageButton btn_projects = (ImageButton)findViewById(R.id.btn_projects);
		btn_projects.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		MenuItem about = menu.add(0, 0, 0, getResources().getString(R.string.about));
		about.setIcon(R.drawable.icon_a);
		
		MenuItem help = menu.add(0, 1, 1, getResources().getString(R.string.need_help));
		help.setIcon(R.drawable.icon_help);
		
		if (currentVersion > android.os.Build.VERSION_CODES.HONEYCOMB){
			about.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			help.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
		return true;
	}
}

package com.mh.mprojects;

import java.io.File;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

public class ActivityProjects extends FragmentActivity {
	
	private int currentVersion;
	
	public static String MAIN_FOLDER = "mProjects"; 
	
	private FragmentProjects f_projects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projects);
		
		currentVersion = android.os.Build.VERSION.SDK_INT;
		
		//Create the Main Folder
		File mainDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
																		MAIN_FOLDER);
		if (!mainDirectory.exists())
			mainDirectory.mkdirs();
			
		if(currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			ActionBar actionBar = getActionBar();
			actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar));
		}
		
		f_projects = (FragmentProjects)getSupportFragmentManager().findFragmentById(R.id.f_projects);
		
		// in and out animation for activity
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		f_projects.restartProjectsLoader();
	}
}
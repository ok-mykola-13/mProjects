package com.mh.mprojects;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class ActivityProject extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		int p_id = getIntent().getIntExtra("p_id", -1);
		
	}

}

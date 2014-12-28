package com.mh.mprojects;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.os.Bundle;
import android.os.CountDownTimer;

public class FragmentMain extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_main, container, false);
		
		final CountDownTimer timer = new CountDownTimer(1500, 1500) {

		     public void onTick(long millisUntilFinished) {}

		     public void onFinish() {
		    	 ((ActivityProjects)getActivity()).hideSplash();
		     }
		 }; 
		 
		// Projects button - follow to Projects activity
		// set onClickListener
		ImageButton btn_projects = (ImageButton)rootView.findViewById(R.id.btn_projects);
		btn_projects.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Go to the ActivityProjects
				timer.cancel();
				((ActivityProjects)getActivity()).hideSplash();
			}
		});
		
		timer.start();
        
        return rootView;
    }
}

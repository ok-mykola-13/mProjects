package com.mh.mprojects;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;   
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityProjects extends ActionBarActivity {
	
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;
    
    private ArrayList<MProject> projectsList;
    private ArrayList<MProject> projectsList_1;
    private ArrayList<MProject> projectsList_2;
    private ArrayList<MProject> projectsList_3;
    private ProjectsAdapter projectsAdapter_1;
    private ProjectsAdapter projectsAdapter_2;
    private ProjectsAdapter projectsAdapter_3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projects);
		
		projectsList = new ArrayList<MProject>();
		projectsList.add(new MProject(1,"Project_1",1));
		projectsList.add(new MProject(2,"Project_2",1));
		projectsList.add(new MProject(3,"Project_3",1));
		projectsList.add(new MProject(4,"Project_4",2));
		projectsList.add(new MProject(5,"Project_5",2));
		projectsList.add(new MProject(6,"Project_6",3));
		projectsList.add(new MProject(7,"Project_7",3));
		
		projectsList_1 = new ArrayList<MProject>();
		projectsList_2 = new ArrayList<MProject>();
		projectsList_3 = new ArrayList<MProject>();
				
		splitProjectsOnGroups(projectsList);
		
		final ActionBar actionBar = getSupportActionBar();
		
		mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        //getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });



	    // Specify that tabs should be displayed in the action bar.
	    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // show the given tab
	        	mViewPager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    // Add 3 tabs, specifying the tab's text and TabListener
	    for (int i = 0; i < 3; i++) {
	        actionBar.addTab(
	                actionBar.newTab()
	                        .setText("Tab " + (i + 1))
	                        .setTabListener(tabListener));
	    }

	}
	
	private void splitProjectsOnGroups(ArrayList<MProject> list){
		
		for (MProject p : list) {
			switch(p.getGroup()){
			case 1:
				projectsList_1.add(p);
				break;
			case 2:
				projectsList_2.add(p);
				break;
			case 3:
				projectsList_3.add(p);
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 0) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
	    public DemoCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	        DemoObjectFragment fragment = new DemoObjectFragment();
	        
	        if(i == 0){
		        projectsAdapter_1 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_1);
		        fragment.adapter = projectsAdapter_1;
	        }else if(i == 1){
	        	projectsAdapter_2 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_2);
	        	fragment.adapter = projectsAdapter_2;
	        }else if(i == 2){
	        	projectsAdapter_3 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_3);
	        	fragment.adapter = projectsAdapter_3;
	        }

	        
	        Bundle args = new Bundle();
	        args.putInt(DemoObjectFragment.ARG_OBJECT, i+1);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return 3;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return "OBJECT " + (position + 1);
	    }
	}

	// Instances of this class are fragments representing a single
	// object in our collection.
	public class DemoObjectFragment extends Fragment {
	    public static final String ARG_OBJECT = "object";
	    
	    ProjectsAdapter adapter;

	    @Override
	    public View onCreateView(LayoutInflater inflater,
	            ViewGroup container, Bundle savedInstanceState) {
	        // The last two arguments ensure LayoutParams are inflated
	        // properly.
	        View rootView = inflater.inflate(
	                R.layout.fragment_projects_page, container, false);
	        
	        GridView gridView = (GridView)rootView.findViewById(R.id.gv_projects);
	        gridView.setAdapter(adapter);
	        return rootView;
	    }
	}
	
	
	//Adapter for GridView of Projects
	private class ProjectsAdapter extends ArrayAdapter<MProject>{

		private int resource;
			
		public ProjectsAdapter(Context context,
				int resource,
				List<MProject> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			RelativeLayout rootView;
			
			MProject item = getItem(position);

			final String name = item.getName();
			final int p_id = item.get_id();
			
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
			TextView nameView = (TextView)rootView.findViewById(R.id.txt_project_name);
			nameView.setText(name);
			/*
			//set project's icon
			File projectIcon = new File(Environment.getExternalStorageDirectory() + 
					"/" + ActivityMain.MAIN_FOLDER +
					"/projects" +
					"/" + name +
					"/projectIcon");
			
			ImageView logoView = (ImageView)rootView.findViewById(R.id.img_project_logo);
			if(projectIcon.exists()){
				Bitmap icon = BitmapFactory.decodeFile(projectIcon.getAbsolutePath());
				logoView.setImageBitmap(icon);				
			}else{
				logoView.setImageDrawable(getResources().getDrawable(R.drawable.p));
			}
			*/
			
			/* Behavior: click -> to project; long click -> edit */
			final Button btn_ch = (Button)rootView.findViewById(R.id.btn_change_project);
			final Button btn_d = (Button)rootView.findViewById(R.id.btn_delete_project);
			final ImageView img_logo = (ImageView)rootView.findViewById(R.id.img_project_logo);
			img_logo.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					
					// animations for buttons
					TranslateAnimation ta1 = new TranslateAnimation(0, 100, 0, 0);
					ta1.setDuration(200);
					ta1.setFillAfter(true);
					final TranslateAnimation ta2 = new TranslateAnimation(100, 0, 0, 0);
					ta2.setDuration(200);
					ta2.setFillAfter(true);
					
					// animation for project's logo
					TranslateAnimation ta3 = new TranslateAnimation(0, -100, 0, 0);
					ta3.setDuration(200);
					ta3.setFillAfter(true);
					final TranslateAnimation ta4 = new TranslateAnimation(-100, 0, 0, 0);
					ta4.setDuration(200);
					ta4.setFillAfter(true);
					
					if(btn_ch.getVisibility() != View.VISIBLE){
						ta1.setAnimationListener(new AnimationListener(){
							@Override
							public void onAnimationStart(Animation animation) {}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								btn_ch.bringToFront();
								btn_d.bringToFront();
								btn_ch.startAnimation(ta2);
								btn_d.startAnimation(ta2);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {}
							
						});
						ta3.setAnimationListener(new AnimationListener(){
							@Override
							public void onAnimationStart(Animation animation) {}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								img_logo.startAnimation(ta4);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {}
							
						});
						btn_ch.setVisibility(View.VISIBLE);
						btn_ch.startAnimation(ta1);
						btn_d.setVisibility(View.VISIBLE);
						btn_d.startAnimation(ta1);
						img_logo.startAnimation(ta3);
						
					}else{
						ta1.setAnimationListener(new AnimationListener(){
							@Override
							public void onAnimationStart(Animation animation) {}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								btn_ch.startAnimation(ta2);
								btn_d.startAnimation(ta2);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {}
							
						});
						ta3.setAnimationListener(new AnimationListener(){
							@Override
							public void onAnimationStart(Animation animation) {}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								img_logo.bringToFront();
								img_logo.startAnimation(ta4);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {}
							
						});
						btn_ch.startAnimation(ta1);
						btn_d.startAnimation(ta1);
						img_logo.startAnimation(ta3);
						btn_ch.setVisibility(View.INVISIBLE);
						btn_d.setVisibility(View.INVISIBLE);
					}
					
					return true;
				}
				
			});
			
			img_logo.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {					
					Intent i = new Intent(getBaseContext(), ActivityProject.class);
					i.putExtra("p_id", p_id);
					startActivity(i);
				}
				
			});
			
			return rootView;
		}
	}
}
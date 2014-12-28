package com.mh.mprojects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;   
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProjects extends FragmentActivity 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public String MAIN_FOLDER = "mProjects"; 
	
	private FragmentMain f_main;
	
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;
    
    private ArrayList<MProject> projectsList;
    private ArrayList<MProject> projectsList_0;
    private ArrayList<MProject> projectsList_1;
    private ArrayList<MProject> projectsList_2;
    private ProjectsAdapter projectsAdapter_0;
    private ProjectsAdapter projectsAdapter_1;
    private ProjectsAdapter projectsAdapter_2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projects);
		
		//Create the Main Folder
		File mainDirectory = new File(Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER);
		if (!mainDirectory.exists())
			mainDirectory.mkdirs();
		
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		f_main = (FragmentMain)getSupportFragmentManager()
							.findFragmentById(R.id.f_main_splash);
		
		projectsList = new ArrayList<MProject>();
		projectsList_0 = new ArrayList<MProject>();
		projectsList_1 = new ArrayList<MProject>();
		projectsList_2 = new ArrayList<MProject>();
				
		splitProjectsOnGroups(projectsList);
		
		mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        
        getSupportLoaderManager().initLoader(0, null, this);
	}
	
	public void restartLoader(){
		getSupportLoaderManager().restartLoader(0, null, this);
	}
	
	private void updateProjectAdapters(){
		
		splitProjectsOnGroups(projectsList);
		if(projectsAdapter_0 != null){
			projectsAdapter_0.notifyDataSetChanged();
		}
		if(projectsAdapter_1 != null){
			projectsAdapter_1.notifyDataSetChanged();
		}
		if(projectsAdapter_2 != null){
			projectsAdapter_2.notifyDataSetChanged();			
		}
		
	}
	
	//separate projects on groups
	private void splitProjectsOnGroups(ArrayList<MProject> list){
		
		projectsList_0.clear();
		projectsList_1.clear();
		projectsList_2.clear();
		
		for (MProject p : list) {
			switch(p.getGroup()){
			case 0:
				projectsList_0.add(p);
				break;
			case 1:
				projectsList_1.add(p);
				break;
			case 2:
				projectsList_2.add(p);
				break;
			}
		}
		
		projectsList_0.add(new MProject(0));
		projectsList_1.add(new MProject(1));
		projectsList_2.add(new MProject(2));
	}
	
	//hide splash fragment
	public void hideSplash(){
		if(f_main != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			//ft.setCustomAnimations(R.animator.slide_left_in, R.animator.slide_left_out);
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			ft.hide(f_main);
			ft.remove(f_main);
			ft.commit();
			f_main = null;
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
	
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
		
	    public DemoCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	        DemoObjectFragment fragment = new DemoObjectFragment();
	        
	        if(i == 0){
		        projectsAdapter_0 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_0);
		        fragment.adapter = projectsAdapter_0;
	        }else if(i == 1){
	        	projectsAdapter_1 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_1);
	        	fragment.adapter = projectsAdapter_1;
	        }else if(i == 2){
	        	projectsAdapter_2 = new ProjectsAdapter(getBaseContext(), 
						R.layout.item_gv_projects, 
						projectsList_2);
	        	fragment.adapter = projectsAdapter_2;
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
	
	//Initialize Loader for Projects
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		CursorLoader loader = new CursorLoader(this,
				MainProvider.CONTENT_URI_PROJECTS,
				null,
				null,
				null,
				MainProvider.PROJECTS_NAME);
		
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
		int IdIndex = cursor.getColumnIndexOrThrow(MainProvider.KEY_ID);
		int NameIndex = cursor.getColumnIndexOrThrow(MainProvider.PROJECTS_NAME);
		int GroupIndex = cursor.getColumnIndexOrThrow(MainProvider.PROJECTS_GROUP);
		int CreatedIndex = cursor.getColumnIndexOrThrow(MainProvider.PROJECTS_CREATE_DATE);
		
		projectsList.clear();
		
		while(cursor.moveToNext()){
			int id = cursor.getInt(IdIndex);
			MProject p = new MProject(id,
					cursor.getString(NameIndex),
					cursor.getInt(GroupIndex),
					cursor.getLong(CreatedIndex));
				
			projectsList.add(p);
		}		
		updateProjectAdapters();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {	
	}
	
	
	public class ProjectsAdapter extends ArrayAdapter<MProject>{

		private Typeface tf;
		
		private int resource;
			
		public ProjectsAdapter(Context context,
				int resource,
				List<MProject> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			tf = Typeface.createFromAsset(getContext().getAssets(),
	                "fonts/comic.ttf");
			
			final RelativeLayout rootView;
			
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
			final TextView nameView = (TextView)rootView.findViewById(R.id.txt_project_name);
			nameView.setText(name);
			nameView.setTypeface(tf);
			nameView.setVisibility(View.VISIBLE);
			final TextView dateView = (TextView)rootView.findViewById(R.id.txt_project_date);
			dateView.setTypeface(tf);
			
			
			final EditText newName = (EditText)rootView.findViewById(R.id.et_project_name);
			newName.setTypeface(tf);
			newName.setVisibility(View.INVISIBLE);
			
			/* Behavior: click -> to project; long click -> edit */
			final Button btn_ch = (Button)rootView.findViewById(R.id.btn_change_project);
			final Button btn_can = (Button)rootView.findViewById(R.id.btn_cancel_project);
			final Button btn_d = (Button)rootView.findViewById(R.id.btn_delete_project);
			final Button btn_add = (Button)rootView.findViewById(R.id.btn_add_project);
			btn_add.setVisibility(View.INVISIBLE);
			
			btn_ch.setTypeface(tf);
			btn_can.setTypeface(tf);
			btn_d.setTypeface(tf);
			
			final RelativeLayout rl_project = (RelativeLayout)rootView.findViewById(R.id.rl_project);
			final RelativeLayout rl_project_edit = (RelativeLayout)rootView.findViewById(R.id.rl_project_edit);
			rl_project_edit.setVisibility(View.INVISIBLE);
			
			final ImageView img_logo = (ImageView)rootView.findViewById(R.id.img_project_logo);
			ImageView img_logo_set = (ImageView)rootView.findViewById(R.id.img_project_logo_change);
			img_logo_set.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Toast.makeText(getContext(), "Select new logo", Toast.LENGTH_SHORT).show();
					DialogPickFile dialog = DialogPickFile.newInstance(
							"Change image: ", 
							0, 
							img_logo);
					dialog.show(getSupportFragmentManager(), "change image");
				}
				
			});
			img_logo.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					if(btn_add.getVisibility() == View.VISIBLE){
						Toast.makeText(getContext(), "change image", Toast.LENGTH_SHORT).show();
						DialogPickFile dialog = DialogPickFile.newInstance(
													"Change image: ", 
													0, 
													(ImageView) v);
						dialog.show(getSupportFragmentManager(), "change image");
					}
				}
				
			});
			rootView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					
					// this is add icon project
					if(p_id == 0) return true;	
					
					// animations for buttons
					Animation ta2 = AnimationUtils.loadAnimation(getContext(), R.animator.slide_top_in);
					Animation ta3 = AnimationUtils.loadAnimation(getContext(), R.animator.slide_top_out);
					
					if(rl_project_edit.getVisibility() != View.VISIBLE){
						rl_project_edit.startAnimation(ta2);
						rl_project_edit.setVisibility(View.VISIBLE);
						newName.setText(nameView.getText());
						nameView.setVisibility(View.GONE);
						newName.setVisibility(View.VISIBLE);					
					}else{
						rl_project_edit.startAnimation(ta3);
						rl_project_edit.setVisibility(View.INVISIBLE);
						
						nameView.setVisibility(View.VISIBLE);
						newName.setVisibility(View.GONE);
					}
					
					return true;
				}
				
			});
			
			btn_add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getContext(), "add project", Toast.LENGTH_SHORT).show();
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(MainProvider.PROJECTS_NAME, newName.getText().toString());
					values.put(MainProvider.PROJECTS_GROUP, 0);
					values.put(MainProvider.PROJECTS_CREATE_DATE, java.lang.System.currentTimeMillis());
					cr.insert(MainProvider.CONTENT_URI_PROJECTS, values);	
					
					restartLoader();
				}
			});
			
			btn_d.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String where = null;
					String[] whereArgs = null;
					
					ContentResolver cr_d = getContentResolver();
					Uri rowURI = ContentUris.withAppendedId(
							MainProvider.CONTENT_URI_PROJECTS,
							p_id);
					int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
					if(deletedRowCount > -1){
						restartLoader();
						Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
					}			
				}
			});
			
			rootView.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					if(p_id != 0){
						Intent i = new Intent(getContext(), ActivityProject.class);
						i.putExtra("p_id", p_id);
						startActivity(i);
					}else{
						if(btn_add.getVisibility() == View.INVISIBLE){
							nameView.setVisibility(View.GONE);
							newName.setVisibility(View.VISIBLE);
							btn_add.setVisibility(View.VISIBLE);
						}else{
							nameView.setVisibility(View.VISIBLE);
							newName.setVisibility(View.INVISIBLE);
							btn_add.setVisibility(View.INVISIBLE);
						}
					}
				}
				
			});
			
			return rootView;
		}
	}
}
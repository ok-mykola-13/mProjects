package com.mh.mprojects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

public class FragmentProjects extends Fragment 
							implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int screenSize;
	
	private Typeface tf;
	
	private ArrayList<MProject> projectsList;
    private ProjectsAdapter projectsAdapter;
    
    /**initialize main views and variables */
/*************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
       
        View rootView = inflater.inflate(
                R.layout.fragment_projects, container, false);
        
        screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;
        
        tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
        
        projectsList = new ArrayList<MProject>();
        projectsAdapter = new ProjectsAdapter(getActivity(),
        									R.layout.item_gv_projects,
        									projectsList);
        GridView gridView = (GridView)rootView.findViewById(R.id.gv_projects);
        gridView.setAdapter(projectsAdapter);
        if(screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL){
			gridView.setNumColumns(1);
		}
        
        return rootView;
    }
/*************************************************************************************/
    
    /**Projects Loader */
/*************************************************************************************/
    // init loader when activity has created
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }
    //just cover to be able restart loader from listeners
    public void restartProjectsLoader(){
    	getActivity().getSupportLoaderManager().restartLoader(0, null, this);
    }
    
    //Initialize Loader for Projects
  	@Override
  	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
  		
  		CursorLoader loader = new CursorLoader(
  				getActivity(),
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
  		
  		//refill list of projects
  		projectsList.clear();
  		
  		while(cursor.moveToNext()){
  			int id = cursor.getInt(IdIndex);
  			MProject p = new MProject(id,
  					cursor.getString(NameIndex),
  					cursor.getInt(GroupIndex),
  					cursor.getLong(CreatedIndex));
  				
  			projectsList.add(p);
  		}		
  		// add an empty projects, that will Add new projects
  		projectsList.add(new MProject(0));
  		
  		projectsAdapter.notifyDataSetChanged();
  	}
  	@Override
  	public void onLoaderReset(Loader<Cursor> arg0) {}
/*******************************************************************************************/
  	
  	/** Additional methods */
/*******************************************************************************************/
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
/*******************************************************************************************/
  	
  	/** ArrayAdapter for Project in GridView */
/*******************************************************************************************/
	public class ProjectsAdapter extends ArrayAdapter<MProject>{		
		private int resource;
			
		public ProjectsAdapter(Context context,
				int resource,
				List<MProject> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			// some data from item. need to be final for ActionListeners
			MProject item = getItem(position);
			final String name = item.getName();
			final int p_id = item.get_id();
			//project's folder
			final File dir = new File(Environment.getExternalStorageDirectory() + "/" +
					ActivityProjects.MAIN_FOLDER + "/" +
					name + "/");
			//projects icon
			final File icon = new File(Environment.getExternalStorageDirectory() + "/" +
					ActivityProjects.MAIN_FOLDER + "/" +
					name + "/icon");
			
			//get the rootView
			RelativeLayout rootView;
			if (convertView == null){
				rootView = new RelativeLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater)getContext().getSystemService(inflater);
				li.inflate(resource, rootView, true);
			}else{
				rootView = (RelativeLayout) convertView;
			}
			
			/** Show-layout ***************************************************************/
			// get the all needed Views from "Show-layout"
			//name of the project
			final TextView nameView = (TextView)rootView.findViewById(R.id.txt_project_name);
			final TextView dateView = (TextView)rootView.findViewById(R.id.txt_project_date);
			final ImageView img_logo = (ImageView)rootView.findViewById(R.id.img_project_logo);
			
			//set name
			nameView.setText(name);
			nameView.setTypeface(tf);
			nameView.setVisibility(View.VISIBLE);
			
			//set created date of the project
			dateView.setTypeface(tf);
			Calendar c = Calendar.getInstance();
			// if is is not add-project item
			if(p_id != 0)
				c.setTimeInMillis(item.getCreatedDate());
			String s_date = c.get(Calendar.DAY_OF_MONTH) + "/" +
						(c.get(Calendar.MONTH)+1) + "/" +
						c.get(Calendar.YEAR);
			dateView.setText(s_date);
			
			//Icon of the project
			if(icon.exists()){
				Bitmap bm = BitmapFactory.decodeFile(icon.getAbsolutePath());
				img_logo.setImageBitmap(bm);
			}else if(p_id == 0) 
				img_logo.setImageResource(R.drawable.plus_big);
			else{
				img_logo.setImageResource(R.drawable.default_project_logo);
			}
			img_logo.setTag(null);
			/***************************************************************************/
			
			/** Edit project layout ****************************************************/
			
			//Main Layout & needed Views
			final RelativeLayout rl_project_edit = 
					(RelativeLayout)rootView.findViewById(R.id.rl_project_edit);
			final EditText edtName = (EditText)rootView.findViewById(
															R.id.et_project_edt_name);
			final Button btn_ch = (Button)rootView.findViewById(R.id.btn_change_project);
			final Button btn_cancel = (Button)rootView.findViewById(R.id.btn_cancel_project);
			final Button btn_d = (Button)rootView.findViewById(R.id.btn_delete_project);
			final ImageView img_logo_set = (ImageView)rootView.findViewById(
															R.id.img_project_logo_change);
			
			//hide edit Views
			rl_project_edit.setVisibility(View.INVISIBLE);
			
			// font need to be equal :)
			edtName.setTypeface(tf);
			
			//Button Change, saves changes - put new name into db,
			//								 rename or create project's folder
			//								 copy new icon if needed
			//								 hide edit-views
			//								 show show-views
			//								 restart loader
			btn_ch.setTypeface(tf);
			btn_ch.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//update name in db
					ContentResolver cr = getActivity().getContentResolver();
					ContentValues values = new ContentValues();
					values.put(MainProvider.PROJECTS_NAME, edtName.getText().toString());
					Uri rowURI = ContentUris.withAppendedId(
										MainProvider.CONTENT_URI_PROJECTS, p_id);
					int updatedRows = cr.update(rowURI,
													values, 
													null, 
													null);
					if(updatedRows > -1){
						//rename (or create new) project folder
						File new_dir = new File(
										Environment.getExternalStorageDirectory() + "/" +
										ActivityProjects.MAIN_FOLDER + "/" + 
										edtName.getText().toString() + "/");
						if(dir.exists()){
							dir.renameTo(new_dir);
						}else{
							new_dir.mkdirs();
						}
						
						//copy new image if it was changed
						if(img_logo.getTag() != null){
							try {
								copyFile(new File(img_logo.getTag().toString()), 
									new File(
											Environment.getExternalStorageDirectory() + "/" +
											ActivityProjects.MAIN_FOLDER + "/" 
											+ edtName.getText().toString() + "/icon"));
							} catch (IOException e) {
								Toast.makeText(
									getActivity(), 
									getResources().getString(R.string.sorry_cant_copy_icon), 
									Toast.LENGTH_SHORT).show();
								//e.printStackTrace();
							}
						}
					}
					// hide change-views & show show-views
					Animation ta3 = AnimationUtils.loadAnimation(getContext(), 
													R.animator.slide_top_out);
					rl_project_edit.startAnimation(ta3);
					rl_project_edit.setVisibility(View.INVISIBLE);
					
					nameView.setVisibility(View.VISIBLE);
					
					restartProjectsLoader();
				}
			});
			
			//Button Cancel edit - hide edit-views
			//					   show title-view
			//					   restore prev icon
			btn_cancel.setTypeface(tf);
			btn_cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Animation ta3 = AnimationUtils.loadAnimation(getContext(), 
																R.animator.slide_top_out);
					//hide edit-views
					rl_project_edit.startAnimation(ta3);
					rl_project_edit.setVisibility(View.INVISIBLE);

					//show title
					nameView.setVisibility(View.VISIBLE);
					//restore prev image
					if(icon.exists()){
						Bitmap bm = BitmapFactory.decodeFile(icon.getAbsolutePath());
						img_logo.setImageBitmap(bm);
					}else
						img_logo.setImageResource(R.drawable.default_project_logo);
					
					img_logo.setTag(null);
				}
			});
			
			//Button Delete project - check if project has lists
			//						  delete project from db
			//						  delete proejct's folder
			//						  restart loader
			btn_d.setTypeface(tf);
			btn_d.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String where = null;
					String[] whereArgs = null;
					ContentResolver cr_d = getActivity().getContentResolver();
					
					//check if it has lists
					Cursor cursor_check = cr_d.query(
							MainProvider.CONTENT_URI_LISTS, 
							new String[] {"_id"},
							MainProvider.LISTS_PROJECT + "=?",
							new String[] {Long.toString(p_id)}, 
							null);
					if(cursor_check.getCount() > 0){
						Toast.makeText(getActivity(), 
								getResources().getString(R.string.project_is_not_empty), 
								Toast.LENGTH_SHORT).show();
						return;
					}					
					
					Uri rowURI = ContentUris.withAppendedId(
							MainProvider.CONTENT_URI_PROJECTS,
							p_id);
					int deletedRowCount = cr_d.delete(rowURI, where, whereArgs);
					//if success - delete project's folder
					if(deletedRowCount > -1){
						if(icon.exists())
							icon.delete();
						if(dir.exists())
							dir.delete();
						
						restartProjectsLoader();
					}			
				}
			});
			
			//change image - show select dialog
			img_logo_set.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					DialogPickFile dialog = DialogPickFile.newInstance(
							getResources().getString(R.string.change_image), 
							0, 
							img_logo);
					dialog.show(getActivity().getSupportFragmentManager(), "change image");
				}
			});
			/******************************************************************************/
			
			/** Add project layout ********************************************************/
			//root add layout
			final RelativeLayout rl_project_add = (RelativeLayout)
											rootView.findViewById(R.id.rl_project_add);
			final EditText addName = (EditText)rootView.findViewById(
														R.id.et_project_add_name);
			final Button btn_add = (Button)rootView.findViewById(R.id.btn_add_project);
			final Button btn_cancel_add = (Button)rootView.findViewById(
														R.id.btn_cancel_add_project);
			final ImageView img_logo_add = (ImageView)rootView.findViewById(
														R.id.img_project_logo_add);
			
			rl_project_add.setVisibility(View.INVISIBLE);
			
			// editText with name
			addName.setTypeface(tf);
			
			//Button Add new project - add project into db
			//						   create folder
			//						   copy icon
			//						   restart loader
			btn_add.setTypeface(tf);
			btn_add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//add project into db
					ContentResolver cr = getActivity().getContentResolver();
					ContentValues values = new ContentValues();
					values.put(MainProvider.PROJECTS_NAME, addName.getText().toString());
					values.put(MainProvider.PROJECTS_GROUP, 0);
					values.put(MainProvider.PROJECTS_CREATE_DATE, 
													java.lang.System.currentTimeMillis());
					cr.insert(MainProvider.CONTENT_URI_PROJECTS, values);
					
					//create folder
					File new_dir = new File(Environment.getExternalStorageDirectory() + "/" +
											ActivityProjects.MAIN_FOLDER + "/" +
											addName.getText().toString() + "/");
					if(!new_dir.exists())
						new_dir.mkdirs();
					
					//copy project's icon if it was set
					if(img_logo.getTag() != null){
						try {
							copyFile(new File(img_logo.getTag().toString()), 
									new File(new_dir.getAbsolutePath() + "/icon"));
						} catch (IOException e) {
							Toast.makeText(
									getActivity(), 
									getResources().getString(R.string.sorry_cant_copy_icon), 
									Toast.LENGTH_SHORT).show();
						}
					}
					
					nameView.setVisibility(View.VISIBLE);
					rl_project_add.setVisibility(View.INVISIBLE);
					
					restartProjectsLoader();
				}
			});
			
			// button cancel - hide "add-project" views
			//				   restore icon 
			btn_cancel_add.setTypeface(tf);
			btn_cancel_add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					nameView.setVisibility(View.VISIBLE);
					rl_project_add.setVisibility(View.INVISIBLE);
					
					img_logo.setImageResource(R.drawable.plus_big);
					img_logo.setTag(null);
				}
			});
			
			//add icon to new project - show select dialog
			img_logo_add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DialogPickFile dialog = DialogPickFile.newInstance(
							getResources().getString(R.string.add_image), 
							0, 
							img_logo);
					dialog.show(getActivity().getSupportFragmentManager(), "change image");
				}
			});
			/******************************************************************************/

			/* Main behavior: click -> to project; long click -> edit */
			rootView.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					if(p_id != 0){
						//if editing - do not go to ActivityProject
						if(rl_project_edit.getVisibility() == View.VISIBLE) return;
						
						Intent i = new Intent(getContext(), ActivityProject.class);
						i.putExtra("p_id", p_id);
						i.putExtra("p_name", name);
						startActivity(i);
					}else{
						//show add views
						nameView.setVisibility(View.GONE);
						rl_project_add.setVisibility(View.VISIBLE);
					}
				}
			});
			rootView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View v) {
					
					// this is add icon project
					if(p_id == 0) return true;	
					
					// animations for buttons
					Animation ta2 = AnimationUtils.loadAnimation(getContext(), 
															R.animator.slide_top_in);
					
					if(rl_project_edit.getVisibility() != View.VISIBLE){
						rl_project_edit.startAnimation(ta2);
						rl_project_edit.setVisibility(View.VISIBLE);
						edtName.setText(nameView.getText());
						nameView.setVisibility(View.GONE);
					}else{}
					
					return true;
				}
			});
			return rootView;
		}
	}
/******************************************************************************************/
}
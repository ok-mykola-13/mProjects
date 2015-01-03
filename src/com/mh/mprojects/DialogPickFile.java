package com.mh.mprojects;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DialogPickFile extends DialogFragment{	
	
	/////////////////////// FIELDS & Setters-Getters /////////////////////////////
	
	private FilesAdapter aa;
	private ArrayList<File> filesList;
	private String currentPath = Environment.getExternalStorageDirectory().toString();
	private File mPath;
	private int activity;

	//Where to set image
	ImageView iv;
	
	private static final String FTYPE_JPG = ".jpg";
	private static final String FTYPE_JPEG = ".jpeg";
	private static final String FTYPE_PNG = ".png";
	private static final String FTYPE_DB = ".db";
	
	/////////////////////////// METHODS /////////////////////////////////////////
	
	//method for create new dialog
	static DialogPickFile newInstance(String title, int activity, ImageView iv){
		
		DialogPickFile dialog = new DialogPickFile();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putInt("activity", activity);
		dialog.setArguments(args);
		
		dialog.iv = iv;
		
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		mPath = new File(currentPath);
		mPath.listFiles();
		activity = getArguments().getInt("activity");
				
		View view = getActivity()
				.getLayoutInflater().inflate(R.layout.dialog_pick_file, null);
		
		//initialize list
		final ListView lv = (ListView)view.findViewById(R.id.fpfd_lv);
		filesList = new ArrayList<File>();
		aa = new FilesAdapter(getActivity(), R.layout.item_file, filesList);
		lv.setAdapter(aa);
		lv.setDrawSelectorOnTop(true);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				File item = (File)lv.getItemAtPosition(position);
				if (item.isDirectory()){
					currentPath = item.getAbsolutePath();
					loadFileList();
					aa.notifyDataSetChanged();
				}else{
					
					switch(activity){
					case 0:
						Bitmap bm = BitmapFactory.decodeFile(item.getAbsolutePath());
						iv.setImageBitmap(bm);
						iv.setTag(item.getAbsolutePath());
						break;
					default: break;
					}					
					dismiss();
				}
			}
		});
		
		//Button back
		Button btn_back = (Button)view.findViewById(R.id.fpfd_btn_back);
		btn_back.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				String root = Environment.getExternalStorageDirectory().toString();
				if (currentPath.equals(root)){
					Toast.makeText(getActivity(), 
							getResources().getString(R.string.this_is_root), 
							Toast.LENGTH_SHORT).show();
				}else{
					File f = new File(currentPath);
					String parent = f.getParent();
					currentPath = parent;
					loadFileList();
					aa.notifyDataSetChanged();
				}
			}
		});
		
		String title = getArguments().getString("title");
		
		AlertDialog dialog =  new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setView(view)
		.setNegativeButton(getResources().getString(android.R.string.cancel), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
		
		.create();
		
		loadFileList();
		
		return dialog;
	}
	
	//get files List
	private void loadFileList(){
		mPath = new File(currentPath);
		try{
			mPath.mkdirs();
		}catch(SecurityException e){
			Toast.makeText(getActivity(), 
					getResources().getString(R.string.can_not_write_on_sd) + e.toString(), 
					Toast.LENGTH_SHORT).show();
		}
		
		filesList.clear();
		
		if(mPath.exists()){
			
			FilenameFilter filter = new FilenameFilter(){
				public boolean accept(File dir, String filename){
					File sel = new File(dir, filename);
					if(activity == 2){
						return filename.contains(FTYPE_DB) ||
								sel.isDirectory();
					}else{
						return filename.contains(FTYPE_JPG) ||
								filename.contains(FTYPE_JPEG) ||
								filename.contains(FTYPE_PNG) ||
								sel.isDirectory();
					}
				}
			};
			
			File[] t_files = mPath.listFiles(filter);
			for(int i = 0; i < t_files.length; ++i){
				filesList.add(t_files[i]);
			}
		}else{
			filesList = new ArrayList<File>();
		}
	}
/******************************************************************************************/
	
	/**ArrayAdapter for ListView of Files*/
/******************************************************************************************/
	private class FilesAdapter extends ArrayAdapter<File>{
		private int resource;
		
		public FilesAdapter(Context context,
				int resource,
				List<File> items){
			super(context, resource, items);
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			//get Item and it's properties
			File item = getItem(position);
			String name = item.getName();
			//get root view
			LinearLayout ll;
			if (convertView == null){
				ll = new LinearLayout(getContext());
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater)getContext().getSystemService(inflater);
				li.inflate(resource, ll,true);
			}else{
				ll = (LinearLayout) convertView;
			}
			
			//setting Views
			TextView nameView = (TextView)ll.findViewById(R.id.f_name);
			ImageView img = (ImageView)ll.findViewById(R.id.imgFile);
			
			//set name
			nameView.setText(name);
			
			//get type of file
			int pos = name.lastIndexOf('.');
			String ext = null;
		    if (pos != -1) {
		      ext = name.substring(name.lastIndexOf('.') + 1,
		          name.length());
		      ext.trim();
		    }
		      
			if(item.isDirectory())
				img.setImageDrawable(getResources().getDrawable(R.drawable.dir));
			else if (!ext.equals("") && 
					(ext.equalsIgnoreCase("jpeg") ||
							ext.equalsIgnoreCase("jpg") ||
							ext.equalsIgnoreCase("png"))){
				img.setImageDrawable(getResources().getDrawable(R.drawable.img));
			}

			return ll;
		}
	}
/******************************************************************************************/
}
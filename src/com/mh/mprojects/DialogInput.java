package com.mh.mprojects;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

public class DialogInput extends DialogFragment{

	//method for create new dialog
	static DialogInput newInstance(String title, String edtText, int _id, int activity){
		
		DialogInput dialog = new DialogInput();
		Bundle args = new Bundle();
		args.putString("title", title);	//title in dialog
		args.putString("edtText", edtText); //text in EditText
		args.putInt("activity", activity);	//id of Activity for result function
		args.putInt("id", _id);	//id of editing element
		dialog.setArguments(args);
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		//get needed information from intent
		String title = getArguments().getString("title");
		final int activity = getArguments().getInt("activity");
		String edtText = getArguments().getString("edtText");
		final int id = getArguments().getInt("id");
		
		//custom font
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/comic.ttf");
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_input, null);
		final EditText edt = (EditText)v.findViewById(R.id.et_dialog_input);
		edt.setText(edtText);
		edt.setSelection(edtText.length(), edt.length());
		edt.setTypeface(tf);
		
		AlertDialog dialog =  new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setView(v)
		.setPositiveButton(getResources().getString(android.R.string.ok), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = edt.getText().toString().trim();
				if (text.equals("")) return;
				
				switch(activity){
				case 0:
					((ActivityProject)getActivity()).fragmentPlan.addListToPlan(text);
					break;
				case 1:
					((ActivityProject)getActivity()).fragmentLists.addItemToList(text, id);
					break;
				case 2:
					((ActivityProject)getActivity()).fragmentPlan.renameList(text, id);
					break;
				case 3:
					((ActivityProject)getActivity()).fragmentLists.renameItem(text, id);
					break;
				default: break;
				}
			}
		})
		.setNegativeButton(getResources().getString(android.R.string.cancel), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//just close dialog
					}
				})
		.create();
		
		return dialog;
	}
}
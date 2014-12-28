package com.mh.mprojects;

public class MProject {
	
	private int _id;
	private String name;
	private int group;
	private long created_date;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public boolean setName(String name) {
		if(name.length() > 50)
			return false;
		
		this.name = name;
		return true;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public long getCreatedDate(){
		return created_date;
	}
	public void setCreatedDate(long created_date){
		this.created_date = created_date;
	}
	
	public MProject(int _id, String name, int group, long created_date) {
		this._id = _id;
		this.name = name;
		this.group = group;
		this.created_date = created_date;
	}
	
	public MProject(int group) {
		this._id = 0;
		this.name = "Add new Project";
		this.group = group;
		this.created_date = 0;
	}

}

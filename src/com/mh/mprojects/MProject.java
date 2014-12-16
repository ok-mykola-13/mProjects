package com.mh.mprojects;

public class MProject {
	
	private int _id;
	private String name;
	private int group;
	
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
	
	public MProject(int _id, String name, int group) {
		this._id = _id;
		this.name = name;
		this.group = group;
	}
	
	

}

package com.mh.mprojects;

public class MList {
	
	private int _id;
	private String name;
	private boolean done;
	private int priority;
	private int project;
	private int type;
	
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
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getProject() {
		return project;
	}
	public void setProject(int project) {
		this.project = project;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public MList(int _id, String name, boolean done, int priority, int project,
			int type) {
		this._id = _id;
		this.name = name;
		this.done = done;
		this.priority = priority;
		this.project = project;
		this.type = type;
	}
	
}

package com.mh.mprojects;

public class MTask {
	
	private int _id;
	private String name;
	private int done;
	private int parent;
	private int prior;
	
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
		if(name.length() > 255)
			return false;
		
		this.name = name;
		return true;
	}
	public int getDone() {
		return done;
	}
	public void setDone(int done) {
		this.done = done;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int getPrior() {
		return prior;
	}
	public void setPrior(int prior) {
		this.prior = prior;
	}
	
	public MTask(int _id, String name, int done, int parent, int prior) {
		this._id = _id;
		this.name = name;
		this.done = done;
		this.parent = parent;
		this.prior = prior;
	}
	
	

}

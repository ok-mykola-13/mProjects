package com.mh.mprojects;

public class MTask {
	
	private int _id;
	private String name;
	private int done;
	private int parent;
	private int order_num;
	
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
	public int getOrder_num() {
		return order_num;
	}
	public void setOrder_num(int order_num) {
		this.order_num = order_num;
	}
	
	public MTask(int _id, String name, int done, int parent, int order_num) {
		this._id = _id;
		this.name = name;
		this.done = done;
		this.parent = parent;
		this.order_num = order_num;
	}
	
	

}

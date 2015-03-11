package com.bb.model;

import java.io.Serializable;


/**
 *  pojo
 * @author Administrator
 *
 */
public class News implements Serializable  {

	public int id = -1 , flag ;
	public String name;
	public 	String pic = null;
	public  String description;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	} 
 
	
	
	
	
	
}

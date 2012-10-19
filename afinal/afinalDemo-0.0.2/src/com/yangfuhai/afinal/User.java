package com.yangfuhai.afinal;

import net.tsz.afinal.annotation.sqlite.Id;

public class User {

	@Id(column="myId")
	private int userId;
	
	private String name;
	private String email;
	
	
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}

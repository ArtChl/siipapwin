package com.luxsoft.utils.domain;

import java.util.Date;

public abstract class PersistentObject extends MutableObject{
	
	private Long id;
	
	public static Date currentTime(){
		return new Date(System.currentTimeMillis());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}

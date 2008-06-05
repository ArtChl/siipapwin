package com.luxsoft.siipap.swing.form;

import java.io.Serializable;

import com.jgoodies.validation.Validator;

public interface GenericFormModel<T,PK extends Serializable> extends Validator,FormModel{
	
	public T getFormBean();
	
	public PK getId();
	
	public boolean isNewBean();
	
	

}

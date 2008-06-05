package com.luxsoft.siipap.swing.form;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;

public interface FormModel {
	
	public ValidationResultModel getValidationModel();
	
	public PresentationModel getPresentationModel();
	
	public void updateValidation();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean val);
	
	public void commit();

}

package com.luxsoft.siipap.swing.form;

//import javax.swing.JComponent;

import com.luxsoft.siipap.swing.controls.ViewControl;

public interface Form extends ViewControl{
	
	
	
	public FormModel getFormModel();
	
	public void setEnabled(boolean enabled);
	//public JComponent add(String property);
	

}

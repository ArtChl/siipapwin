package com.luxsoft.siipap.swing.form2;

import javax.swing.JComponent;

public class FormControl {
	
	private JComponent control;
	private String labelKey;
	private String property;
	private String label;
	
	
	public FormControl(JComponent control, String property) {
		this(property,control,property);
	}

	public FormControl(String property,JComponent control, String label) {
		this.labelKey = label;
		this.control = control;
		control.setName(property);
		this.property = property;
		this.label=labelKey;
	}
	
	public JComponent getControl() {
		return control;
	}
	public void setControl(JComponent control) {
		this.control = control;
	}
	
	public String getLabelKey() {
		return labelKey;
	}
	public void setLabelKey(String label) {
		this.labelKey = label;
	}
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}

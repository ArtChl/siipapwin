package com.luxsoft.siipap.compras;

import javax.swing.Action;
import javax.swing.JComponent;

public interface AnalisisView {
	
	public static final String SUPPORT_KEY="analisisView";
	
	public JComponent getFilterPanel();
	
	public Action[] getOperaciones();

}

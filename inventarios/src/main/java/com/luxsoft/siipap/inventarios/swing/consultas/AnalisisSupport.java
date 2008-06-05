package com.luxsoft.siipap.inventarios.swing.consultas;

import javax.swing.Action;
import javax.swing.JComponent;

public interface AnalisisSupport {
	
	public static final String SUPPORT_KEY="analisisSupport";
	
	public JComponent getFilterPanel();
	
	public Action[] getOperaciones();

}

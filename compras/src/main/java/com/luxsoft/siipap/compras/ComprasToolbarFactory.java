package com.luxsoft.siipap.compras;

import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.impl.ToolbarFactoryImpl;

public class ComprasToolbarFactory extends ToolbarFactoryImpl{
	
	
	protected void addCustomButtons(ToolBarBuilder builder){
		builder.add(getActionManager().getAction(ComprasActions.AltI.getId()));
		builder.add(getActionManager().getAction(ComprasActions.ConsultasView.getId()));
	}

}

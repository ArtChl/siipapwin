package com.luxsoft.siipap.inventarios;

import com.jgoodies.uif.builder.ToolBarBuilder;
import com.luxsoft.siipap.swing.impl.ToolbarFactoryImpl;

public class InvToolbarFactory extends ToolbarFactoryImpl{
	
	
	protected void addCustomButtons(ToolBarBuilder builder){
		builder.add(getActionManager().getAction(InvActions.ShowReportsView.getId()));
		builder.add(getActionManager().getAction(InvActions.ShowInventarioDeMaquilaView.getId()));
	}

}

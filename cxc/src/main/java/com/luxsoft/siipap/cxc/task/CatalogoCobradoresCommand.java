package com.luxsoft.siipap.cxc.task;

import com.luxsoft.siipap.cxc.catalogos.CobradoresDialog;
import com.luxsoft.siipap.swing.actions.SWXAction;

public class CatalogoCobradoresCommand extends SWXAction{

	@Override
	protected void execute() {
		final CobradoresDialog dialog=new CobradoresDialog();
		dialog.open();
		
	}

}

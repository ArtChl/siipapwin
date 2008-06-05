package com.luxsoft.siipap.cxc.task;

import com.luxsoft.siipap.cxc.catalogos.VendedoresDialog;
import com.luxsoft.siipap.swing.actions.SWXAction;

public class CatalogoVendedoresCommand extends SWXAction{

	@Override
	protected void execute() {
		final VendedoresDialog dialog=new VendedoresDialog();
		dialog.open();
		
	}

}

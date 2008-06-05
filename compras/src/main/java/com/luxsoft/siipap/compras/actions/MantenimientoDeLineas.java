package com.luxsoft.siipap.compras.actions;

import com.luxsoft.siipap.compras.catalogos.CatalogosController;
import com.luxsoft.siipap.swing.actions.SWXAction;

public class MantenimientoDeLineas extends SWXAction{
	
	private CatalogosController controller;

	@Override
	protected void execute() {
		getController().mostrarCatalogoDeLineas();
		
	}

	public CatalogosController getController() {
		return controller;
	}

	public void setController(CatalogosController controller) {
		this.controller = controller;
	}
	
	

}

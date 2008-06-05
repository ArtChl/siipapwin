package com.luxsoft.siipap.compras.actions;

import com.luxsoft.siipap.compras.catalogos.CatalogosController;
import com.luxsoft.siipap.swing.actions.SWXAction;

public class CatalogosCommand extends SWXAction{
	
	private String catalogo;
	private CatalogosController controller;

	@Override
	protected void execute() {
		if("linea".equals(getCatalogo())){
			getController().mostrarCatalogoDeLineas();
		}else if("clase".equals(getCatalogo())){
			getController().mostrarCatalogoDeClases();
		}else if("marca".equals(getCatalogo())){
			getController().mostrarCatalogoDeMarcas();
		}else if("articulo".equals(getCatalogo())){
			getController().mostrarCatalogoDeArticulos();
		}
		
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	public CatalogosController getController() {
		return controller;
	}

	public void setController(CatalogosController controller) {
		this.controller = controller;
	}
	
	
	

}

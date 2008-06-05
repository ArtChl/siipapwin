package com.luxsoft.siipap.compras.catalogos;


/**
 * Facade para control y mantenimiento de catalogos
 * 
 * @author Ruben Cancino
 *
 */
public class CatalogosController {
	
	
	public void mostrarCatalogoDeLineas(){
		final LineasBrowser browser=new LineasBrowser();
		browser.open();
	}
	
	public void mostrarCatalogoDeMarcas(){
		final MarcasBrowser browser=new MarcasBrowser();
		browser.open();
	}
	
	public void mostrarCatalogoDeClases(){
		final ClaseBrowser browser=new ClaseBrowser();
		browser.open();
	}
	public void mostrarCatalogoDeArticulos(){
		final ArticuloBrowser browser=new ArticuloBrowser();
		browser.open();
	}
	
	
	
	
	public static void main(String[] args) {
		CatalogosController controler=new CatalogosController();		
		//control.mostrarCatalogoDeMarcas();
		//control.mostrarCatalogoDeLineas();
		//controler.mostrarCatalogoDeClases();
		controler.mostrarCatalogoDeArticulos();
		System.exit(0);
	}
	

}

package com.luxsoft.siipap.swing.selectores;

import com.luxsoft.siipap.swing.PruebaVisualConDao;


public class SelectorDeArticulosPrueba extends PruebaVisualConDao{

	@Override
	protected void execute() {
		final SelectorDeArticulos dialog=new SelectorDeArticulos();
		//dialog.setManager(dao);
		dialog.open();
		
		if(dialog.hasBeenCanceled()){
			System.out.println("Busqueda en catalogo cancelada ");
		}else
			System.out.println("Seleccion: "+dialog.getSelection());
	}
	
	public static void main(String[] args) {
		new SelectorDeArticulosPrueba().start();
		System.exit(0);
	}
	

}

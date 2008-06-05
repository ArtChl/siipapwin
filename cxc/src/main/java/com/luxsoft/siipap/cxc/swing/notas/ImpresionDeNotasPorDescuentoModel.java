package com.luxsoft.siipap.cxc.swing.notas;

import org.apache.log4j.Logger;

import com.jgoodies.binding.PresentationModel;

public class ImpresionDeNotasPorDescuentoModel extends PresentationModel{
	
	private Logger logger=Logger.getLogger(getClass());
	
	public ImpresionDeNotasPorDescuentoModel() {
		super(new ImpresionPorDescuento());
	}
	
	
	
	public void execute(){
		logger.debug("Ejecutando preparacion de notas de credito");
	}
	
	public void print(){
		logger.debug("Imprimiento notas de descuento ");
	}
	
	

}

package com.luxsoft.siipap.cxc.domain;

import java.util.Date;

import com.luxsoft.siipap.domain.CantidadMonetaria;



/**
 * Maestro de pago para pagos con otros
 * 
 * @author Ruben Cancino
 *
 */
public class Anticipo extends PagoM{
	
	
	
	public Anticipo(Cliente c, String tipo) {
		super(c, tipo);
		
	}

	public Anticipo(){
		super();
		setFecha(new Date());
		setImporte(CantidadMonetaria.pesos(0));
	}

	@Override
	public String getLabel() {
		return "Anticipo";
	}
	
	
	
}

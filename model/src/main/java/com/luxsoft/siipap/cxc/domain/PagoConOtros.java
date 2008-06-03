package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;



/**
 * Maestro de pago para pagos con otros
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConOtros extends PagoM{
	
	private PagoM origen;
	
	

	public PagoM getOrigen() {
		return origen;
	}
	public void setOrigen(PagoM origen) {
		this.origen = origen;
	}
	/*
	public BigDecimal getDisponible(){
		return BigDecimal.ZERO;
	}
	*/
	
	@Override
	public String getLabel() {
		return "Con Otros";
	}
	@Override
	public BigDecimal getDisponible() {
		return BigDecimal.ZERO;
	} 
	
	
}

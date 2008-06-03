package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.luxsoft.siipap.domain.CantidadMonetaria;



/**
 * Maestro de pago para pagos con otros
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConNota extends PagoM{
	
	private NotaDeCredito nota;
	
	public PagoConNota(){
		super();
		setFecha(new Date());
		setImporte(CantidadMonetaria.pesos(0));
	}
	
	public NotaDeCredito getNota() {
		return nota;
	}
	public void setNota(NotaDeCredito nota) {
		this.nota = nota;
	}
/*
	public BigDecimal getDisponible(){
		return BigDecimal.ZERO;
	}
	
	*/
	
	@Override
	public String getLabel() {
		return "Con Nota";
	}

	@Override
	public BigDecimal getDisponible() {
		return BigDecimal.ZERO;
	} 
	
	
}

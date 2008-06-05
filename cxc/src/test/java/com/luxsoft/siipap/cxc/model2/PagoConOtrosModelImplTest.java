package com.luxsoft.siipap.cxc.model2;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.luxsoft.siipap.cxc.domain.PagoM;

/**
 * Valida el estado y comportamiento de PagosConO
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConOtrosModelImplTest extends TestCase{
	
	private PagoConOtrosModel model=new PagoConOtrosModelImpl();
	
	public void testValidarPago(){
		PagoM pago=new PagoM();
		pago.setDisponible(BigDecimal.ZERO);		
		assertFalse(model.isValid(pago));
		pago.setDisponible(BigDecimal.valueOf(10));
		assertTrue(model.isValid(pago));
	}
	
	public void testValidarVentasParaPago(){
		
	}

}

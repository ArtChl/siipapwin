package com.luxsoft.siipap.cxc.pagos;

import java.math.BigDecimal;
import java.util.List;

import com.luxsoft.siipap.cxc.DatosDePrueba;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.ventas.domain.Venta;

import junit.framework.TestCase;

/**
 * Prueba las operaciones utilitarias de PagosUtils
 * 
 * @author Ruben Cancino
 *
 */
public class PagosUtilsTest extends TestCase{
	
	
	
	
	public void testPagoAutomatico(){
		
		//Hardware stuff		
		final List<Venta> ventas=DatosDePrueba.ventasDePrueba();
		for(Venta v:ventas){
			v.setSaldo(BigDecimal.valueOf(99));
		}
		PagoM pago=PagosUtils.generarPagoAutomatico(ventas);
		assertEquals(ventas.size(),pago.getPagos().size());
		for(Pago pp:pago.getPagos()){
			assertEquals(99.0, pp.getImporte().amount().doubleValue());
		}
		
	}

}

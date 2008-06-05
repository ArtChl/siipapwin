package com.luxsoft.siipap.cxc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Probar que PagoAutomaticoFactory opere adecuadamente
 * 
 * @author Ruben Cancino
 *
 */
public class PagosFactoryTest extends MockObjectTestCase{
	
	private PagosFactory factory;
	private List<Venta> ventas;
	final Cliente cliente=new Cliente("U001","TESTU");
	final int CONSALDO=60;
	final CantidadMonetaria SALDO=CantidadMonetaria.pesos(40);
	
	public void setUp(){
		factory=new PagosFactoryImpl();
		ventas=new ArrayList<Venta>();
		for(int i=0;i<100;i++){
			Venta v=new Venta();
			v.setSucursal(10);
			v.setCliente(cliente);
			if(i<CONSALDO){
				v.setSaldo(SALDO.amount());
				v.setTipo("E");
				if(i%2==0){
					v.setTipo("S");
				}
			}
			else{ 
				v.setSaldo(BigDecimal.ZERO);
				v.setTipo("M");
			}
			ventas.add(v);
		}
	}	
	
	
	/**
	 * Prueba q' el metodo genere los pagos automaticos de forma adecuada
	 */
	public void testGenerarPagosAutomaticos(){
		final PagoM pago=factory.crearPagoAutomatico(ventas);
		assertEquals(CONSALDO/2, pago.getPagos().size());
		for(Pago p:pago.getPagos()){
			assertTrue(p.getImporte().amount().doubleValue()>0);
			assertTrue(p.getImporte().amount().doubleValue()<=PagosFactory.TOLERANCIA_AUTOMATICA.doubleValue());
			assertEquals(pago.getTipoDeDocumento(), p.getTipoDocto());
			assertEquals("D",p.getFormaDePago());			
			assertEquals(SALDO, p.getImporte());
		}
		assertEquals(SALDO.multiply(CONSALDO/2), pago.getImporte());
		assertEquals(CantidadMonetaria.pesos(0), pago.getDisponible());		
	}
	

}

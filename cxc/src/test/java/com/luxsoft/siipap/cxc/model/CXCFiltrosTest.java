package com.luxsoft.siipap.cxc.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.ventas.domain.Venta;

import junit.framework.TestCase;

/**
 * Pruebas para la funcionalidad de los diversos filtros requeridos
 * en CXC
 * 
 * @author Ruben Cancino
 *
 */
public class CXCFiltrosTest extends TestCase{
	
	private List<Venta> ventas;
	final Cliente cliente=new Cliente("U001","TESTU");
	final int CONSALDO=60;
	final BigDecimal SALDO=BigDecimal.valueOf(40);
	
	public void setUp(){
		
		ventas=new ArrayList<Venta>();
		for(int i=0;i<100;i++){
			Venta v=new Venta();
			v.setSucursal(10);
			v.setCliente(cliente);
			if(i<CONSALDO){
				v.setSaldo(BigDecimal.valueOf(40));
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
	 * Probar q' el metodo filtre ventas para un solo tipo
	 *
	 */
	public void testFiltrarParaUnTipo(){
		CXCFiltros.filtrarVentasParaUnTipo(ventas, "E");
		for(Venta v:ventas){
			assertEquals("E", v.getTipo());
		}
		assertEquals(CONSALDO/2, ventas.size());
	}
	
	/**
	 * Probar q' el metodo filtrar ventas opere satisfactoriamente
	 * filtrando solo para ventas con 0<saldo<=100 
	 */
	public void testFiltrarVentas(){
		CXCFiltros.filtrarParaPagoAutomatico(ventas);
		assertEquals(CONSALDO, ventas.size());
		for(Venta v:ventas){
			assertTrue(v.isProvisionable());
		}
	}
	
	public void testFiltrarParaMismoCliente(){
		for(Venta v:ventas){
			
		}
	}

}

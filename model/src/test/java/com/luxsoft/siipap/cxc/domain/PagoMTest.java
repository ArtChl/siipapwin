package com.luxsoft.siipap.cxc.domain;

import java.util.List;

import junit.framework.TestCase;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Prueba el comportamiento del bean PagosConDescuentos
 * 
 * @author Ruben Cancino
 *
 */
public class PagoMTest extends TestCase{
	
	/**
	 * Probar que solo se agregen pagos para un mismo cliente
	 *
	 */
	public void testAgregarPagosMismoCliente(){
		
		List<Venta> ventas=Datos.ventasDePrueba();
		Venta v1=ventas.get(0);
		Venta v2=ventas.get(1);
		Cliente c2=new Cliente();
		c2.setClave("U452582");
		c2.setNombre("TEST");
		v2.setCliente(c2);
		PagoM p=new PagoM(v1.getCliente(),"E");
		p.aplicarPago(v1,v1.getSaldoEnMoneda());
		try {
			p.aplicarPago(v2,v2.getSaldoEnMoneda());
			assertFalse("Debio mandar error",true);
		} catch (Exception e) {
			
		}
		
	}
	
	/**
	 * Probar que el numero de pagos sea igual al numero de ventas 
	 *
	 */
	public void testMismasVentasPagos(){
		
		List<Venta> ventas=Datos.ventasDePrueba();
		Cliente c=ventas.get(0).getCliente();
		PagoM p=new PagoM(c,"E");
		for(Venta v:ventas){
			p.aplicarPago(v,v.getSaldoEnMoneda());
		}
		assertEquals(ventas.size(), p.getPagos().size());		
	}
	
	/**
	 * Valida que se eliminen los pagos con importe cero
	 */
	public void testDepurarPagos(){
		List<Venta> ventas=Datos.ventasDePrueba();
		assertTrue(ventas.size()>1);
		Cliente c=ventas.get(0).getCliente();
		PagoM grupo=new PagoM(c,"E");
		
		for(Venta v:ventas){
			grupo.aplicarPago(v, v.getSaldoEnMoneda());
		}
		//Ponemos el primer pago en cero, este es el pago que debe eliminar del set
		final Pago pp= grupo.getPagos().iterator().next();
		pp.setImporte(CantidadMonetaria.pesos(0));
		
		grupo.depurar();
		assertEquals(ventas.size()-1,grupo.getPagos().size());
		assertFalse(grupo.getPagos().contains(pp));
		
	}
	
	
	
	/**
	 * Valida que el metodo calcularDisponible guarde en la variable
	 * de instancia PagoM.disponible la diferencia entre el PagoM
	 * y sus partidas
	 */
	public void testGeneracionDeDiferencia(){
		List<Venta> ventas=Datos.ventasDePrueba();
		assertTrue(ventas.size()>1);
		Cliente c=ventas.get(0).getCliente();
		PagoM grupo=new PagoM(c,"E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(int i=0;i<ventas.size();i++){
			final Venta v=ventas.get(i);
			importe=importe.add(v.getSaldoEnMoneda());
			grupo.aplicarPago(v, v.getSaldoEnMoneda());
		}
		final CantidadMonetaria deMas=CantidadMonetaria.pesos(15000);
		importe=importe.add(deMas);
		
		grupo.setImporte(importe);		
		assertEquals(deMas, grupo.getDisponible());
	}
	
	/**
	 * Prueba la generacion de partidas tipo pago
	 * a partir de ventas
	 *
	 */
	public void testPagoParaVenta(){
		List<Venta> ventas=Datos.ventasDePrueba();
		for(Venta v:ventas){
			Pago p=PagoM.crearPagoParaVenta(v);
			assertEquals(p.getVenta(), v); //Misma venta
			
			assertEquals(v.getSaldoEstimado(), p.getImporte());
			assertEquals(p.getCliente(), v.getCliente());
			assertEquals(p.getClave(),v.getClave());
			
			assertEquals(p.getSucursal(),v.getSucursal().intValue());
			assertEquals(p.getTipoDocto(),v.getTipo());
			assertEquals(p.getOrigen(),v.getOrigen());
		}
		
		
		
	}

}

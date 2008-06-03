package com.luxsoft.siipap.cxc.domain;

import junit.framework.TestCase;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * 
 * @author Ruben Cancino 
 * 
 *
 */
public class DescuentoPorClienteTest extends TestCase{
	
	/**
	 * 
	 *
	 */
	public void testCalcularImporte(){
		
		CantidadMonetaria esperado=CantidadMonetaria.pesos(300);
		
		String clave="U050008";
		
		Venta venta=new Venta();
		venta.setClave(clave);
		venta.setTipo("E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(1000);
		venta.setImporteBruto(importe);
		
		DescuentoPorCliente desc=new DescuentoPorCliente();
		desc.setClave("U050008");
		desc.setDescuento(30.00d);
		desc.setTipoFac("E");
		
		CantidadMonetaria actual=desc.calcularDescuento(venta);
		
		assertEquals(esperado, actual);
		
	}
	
	
	public void testMismoCliente(){
		CantidadMonetaria esperado=CantidadMonetaria.pesos(0);
		
		String clave="U050008";
		
		Venta venta=new Venta();
		venta.setClave(clave);
		venta.setTipo("E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(1000);
		venta.setImporteBruto(importe);
		
		DescuentoPorCliente desc=new DescuentoPorCliente();
		desc.setClave("U050009");
		desc.setDescuento(30.00d);
		desc.setTipoFac("E");
		
		CantidadMonetaria actual=desc.calcularDescuento(venta);
		
		assertEquals(esperado, actual);
	}
	
	
	public void testValidarMistmoTipo(){
		
		CantidadMonetaria esperado=CantidadMonetaria.pesos(0);		
		String clave="U050008";
		
		Venta venta=new Venta();
		venta.setClave(clave);
		venta.setTipo("E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(1000);
		venta.setImporteBruto(importe);
		
		DescuentoPorCliente desc=new DescuentoPorCliente();
		desc.setClave("U050008");
		desc.setDescuento(30.00d);
		desc.setTipoFac("N");
		
		CantidadMonetaria actual=desc.calcularDescuento(venta);
		
		assertEquals(esperado, actual);
	}
	
	public void testCalcularAdicional(){
		
		CantidadMonetaria esperado=CantidadMonetaria.pesos(430);
		
		String clave="U050008";
		
		Venta venta=new Venta();
		venta.setClave(clave);
		venta.setTipo("E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(1000);
		venta.setImporteBruto(importe);
		
		DescuentoPorCliente desc=new DescuentoPorCliente();
		desc.setClave("U050008");
		desc.setDescuento(40.00d);
		desc.setTipoFac("E");
		desc.setAdicional(.05);
		CantidadMonetaria actual=desc.calcularDescuento(venta);
		
		assertEquals(esperado, actual);
	}
	
	public void testCalcularInactivo(){
		
		CantidadMonetaria esperado=CantidadMonetaria.pesos(0);
		
		String clave="U050008";
		
		Venta venta=new Venta();
		venta.setClave(clave);
		venta.setTipo("E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(1000);
		venta.setImporteBruto(importe);
		
		DescuentoPorCliente desc=new DescuentoPorCliente();
		desc.setClave("U050008");
		desc.setDescuento(40.00d);
		desc.setTipoFac("E");
		desc.setActivo(false);
		desc.setAdicional(.05);
		CantidadMonetaria actual=desc.calcularDescuento(venta);
		
		assertEquals(esperado, actual);
	}

}

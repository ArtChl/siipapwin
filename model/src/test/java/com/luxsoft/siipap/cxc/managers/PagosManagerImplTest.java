package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.cxc.dao.PagoMDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Datos;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Pruebas para la implementacion de PagosManager 
 * 
 * @author Ruben Cancino
 *
 */
public class PagosManagerImplTest extends MockObjectTestCase{
	
	private PagosManagerImpl manager;
	
	
	
	/**
	 * Prueba que los datos de los pagos sean los mismos que el del grupo
	 * Fecha,Comentario,Year,Mes,Clave etc
	 *
	 */
	public void testSalvarPagoM(){
		
		manager=new PagosManagerImpl();
		Mock mock=mock(PagoMDao.class);
		mock.expects(once()).method("salvar");
		manager.setPagoMDao((PagoMDao)mock.proxy());
		final Cliente c=new Cliente("U050008","Union de Credito");
		
		
		PagoM grupo=new PagoM(c,"E");
		grupo.setFecha(new Date());
		grupo.setBanco("BANAMEX");
		grupo.setComentario("TEST");
		grupo.setFormaDePago(FormaDePago.H);
		grupo.setReferencia("445");
		
		manager.salvarGrupoDePagos(grupo);
		
	}
	
	/**
	 * Probar que el disponible simpre sea >=0
	 */
	public void testDisponible(){
		
		manager=new PagosManagerImpl();
		Mock mock=mock(PagoMDao.class);
		manager.setPagoMDao((PagoMDao)mock.proxy());
		
		final Cliente c=new Cliente("U050008","Union de Credito");
		final List<Venta> ventas=Datos.ventasDePrueba();
		assertFalse(ventas.isEmpty());
		PagoM grupo=new PagoM(c,"E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			grupo.aplicarPago(v, v.getSaldoEnMoneda());
			importe=importe.add(v.getSaldoEnMoneda());
		}
		grupo.setFecha(DateUtils.obtenerFecha("15/07/2007"));
		grupo.setBanco("BANAMEX");
		grupo.setComentario("TEST");
		grupo.setFormaDePago(FormaDePago.H);
		grupo.setReferencia("445");
		grupo.setImporte(importe);
		
		try {
			final CantidadMonetaria imp2=grupo.getImporte().subtract(CantidadMonetaria.pesos(100));
			grupo.setImporte(imp2);
			manager.salvarGrupoDePagos(grupo);
			assertTrue("Debio mandar error",false);
		} catch (Exception e) {
			System.out.println(e.getMessage());			
		}
		
		
	}
	
	public void testConsistenciaEnSalvarPagoM(){
		manager=new PagosManagerImpl();
		Mock mock=mock(PagoMDao.class);
		mock.expects(once()).method("salvar");
		manager.setPagoMDao((PagoMDao)mock.proxy());
		
		final Cliente c=new Cliente("U050008","Union de Credito");
		final List<Venta> ventas=Datos.ventasDePrueba();
		assertFalse(ventas.isEmpty());
		PagoM grupo=new PagoM(c,"E");
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			grupo.aplicarPago(v, v.getSaldoEnMoneda());
			importe=importe.add(v.getSaldoEnMoneda());
		}
		grupo.setFecha(DateUtils.obtenerFecha("15/07/2007"));
		grupo.setBanco("BANAMEX");
		grupo.setComentario("TEST");
		grupo.setFormaDePago(FormaDePago.H);
		grupo.setReferencia("445");
		grupo.setImporte(importe);
		
		manager.salvarGrupoDePagos(grupo);
		
		for(Pago pago:grupo.getPagos()){
			assertEquals("Clave",grupo.getClave(),pago.getClave());
			assertEquals("Cliente",grupo.getCliente(),pago.getCliente());
			assertEquals("Comentario",grupo.getComentario(),pago.getComentario());
			assertEquals("Fecha",grupo.getFecha(), pago.getFecha());
			assertEquals("FormaDePago",grupo.getFormaDePago(), pago.getFormaDePago2());
			assertEquals("FormaDePago.id",grupo.getFormaDePago().getId(), pago.getFormaDePago());
			assertEquals("Mes",grupo.getMes(), pago.getMes());
			assertEquals("Referencia",grupo.getReferencia(), pago.getReferencia());
			assertEquals("Year",grupo.getYear(), pago.getYear());
			
			assertEquals("Mes debe ser 7", 7,grupo.getMes());
			assertEquals("Year debe ser 2007", 2007,grupo.getYear());
		}
	}

}

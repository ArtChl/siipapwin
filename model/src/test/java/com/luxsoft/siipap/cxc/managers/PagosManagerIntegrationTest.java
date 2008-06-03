package com.luxsoft.siipap.cxc.managers;

import java.util.List;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Prueba de PagosManagerImpl integrados a la base de datos
 * 
 * @author Ruben Cancino Ramos
 *
 */
public class PagosManagerIntegrationTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private PagosManager manager;
	private ClienteDao clienteDao;
	private VentasDao ventaDao;
	
	/**
	 * Simplemente prueba que la persistencia de un bean PagoM se ejecute
	 * sin errores, otros test validan la funcionalidad independiente
	 */
	@NotTransactional
	public void testPersistenciaDePagoM(){		
		final String clave="U050008";
		final Cliente cliente=clienteDao.buscarPorClave(clave);		
		final List<Venta> ventas=ventaDao.buscarVentasConSaldo(clave).subList(0, 10);
		assertTrue(!ventas.isEmpty());
		
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);		
		for(Venta v:ventas){
			importe=importe.add(v.getSaldoEnMoneda());
		}
		importe=importe.add(CantidadMonetaria.pesos(50000));
		
		final PagoM pd=new PagoM(cliente,"E");		
		pd.setClave(clave);
		pd.setFecha(DateUtils.obtenerFecha("15/07/2007"));
		pd.setImporte(importe);
		pd.setBanco("BANCOMER");
		pd.setComentario("PRUEBAS");
		pd.setFormaDePago(FormaDePago.H);
		pd.setReferencia("45285");
		
		assertFalse(ventas.isEmpty());		
		
		for(Venta v:ventas){
			pd.aplicarPago(v, v.getSaldoEnMoneda());
		}		
		
		pd.setFecha(DateUtils.obtenerFecha("15/07/2007"));
		pd.setBanco("BANAMEX");
		pd.setComentario("TEST");
		pd.setFormaDePago(FormaDePago.H);
		pd.setReferencia("445");
		pd.setImporte(importe);		
		manager.salvarGrupoDePagos(pd);
		setComplete();
	}
	
	public void testPosiblesPagosAutomaticos(){
		
	}
	
	public void setManager(PagosManager manager) {
		this.manager = manager;
	}
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:swx-dao-ctx.xml"};
	}
	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}
	public void setVentaDao(VentasDao ventaDao) {
		this.ventaDao = ventaDao;
	}
	
}

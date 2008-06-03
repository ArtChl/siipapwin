package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Prueba la persistencia de los pagos en grupo
 *  
 * @author Ruben Cancino
 *
 */
public class PagoMDaoTest extends AbstractDaoTest{
	
	private PagoMDao dao;
	private VentasDao ventaDao;
	private ClienteDao clienteDao;
	
	//@NotTransactional
	public void testCURD(){
		String clave="U050008";
		Cliente cliente=clienteDao.buscarPorClave(clave);		
		List<Venta> ventas=ventaDao.buscarVentasConSaldo(clave).subList(0, 10);
		assertTrue(!ventas.isEmpty());
		
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		
		for(Venta v:ventas){
			importe=importe.add(v.getSaldoEnMoneda());
		}
		importe=importe.add(CantidadMonetaria.pesos(50000));
		
		PagoM pd=new PagoM();
		pd.setCliente(cliente);
		pd.setClave(clave);
		pd.setFecha(DateUtils.obtenerFecha("15/07/2007"));
		pd.setImporte(importe);
		pd.setBanco("BANCOMER");
		pd.setComentario("PRUEBAS");
		pd.setFormaDePago(FormaDePago.H);
		pd.setReferencia("45285");
		
		for(int i=0;i<10;i++){
			Venta v=ventas.get(i);
			Pago pago=pd.aplicarPago(v,v.getSaldoEnMoneda());
			pago.setImporte(v.getSaldoEnMoneda());
		}
		dao.salvar(pd);
	}
	
	public void testBuscarDisponibles(){
		final String clave="A010406";
		final List<PagoM> pagos=dao.buscarDisponibles(clave);
		assertFalse(pagos.isEmpty());
	}

	public void setDao(PagoMDao dao) {
		this.dao = dao;
	}

	public void setVentaDao(VentasDao ventaDao) {
		this.ventaDao = ventaDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}
	
	
	
	

}

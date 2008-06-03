package com.luxsoft.siipap.ventas.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

public class VentasDaoTest extends AbstractDaoTest{
	
	private VentasDao dao;
	
	public void testVentasParaRevision(){
		final Date fecha=DateUtils.obtenerFecha("15/05/2007");
		Object[] vals={fecha};
		int types[]={Types.DATE};
		int expected=jdbcTemplate.queryForInt("select count(0) from SW_VENTASCREDITO a join V_VENTAS b on(a.venta_id=b.venta_id) where a.FECHAREVISION=? and b.saldo>1",vals,types);
		final List<Venta> ventas=dao.buscarVentasParaRevisar(fecha);
		int actual=ventas.size();
		System.out.println(actual);
		assertEquals(expected, actual);
		for(Venta v:ventas){
			assertNotNull(v.getCredito().getFechaRevisionCxc());
		}
	}
	
	public void testVentasParaPago(){
		final Date fecha=DateUtils.obtenerFecha("11/06/2007");
		Object[] vals={fecha};
		int types[]={Types.DATE};
		int expected=jdbcTemplate.queryForInt("select count(0) from SW_VENTASCREDITO a join V_VENTAS b on(a.venta_id=b.venta_id) where a.reprogramarPago=? and b.saldo>1",vals,types);
		final List<Venta> ventas=dao.buscarVentasParaCobrar(fecha);
		int actual=ventas.size();
		System.out.println(actual);
		assertEquals(expected, actual);
		for(Venta v:ventas){
			assertNotNull(v.getCredito().getFechaRevisionCxc());
		}
	}

	public void setDao(VentasDao dao) {
		this.dao = dao;
	}
	
	
	

}

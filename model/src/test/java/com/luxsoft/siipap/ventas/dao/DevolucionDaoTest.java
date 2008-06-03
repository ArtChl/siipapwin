package com.luxsoft.siipap.ventas.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

public class DevolucionDaoTest extends AbstractDaoTest{
	
	private DevolucionDao dao;
	
	@NotTransactional
	public void testDevolucionesSinAplicar(){
		String clave="U050008";
		String sql="select count(*) from sw_devodet where year>2006 and devo_id in(select devo_id from sw_devo where year>2006 and cliente=?)  and nota_id is  null ";
		int expected=jdbcTemplate.queryForInt(sql,new Object[]{clave});		
		List<DevolucionDet> dets=dao.buscarDevolucionesSinAplicar(clave);
		int found=dets.size();
		for(DevolucionDet d:dets){
			System.out.println(d.getDevolucion().getCliente()+" Year "+d.getYear()+" Devo: "+d.getDevolucion().getId());
			assertNotNull(d.getArticulo().getClave());
			assertNotNull(d.getVentaDet().getPrecioFacturado());
			assertNotNull(d.getVentaDet().getVenta());
			assertNotNull(d.getVentaDet().getVenta().getCliente());
		}
		assertEquals(expected, found);
	}
	
	
	public void setDao(DevolucionDao dao) {
		this.dao = dao;
	}
	
	
	

}

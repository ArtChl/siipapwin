package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.dao.AbstractDaoTest;

public class DescuentosEspecialDaoTest extends AbstractDaoTest{
	
	private DescuentoEspecialDao dao;
	
	@NotTransactional
	public void testBuscarDescuentosPorCliente(){
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_DESC_ESP");
		System.out.println("Expected: "+expected);
		List<DescuentoEspecial> descs=dao.buscar();
		int actual=descs.size();
		assertEquals(expected, actual);
		//La venta not lazy
		for(DescuentoEspecial d:descs){
			d.getVenta().getClave();
			d.getVenta().getNombre();
		}
	}

	public void setDao(DescuentoEspecialDao dao) {
		this.dao = dao;
	}
	
	

		

}

package com.luxsoft.siipap.cxc.dao;

import org.springframework.test.annotation.NotTransactional;


import com.luxsoft.siipap.dao.AbstractDaoTest;

public class DescuentosPorClienteDaoTest extends AbstractDaoTest{
	
	private DescuentoPorClienteDao dao;
	
	@NotTransactional
	public void testBuscarDescuentosPorCliente(){
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_DESC_CTE where tipo=?",new Object[]{"C"});
		System.out.println("Expected: "+expected);
		int actual=dao.buscarDescuentos().size();
		assertEquals(expected, actual);
	}
	
	

	public void setDao(DescuentoPorClienteDao dao) {
		this.dao = dao;
	}
	
	

	

	
	
	
	

}

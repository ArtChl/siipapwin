package com.luxsoft.siipap.cxc.dao;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.dao.AbstractDaoTest;

public class DescuentosPorVolumenDaoTest extends AbstractDaoTest{
	
	private DescuentoPorVolumenDao dao;
	
	@NotTransactional
	public void testBuscarDescuentosPorCliente(){
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_DESC_VOL ");
		System.out.println("Expected: "+expected);
		int actual=dao.buscar().size();
		assertEquals(expected, actual);
	}
	
	public void testDescuentoChequeP(){
		
	}

	public void setDao(DescuentoPorVolumenDao dao) {
		this.dao = dao;
	}
	
	

		
	

}

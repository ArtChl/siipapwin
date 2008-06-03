package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.dao.AbstractDaoTest;

public class ProvisionDaoTest extends AbstractDaoTest{
	
	private ProvisionDao dao;
	
	@NotTransactional
	public void testNotasPorPeriodo(){
		
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_PROVISION ");
				
		logger.info("Provisiones del periodo: "+expected);
		
		List<Provision> list=dao.buscarProvisiones();
		int actual=list.size();
		assertEquals(expected, actual);
		
		//Probar venta  not lazy
		for(Provision p:list){
			assertNotNull(p.getVenta().getClave());
		}		
	}

	public void setDao(ProvisionDao dao) {
		this.dao = dao;
	}
	
	

	
}

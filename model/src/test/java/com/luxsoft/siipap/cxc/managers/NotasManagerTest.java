package com.luxsoft.siipap.cxc.managers;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.ProvisionDao;
import com.luxsoft.siipap.domain.Periodo;

public class NotasManagerTest extends MockObjectTestCase{
	
	NotasManagerImpl manager;
	
	public void setUp(){
		manager=new NotasManagerImpl();
	}
	
	public void testBuscarNotas(){
		Mock mock=mock(NotaDeCreditoDao.class);
		mock.expects(once()).method("buscarNotas").with(this.isA(Periodo.class));
		
		manager.setNotaDeCreditoDao((NotaDeCreditoDao)mock.proxy());
		manager.buscarNotasCre(Periodo.periodoDeloquevaDelMes());
		
		
	}
	
	public void testBuscarProvisiones(){
		Mock mock=mock(ProvisionDao.class);
		mock.expects(once()).method("buscarProvisiones").withNoArguments();
		
		manager.setProvisionDao((ProvisionDao)mock.proxy());
		manager.buscarProvisiones();
	}

}

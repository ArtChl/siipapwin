package com.luxsoft.siipap.em.replica.dao;

import java.util.Date;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;


public abstract class ReplicaLogDaoImplTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private ReplicaLogDao replicaDao;
	
	public void testAltasBajas(){
		ReplicaLog log=new ReplicaLog();
		log.setEntity("TEST");
		log.setTabla("TEST_TAB");
		log.setPeriodo(Periodo.periodoDeloquevaDelMes().toString());
		log.setCreado(new Date());
		log.setMonth(2);
		log.setYear(2007);		
		log.setBeans(100);
		log.setRegistros(100);
		replicaDao.salvar(log);
		
		
		assertNotNull(log.getId());
		Long id=log.getId();
		replicaDao.borrar(log);
		
		ReplicaLog log2=replicaDao.buscar(id);
		assertNull(log2);
		
	}
	/*
	public void testNoDubplicados(){
		ReplicaLog log=new ReplicaLog();
		log.setEntity("TEST2");
		log.setTabla("TEST2_TAB");
		log.setPeriodo(Periodo.periodoDeloquevaDelMes().toString());
		log.setCreado(new Date());
		log.setMonth(3);
		log.setYear(2007);		
		log.setBeans(100);
		log.setRegistros(100);
		
		replicaDao.salvar(log);
		endTransaction();
		setComplete();
		
		ReplicaLog log2=new ReplicaLog();
		log2.setEntity("TEST2");
		log2.setTabla("TEST2_TAB");
		log2.setPeriodo(Periodo.periodoDeloquevaDelMes().toString());
		log2.setCreado(new Date());
		log2.setMonth(3);
		log2.setYear(2007);		
		log2.setBeans(100);
		log2.setRegistros(100);
		
		assertEquals(log, log2);
		try{
			replicaDao.salvar(log2);
			assertTrue(false);
		}catch (Exception e) {
			//e.printStackTrace();
			assertTrue(true);			
		}
		
	}
	
	public void testBuscarLog(){
		ReplicaLog log=new ReplicaLog();
		log.setEntity("TEST3");
		log.setTabla("TEST3_TAB");
		log.setPeriodo(Periodo.periodoDeloquevaDelMes().toString());
		log.setCreado(new Date());
		log.setMonth(1);
		log.setYear(2007);		
		log.setBeans(100);
		log.setRegistros(100);
		
		replicaDao.salvar(log);
		
		
		ReplicaLog log2=replicaDao.buscar(log.getEntity(),log.getTabla(),log.getPeriodo());
		assertEquals(log,log2);
	}
	
	*/
	
	public void setReplicaDao(ReplicaLogDao replicaDao) {
		this.replicaDao = replicaDao;
	}
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}

}

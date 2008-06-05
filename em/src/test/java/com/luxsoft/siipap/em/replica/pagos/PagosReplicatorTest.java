package com.luxsoft.siipap.em.replica.pagos;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public abstract class PagosReplicatorTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private PagosReplicator replicator;
	private SiipapJdbcTemplateFactory factory;
	
		
	
	
	public void testImportacionBulk(){
		/*
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from PAGCRE01 ");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from  PAGCAM01");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from  PAGCHE01");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from  PAGJUR01");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from  CAJNOR01");
		
		replicator.bulkImport(mes);
		logger.debug("Expected: "+expected);		
		*/
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}
	

	

	public void setReplicator(PagosReplicator replicator) {
		this.replicator = replicator;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	

}

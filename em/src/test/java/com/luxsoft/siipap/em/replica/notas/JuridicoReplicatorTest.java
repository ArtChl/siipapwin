package com.luxsoft.siipap.em.replica.notas;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public abstract class JuridicoReplicatorTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private JuridicoReplicator replicator;
	private SiipapJdbcTemplateFactory factory;
	
		
	
	
	public void testImportacionBulk(){
		/*
		Periodo mes=Periodo.getPeriodoEnUnMes(1, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOVJUR01 ");		
		replicator.bulkImport(mes);
		logger.debug("Expected: "+expected);
		*/
		
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}
	

	public void setReplicator(JuridicoReplicator replicator) {
		this.replicator = replicator;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	

}

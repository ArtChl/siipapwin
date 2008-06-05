package com.luxsoft.siipap.em.replica.notas;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public abstract class NotasDeCreditoReplicatorTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private NotasDeCreditoReplicator replicator;
	private SiipapJdbcTemplateFactory factory;
	/*
	public void testGenerarNotasMovcre(){
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOVCRE01 where MCRIDENOPE in(3,4)");
		List<NotaDeCredito> notas=replicator.generarNotasMovcre(mes);
		for(NotaDeCredito nota:notas){
			assertFalse(StringUtils.isEmpty(nota.getOrigen()));
			
		}
		int actual=notas.size();
		assertEquals(expected, actual);
	}
	
	public void testSalvarNotasGeneradoas(){
		
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOVCRE01 where MCRIDENOPE in(3,4)");
		List<NotaDeCredito> notas=replicator.generarNotasMovcre(mes);
		
		replicator.salvar(notas.toArray());
		endTransaction();
		int actual=0;
		for(NotaDeCredito n:notas){
			assertNotNull(n.getId());
			actual++;
		}		
		assertEquals(expected, actual);
		
	}
	*/
	
	public void testImportacionBulk(){
		/*
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOVCRE01 where MCRIDENOPE in(3,4)");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOCOCA01 where MCAIDENOPE in(3,4)");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from MOCOMO01 where MCMIDENOPE in(3,4)");
		//replicator.bulkImport(mes);
		logger.debug("Expected: "+expected);
		*/
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}

	public void setReplicator(NotasDeCreditoReplicator replicator) {
		this.replicator = replicator;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	

}

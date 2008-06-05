package com.luxsoft.siipap.em.replica.notas;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;


import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public abstract class NotasDeCreditoDetReplicatorTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private NotasDeCreditoDetReplicator replicator;
	private SiipapJdbcTemplateFactory factory;
	/*
	public void testGenerarNotasMovcre(){
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOCRE01 ");
		List<NotasDeCreditoDet> notas=replicator.generarNotasDeDDOCRE(mes);
		for(NotasDeCreditoDet det:notas){
			assertFalse(StringUtils.isEmpty(det.getOrigen()));			
		}
		int actual=notas.size();
		assertEquals(expected, actual);
	}
	
	public void testGenerarNotasMococa(){
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOCAM01 ");
		List<NotasDeCreditoDet> notas=replicator.generarNotasDeDDOCAM(mes);
		for(NotasDeCreditoDet det:notas){
			assertFalse(StringUtils.isEmpty(det.getOrigen()));			
		}
		int actual=notas.size();
		assertEquals(expected, actual);
	}
	
	public void testGenerarNotasMocomo(){
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOMOS01 ");
		List<NotasDeCreditoDet> notas=replicator.generarNotasDeDDOMOS(mes);
		for(NotasDeCreditoDet det:notas){
			assertFalse(StringUtils.isEmpty(det.getOrigen()));			
		}
		int actual=notas.size();
		assertEquals(expected, actual);
	}
	
	public void testVincular(){
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);		
		List<NotasDeCreditoDet> dets=replicator.generarNotasDeDDOCRE(mes);
		replicator.vincularConMaestro(mes, replicator.getNotas(mes), dets);
		
	}
	
	*/
	
	public void testImportacionBulk(){
		/*
		Periodo mes=Periodo.getPeriodoEnUnMes(0, 2006);
		int expected=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOCRE01 ");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOCAM01 ");
		expected+=factory.getJdbcTemplate(mes).queryForInt("select count(*) from DDOMOS01 ");
		replicator.bulkImport(mes);
		logger.debug("Expected: "+expected);
		*/
		
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}

	

	public void setReplicator(NotasDeCreditoDetReplicator replicator) {
		this.replicator = replicator;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	

}

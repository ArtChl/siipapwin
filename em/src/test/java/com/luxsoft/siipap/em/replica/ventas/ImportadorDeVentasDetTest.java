package com.luxsoft.siipap.em.replica.ventas;

import java.sql.Types;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.ReplicationUtils;

public abstract  class ImportadorDeVentasDetTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	
	private SiipapJdbcTemplateFactory factory;
	/*
	public void testGeneracionDeBeans(){
		Periodo p=new Periodo("04/01/2006","04/01/2006");
		int actual=importador.generaBeans(p.getFechaInicial()).size();
		String wh=ReplicationUtils.resolveWherePart(p, "ALMFECHA");
		String sql="select count(*) from ALMACE01 "+wh;
		int expected=factory.getJdbcTemplate(2006).queryForInt(sql);
		assertEquals(expected, actual);
	}
	
	public void testBulkImport(){
		Periodo p=new Periodo("01/01/2006","10/01/2006");
		
		String wh=ReplicationUtils.resolveWherePart(p, "ALMFECHA");
		String sql="select count(*) from ALMACE01 "+wh;
		int expected=factory.getJdbcTemplate(2006).queryForInt(sql);
		
		int importados=importador.bulkImport(p);
		logger.debug("Importados: "+importados);
		Object[] vals={p.getFechaInicial(),p.getFechaFinal()};
		int[] types={Types.DATE,Types.DATE};
		int actual=jdbcTemplate.queryForInt("select count(*) from SW_VENTASDET where fecha between ? and ?", vals,types); 
		
		assertEquals(expected, actual);
	}
	*/
	public void testVincular(){
		/*
		Periodo p=new Periodo("01/01/2006","10/01/2006");
		int importados=importador.bulkImport(p);
		assertTrue(importados>0);
		importador.vincular(p);
		Object[] vals={p.getFechaInicial(),p.getFechaFinal()};
		int[] types={Types.DATE,Types.DATE};
		int actual=jdbcTemplate.queryForInt("select count(*) from SW_VENTASDET where venta_id is not null and fecha between ? and ?", vals,types);
		logger.debug("VentasDet sin vinculo a Venta :"+actual);
		assertEquals(0, actual);
		*/
		
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}

	

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	
	
}

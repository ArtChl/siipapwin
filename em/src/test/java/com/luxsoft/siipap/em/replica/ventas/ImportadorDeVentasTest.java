package com.luxsoft.siipap.em.replica.ventas;

import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.BulkImportador;

public abstract  class ImportadorDeVentasTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	private VentasReplicator importadorDeVentas;
	private SiipapJdbcTemplateFactory factory;
	
	/*
	public void testGeneracionDesdeMocomo(){
		
		final int year=2006;
		final String sql="select count(*) from MOCOMO01 where  MCMIDENOPE=1 ";
		final Periodo p=Periodo.getPeriodoEnUnMes(0, year);
		JdbcTemplate tm=factory.getJdbcTemplate(year);
		int expected=tm.queryForInt(sql);
		int actual=importadorDeVentas.cargarMocomos(p).size();
		assertEquals(expected, actual);
	}
	
	public void testGeneracionDesdeMococa(){
		final int year=2006;
		final String sql="select count(*) from MOCOCA01 where  MCAIDENOPE=1 ";
		final Periodo p=Periodo.getPeriodoEnUnMes(0, year);
		JdbcTemplate tm=factory.getJdbcTemplate(year);
		int expected=tm.queryForInt(sql);
		int actual=importadorDeVentas.cargarMococas(p).size();
		assertEquals(expected, actual);
	}
	
	public void testGeneracionDesdeMovcre(){
		final int year=2006;
		final String sql="select count(*) from MOVCRE01 where  MCRIDENOPE=1 ";
		final Periodo p=Periodo.getPeriodoEnUnMes(0, year);
		JdbcTemplate tm=factory.getJdbcTemplate(year);
		int expected=tm.queryForInt(sql);
		int actual=importadorDeVentas.cargarMovcres(p).size();
		assertEquals(expected, actual);
	}
	*/
	public void testImportacionBulk(){
		/*
		final int year=2006;
		int expected=factory.getJdbcTemplate(year).queryForInt("select count(*) from MOCOMO01 where  MCMIDENOPE=1 ");
		expected+=factory.getJdbcTemplate(year).queryForInt("select count(*) from MOCOCA01 where  MCAIDENOPE=1 ");
		expected+=factory.getJdbcTemplate(year).queryForInt("select count(*) from MOVCRE01 where  MCRIDENOPE=1 ");
		final Periodo p=Periodo.getPeriodoEnUnMes(0, year);
		importadorDeVentas.cleanImport(p);
		//setComplete();
		Object[] vals={p.getFechaInicial(),p.getFechaFinal()};
		int[] types={Types.DATE,Types.DATE};
		int actual=jdbcTemplate.queryForInt("select count(*) from sw_ventas where fecha between ? and ?",vals,types);
		//int actual=jdbcTemplate.queryForInt("select count(*) from sw_ventas where to_char(creado,\'MMYYYY\')=?",new String[]{"012006"});
		assertEquals(expected, actual);
		*/
		
	}
	
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}

	

	public void setImportadorDeVentas(VentasReplicator importadorDeVentas) {
		this.importadorDeVentas = importadorDeVentas;
	}


	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	
	

}

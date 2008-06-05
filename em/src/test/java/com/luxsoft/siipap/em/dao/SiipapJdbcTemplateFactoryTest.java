package com.luxsoft.siipap.em.dao;

import java.util.Calendar;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public abstract class SiipapJdbcTemplateFactoryTest extends AbstractDependencyInjectionSpringContextTests{
	
	private SiipapJdbcTemplateFactory factory;
	
	public void testTemplatesCreation(){
		int current=Calendar.getInstance().get(Calendar.YEAR);
		for(int year=2002;year<=current;year++){
			JdbcTemplate tm=factory.getJdbcTemplate(year);
			assertNotNull(tm);
		}
	}
	
	public void testExecution(){
		factory.getJdbcTemplate(2006).queryForInt("select count(*) from ALMACE01");
	}
	
	
	
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}


	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	
	

}

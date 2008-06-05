package com.luxsoft.siipap.em.dao;



import java.util.Calendar;

import javax.sql.DataSource;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;


/**
 * Prueba que existan los componentes adecuados en Spring
 * 
 * @author Ruben Cancino 
 *
 */
public abstract class DataSourcesTest extends AbstractDependencyInjectionSpringContextTests{
	
	private SiipapDataSourceFactory factory;
	
	
	public void testDataSources(){
		int current=Calendar.getInstance().get(Calendar.YEAR);
		for(int year=2002;year<=current;year++){
			DataSource ds=factory.getDataSource(year);
			assertNotNull(ds);
		}
	}
	
	public void testSinletonDs(){
		DataSource ds=factory.getDataSource(2004);
		DataSource ds2=factory.getDataSource(2004);
		assertTrue(ds==ds2);
	}
	
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}


	public void setFactory(SiipapDataSourceFactory factory) {
		this.factory = factory;
	}
	
	

}

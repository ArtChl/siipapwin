package com.luxsoft.siipap.em.importar;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public abstract class AbstractImportadoTest extends AbstractDependencyInjectionSpringContextTests{
	
	protected SiipapJdbcTemplateFactory factory;
	
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"swx-dao-ctx.xml","em-import-ctx.xml"};							 
	}
	
	
	

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}	
	

}

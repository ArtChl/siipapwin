package com.luxsoft.siipap.em.replica;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public class AbstractReplicaTest extends AbstractTransactionalDataSourceSpringContextTests{
	
	protected SiipapJdbcTemplateFactory factory;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}
	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

}

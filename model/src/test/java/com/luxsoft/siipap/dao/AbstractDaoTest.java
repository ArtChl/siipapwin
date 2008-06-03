package com.luxsoft.siipap.dao;


import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

public abstract class AbstractDaoTest extends AbstractAnnotationAwareTransactionalTests{
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:swx-dao-ctx.xml"};
	}



}

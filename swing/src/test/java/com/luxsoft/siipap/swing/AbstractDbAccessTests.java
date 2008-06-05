package com.luxsoft.siipap.swing;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

public class AbstractDbAccessTests extends AbstractAnnotationAwareTransactionalTests{
	
	public static final String DAO_PATH="swx-dao-test-ctx.xml";
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{
				DAO_PATH
				}; 
	}

}

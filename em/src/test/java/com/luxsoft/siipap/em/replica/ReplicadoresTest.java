package com.luxsoft.siipap.em.replica;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Integration test para verificar la creacion de Replicadores
 * por parte de Spring
 * 
 * @author Ruben Cancino
 *
 */
public abstract class ReplicadoresTest extends AbstractDependencyInjectionSpringContextTests{
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"classpath:em-dao-context.xml"};
	}
	
	public void testReplicadores(){
		for(Replicadores r:Replicadores.values()){
			assertTrue("No encontro el Replicador: "+r.name(),applicationContext.containsBean(r.toString()));
		}
	}
	
	


}

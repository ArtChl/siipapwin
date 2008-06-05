package com.luxsoft.siipap.swing;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Prueba que los  componentes del framwork sean armados
 * adecuadamente por Spring IoC
 * 
 * @author Ruben Cancino
 *
 */
public class SWgIntegrationTest extends AbstractDependencyInjectionSpringContextTests{
	
	
	@Override
	protected String[] getConfigLocations() {
		return SWResources.TEXT_CONTEXTS;
	}
	
	public void testApplicationInstance(){
		for(SWBeans bean:SWBeans.values()){
			logger.debug("Localizando bean: "+bean.toString());
			boolean found=applicationContext.containsBean(bean.toString());
			if(!found)
				logger.error("No existe el bean: "+bean);
			assertTrue(found);
		}
	}
	
	public void testApplication(){
		Application app=(Application)applicationContext.getBean("application");
		assertNotNull(app);
		Application app2=Application.instance();
		assertTrue(app==app2);
	}
	

}

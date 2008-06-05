package com.luxsoft.siipap.compras;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractApplicationStarter;

/**
 * Modulo de Compras para SiipapWin
 * 
 * @author Ruben Cancino
 *
 */
public class Compras extends AbstractApplicationStarter{
	
	
	

	@Override
	protected String[] getContextPaths() {
		return new String[]{	
				"swx-compras-ctx.xml"
				,"swx-compras-actions-ctx.xml"				
		};
	}
	
	@Override
	protected ApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(getContextPaths(),ServiceLocator.getDaoContext());
		
	}
	

	public static void main(String[] args) {
		new Compras().start();
	}
	
}

package com.luxsoft.siipap.inventarios;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.AbstractApplicationStarter;


public class InventariosPruebas extends AbstractApplicationStarter{
	
	
		

	@Override
	protected String[] getContextPaths() {
		return new String[]{				
				"swx-inv-ctx.xml"
				,"swx-inv-actions-ctx.xml"
		};
	}
	
	@Override
	protected ApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(getContextPaths(),ServiceLocator.getDaoContextPruebas());
		
	}
	
	

	public static void main(String[] args) {
		new InventariosPruebas().start();
	}
	
}

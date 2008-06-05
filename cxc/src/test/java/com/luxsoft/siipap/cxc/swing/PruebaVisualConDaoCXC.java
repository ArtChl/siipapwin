package com.luxsoft.siipap.cxc.swing;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;

public abstract class PruebaVisualConDaoCXC {
	
	ApplicationContext ctx;
	Application app;
	
	
	
	protected String[] getContextPath(){
		return new String[]{				
				"swx-cxc-services.xml"
				,"swx-cxc-ctx.xml"
				,"swx-cxc-actions-ctx.xml"
				};
	}
	
	private void initContext(){
		ctx=new ClassPathXmlApplicationContext(getContextPath()
				//,ServiceLocator.getDaoContext()
				);
	}
	
	public void start() {
		doInEventDispatch();
		
	}
	
	protected void doInEventDispatch(){
		try {
			SwingUtilities.invokeAndWait(new Runnable(){

				public void run() {
					initContext();
					app=(Application)ctx.getBean("application");
					execute();
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void execute();

}

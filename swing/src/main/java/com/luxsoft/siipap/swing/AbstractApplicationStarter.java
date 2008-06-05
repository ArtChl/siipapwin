package com.luxsoft.siipap.swing;

import java.awt.KeyboardFocusManager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jgoodies.uif.util.UIFFocusTraversalPolicy;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;




/**
 * Inicializa la aplicacion, El objeto principal de esta aplicacion es inicializar el Spring ApplicationContext
 * 
 * 
 * @author Ruben Cancino
 *
 */
public abstract class AbstractApplicationStarter {
	
	
	protected Logger logger=Logger.getLogger(getClass());
	
	
	protected AbstractApplicationStarter start(){
		try {
			
			configureActions();
			
			ApplicationContext ctx=createApplicationContext();
			
			Application app=(Application)ctx.getBean("application");
			
			configureUI();
			
			checkSetup();
			
			configureHelp();
			
			app.open();
			
			return this;
			
		} catch (Exception e) {
			e.printStackTrace();			
			logger.error("Error al inicializar la aplicacion ",e);
			exit();
			return null;
		}
		
	}
	
	protected void configureActions(){
	}
	
	protected  ApplicationContext createApplicationContext(){
		return new ClassPathXmlApplicationContext(getContextPaths());
	}
	
	protected abstract String[] getContextPaths();
	
	
	protected void configureUI() {		
	    System.setProperty("apple.laf.useScreenMenuBar", "true");	    
	    SWExtUIManager.setup();	    
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
            setDefaultFocusTraversalPolicy(UIFFocusTraversalPolicy.DEFAULT);
	}
	
	 
	

	/**
	 * Checks whether a setup is necessary. For example, one can check
	 * whether the user has accepted the license agreement.
	 */
	protected void checkSetup() {
		/*
		if (!SWSetupManager.checkLicense()) 
			exit();
			*/
	}
	
	
	protected void configureHelp(){
		
	}
	
	
	protected void exit(){
		System.exit(1);
	}	 

}

package com.luxsoft.siipap.services;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TODO Eliminar
 * @author RUBEN
 * @deprecated
 *
 */
public class ServiceManager {
	
	private static ServiceManager INSTANCE;
	
	public static final String DAO_CONTEXT_PATH="siipapwin-dao-context.xml";
	
	private ApplicationContext daoContext;
	
	private Logger logger=Logger.getLogger(getClass());
	
	private ServiceManager(){
		
	}
	
	public static ServiceManager instance(){
		if(INSTANCE==null){
			INSTANCE=new ServiceManager();
		}
		return INSTANCE;
	}
	
	public ApplicationContext getDaoContext(){
		if(daoContext==null){			
			logger.info("Inicializando Spring's DAO Context para SIIPAPWIN ...");			
			daoContext=new ClassPathXmlApplicationContext(DAO_CONTEXT_PATH);
		}
		return daoContext;
	}
	
	

}

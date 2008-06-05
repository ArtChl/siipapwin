package com.luxsoft.siipap.em.managers;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.importar.VentasSync;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;


public class EMServiceLocator {
	
	private static EMServiceLocator INSTANCE;
	
	public static final String TASK_CONTEXT_PATH="em-tasks-ctx.xml";
	
	private ApplicationContext context;
	private ApplicationContext taskContext;
	
	private Logger logger=Logger.getLogger(getClass());
	
	private EMServiceLocator(){
		
	}
	
	public static EMServiceLocator instance(){
		if(INSTANCE==null){
			INSTANCE=new EMServiceLocator();
		}
		return INSTANCE;
	}
	
	public ApplicationContext getContext(){
		if(context==null){			
			logger.info("Inicializando contexto de servicios...");			
			ApplicationContext daoCtx=ServiceLocator.getDaoContext();
			context=new ClassPathXmlApplicationContext(new String[]{"em-import-ctx.xml"},daoCtx);
		}
		return context;
	}
	
	public JdbcTemplate getDefaultJdbcTemplate(){
		return (JdbcTemplate)getContext().getBean("jdbcTemplate");
	}
	public SiipapJdbcTemplateFactory getSiipapTemplateFactory(){
		return (SiipapJdbcTemplateFactory)getContext().getBean("siipapJdbcTemplateFactory");
	}
	
	public ImportadorManager getImportadorManager(){
		return (ImportadorManager)getContext().getBean("importadorManager");
	}
	
	public VentasSync getSincronizadorDeVentas(){
		return(VentasSync)getContext().getBean("ventasSync");
	}
	
	public ApplicationContext getTaskContext(){
		if(taskContext==null){
			taskContext=new ClassPathXmlApplicationContext(new String[]{TASK_CONTEXT_PATH},getContext());
			//taskContext=new ClassPathXmlApplicationContext(new String[]{TASK_CONTEXT_PATH});
		}
		return taskContext;
	}
	
	public static void main(String[] args) {
		//EMServiceLocator.instance().getTaskContext();
		Date dia=DateUtils.obtenerFecha("16/01/2008");		
		EMServiceLocator.instance().getSincronizadorDeVentas().sinconizar(dia);
	}
	
}

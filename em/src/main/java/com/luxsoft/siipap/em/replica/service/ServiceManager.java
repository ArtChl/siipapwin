package com.luxsoft.siipap.em.replica.service;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.legacy.BasicReplicator;
import com.luxsoft.siipap.em.replica.notas.NotasDeCreditoDetReplicator;
import com.luxsoft.siipap.services.ServiceLocator;


public class ServiceManager {
	
	private static ServiceManager INSTANCE;
	
	private ApplicationContext context;
	private Logger logger=Logger.getLogger(getClass());
	
	private ServiceManager(){
		
	}
	
	public static ServiceManager instance(){
		if(INSTANCE==null){
			INSTANCE=new ServiceManager();
		}
		return INSTANCE;
	}
	
	public ApplicationContext getContext(){
		if(context==null){			
			logger.info("Inicializando contexto de servicios...");			
			ApplicationContext daoCtx=ServiceLocator.getDaoContext();
			context=new ClassPathXmlApplicationContext(new String[]{"em-context.xml"},daoCtx);
		}
		return context;
	}
	
	public ReplicationManager getReplicationManager(){
		return (ReplicationManager)getContext().getBean("replicationManager");
	}
	
	public JdbcTemplate getDefaultJdbcTemplate(){
		return (JdbcTemplate)getContext().getBean("jdbcTemplate");
	}
	public SiipapJdbcTemplateFactory getSiipapTemplateFactory(){
		return (SiipapJdbcTemplateFactory)getContext().getBean("siipapJdbcTemplateFactory");
	}
	
	public Replicador getReplicador(Replicadores r){
		return (Replicador)getContext().getBean(r.toString());
	}
	
	public NotasDeCreditoDetReplicator getNotasReplicator(){
		return (NotasDeCreditoDetReplicator)getContext().getBean("notasDeCreditoReplicator");
	}
	
	public BasicReplicator getCarterReplicator(){
		BasicReplicator cr=(BasicReplicator)getContext().getBean("carterReplicator");
		return cr;
	}
	public BasicReplicator getPagcreReplicator(){
		BasicReplicator pc=(BasicReplicator)getContext().getBean("pagcreReplicator");
		return pc;
	}
	
	public BasicReplicator getDdocreReplicator(){
		BasicReplicator pc=(BasicReplicator)getContext().getBean("ddocreReplicator");
		return pc;
	}
}

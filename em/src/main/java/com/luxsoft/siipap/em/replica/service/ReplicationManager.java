package com.luxsoft.siipap.em.replica.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.notas.VinculacionDeNotas;

/**
 * Bean central para administrar el sistema de replicacion. Funciona como 
 * Facade a los diversos replicadores que permiten la sicronizacion de informacion
 * entre SIIPAP y SIIPAPWIN. 
 * 
 *   TODO: Habilitar para ser administrado desde JMX
 * 
 * @author Ruben Cancino
 *
 */
public class ReplicationManager implements ApplicationContextAware{
	
	private ApplicationContext context;
	private Logger logger=Logger.getLogger(getClass());
	
	
	private Replicador getReplicador(Replicadores r){
		return (Replicador)context.getBean(r.toString());
	}
	
	public void replicarVentas(Periodo p){
		replicar(Replicadores.VentasReplicator,p);		
		//validarReplica(p,Replicadores.VentasReplicator);		
	}
	
	public void replicarDevoluciones(Periodo p){
		replicar(Replicadores.DevolucionesReplicator,p);
		//validarReplica(p,Replicadores.DevolucionesReplicator);		
	}
	
	public void replicarNotasDeCredito(Periodo p){
		replicar(Replicadores.NotasDeCreditoReplicator,p);
		//validarReplica(p,Replicadores.NotasDeCreditoReplicator);
		
	}
	public void replicarPagos(Periodo p){
		replicar(Replicadores.PagosReplicator,p);
		//validarReplica(p,Replicadores.PagosReplicator);
		
	}
	
	public void replicar(Replicadores rr,Periodo p){
		try{
			Replicador r=getReplicador(rr);
			r.importar(p);
		}catch (Exception e) {
			logger.error(e);
		}
	}
	
	public List<ReplicaLog> validarReplica(Replicadores rep,Date dia){
		Replicador r=getReplicador(rep);
		return r.validar(new Periodo(dia));
	}
	
	@SuppressWarnings("unchecked")
	public List<ReplicaLog> validarReplica(Periodo p,Replicadores rep){
		Iterator<Date> dias=p.getDiasIterator();
		List<ReplicaLog> logs=new ArrayList<ReplicaLog>();
		while(dias.hasNext()){
			logs.addAll(validarReplica(rep,dias.next()));
		}
		return logs;
	}
	
	public void replicar(Periodo periodo){
		
		List<Date> dias=periodo.getListaDeDias();
		for(Date dia:dias){
			Periodo p=new Periodo(dia);
			//replicarVentas(p);
			replicarDevoluciones(p);
			replicarNotasDeCredito(p);
			replicarPagos(p);			
		}
		
	}
	
	public void validar(Periodo periodo){
		
		List<Date> dias=periodo.getListaDeDias();
		for(Date dia:dias){
			Periodo p=new Periodo(dia);
			//validarReplica(p,Replicadores.VentasReplicator);
			validarReplica(p,Replicadores.DevolucionesReplicator);		
			validarReplica(p,Replicadores.NotasDeCreditoReplicator);
			validarReplica(p,Replicadores.PagosReplicator);			
		}
		
	}

	public static void main(String[] args) {
		
		ServiceManager.instance().getReplicationManager()
		.replicar(new Periodo("22/04/2008","22/04/2008"));
		
		VinculacionDeNotas manager=new VinculacionDeNotas();
		manager.vincularClientes();
	    manager.vincularDevoluciones();
		//manager.vincularVentas();
		manager.vincularClientesPagos();
		 
		
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context=applicationContext;
		
	}
	

}

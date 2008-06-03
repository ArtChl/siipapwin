package com.luxsoft.siipap.cxc.managers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import com.luxsoft.siipap.clipper.ExportadorCliente;
import com.luxsoft.siipap.cxc.domain.ClienteHolder;

/**
 * Hibernate's {@link Interceptor} implementation
 * para el mantenimiento de los registros de CXC
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class CXCInterceptor2 extends EmptyInterceptor{
	
	private ExportadorCliente exportadorClientes;
	private Set insertsdeletes=new HashSet();
	private Set updates=new HashSet();
	private Logger logger=Logger.getLogger(getClass());
	private boolean enabled=true;
	
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {		
		if (entity instanceof ClienteHolder){
			insertsdeletes.add(entity);
		}
		return false;
	}


	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {		
		if (entity instanceof ClienteHolder)
			updates.add(entity);
		return false;
	}


	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof ClienteHolder)
			insertsdeletes.add(entity);
	}

	
	
	@Override
	public void postFlush(Iterator entities) {
		if(!isEnabled())
			return;
		try{
			for(Iterator it=insertsdeletes.iterator();it.hasNext();){
				final Object entity=it.next();
				System.out.println("Insertando/Eliminando registro nuevo: "+entity.toString());
				ClienteHolder cc=(ClienteHolder)entity;
				getExportadorClientes().exportarSaldo(cc.getClave());
				
			}
			/**
			for(Iterator it=updates.iterator();it.hasNext();){
				final Object entity=it.next();
			
			}
			
			for(Iterator it=deletes.iterator();it.hasNext();){
				final Object entity=it.next();
				//System.out.println("Eliminando registro CXC DELETE con: "+it.next().toString());
			}
			*/
			
		}catch (Exception e) {
			logger.error("Error en interceptor central",e);
		}finally{
			insertsdeletes.clear();
			updates.clear();
		}
	}
	
	


	public ExportadorCliente getExportadorClientes() {
		return exportadorClientes;
	}
	public void setExportadorClientes(ExportadorCliente exportadorClientes) {
		this.exportadorClientes = exportadorClientes;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
	

}

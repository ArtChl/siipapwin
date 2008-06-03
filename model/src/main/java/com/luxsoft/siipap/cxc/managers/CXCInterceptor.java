package com.luxsoft.siipap.cxc.managers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Hibernate's {@link Interceptor} implementation
 * para el mantenimiento de los registros de CXC
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class CXCInterceptor extends EmptyInterceptor{
	
	private Session session;
	private Set inserts=new HashSet();
	private Set updates=new HashSet();
	private Set deletes=new HashSet();
	
	
	public void preFlush(Iterator entities) throws CallbackException{
		while(entities.hasNext()){
			Object obj=entities.next();
			if(obj instanceof Venta){
				Venta v=(Venta)obj;
				if(v.getOrigen().equals("CRE")){
					if(v.getTipo().endsWith("G")){
						double desc=v.getDescuentoFacturado();
						if(v.getCredito()==null)
							v.actualizarDatosDeCredito();
						System.out.println("Registrando descuento: "+desc);
						v.getCredito().setDescuento(desc);
					}
					
					else if(v.getTipo().endsWith("N")||v.getTipo().endsWith("P")||v.getTipo().endsWith("X")){
						//double desc=v.getDescuentoFacturado();
						CantidadMonetaria importeF=CantidadMonetaria.pesos(0);
						CantidadMonetaria importeL=CantidadMonetaria.pesos(0);
						for(VentaDet det:v.getPartidas()){
							importeL=importeL.add(CantidadMonetaria.pesos(det.getImporteBruto()));
							importeF=importeF.add(CantidadMonetaria.pesos(det.getImporteSemiNeto()));
						}
						CantidadMonetaria cs=importeF.divide(importeL.amount());
						double desc=cs.multiply(100).amount().doubleValue();
						System.out.println("Aplicando descuento sobre venta N P X");
						v.getCredito().setDescuento(desc);
					}
					
				}
			}
		}
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {		
		if (entity instanceof Venta){
			inserts.add(entity);
			//Venta v=(Venta)entity;
			
		}
		return false;
	}


	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {		
		if (entity instanceof Venta)
			updates.add(entity);
		return false;
	}


	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof Venta)
			deletes.add(entity);
	}


	/**
	@Override
	public void postFlush(Iterator entities) {
		try{
			for(Iterator it=inserts.iterator();it.hasNext();){
				final Object entity=it.next();
				System.out.println("Insertando registro nuevo: "+entity.toString());
				//System.out.println("Actualizando registro nuevo : "+it.next().toString());
				
			}
			
			for(Iterator it=updates.iterator();it.hasNext();){
				final Object entity=it.next();
				//System.out.println("Actualizando registro CXC UPDATE con: "+it.next().toString());
			}
			
			for(Iterator it=deletes.iterator();it.hasNext();){
				final Object entity=it.next();
				//System.out.println("Eliminando registro CXC DELETE con: "+it.next().toString());
			}
			
		}finally{
			inserts.clear();
			updates.clear();
			deletes.clear();
		}
	}
	
	**/
	
	

}
